"use client";

import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import styles from "./page.module.css";
import Filters from "./filters";
import Game from "./game";
import Carrito from "./carrito";

const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL;

export default function ClientHome({ initialGames, initialPage }) {
  const [finalLayout, setFinalLayout] = useState(false);
  const [filters, setFilters] = useState({
    genero: "",
    precio: 30,
  });
  const [searchTerm, setSearchTerm] = useState("");

  const [page, setPage] = useState(initialPage || 1);
  const [lastPage, setLastPage] = useState(Math.ceil(initialGames.total / initialGames.size));
  const [allGames, setAllGames] = useState(initialGames.items);
  const [filteredGames, setFilteredGames] = useState(initialGames.items);
  const [isClientData, setIsClientData] = useState(false);
  const [showCart, setShowCart] = useState(false);


  const router = useRouter();

  useEffect(() => {
    setFinalLayout(true);
  }, []);

  const handleFilterChange = (e) => {
    const { name, value } = e.target;

    if (name === "precio") {
      let precio = Number(value);
      if (precio > 30) precio = 30;
      if (precio < 0) precio = 0;

      setFilters((prev) => ({
        ...prev,
        precio,
      }));
    } else {
      setFilters((prev) => ({
        ...prev,
        [name]: value,
      }));
    }
  };

  const applyFilters = () => {
    const base = isClientData ? allGames : initialGames.items;

    const filtered = base.filter((game) => {
      const matchGenero = !filters.genero || game.genero === filters.genero;
      const matchPrecio = game.precio <= filters.precio;
      const matchNombre = game.nombre.toLowerCase().includes(searchTerm.toLowerCase());

      return matchGenero && matchPrecio && matchNombre;
    });

    setFilteredGames(filtered);
  };

  const handlePageChange = (newPage) => {
    router.push(`/page/${newPage}`);
  };

  return (
    <>
      {showCart && <Carrito onClose={() => setShowCart(false)} />}

      <header className={`${styles.header} ${finalLayout ? styles.final : ""}`}>
        <div className={styles.titleContainer}>
          <h1 className={styles.title}>RetroGameHub</h1>
          <p className={styles.subtitle}>Tu Tienda De Videojuegos Retro</p>
        </div>

        <div className={styles.search}>
          <svg
            xmlns="http://www.w3.org/2000/svg"
            height="24px"
            viewBox="0 -960 960 960"
            width="24px"
            fill="#000000"
          >
            <path d="M784-120 532-372q-30 24-69 38t-83 14q-109 0-184.5-75.5T120-580q0-109 75.5-184.5T380-840q109 0 184.5 75.5T640-580q0 44-14 83t-38 69l252 252-56 56ZM380-400q75 0 127.5-52.5T560-580q0-75-52.5-127.5T380-760q-75 0-127.5 52.5T200-580q0 75 52.5 127.5T380-400Z" />
          </svg>

          <input
            type="text"
            placeholder="Buscar..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            onKeyDown={(e) => {
              if (e.key === "Enter") applyFilters();
            }}
          />

          <a href={API_BASE_URL+"/login"}>
            <svg
              xmlns="http://www.w3.org/2000/svg"
              height="24px"
              viewBox="0 -960 960 960"
              width="24px"
              fill="#000000"
              className={styles.userIcon}
            >
              <path d="M234-276q51-39 114-61.5T480-360q69 0 132 22.5T726-276q35-41 54.5-93T800-480q0-133-93.5-226.5T480-800q-133 0-226.5 93.5T160-480q0 59 19.5 111t54.5 93Zm246-164q-59 0-99.5-40.5T340-580q0-59 40.5-99.5T480-720q59 0 99.5 40.5T620-580q0 59-40.5 99.5T480-440Zm0 360q-83 0-156-31.5T197-197q-54-54-85.5-127T80-480q0-83 31.5-156T197-763q54-54 127-85.5T480-880q83 0 156 31.5T763-763q54 54 85.5 127T880-480q0 83-31.5 156T763-197q-54 54-127 85.5T480-80Z" />
            </svg>
          </a>

          <button onClick={() => setShowCart(true)} className={styles.cartButton}>🛒</button>

        </div>

      </header>

      <main className={`${styles.main} ${finalLayout ? styles.finalMain : ""}`}>
        <Filters
          filters={filters}
          handleFilterChange={handleFilterChange}
          applyFilters={applyFilters}
        />

        <div className={styles.TarjetaJuegos}>
          {filteredGames.map((game) => (
            <Game game={game} key={game.id} />
          ))}
        </div>

        <div className={styles.pagination}>
          <button disabled={page === 1} onClick={() => handlePageChange(page - 1)}>
            Anterior
          </button>
          <span>Página {page} de {lastPage}</span>
          <button disabled={page === lastPage} onClick={() => handlePageChange(page + 1)}>
            Siguiente
          </button>
        </div>

      </main>
    </>
  );
}
