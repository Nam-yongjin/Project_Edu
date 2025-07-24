import { useState } from "react";
import useLogin from "../../hooks/useLogin";
import KakaoLoginComponent from "./KakaoLoginComponent";
import useMove from "../../hooks/useMove";

const initState = {
    memId: '',
    pw: ''
}

const LoginComponent = () => {
    const [loginParam, setLoginParam] = useState({ ...initState })

    const { doLogin } = useLogin()
    const { moveToPath } = useMove()

    const handleChange = (e) => {
        loginParam[e.target.name] = e.target.value

        setLoginParam({ ...loginParam })
    }

    const handleClickLogin = (e) => {
        doLogin(loginParam)
            .then(data => {
                if (data.error) {
                    alert("아이디와 패스워드를 다시 확인하세요")
                } else {
                    alert("로그인 되었습니다.")
                    moveToPath('/')
                }
            })
    }
    const handleKeydown = (e) => {
        if (e.key === "Enter")
            handleClickLogin();
    }

    return (
        <div className="border-2 border-sky-200 mt-10 m-2 p-4">
            <div className="flex justify-center">
                <div className="text-4xl m-4 p-4 font-extrabold text-blue-500">Login Component</div>
            </div>
            <div className="flex justify-center">
                <div className="relative mb-4 flex w-full flex-wrap items-center">
                    <div className="w-full p-3 text-left font-bold">ID</div>
                    <input className="w-full p-3 rounded-r border border-solid border-neutral-500 shadow-md"
                        name="memId" type={'text'} value={loginParam.memId} onChange={handleChange} /> </div>
            </div>
            <div className="flex justify-center">
                <div className="relative mb-4 flex w-full flex-wrap items-center">
                    <div className="w-full p-3 text-left font-bold">Password</div>
                    <input className="w-full p-3 rounded-r border border-solid border-neutral-500 shadow-md"
                        name="pw" type={'password'} value={loginParam.pw} onChange={handleChange} onKeyDown={handleKeydown}/> </div>
            </div>
            <div className="flex justify-center">
                <div className="relative mb-4 flex w-full justify-center">
                    <div className="w-2/5 p-6 flex justify-center font-bold">
                        <button className="rounded p-4 w-36 bg-blue-500 text-xl	text-white"
                            onClick={handleClickLogin}>
                            LOGIN
                        </button>
                    </div>
                </div>
            </div>
            <KakaoLoginComponent />
        </div>
    )

}
export default LoginComponent
