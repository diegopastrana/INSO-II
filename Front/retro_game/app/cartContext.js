// context/CartContext.tsx
'use client';

import { createContext, useContext, useEffect, useState } from 'react';

const CartContext = createContext();

export const CartProvider = ({ children }) => {
  const [cart, setCart] = useState([]);
  const [loading, setLoading] = useState(false);

  const fetchCart = async () => {
    setLoading(true);
    const res = await fetch('http://localhost:8080/api/cart', {
      credentials: "include"
    });
    const data = await res.json();
    setCart(data.items);
    setLoading(false);
  };

  const addToCart = async (videojuegoId, cantidad = 1) => {
    const res = await fetch('http://localhost:8080/api/cart', {
      credentials: "include",  
      method: 'POST',
      body: JSON.stringify({ videojuegoId, cantidad }),
      headers: { 'Content-Type': 'application/json' },
    });
    const data = await res.json();
    setCart(data.items);
  };

  const removeFromCart = async (productId) => {
    const res = await fetch(`http://localhost:8080/api/cart/${productId}`, {
      credentials: "include",
      method: 'DELETE',
      headers: { 'Content-Type': 'application/json' },
    });
    const data = await res.json();
    setCart(data.items);
  };

  useEffect(() => {
    fetchCart();
  }, []);

  return (
    <CartContext.Provider value={{ cart, addToCart, removeFromCart, loading }}>
      {children}
    </CartContext.Provider>
  );
};

export const useCart = () => useContext(CartContext);
