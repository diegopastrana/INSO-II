'use client';

import { createContext, useContext, useEffect, useState } from 'react';

const CartContext = createContext();

// 🧩 URL configurable desde .env.local
const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080";

export const CartProvider = ({ children }) => {
  const [cart, setCart] = useState([]);
  const [loading, setLoading] = useState(false);

  const getToken = () => (typeof window !== 'undefined' ? localStorage.getItem('token') : null);

  const fetchCart = async () => {
    setLoading(true);
    try {
      const token = getToken();
      const res = await fetch(`${API_BASE_URL}/api/cart`, {
        headers: {
          Authorization: `Bearer ${token}`
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
      const token = getToken();
      await fetch(`${API_BASE_URL}/api/cart`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`
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
      const token = getToken();
      await fetch(`${API_BASE_URL}/api/cart/${videojuegoId}`, {
        method: 'DELETE',
        headers: {
          Authorization: `Bearer ${token}`
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
