import Header from "./Header";
import Footer from "./Footer";

const BasicLayout = ({ children }) => {
    return (
        <div>
            <Header />
            <div className="bg-white my-5 w-full h-full flex flex-col space-y-1 md:flex-row md:space-x-1 md:space-y- 0">
                <main className="max-w-[1000px] mx-auto min-h-[600px] w-full my-5 py-5">{children}</main>
            </div>
            <Footer />
        </div>
    );
}

export default BasicLayout;