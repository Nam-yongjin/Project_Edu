import Header from "./Header";
import Footer from "./Footer";

const BasicLayout = ({ children, isFullWidth = false }) => {
    return (
        <div>
            <Header />
            <div className={`pt-20 bg-white w-full h-full flex flex-col md:flex-row md:space-x-1 md:space-y-0`}>
                <main className={`${isFullWidth ? 'w-full' : 'max-w-screen-xl mx-auto'} main-content `}>
                    {children}
                </main>
            </div>
            <Footer />
        </div>
    );
}

export default BasicLayout;