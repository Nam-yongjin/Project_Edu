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

// 행사 상세정보 조회 (JWT 필요)
export const getEventById = async (eventNum) => {
  const res = await jwtAxios.get(`${event}/eventDetail`, {
        params : {eventNum: eventNum}
    });
  return res.data;
};

export const getUserReserveDetail = async (eventNum, memId) => {
  const res = await jwtAxios.get(`${event}/userReserveDetail`, {
    params: { eventNum, memId }
  });
  return res.data;
};

// 행사 수정 요청 (JWT 필요)
export const updateEvent = async (eventNum, formData) => {
  const res = await jwtAxios.put(`${event}/update?eventNum=${eventNum}`, formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  });
  return res.data;
};

// 행사 취소 요청 (JWT 필요)
export const deleteEvent = async (eventNum) => {
  return await jwtAxios.delete(`${event}/delete`, {
    params: { eventNum },
  });
};

// 행사 리스트 조회 요청 (비로그인 허용, 일반 axios 사용)
export const getList = async (current) => {
  const res = await axios.get(`${event}/List`, {
    params: { page: current }
  });
  return res.data;
};

// 행사 검색
export const getSearchList = async ({ page, searchType, keyword, state, category, sortOrder }) => {
  const res = await axios.get(`${event}/search`, {
    params: {
      page,
      searchType,
      keyword,
      state,
      category,
      sortOrder
    }
  });
  return res.data;
};

// 행사 신청 요청
export const applyEvent = async (dto) => {
  const res = await jwtAxios.post(`${event}/apply`, dto);
  return res.data;
};

// 사용자 예약행사 조회
export const getReservationList = async ({ page = 0, size = 5 }) => {
  const res = await jwtAxios.get(`${event}/reservation`, {
    params: { page, size },
  });
  return res.data;
};

// 사용자 예약 취소
export const cancelReservation = async (evtRevNum) => {
  const res = await jwtAxios.delete(`${event}/cancel`, {
    params: { evtRevNum },
    withCredentials: true, // 이걸 추가해야 로그인으로 처리되어 취소 가능
  });
  return res.data;
};

//============================================
// 배너로 사용할 행사 불러오는 코드
export const getBannerList = async (current) => {
  const res = await axios.get(`${event}/banners`, {
    params: { page: current }
  });
  return res.data;
};

export const registerBanner = async (formData) => {
  const response = await axios.post(`${event}/banners/register`, formData); // ✅ 헤더 제거
  return response.data;
};