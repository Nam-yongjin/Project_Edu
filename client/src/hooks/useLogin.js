import { useNavigate, createSearchParams } from "react-router-dom";
import { logout, login } from "../slices/loginSlice";
import { useDispatch, useSelector } from "react-redux";
import { loginPost } from "../api/memberApi";

const useLogin = () => {

    const navigate = useNavigate();
    const dispatch = useDispatch();
    const loginState = useSelector((state) => state.loginState) || {};
    const isLogin = !!loginState.memId;    // 로그인 여부

    // 로그인
    const doLogin = async (loginParam) => {
        try {
            const result = await loginPost(loginParam);
            if (result.error) {
                return result; // 실패면 쿠키 저장하지 않고 리턴
            }
            dispatch(login(result));
            return result;
        } catch (ex) {
            console.error("로그인 실패:", ex);

            return {
                error: true,
                message: "아이디 또는 비밀번호가 잘못되었습니다."
            };
        }
    };

    // 로그아웃
    const doLogout = () => {
        dispatch(logout())
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
    return { loginState, isLogin, doLogin, doLogout, exceptionHandle }
}
export default useLogin