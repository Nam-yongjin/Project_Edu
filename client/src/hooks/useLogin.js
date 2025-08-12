import { useDispatch } from "react-redux";
import { loginPostAsync, logout } from "../slices/loginSlice";

const useLogin = () => {
    const dispatch = useDispatch();

    const doLogin = async (loginParam) => {
        return dispatch(loginPostAsync(loginParam)).unwrap();
    };

    const doLogout = () => dispatch(logout());

    return { doLogin, doLogout };
};

export default useLogin;
