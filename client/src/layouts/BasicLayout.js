import Header from "./Header";
import Footer from "./Footer";

const BasicLayout = ({ children, isFullWidth = false }) => {
    return (
        <div>
            <Header />
            <div className={`pt-20 bg-white w-full h-full`}>
                <main className={`${isFullWidth ? 'w-full' : 'max-w-screen-xl mx-auto'} main-content `}>
                    {children}
                </main>
            </div>
            <Footer />
        </div>
    );
}

export default BasicLayout;