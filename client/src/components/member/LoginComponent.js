import { useState } from "react";
import useLogin from "../../hooks/useLogin";
import useMove from "../../hooks/useMove";
import { Link } from "react-router-dom";
import KakaoLoginComponent from "./KakaoLoginComponent";
import { removeCookie } from "../../util/cookieUtil";

const initState = { memId: '', pw: '' };

const LoginComponent = () => {
    const [loginParam, setLoginParam] = useState({ ...initState });
    const { doLogin } = useLogin();
    const { moveToPath } = useMove();

    const handleChange = (e) => {
        setLoginParam(prev => ({ ...prev, [e.target.name]: e.target.value }));
    };

    const handleClickLogin = () => {
        doLogin(loginParam).then(res => {
            alert("로그인 되었습니다.");
            moveToPath('/');
        }).catch(error => {
            alert(error.message || "로그인에 실패했습니다.");
            removeCookie("member");
        });
    };

    const handleKeyDown = (e) => {
        if (e.key === "Enter") handleClickLogin();
    };

    return (
        <div className="p-4 border-2 border-blue-300 mt-10">
            <h2 className="text-4xl text-center text-blue-600 font-bold mb-6">Login</h2>

            <div className="mb-4">
                <label className="font-bold block">ID</label>
                <input
                    name="memId"
                    value={loginParam.memId}
                    onChange={handleChange}
                    onKeyDown={handleKeyDown}
                    className="w-full p-3 border rounded"
                    autoFocus
                />
            </div>

            <div className="mb-4">
                <label className="font-bold block">Password</label>
                <input
                    name="pw"
                    type="password"
                    value={loginParam.pw}
                    onChange={handleChange}
                    onKeyDown={handleKeyDown}
                    className="w-full p-3 border rounded"
                />
            </div>

            <div className="flex justify-center">
                <button
                    onClick={handleClickLogin}
                    className="bg-blue-500 text-white font-bold p-3 w-36 rounded hover:bg-blue-600"
                >
                    LOGIN
                </button>
            </div>

            <div className="text-center mt-4">
                <Link to="/findId" className="text-sm text-gray-600 hover:underline">아이디 찾기</Link>
                <span className="mx-2">|</span>
                <Link to="/resetPw" className="text-sm text-gray-600 hover:underline">비밀번호 재설정</Link>
            </div>
            <KakaoLoginComponent />
        </div>
    );
};

export default LoginComponent;
