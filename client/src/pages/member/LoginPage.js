import LoginComponent from "../../components/member/LoginComponent"
import BasicLayout from "../../layouts/BasicLayout"

const LoginPage = () => {
    return (
        <div>
            <BasicLayout>
                <div className='top-0 left-0 z-[1055] flex flex-col h-full w-full'>
                    <div className="flex flex-wrap w-full h-full justify-center	items-center border-2">
                        <LoginComponent />
                    </div>
                </div>
            </BasicLayout>
        </div>
    )
}
export default LoginPage