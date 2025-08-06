import jwtAxios from "../util/jwtUtil";
import { API_SERVER_HOST } from "./config";
import { API_MAPPING } from "./config";
const host = `${API_SERVER_HOST}/api`
const admin = `${host}${API_MAPPING.admin}`;

// 관리자 회원조회
export const viewMembers = async (params) => {
    const res = await jwtAxios.get(`${admin}/members`, { params });
    return res.data;
};

// 관리자 회원상태(블랙리스트, 탈퇴) 변경
export const memberStateChange = async ({ memId, state }) => {
    const res = await jwtAxios.put(`${admin}/MemberStateChange`, { memId, state });
    return res.data;
};
