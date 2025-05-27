"use client";

import styles from "./page.module.css";

export default function Filters({ filters, handleFilterChange, applyFilters }) {
  return (
    <div className={styles.filterContainer}>
      {/* FILTRO POR GÉNERO */}
      <div className={styles.filter}>
        <label htmlFor="genero">Género:</label>
        <select
          id="genero"
          name="genero"
          value={filters.genero}
          onChange={handleFilterChange}
        >
          <option value="">Todos</option>
          <option value="Racing">Racing</option>
          <option value="Shooter">Shooter</option>
          <option value="Fighting">Fighting</option>
          <option value="Puzzle">Puzzle</option>
          <option value="Platform">Platform</option>
          <option value="Strategy">Strategy</option>
          <option value="Adventure">Adventure</option>
          <option value="Simulator">Simulator</option>
          <option value="Indie">Indie</option>
          <option value="Arcade">Arcade</option>
          <option value="Role-playing (RPG)">RPG</option>
          <option value="Hack and slash/Beat 'em up">Hack and Slash</option>
          <option value="Visual Novel">Visual Novel</option>
          <option value="Sports">Sports</option>
        </select>
      </div>

      {/* FILTRO POR PRECIO */}
      <div className={styles.filter}>
        <label htmlFor="precio">Precio (máx. 30 €):</label>
        <input
          type="number"
          id="precio"
          name="precio"
          value={filters.precio}
          min="1"
          max="30"
          onChange={handleFilterChange}
        />
      </div>


      {/* BOTÓN DE APLICAR FILTROS */}
      <button
        type="button"
        className={styles.applyButton}
        onClick={applyFilters}
      >
        Aplicar filtros
      </button>

    </div>
  );
}
