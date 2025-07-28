import jwtAxios from "../util/jwtUtil";
import { API_SERVER_HOST } from "./config";
import { API_MAPPING } from "./config";

const host = `${API_SERVER_HOST}/api/demonstration`
const mapping = `${API_MAPPING}`

export const postAdd = async (formData) => {

    const res = await jwtAxios.post(`${host}/addDem`,formData, {
        headers: { 'Content-Type': 'multipart/form-data' }
    });
    return res.data;
}

export const selectUpdate=async(formData)=> {
       const res=await jwtAxios.put(`${host}/addDem`,formData, {
         headers: { 'Content-Type': 'multipart/form-data' }
    });
    return res.data;
}

export const putUpdate=async(formData)=> {
    const res=await jwtAxios.put(`${host}/addDem`,formData, {
         headers: { 'Content-Type': 'multipart/form-data' }
    });
    return res.data;
}