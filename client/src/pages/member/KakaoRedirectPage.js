import { useSearchParams } from "react-router-dom";
import { useEffect } from "react";
import { getAccessToken, getMemberWithAccessToken } from "../../api/kakaoApi";
import {login} from "../../slices/loginSlice";
import { useDispatch } from "react-redux";
import useMove from "../../hooks/useMove";

// 인가 코드의 페이지 처리
const KakaoRedirectPage = () => {

    const [searchParams] = useSearchParams()
    const authCode = searchParams.get("code")
    const dispatch = useDispatch()
    const {moveToPath} = useMove()

    // 인가 코드가 변경되었을 때 getAccessToken()을 호출
    useEffect(() => {
        getAccessToken(authCode).then(accessToken => {
            getMemberWithAccessToken(accessToken).then(memberInfo => {
                dispatch(login(memberInfo))
                moveToPath("/");
            })
        })
    }, [authCode])

    return (
        <div>
        </div>
    )
}

export default KakaoRedirectPage
