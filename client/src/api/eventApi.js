import axios from "axios";
import jwtAxios from "../util/jwtUtil";
import { API_SERVER_HOST } from "./config";
import { API_MAPPING } from "./config";

const host = `${API_SERVER_HOST}/api`;
const event = `${host}${API_MAPPING.event}`;

// 행사 등록 요청 (JWT 필요)
export const postAddEvent = async (formData) => {
  const res = await jwtAxios.post(`${event}/register`, formData);
  return res.data;
};

// 행사 단건 조회 (JWT 필요)
export const getEventById = async (eventNum) => {
  const res = await jwtAxios.get(`${event}/eventDetail`, {
        params : {eventNum: eventNum}
    });
  return res.data;
};

// 행사 수정 요청 (JWT 필요)
export const updateEvent = async (formData) => {
  const res = await jwtAxios.get(`${event}/UpdateEvt`, formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  });
  return res.data;
};

// 행사 삭제 요청 (JWT 필요)
export const delEvt = async (evtNum) => {
  const res = await jwtAxios.delete(`${event}/DeleteEvt`, {
    params: { evtNum }
  });
  return res.data;
};

// ✅ [수정됨] 행사 리스트 조회 요청 (비로그인 허용, 일반 axios 사용)
export const getList = async (current) => {
  const res = await axios.get(`${event}/List`, {
    params: { page: current }
  });
  return res.data;
};