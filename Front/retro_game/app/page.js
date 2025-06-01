import { redirect } from 'next/navigation';
import { useEffect } from "react";
import { useRouter } from "next/router";

export default function Home() {
  const router = useRouter();

  useEffect(() => {
    const token = new URLSearchParams(window.location.search).get("token");
    if (token) {
      document.cookie = `AUTH_TOKEN=${token}; Path=/; Secure; SameSite=None`;
      router.replace("/"); // Redirige para limpiar la URL
    }
  }, []);

  redirect('/page/1');
}
