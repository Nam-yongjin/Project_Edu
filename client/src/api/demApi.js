import jwtAxios from "../util/jwtUtil";
import { API_SERVER_HOST } from "./config";
import { API_MAPPING } from "./config";
const host = `${API_SERVER_HOST}/api`
const demonstration = `${host}${API_MAPPING.demonstration}`;

// 실증 상품 등록하는 요청 jwtAxios에서는 헤더를 직접 달아줘야 한다.
export const postAdd = async (formData) => {
    const res = await jwtAxios.post(`${demonstration}/addDem`,formData,{
            headers: { 'Content-Type': 'multipart/form-data' }
    });
    return res.data;
}

// 실증 상품 수정 페이지에서 상품 정보를 불러오기 위한 요청
export const getOne = async (demNum) => {
  const res = await jwtAxios.get(`${demonstration}/SelectOne`, {
    params: { demNum: demNum }
  });
  return res.data;
}

// 실증 상품 수정 페이지에서 상품 정보를 수정하기 위한 요청
export const putUpdate=async(formData)=> {
    const res=await jwtAxios.put(`${demonstration}/UpdateDem`,formData, {
         headers: { 'Content-Type': 'multipart/form-data' }
    });
    return res.data;
}

// 실증 상품 목록 페이지에서 실증 상품 삭제하기 위한 요청
export const delDem=async(demNum)=> {
    const res=await jwtAxios.delete(`${host}/DeleteDem`,demNum)
}

// 실증 상품 리스트 페이지에서 상품 정보 리스트를 얻어오기 위한 요청
export const getList=async()=> {
     const res=await jwtAxios.get(`${host}/demList`)
}