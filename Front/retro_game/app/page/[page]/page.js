import ClientHome from "../../clientHome";

export default async function Page({ params }) {
  const { page } = await params 
  const res = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/api/games?page=${page}&size=12`);
  console.log(res);
  const data = await res.json();

  return <ClientHome initialGames={data} initialPage={page} />;
}
