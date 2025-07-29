import axios from "axios";
import { API_SERVER_HOST } from "./config";

const REST_API_KEY = `40a6bcfce5d2ae851c94353d664e94f8`; // REST API키 
const REDIRECT_URI = `http://127.0.0.1:3000/login/kakao`;
const AUTH_CODE_PATH = `https://kauth.kakao.com/oauth/authorize`;
const ACCESS_TOKEN_URL = `https://kauth.kakao.com/oauth/token`;
const host = `${API_SERVER_HOST}/api`;

export const getKakaoLoginLink = () => {
    const kakaoURL = `${AUTH_CODE_PATH}?client_id=${REST_API_KEY}&redirect_uri=${REDIRECT_URI}&response_type=code`;
    return kakaoURL;
};

export const getAccessToken = async (authCode) => {
    const header = {
        headers: {
            "Content-Type": "application/x-www-form-urlencoded",
        }
    };
    const params = {
        grant_type: "authorization_code",   // 고정값: 인증 방식
        client_id: REST_API_KEY,
        redirect_uri: REDIRECT_URI,
        code: authCode
    };
    // 카카오의 토큰 발급 서버로 POST 요청을 보냄
    const res = await axios.post(ACCESS_TOKEN_URL, params, header);
    const accessToken = res.data.access_token;

    return accessToken;
};

export const getMemberWithAccessToken = async (accessToken) => {
    const res = await axios.get(`${host}/login/kakao?accessToken=${accessToken}`);
    return res.data;
};