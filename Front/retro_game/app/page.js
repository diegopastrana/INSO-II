"use client";

import { useState, useEffect } from "react";
import styles from "./page.module.css";

export default function Home() {
  const [finalLayout, setFinalLayout] = useState(false);

  useEffect(() => {
    const timer = setTimeout(() => {
      setFinalLayout(true);
    }, 5000); // 5 segundos de splash
    return () => clearTimeout(timer);
  }, []);

  return (
    <>
      <header className={`${styles.header} ${finalLayout ? styles.final : ""}`}>
        <div className={styles.titleContainer}>
          <h1 className={styles.title}>RetroGameHub</h1>
          <p className={styles.subtitle}>Tu Tienda De Videojuegos Retro</p>
        </div>
        <div className={styles.search}>
          <svg xmlns="http://www.w3.org/2000/svg" height="24px" viewBox="0 -960 960 960" width="34px" fill="#000000"><path d="M784-120 532-372q-30 24-69 38t-83 14q-109 0-184.5-75.5T120-580q0-109 75.5-184.5T380-840q109 0 184.5 75.5T640-580q0 44-14 83t-38 69l252 252-56 56ZM380-400q75 0 127.5-52.5T560-580q0-75-52.5-127.5T380-760q-75 0-127.5 52.5T200-580q0 75 52.5 127.5T380-400Z"/></svg>
          <input type="text" placeholder="Buscar..." />
        </div>
      </header>
      <main className={`${styles.main} ${finalLayout ? styles.finalMain : ""}`}>
        {/* Aquí irá el catálogo de juegos y el selector de temáticas en el futuro */}
      </main>
    </>
  );
}
