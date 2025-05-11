"use client";

import { useState, useEffect } from "react";
import styles from "./page.module.css";

export default function Fiters(props) {
    return(
        <div className={styles.filterContainer}>
          <div className={styles.filter}>
            <label htmlFor="genre">Género:</label>
            <select
              id="genre"
              name="genre"
              value={props.filters.genre}
              onChange={props.handleFilterChange}
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
              value={props.filters.year}
              min="1850"
              onChange={props.handleFilterChange}
            />
          </div>
          <div className={`${styles.filter} ${styles.priceFilter}`}>
            <label htmlFor="price">Precio: {props.filters.price}€</label>
            <input
              type="range"
              id="price"
              name="price"
              min="1"
              max="200"
              value={props.filters.price}
              onChange={props.handleFilterChange}
            />
          </div>
          <button className={styles.applyButton} onClick={props.applyFilters}>
            Aplicar filtros
          </button>
        </div>
    )
    
}