import jwtAxios from "../util/jwtUtil";
import axios from "axios";
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

// 메인페이지 배너 등록
export const uploadBanners = async (params) => {
    const res = await jwtAxios.post(`${admin}/banner/upload`, params, {
        headers: {
            'Content-Type': 'multipart/form-data',
        },
    });
    return res.data;
}

// 배너 조회
export const getAllBanners = async () => {
    const res = await axios.get(`${admin}/banner/list`);
    return res.data;
}

// 배너 삭제
export const deleteBanner = async (params) => {
    const res = await jwtAxios.delete(`${admin}/banner/delete/${params}`);
    return res.data;
}

// 배너 순서 변경
export const updateBannerSequence = async (params) => {
    const res = await jwtAxios.put(`${admin}/banner/sequence`, params);
    return res.data;
}

// 배너 이미지 불러오기
export const getBannerImage = (params) => {
    return `${admin}/banner/view?filePath=${encodeURIComponent(params)}`;
}