import LoginComponent from "../../components/member/LoginComponent"
import MainMenu from "../../menus/MainMenu"
import { Outlet } from "react-router-dom"

const LoginPage = () => {
    return (
        <div className='fixed top-0 left-0 z-[1055] flex flex-col h-full w-full'>
            <MainMenu />
            <div className="flex flex-wrap w-full h-full justify-center	items-center border-2">
                <LoginComponent />
                <Outlet />
            </div>
        </div>
    )
}
export default LoginPage