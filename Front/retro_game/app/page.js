import ClientHome from "./clientHome";

export default async function Home() {
 const res = await fetch("http://localhost:8080/api/games");
 const games = await res.json();
 return <ClientHome initialGames = {games}/>
}
