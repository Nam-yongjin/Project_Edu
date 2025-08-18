import axios from "axios";
import jwtAxios from "../util/jwtUtil";
import { API_SERVER_HOST } from "./config";
import { API_MAPPING } from "./config";
const host = `${API_SERVER_HOST}/api`
const question = `${host}${API_MAPPING.question}`;
const answer = `${host}${API_MAPPING.answer}`;

// 문의 사항 글 추가하는 요청
export const questionAdd = async (title, content, state) => {
    const res = await jwtAxios.post(`${question}/addQuestion`, {
        title: title,
        content: content,
        state: state
    });
    return res.data;
};

// 문의 사항 글 목록 불러오기 (검색어 없음)
export const getSelect = async (pageCount, sortBy, sort, startDate, endDate) => {
    const res = await axios.get(`${question}/QnAView`, {
        params: {
            pageCount: pageCount,
            sort: sort,
            sortBy: sortBy,
            startDate: startDate,
            endDate: endDate
        }
    });
    return res.data;
}

// 문의 사항 글 목록 불러오기 (검색어 있음)
export const getSelectSearch = async (search, type, pageCount, sortBy, sort, startDate, endDate) => {
    console.log(search,type,pageCount,sortBy,sort,startDate,endDate);
    const res = await axios.get(`${question}/QnAView`, {
        params: {
            search: search,
            type: type,
            pageCount: pageCount,
            sortBy: sortBy,
            sort: sort,
            startDate: startDate,
            endDate: endDate
        }
    });
    return res.data;
}

// 질문 글 삭제 하는 요청
export const deleteQuestions = async (questionNums) => {
    const res = await jwtAxios.delete(`${question}/deleteQuestions`, {
        data: questionNums,  // 배열을 body에 직접 담아서 보냄
    });
    return res.data;
}

// 글 상세 페이지에서 글에 대한 정보를 불러오기 위한 요청
export const getDetail = async (questionNum) => {
    const res = await axios.get(`${question}/QnADetail/${questionNum}`);
    return res.data;
}

// 글 상세 페이지에서 질문 글 수정 요청
export const questionUpdate = async (questionNum,state,title,content) => {
    const res = await jwtAxios.put(`${question}/updateQuestion`, {
        questionNum,
        state,
        title,
        content
    });
    return res.data;
}

// 문의 사항 답변 추가 요청
export const answerAdd = async  (content,questionNum) => {
    const res = await jwtAxios.post(`${answer}/addAnswer`, {
        content: content,
        questionNum:questionNum
    });
    return res.data;
};

// 글 상세 페이지에서 답변 수정 요청
export const answerUpdate = async (answerNum,content) => {
    const res = await jwtAxios.put(`${answer}/updateAnswer`, {
        answerNum,
        content
    });
    return res.data;
}

// 글 상세 페에지에서 답변 글 삭제 하는 요청
export const deleteAnswer = async (answerNum) => {
    const res = await jwtAxios.delete(`${answer}/deleteAnswer/${answerNum}`);
    return res.data;
};