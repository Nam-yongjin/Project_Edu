import { useSearchParams } from "react-router-dom";
import { useEffect } from "react";
import { useDispatch } from "react-redux";
import useMove from "../../hooks/useMove";
import { loginSocialAsync } from "../../slices/loginSlice";

// 인가 코드의 페이지 처리
const KakaoRedirectPage = () => {
    const [searchParams] = useSearchParams();
    const authCode = searchParams.get("code");
    const dispatch = useDispatch();
    const { moveToPath } = useMove();

    // 인가 코드가 변경되었을 때 loginSocialAsync안의 getAccessToken() 호출
    useEffect(() => {
        if (!authCode) {
            alert("인가 코드가 존재하지 않습니다.");
            return;
        }

        dispatch(loginSocialAsync(authCode))
            .unwrap()
            .then(() => {
                alert("카카오 계정으로 로그인 되었습니다.");
                moveToPath("/");
            })
            .catch((err) => {
                alert("카카오 로그인 실패.");
                moveToPath("/login");
            });
    }, [authCode]);

    return <div>카카오 로그인 중입니다...</div>;
};

export default KakaoRedirectPage;
