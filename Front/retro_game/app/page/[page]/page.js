import ClientHome from "../../clientHome";

export default async function Page({ params }) {
  const { page } = await params 
  const res = await fetch(`https://inso-ii.onrender.com/api/games?page=${page}&size=12`);
  const data = await res.json();

  return <ClientHome initialGames={data} initialPage={page} />;
}
