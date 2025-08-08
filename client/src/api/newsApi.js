import axios from "axios";
import jwtAxios from "../util/jwtUtil";
import { API_SERVER_HOST } from "./config";
import { API_MAPPING } from "./config";

const host = `${API_SERVER_HOST}/api`
const news = `${host}${API_MAPPING.news}`

//언론보도 전체(검색 + 페이징)
export const NewsList = async (params) => {
     const res = await axios.get(`${news}/NewsList`, { params });
    return res.data;
}

//언론보도 상세(조회수 증가)
export const NewsDetail = async (newsNum) => {
    const res = await axios.get(`${news}/NewsDetail/${newsNum}`); //식별자(상세조회, 수정, 삭제)
    return res.data;
}

//언론보도 등록
export const createNews = async (formData) => {
    const res = await jwtAxios.post(`${news}/AddNews`, formData, {
        headers: {"Content-Type": "multipart/form-data"},
    });
    return res.data;
}

//언론보도 수정
export const updateNews = async (newsNum, formData) => {
    const res = await jwtAxios.put(`${news}/UpdateNews/${newsNum}`, formData, {
        headers: {"Content-Type": "multipart/form-data"}, //자동설정 되지만 명시하면 더 안전
    });
    return res.data;
}

//언론보도 삭제(단일)
export const deleteNews = async (newsNum) => {
    console.log("프론트에서 백엔드로 보낼 newsNum:", newsNum);
    const res = await jwtAxios.delete(`${news}/DeleteNews/${newsNum}`);
    return res.data;
}

//언론보도 삭제(일괄)
export const deleteNewsByIds = async (newsNums) => {
    const res = await jwtAxios.delete(`${news}//DeleteNewsByIds`, {
        data: newsNums //배열로 받기 - 백엔드 @RequestBody List<Long> newsNums
    });
    return res.data;
}