import jwtAxios from "../util/jwtUtil";
import { API_SERVER_HOST } from "./config";
import { API_MAPPING } from "./config";
import axios from "axios";
const host = `${API_SERVER_HOST}/api/event`
const mapping = `${API_MAPPING}`


//  행사 등록하는 요청
export const postAddEvent = async (formData) => {
    const res = await jwtAxios.post(`${host}/register`,formData);
    return res.data;
}

// 행사 수정 페이지에서 행사 정보를 불러오기 위한 요청
export const getEventById=async(evtNum)=> {
       const res=await jwtAxios.get(`${host}/SelectOne`,evtNum);
    return res.data;
}

// 행사 수정 페이지에서 상품 정보를 수정하기 위한 요청
export const updateEvent=async(formData)=> {
    const res = await jwtAxios.get(`${host}/UpdateEvt`,formData, {
        headers: { 'Content-Type': 'multipart/form-data' }
    });
    return res.data;
}

// 행사 목록 페이지에서 행사 삭제하기 위한 요청
export const delEvt = async (evtNum) => {
  const res = await jwtAxios.delete(`${host}/DeleteEvt`, {
    params: { evtNum }
  });
  return res.data;
};

// 행사 리스트 페이지에서 행사 리스트를 얻어오기 위한 요청
export const getList=async()=> {
     const res=await jwtAxios.get(`${host}/evtList`)
}