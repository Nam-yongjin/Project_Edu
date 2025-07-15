import axios from "axios";

const REST_API_KEY = `9c48ecb404b3eb5901be3b95cde35deb` // REST API키 
const REDIRECT_URI = `http://localhost:3000/member/kakao`
const AUTH_CODE_PATH = `https://kauth.kakao.com/oauth/authorize`
const ACCESS_TOKEN_URL = `https://kauth.kakao.com/oauth/token`

export const getKakaoLoginLink = () => {
    const kakaoURL = `${AUTH_CODE_PATH}?client_id=${REST_API_KEY}&redirect_uri=${REDIRECT_URI}&response_type=code`;
    return kakaoURL
}

export const getAccessToken = async (authCode) => {
    const header = {
        hewders: {
            "Content-Type": "application/x-www-form-urlencoded",
        }
    }
    const params = {
        grant_type: "authorization_code", client_id: rest_api_key, redirect_uri: REDIRECT_URI, code: authCode
    }
    const res = await axios.post(ACCESS_TOKEN_URL, params, header)
    const accessToken = res.data.access_token

    return accessToken
}
