import { useRecoilState, useResetRecoilState } from "recoil";
import { setCookie, removeCookie } from "../util/cookieUtil";
import { loginPost } from "../api/memberApi";
import { useNavigate, createSearchParams } from "react-router-dom";
import signinState from "../atoms/signinState";

const useLogin = () => {

    const navigate = useNavigate()
    const [loginState, setLoginState] = useRecoilState(signinState)
    const isLogin = loginState.memId ? true : false     // 로그인 여부
    const resetState = useResetRecoilState(signinState)

    // 로그인
    const doLogin = async (loginParam) => {
        try {
            const result = await loginPost(loginParam);
            if (result.error) {
                return result; // 실패면 쿠키 저장하지 않고 리턴
            }
            saveAsCookie(result);
            return result;
        } catch (ex) {
            console.error("로그인 실패:", ex);

            // 401 에러 메시지를 사용자에게 보여줄 수 있도록
            return {
                error: true,
                message: "아이디 또는 비밀번호가 잘못되었습니다."
            };
        }
    };

    // 로그아웃
    const doLogout = () => {
        removeCookie('member')
        resetState()
    };

    // 1일 동안 쿠키 저장
    const saveAsCookie = (data) => {
        setCookie("member", JSON.stringify(data), 1);
        setLoginState(data);
    };

    const exceptionHandle = (ex) => {
        const errorMsg = ex.response.data.error
        const errorStr = createSearchParams({ error: errorMsg }).toString()

        if (errorMsg === 'REQUIRE_LOGIN') {
            alert("로그인 해야 합니다.")
            navigate({ pathname: '/login', search: errorStr })
            return
        }
        if (ex.response.data.error === 'ERROR_ACCESSDENIED') {
            alert("해당 메뉴를 사용할 수 있는 권한이 없습니다.")
            navigate({ pathname: '/login', search: errorStr })
            return
        }
    }
    return { loginState, isLogin, doLogin, doLogout, /*doLoginKakao,*/ saveAsCookie, exceptionHandle }
}
export default useLogin