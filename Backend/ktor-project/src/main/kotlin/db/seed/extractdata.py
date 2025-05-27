#!/usr/bin/env python3
import os
import sys
import json
import time
import random
import requests
from pathlib import Path

# --- CONFIGURACIÓN ---
CLIENT_ID = os.getenv("TWITCH_CLIENT_ID")
CLIENT_SECRET = os.getenv("TWITCH_CLIENT_SECRET")
OUTPUT_FILE = Path("./games.json")

# Parámetros para la distribución de precios
MEAN_PRICE = 20.0
STD_DEV = MEAN_PRICE * 0.1  # 10% de la media

if not CLIENT_ID or not CLIENT_SECRET:
    print("Error: establece TWITCH_CLIENT_ID y TWITCH_CLIENT_SECRET en tu entorno.", file=sys.stderr)
    sys.exit(1)


# --- AUTENTICACIÓN IGDB ---
def get_igdb_token(cid, secret):
    resp = requests.post(
        "https://id.twitch.tv/oauth2/token",
        params={
            "client_id": cid,
            "client_secret": secret,
            "grant_type": "client_credentials"
        }
    )
    resp.raise_for_status()
    return resp.json()["access_token"]


token = get_igdb_token(CLIENT_ID, CLIENT_SECRET)
IGDB_HEADERS = {
    "Client-ID": CLIENT_ID,
    "Authorization": f"Bearer {token}"
}

# --- Paginación y extracción de IGDB ---
all_games = []
LIMIT = 50
offset = 0
TOTAL_GAMES = 50

print("🔄 Extrayendo juegos de IGDB y asignando precios ficticios...")

while len(all_games) < TOTAL_GAMES:
    query = f"""
        fields name, summary, cover.url, genres.name, language_supports, similar_games.name;
        limit {LIMIT};
        offset {offset};
    """
    resp = requests.post("https://api.igdb.com/v4/games", headers=IGDB_HEADERS, data=query)
    resp.raise_for_status()
    batch = resp.json()

    if not batch:
        break

    for g in batch:
        print(g)
        name = g.get("name", "").strip()
        desc = g.get("summary", "") or ""
        first_genre = g.get("genres", [{"name": "Unkown"}])[0].get('name', "")
        cover = g.get("cover", {}).get("url", "")

        if cover != "":
            cover = "https:" + cover

        # Generar precio ficticio: N(MEAN_PRICE, STD_DEV)
        fake_price = round(max(0.99, random.gauss(MEAN_PRICE, STD_DEV)), 2)

        all_games.append({
            "nombre":      name,
            "precio":      fake_price,
            "descripcion": desc,
            "genero": first_genre,
            "cover": cover
        })

    offset += LIMIT
    print(f"  • Procesados {len(all_games)} juegos...", end="\r")
    time.sleep(0.3)  # respetar rate limit IGDB

print(f"\n✅ Total procesados: {len(all_games)} juegos.")

# --- Volcado a JSON ---
OUTPUT_FILE.parent.mkdir(parents=True, exist_ok=True)
with OUTPUT_FILE.open("w", encoding="utf-8") as f:
    json.dump(all_games, f, ensure_ascii=False, indent=2)

print(f"🎉 JSON generado en {OUTPUT_FILE}")
