//import axios from "axios";
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