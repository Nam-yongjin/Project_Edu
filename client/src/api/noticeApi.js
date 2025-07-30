import axios from "axios";
import jwtAxios from "../util/jwtUtil";
import { API_SERVER_HOST } from "./config";
import { API_MAPPING } from "./config";

const host = `${API_SERVER_HOST}/api`
const mapping = `${API_MAPPING}`
const notice = `${host}${mapping.notice}`

//공지사항 전체(검색 + 페이징)
export const NoticeList = async (params) => {
    const res = await axios.get(`${notice}/NoticeList`, {params}); //쿼리스트링(검색, 페이지)
    return res.data;
}

export const NoticeAllList = async () => {
    const res = await axios.get(`${host}/notice/NoticeList`); //쿼리스트링(검색, 페이지)
    return res.data;
}

//공지사항 상세(조회수 증가)
export const NoticeDetail = async (noticeNum) => {
    const res = await axios.get(`${notice}/${noticeNum}`); //식별자(상세조회, 수정, 삭제)
    return res.data;
}

//고정 공지사항 조회
export const PinnedNotice = async () => {
    const res = await axios.get(`${notice}/pinned`); //정해진 서브 경로(특정 목적의 Api)
    return res.data;
}

//공지사항 등록
export const createNotice = async (formData) => {
    const res = await jwtAxios.post(`${notice}`, formData, {
        headers: {"Content-Type": "multipart/form-data"},
    });
    return res.data;
}

//공지사항 수정
export const updateNotice = async (noticeNum, formData) => {
    const res = await jwtAxios.post(`${notice}/${noticeNum}`, formData, {
        headers: {"Content-Type": "multipart/form-data"}, //자동설정 되지만 명시하면 더 안전
    });
    return res.data;
}

//공지사항 삭제(단일)
export const deleteNotice = async (noticeNum) => {
    const res = await jwtAxios.delete(`${notice}/${noticeNum}`);
    return res.data;
}

//공지사항 삭제(일괄)
export const deleteNotices = async (noticeNums) => {
    const res = await jwtAxios.delete(`${notice}`, {
        data: noticeNums //배열로 받기 - 백엔드 @RequestBody List<Long> noticeNums
    });
    return res.data;
}


