import jwtAxios from "../util/jwtUtil";
import { API_SERVER_HOST } from "./config";
import { API_MAPPING } from "./config";
import axios from "axios";
const host = `${API_SERVER_HOST}/api/demonstration`
const mapping = `${API_MAPPING}`


// 실증 상품 등록하는 요청
export const postAdd = async (formData) => {
    const res = await jwtAxios.post(`${host}/addDem`,formData);
    return res.data;
}

// 실증 상품 수정 페이지에서 상품 정보를 불러오기 위한 요청
export const getOne=async(demNum)=> {
       const res=await jwtAxios.get(`${host}/SelectOne`,demNum);
    return res.data;
}

// 실증 상품 수정 페이지에서 상품 정보를 수정하기 위한 요청
export const putUpdate=async(formData)=> {
    const res=await jwtAxios.put(`${host}/UpdateDem`,formData, {
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