import { useRecoilState, useResetRecoilState } from "recoil";
import { setCookie, removeCookie } from "../util/cookieUtil";
import { loginPost } from "../api/memberApi";
import { loginKakao } from "../api/kakaoApi";
import { useNavigate, createSearchParams } from "react-router-dom";
import signinState from "../atoms/signinState";

const useLogin = () => {

    const navigate = useNavigate()
    const [loginState, setLoginState] = useRecoilState(signinState)
    const isLogin = loginState.memId ? true : false     // 로그인 여부
    const resetState = useResetRecoilState(signinState)

    // 로그인
    const doLogin = async (loginParam) => {
        const result = await loginPost(loginParam);

        saveAsCookie(result);
        return result;
    };

    // 로그아웃
    const doLogout = () => {
        removeCookie('member')
        resetState()
    };

    // 카카오 로그인
    // const doLoginKakao = async (token) => {
    //     const formData = new FormData()
    //     formData.append("accessToken", token)
    //     const result = await loginKakao(formData);
    //     saveAsCookie(result)
    //     return result
    // }

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