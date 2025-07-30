import { useNavigate } from "react-router-dom";

const useMove = () => {

    const navigate = useNavigate();

    // 해당 경로로 이동
    const moveToPath = (path) => {
        navigate({ pathname: path }, { replace: true });
    };

    // 뒤로가기
    const moveToReturn = () => {
        navigate(-1);
    };

    // 로그인 페이지로 이동
    const moveToLogin = () => {
        navigate({ pathname: '/login' }, { replace: true });
    };
    return { moveToPath, moveToReturn, moveToLogin };
};
export default useMove;