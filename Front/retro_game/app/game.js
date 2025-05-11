import styles from "./page.module.css";
import { useCart } from "./cartContext";
export default function Game({game}) {
    const {addToCart, loading} = useCart();
    const handleCLick = () => {
        addToCart(game.id)
    }
    return(
        <div className={styles.InfoJuego}>
            <h3>
            {game.nombre}
            </h3>
            <p>
            {game.descripcion}
            </p>
            <p>
            {game.precio}
            </p>
            <button onClick={handleCLick}>{"Añadir al carrito"}</button>
        </div>
    )
}


