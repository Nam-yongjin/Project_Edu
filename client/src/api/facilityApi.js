import axios from "axios";
import jwtAxios from "../util/jwtUtil";
import { API_SERVER_HOST } from "./config";
import { API_MAPPING } from "./config";

const host = `${API_SERVER_HOST}/api`;
const facility = `${host}${API_MAPPING.facility}`;

// 시설 등록 요청 (JWT 필요)
export const registerFacility = async (formData) => {
  const res = await jwtAxios.post(`${facility}/register`, formData);
  return res.data;
};

export const FacilityList = async ({ page = 1, size = 12, keyword = "" }) => {
  const res = await jwtAxios.get(`${facility}/list`, {
    params: { page, size, keyword }
  });
  return res.data;
};

export const getFacilityDetail = async (facRevNum) => {
  const res = await jwtAxios.get(`${facility}/facilityDetail`, {
    params: { facRevNum }
  });
  return res.data;
};

// 전체 시설 기준 휴무일 (HolidayDayDTO[])
export const getAllHolidays = async () => {
  const res = await axios.get(`${facility}/holidays`);
  return res.data; 
};

// 특정 시설 기준 휴무일(옵션) (HolidayDayDTO[])
export const getFacilityHolidays = async (facRevNum) => {
  const res = await axios.get(`${facility}/holidays`, {
    params: { facRevNum }
  });
  return res.data;
};

export const addPublicHoliday = async ({ date, label }) => {
  const res = await jwtAxios.post(`${facility}/addholiday`, { 
    date, label 
  });
  return res.data;
};

export const deleteHoliday = async (date) => {
  return (await jwtAxios.delete(`${facility}/deleteHoliday`, { 
    params: { date } 
  })).data;
};

export const createReservation = async ({ facRevNum, facDate, startTime, endTime }) => {
  const res = await jwtAxios.post(`${facility}/reserve`, {
    facRevNum,
    facDate,
    startTime,
    endTime,
  });
  return res.data;
};

// 특정 날짜의 예약 불가 시간대 조회 (ReservedBlockDTO[])
export const getReservedBlocks = async (facRevNum, date) => {
  const res = await axios.get(`${facility}/facility/reserved`, {
    params: { facRevNum, date }
  });
  return res.data;
};

// 사용자 장소예약 조회
export const getMyReservations = async ({ page = 0, size = 5, sort = "reserveAt,DESC" } = {}) => {
  const res = await jwtAxios.get(`${facility}/reservations`, {
    params: { page, size, sort },
  });
  return res.data; // Spring Page<EventUseDTO>와 동일한 구조 { content, totalPages, ... }
};

// 사용자 장소예약 취소
export const cancelReservation = async (reserveId) => {
  if (!reserveId) throw new Error("reserveId는 필수입니다.");

  const res = await jwtAxios.delete(`${facility}/cancel`, {
    params: { reserveId },
  });
  return res.data;
};

// 관리자 예약현황 확인
export const getAdminReservations = async ({ state, from, to } = {}) => {
  const stateParam = state === "CANCELLED" ? "CANCEL" : state;
  const res = await jwtAxios.get(`${facility}/adminreservations`, {
    params: {
      state: stateParam || undefined,
      from: from || undefined,
      to: to || undefined,
    },
  });
  return res.data;
};

// 관리자 예약 수락 및 거절
export const updateReservationState = async ({ reserveId, state }) => {
  if (!reserveId) throw new Error("reserveId는 필수입니다.");
  if (!state) throw new Error("state는 필수입니다.");
  const { data } = await jwtAxios.post(`${facility}/approve`, { reserveId, state });
  return data;
};

// 관리자 예약 강제 취소
export const adminCancelReservation = async ({ reserveId, requesterId }) => {
  if (!reserveId) throw new Error("reserveId는 필수입니다.");
  const { data } = await jwtAxios.patch(`${facility}/admincancel`, null, {
    params: { reserveId, requesterId },
  });
  return data;
};