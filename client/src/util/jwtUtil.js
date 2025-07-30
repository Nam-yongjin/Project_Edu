import axios from "axios";
import { getCookie, setCookie } from "./cookieUtil";
import { API_SERVER_HOST } from "../api/config";

const jwtAxios = axios.create();

// 요청 전 실행
const beforeReq = (config) => {
    const member = getCookie("member");
    if (!member?.accessToken) {
        // 로그인 상태가 아니면 요청 자체를 강제로 실패
        return Promise.reject({ response: { data: { error: "REQUIRE_LOGIN" } } });
    }

    // accessToken이 있으면 Authorization: Bearer accessToken을 헤더에 추가
    config.headers.Authorization = `Bearer ${member.accessToken}`;
    return config;
};

// 응답을 받은 후 실행
const beforeRes = async (res) => {
    const data = res.data;
    if (data?.error === "ERROR_ACCESS_TOKEN") {
        const member = getCookie("member");
        // accessToken 재발급 요청
        const result = await refreshJWT(member.accessToken, member.refreshToken);

        const updated = { ...member, ...result };
        // 새로 발급받은 토큰을 쿠키에 저장
        setCookie("member", JSON.stringify(updated), 1);

        // 원래 요청을 한 번 더 보냄
        res.config.headers.Authorization = `Bearer ${result.accessToken}`;
        return await axios(res.config);
    }
    return res;
};

// 현재 accessToken과 refreshToken을 사용하여 서버에서 새 토큰 발급
 // accessToken의 유효기간이 지낫을 경우 서버 호출
const refreshJWT = async (accessToken, refreshToken) => {
    const header = { headers: { Authorization: `Bearer ${accessToken}` } };
    const res = await axios.get(`${API_SERVER_HOST}/api/refresh?refreshToken=${refreshToken}`, header);
    return res.data;
};

jwtAxios.interceptors.request.use(beforeReq, err => Promise.reject(err));   // 요청을 보내기 직전 실행
jwtAxios.interceptors.response.use(beforeRes, err => Promise.reject(err));  // 응답을 받은 직후 실행

export default jwtAxios;
