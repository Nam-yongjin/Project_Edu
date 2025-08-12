import axios from "axios";
import jwtAxios from "../util/jwtUtil";
import { API_SERVER_HOST } from "./config";
import { API_MAPPING } from "./config";
const host = `${API_SERVER_HOST}/api`
const qna = `${host}${API_MAPPING.qna}`;


// 문의 사항 글 추가하는 요청
export const postAdd = async (title, content, state) => {
  const res = await jwtAxios.post(`${qna}/addQuestion`, {
    title: title,
    content: content,
    state:state
  });
  return res.data;
};

// 글 상세 페이지에서 글에 대한 정보를 불러오기 위한 요청
export const getDetail = async (questionNum) => {
    const res = await axios.get(`${qna}/getOne`, {
        params: { questionNum: questionNum }
    });
    return res.data;
}

// 문의 사항 글 목록 불러오기 (검색어 없음)
export const getSelect = async (pageCount, sortBy, sort) => {
    const res = await axios.get(`${qna}/QnAView`, {
     params: {pageCount:pageCount,
            sort:sort,
            sortBy:sortBy
     }
    });
    return res.data;
}

// 문의 사항 글 목록 불러오기 (검색어 있음)
export const getSelectSearch = async (search, type, pageCount, sortBy, sort) => {
    const res = await axios.get(`${qna}/QnAView`, {
    params: {
    search:search,
    type:type,   
    pageCount:pageCount,
    sortBy:sortBy,
    sort:sort,
    }
    });
    return res.data;
}