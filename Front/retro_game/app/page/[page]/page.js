import ClientHome from "../../clientHome";

export default async function Page({ params }) {
  const page = parseInt(params?.page || "1", 10);
  const res = await fetch(`http://localhost:8080/api/games?page=${page}&size=12`);
  const data = await res.json();

  return <ClientHome initialGames={data} initialPage={page} />;
}
