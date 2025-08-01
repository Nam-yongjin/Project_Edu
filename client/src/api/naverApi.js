import axios from "axios";
import { API_SERVER_HOST } from "./config";

const CLIENT_ID = "XK757MnqVq2Mgu15SLns";
const REDIRECT_URI = "http://127.0.0.1:3000/login/naver";
const AUTH_CODE_PATH = "https://nid.naver.com/oauth2.0/authorize";
const host = `${API_SERVER_HOST}/api`;

// 네이버 인가 코드 받기 URL (SocialLoginComponent 사용)
export const getNaverLoginLink = () => {
    const state = Date.now().toString(36);  // CSRF 방지용
    const params = new URLSearchParams({
        response_type: "code",
        client_id: CLIENT_ID,
        redirect_uri: REDIRECT_URI,
        state
    });
    return `${AUTH_CODE_PATH}?${params.toString()}`;
};

// 백엔드로 code,state 넘겨서 JWT 발급까지 처리
export const getMemberWithNaverCode = async (code, state) => {
    const res = await axios.get(`${host}/login/naver`, {
        params: { code, state }
    });
    return res.data;
};
