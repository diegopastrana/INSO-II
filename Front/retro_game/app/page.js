"use client"
import { useEffect } from "react";
import { useRouter } from "next/navigation"; // ← Ojo, esta es la versión buena para app router (Next.js 13+)

export default function Home() {
  const router = useRouter();

  useEffect(() => {
    const token = new URLSearchParams(window.location.search).get("token");
    if (token) {
      document.cookie = `AUTH_TOKEN=${token}; Domain=.onrender.com; Path=/; Secure; SameSite=None`;
    }

    router.replace("/page/1"); // redirige sí o sí
  }, []);

  return <p>Redirigiendo...</p>;
}
