import { useDispatch } from "react-redux";
import { loginPostAsync, logout } from "../slices/loginSlice";

const useLogin = () => {
    const dispatch = useDispatch();

    const doLogin = async (loginParam) => {
        // unwrap -> 성공 시: payload만 바로 반환, 실패 시: throw 된 에러가 catch로 전달됨
        return dispatch(loginPostAsync(loginParam)).unwrap();
    };

    const doLogout = () => dispatch(logout());

    return { doLogin, doLogout };
};

export default useLogin;
