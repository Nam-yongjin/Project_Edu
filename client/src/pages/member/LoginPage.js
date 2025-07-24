import LoginComponent from "../../components/member/LoginComponent"
import Header from "../../layouts/Header"
import Footer from "../../layouts/Footer"
import { Outlet } from "react-router-dom"

const LoginPage = () => {
    return (
        <div className='fixed top-0 left-0 z-[1055] flex flex-col h-full w-full'>
            <Header />
            <div className="flex flex-wrap w-full h-full justify-center	items-center border-2">
                <LoginComponent />
                <Outlet />
            </div>
            <Footer />
        </div>
    )
}
export default LoginPage