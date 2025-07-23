import { useSearchParams } from "react-router-dom";
import { useEffect } from "react";
import { getAccessToken, getMemberWithAccessToken } from "../../api/kakaoApi";
import moveToPath from "../../hooks/useMove";
import {login} from "../../slices/loginSlice";
import { useDispatch } from "react-redux";

// 인가 코드의 페이지 처리
const KakaoRedirectPage = () => {

    const [searchParams] = useSearchParams()
    const authCode = searchParams.get("code")
    const dispatch = useDispatch()

    // 인가 코드가 변경되었을 때 getAccessToken()을 호출
    useEffect(() => {
        getAccessToken(authCode).then(accessToken => {
            getMemberWithAccessToken(accessToken).then(memberInfo => {
                dispatch(login(memberInfo))
            }).catch(err => {
                console.error("❌ getMemberWithAccessToken 실패2:", err)
            })
        }).catch(err => {
            console.error("❌ getAccessToken 실패2:", err)
        })
    }, [authCode])

    return (
        <div>
        </div>
    )
}

export default KakaoRedirectPage
