import { useSearchParams } from "react-router-dom";
import { useEffect } from "react";
import { getAccessToken } from "../api/KakaoApi";

// 인가 코드의 페이지 처리
const KakaoRedirectPage = () => {

    const [searchParams] = useSearchParams()
    const authCode = searchParams.get("code") 

    // 인가 코드가 변경되었을 때 getAccessToken()을 호출
    useEffect(() => {
        getAccessToken(authCode).then(data => {
            console.log(data)
        })
    }, [authCode])

    return (
        <div>
            <div>Kakao Login Redirect</div>
            <div>{authCode}</div>
        </div>
    )
}

export default KakaoRedirectPage
