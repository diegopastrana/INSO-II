"use client";

import { useState, useEffect } from "react";
import styles from "./page.module.css";

export default function Home() {
  const [finalLayout, setFinalLayout] = useState(false);
  const [filters, setFilters] = useState({
    genre: "",
    year: 1900,
    price: 100,
  });

  useEffect(() => {
    const timer = setTimeout(() => {
      setFinalLayout(true);
    }, 5000); // 5 segundos de splash
    return () => clearTimeout(timer);
  }, []);

  const handleFilterChange = (e) => {
    const { name, value } = e.target;
    setFilters((prevFilters) => ({
      ...prevFilters,
      [name]: value,
    }));
  };

  const applyFilters = () => {
    // Lógica para aplicar los filtros
    console.log(filters);
  };

  return (
      <>
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
                width="34px"
                fill="#000000"
            >
              <path d="M784-120 532-372q-30 24-69 38t-83 14q-109 0-184.5-75.5T120-580q0-109 75.5-184.5T380-840q109 0 184.5 75.5T640-580q0 44-14 83t-38 69l252 252-56 56ZM380-400q75 0 127.5-52.5T560-580q0-75-52.5-127.5T380-760q-75 0-127.5 52.5T200-580q0 75 52.5 127.5T380-400Z" />
            </svg>
            <input type="text" placeholder="Buscar..." />
          </div>
        </header>
        <main className={`${styles.main} ${finalLayout ? styles.finalMain : ""}`}>
          <div className={styles.filterContainer}>
            <div className={styles.filter}>
              <label htmlFor="genre">Género:</label>
              <select
                  id="genre"
                  name="genre"
                  value={filters.genre}
                  onChange={handleFilterChange}
              >
                <option value="">Selecciona un género</option>
                <option value="accion">Acción</option>
                <option value="aventura">Aventura</option>
                <option value="rpg">RPG</option>
                <option value="plataformas">Plataformas</option>
                <option value="deportes">Deportes</option>
                <option value="puzzle">Puzzle</option>
              </select>
            </div>
            <div className={styles.filter}>
              <label htmlFor="year">Año:</label>
              <input
                  type="number"
                  id="year"
                  name="year"
                  value={filters.year}
                  min="1850"
                  onChange={handleFilterChange}
              />
            </div>
            <div className={`${styles.filter} ${styles.priceFilter}`}>
              <label htmlFor="price">Precio: {filters.price}€</label>
              <input
                  type="range"
                  id="price"
                  name="price"
                  min="1"
                  max="200"
                  value={filters.price}
                  onChange={handleFilterChange}
              />
            </div>
            <button className={styles.applyButton} onClick={applyFilters}>
              Aplicar filtros
            </button>
          </div>
          {/* Aquí irá el catálogo de juegos filtrado */}
        </main>
      </>
  );
}
