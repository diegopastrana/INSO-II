'use client';

import { createContext, useContext, useEffect, useState } from 'react';

const CartContext = createContext();

// 🧩 URL configurable desde .env.local
// || "https://inso-ii.onrender.com"
const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL;

export const CartProvider = ({ children }) => {
  const [cart, setCart] = useState([]);
  const [loading, setLoading] = useState(false);

  const fetchCart = async () => {
    setLoading(true);
    console.log("🌐 API_BASE_URL:", API_BASE_URL);
    try {
      const res = await fetch(`${API_BASE_URL}/api/cart`, {
        credentials: 'include',
        headers: {
          'Content-Type': 'application/json'
        }
      });

      if (!res.ok) throw new Error(`Error ${res.status}: al obtener el carrito`);

      const data = await res.json();
      setCart(data);
    } catch (error) {
      console.error('fetchCart error:', error);
    } finally {
      setLoading(false);
    }
  };

  const addToCart = async (videojuegoId, cantidad = 1) => {
    try {
      await fetch(`${API_BASE_URL}/api/cart`, {
        method: 'POST',
        credentials: 'include',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({ videojuegoId, cantidad })
      });

      await fetchCart();
    } catch (error) {
      console.error('addToCart error:', error);
    }
  };

  const removeFromCart = async (videojuegoId) => {
    try {
      await fetch(`${API_BASE_URL}/api/cart/${videojuegoId}`, {
        method: 'DELETE',
        credentials: 'include',
        headers: {
          'Content-Type': 'application/json'
        }
      });

      await fetchCart();
    } catch (error) {
      console.error('removeFromCart error:', error);
    }
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
