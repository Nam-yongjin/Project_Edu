import axios from "axios"
import { getCookie, setCookie } from "./cookieUtil"
import { API_SERVER_HOST } from "../api/config"

const jwtAxios = axios.create()

// 요청 전에 실행
const beforeReq = (config) => {
    const memberInfo = getCookie("member")
    if (!memberInfo) {
        return Promise.reject( // 로그인 상태가 아니면 요청 자체를 강제로 실패
            {
                response:
                {
                    data:
                        { error: "REQUIRE_LOGIN" }
                }
            }
        )
    }
    const { accessToken } = memberInfo

    // accessToken이 있으면 Authorization: Bearer accessToken을 헤더에 추가
    config.headers.Authorization = `Bearer ${accessToken}`
    return config
}

// 요청을 보내기 전에 발생하는 오류를 처리
const requestFail = (err) => {  
    return Promise.reject(err)
}

// 응답을 받은 후 실행
const beforeRes = async (res) => { 

    const data = res.data

    // 응답 내용에 ERROR_ACCESS_TOKEN 이 있으면 
    if (data && data.error === 'ERROR_ACCESS_TOKEN') {
        const memberCookieValue = getCookie("member")
        // refreshJWT 함수로 accessToken을 재발급 요청
        const result = await refreshJWT(memberCookieValue.accessToken, memberCookieValue.refreshToken)

        memberCookieValue.accessToken = result.accessToken
        memberCookieValue.refreshToken = result.refreshToken

        // 새로 발급받은 토큰을 쿠키에 저장
        setCookie("member", JSON.stringify(memberCookieValue), 1)

        const originalRequest = res.config
        // 원래 요청(originalRequest)을 한 번 더 보냄
        originalRequest.headers.Authorization = `Bearer ${result.accessToken}`
        return await axios(originalRequest)
    }
    return res
}

// 응답 실패 처리
const responseFail = (err) => {
    return Promise.reject(err);
}

// 현재 accessToken과 refreshToken을 사용하여 서버에서 새 토큰을 받음
const refreshJWT = async (accessToken, refreshToken) => { // accessToken의 유효기간이 지낫을 경우 서버 호출

    const host = API_SERVER_HOST
    const header = { headers: { "Authorization": `Bearer ${accessToken}` } }
    const res = await axios.get(`${host}/api/refresh?refreshToken=${refreshToken}`, header)

    return res.data
}

jwtAxios.interceptors.request.use(beforeReq, requestFail) // 요청을 보내기 직전 실행 예: 토큰 붙이기, 로그인 여부 확인
jwtAxios.interceptors.response.use(beforeRes, responseFail) //응답을 받은 직후 실행 예: accessToken 만료되었는지 확인하고 자동 재발급 처리

export default jwtAxios