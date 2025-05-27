import styles from "./page.module.css";
import { useCart } from "./cartContext";

export default function Game({ game }) {
  const { addToCart, loading } = useCart();

  const handleCLick = () => {
    addToCart(game.id);
  };

  return (
    <div className={styles.TarjetaJuego}>
      <h3>{game.nombre}</h3>
      <img src={game.cover} alt={game.nombre} />
      <p>{game.genero}</p>
      <p>{game.precio} €</p>
      <button onClick={() => {
          console.log("Añadiendo", game.videojuegoId || game.id);
          addToCart(game.videojuegoId || game.id);
        }}>
          Añadir al carrito
      </button>

    </div>
  );
}
