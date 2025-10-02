import Header from "../Components/Header";
import ActionButtons from "../Components/ActionButtons";
import SearchBar from "../Components/SearchBar";
import Layout from "../Components/Layout";

export default function Home() {
  return (
    <Layout>
      <div className="page-container">
        <Header />
        <ActionButtons />
        <SearchBar />
      </div>
    </Layout>
  );
}
