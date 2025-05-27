"use client";
import { useCart } from "./cartContext";
import styles from "./page.module.css";
import { useState } from "react";

export default function Carrito({ onClose }) {
  const { cart, removeFromCart, loading } = useCart();
  const [compraFinalizada, setCompraFinalizada] = useState(false);

  const total = cart.reduce(
    (acc, item) => acc + item.precioUnitario * item.cantidad,
    0
  ).toFixed(2);

  const handleSubmit = (e) => {
    e.preventDefault();
    // Aquí podrías lanzar la integración con Stripe
    setCompraFinalizada(true);
  };

  return (
    <div className={styles.carritoOverlay}>
      <div className={styles.carrito}>
        <h2>🛒 Tu Carrito</h2>

        {loading ? (
          <p>Cargando...</p>
        ) : cart.length === 0 ? (
          <p>El carrito está vacío.</p>
        ) : (
          <>
            <ul>
              {cart.map((item) => (
                <li key={item.videojuegoId}>
                  <p>{item.nombre}</p>
                  <p>{item.precioUnitario} €</p>
                  <button onClick={() => removeFromCart(item.videojuegoId)}>
                    Eliminar
                  </button>
                </li>
              ))}
            </ul>

            <div className={styles.checkoutSection}>
              <h3>Resumen del pedido</h3>
              <p>Total: {total} €</p>

              {compraFinalizada ? (
                <p className={styles.confirmacion}>✅ ¡Compra realizada con éxito!</p>
              ) : (
                <form className={styles.checkoutForm} onSubmit={handleSubmit}>
                  <input type="text" placeholder="Nombre completo" required />
                  <input type="email" placeholder="Correo electrónico" required />
                  <input type="text" placeholder="Dirección de envío" required />
                  <button type="submit">Finalizar compra</button>
                </form>
              )}
            </div>
          </>
        )}

        <button onClick={onClose}>Cerrar</button>
      </div>
    </div>
  );
}
