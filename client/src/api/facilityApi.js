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