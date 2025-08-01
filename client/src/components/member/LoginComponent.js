import { useState, useEffect } from "react";
import useLogin from "../../hooks/useLogin";
import useMove from "../../hooks/useMove";
import { Link } from "react-router-dom";
import SocialLoginComponent from "./SocialLoginComponent";
import { removeCookie } from "../../util/cookieUtil";

const initState = { memId: '', pw: '' };

const LoginComponent = () => {
    const [loginParam, setLoginParam] = useState({ ...initState });
    const { doLogin } = useLogin();
    const { moveToPath } = useMove();
    const [failCount, setFailCount] = useState(0);
    const [cooldown, setCooldown] = useState(0);

    const handleChange = (e) => {
        setLoginParam(prev => ({ ...prev, [e.target.name]: e.target.value }));
    };

    const handleClickLogin = () => {
        doLogin(loginParam).then(res => {
            alert("로그인 되었습니다.");
            setFailCount(0);
            moveToPath('/');
        }).catch(error => {
            alert(error.message || "로그인에 실패했습니다.");
            const nextFailCount = failCount + 1;
            setFailCount(nextFailCount);
            removeCookie("member");
        });
    };

    const handleKeyDown = (e) => {
        if (e.key === "Enter") handleClickLogin();
    };

    useEffect(() => {
        if(failCount >= 5){
            setCooldown(30);
            alert("로그인에 5회 이상 실패하셨습니다.")
        };
    }, [failCount]);

    useEffect(() => {
        if (cooldown <= 0) {
            return setFailCount(0);
        }
        const timer = setInterval(() => setCooldown(c => c - 1), 1000);
        return () => clearInterval(timer);
    }, [cooldown]);

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

            {cooldown > 0 ?
                <>
                    <div className="flex justify-center font-bold">{cooldown}초 후 로그인 재시도</div>
                    <div className="flex justify-center">

                        <button
                            className="bg-blue-500 text-white font-bold p-3 w-36 rounded active:bg-blue-600"
                        >
                            LOGIN
                        </button>
                    </div>
                </>
                :
                <div className="flex justify-center">
                    <button
                        onClick={handleClickLogin}
                        className="bg-blue-500 text-white font-bold p-3 w-36 rounded active:bg-blue-600"
                    >
                        LOGIN
                    </button>
                </div>}


            <div className="text-center mt-4">
                <Link to="/findId" className="text-sm text-gray-600 hover:underline">아이디 찾기</Link>
                <span className="mx-2">|</span>
                <Link to="/resetPw" className="text-sm text-gray-600 hover:underline">비밀번호 재설정</Link>
            </div>
            <SocialLoginComponent />
        </div>
    );
};

export default LoginComponent;
