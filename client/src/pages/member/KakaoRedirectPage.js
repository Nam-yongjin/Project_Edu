import { useSearchParams } from "react-router-dom";
import { useEffect } from "react";
import { getAccessToken, getMemberWithAccessToken } from "../../api/kakaoApi";
import { login } from "../../slices/loginSlice";
import { useDispatch } from "react-redux";
import useMove from "../../hooks/useMove";

// 인가 코드의 페이지 처리
const KakaoRedirectPage = () => {

    const [searchParams] = useSearchParams();
    const authCode = searchParams.get("code");
    const dispatch = useDispatch();
    const { moveToPath } = useMove();

    // 인가 코드가 변경되었을 때 getAccessToken()을 호출
    useEffect(() => {
        if (!authCode) {
            console.warn("authCode 없음");
            return;
        };
        getAccessToken(authCode).then(accessToken => {
            getMemberWithAccessToken(accessToken).then(memberInfo => {
                dispatch(login(memberInfo));
                moveToPath("/");
            }).catch(err => {
                console.error("memberInfo 조회 실패", err);
            });
        }).catch(err => {
            console.error("accessToken 발급 실패", err);
        });
    }, [authCode]);

    return (
        <div>
            카카오 로그인 중입니다...
        </div>
    );
};

export default KakaoRedirectPage;
