import { useDispatch, useSelector } from "react-redux";
import { loginPostAsync, logout } from "../slices/loginSlice";
import { useNavigate, createSearchParams } from "react-router-dom";

const useLogin = () => {
    const dispatch = useDispatch();
    const navigate = useNavigate();

    const loginState = useSelector((state) => state.loginState);
    const isLogin = !!loginState?.memId;

    const doLogin = async (loginParam) => {
        return dispatch(loginPostAsync(loginParam)).unwrap();
    };

    const doLogout = () => dispatch(logout());

    const exceptionHandle = (e) => {
        const errorMsg = e.response?.data?.error;
        const errorStr = createSearchParams({ error: errorMsg }).toString();

        if (errorMsg === 'REQUIRE_LOGIN') {
            alert("로그인이 필요합니다.");
            navigate({ pathname: '/login', search: errorStr });
        } else if (errorMsg === 'ERROR_ACCESSDENIED') {
            alert("접근 권한이 없습니다.");
            navigate({ pathname: '/login', search: errorStr });
        };
    };

    return { loginState, isLogin, doLogin, doLogout, exceptionHandle };
};

export default useLogin;
