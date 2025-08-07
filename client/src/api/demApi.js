import axios from "axios";
import jwtAxios from "../util/jwtUtil";
import { API_SERVER_HOST } from "./config";
import { API_MAPPING } from "./config";
const host = `${API_SERVER_HOST}/api`
const demonstration = `${host}${API_MAPPING.demonstration}`;

// 실증 상품 등록하는 요청 jwtAxios에서는 헤더를 직접 달아줘야 한다.
export const postAdd = async (formData) => {
    const res = await jwtAxios.post(`${demonstration}/addDem`, formData, {
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    });

    return res.data;
};


// 실증 상품 수정 페이지에서 상품 정보를 불러오기 위한 요청
export const getOne = async (demNum) => {
    const res = await jwtAxios.get(`${demonstration}/SelectOne`, {
        params: { demNum: demNum }
    });
    return res.data;
}

// 실증 상품 수정 페이지에서 상품 정보를 수정하기 위한 요청
export const putUpdate = async (formData) => {
    const res = await jwtAxios.put(`${demonstration}/UpdateDem`, formData, {
        headers: { 'Content-Type': 'multipart/form-data' }
    });
    return res.data;
}

// 실증 상품 목록 페이지에서 실증 상품 삭제하기 위한 요청 (임시)
export const delDem = async (demNum) => {
    const res = await jwtAxios.delete(`${demonstration}/DeleteDem`, demNum);
    return res.data;
}

// 실증 상품 리스트 페이지에서 상품 정보 리스트를 얻어오기 위한 요청
export const getList = async (current,searchType,search) => {
    console.log(searchType);
    console.log(search);
    const res = await axios.get(`${demonstration}/demList`, {
        params : {pageCount: current,
                type:searchType,
                search:search,
        }
    });
    return res.data;
}

// 실증 상품 상세 페이지에서 상품 정보 리스트를 얻어오기 위한 요청
export const getDetail = async (demNum) => {
    const res = await axios.get(`${demonstration}/demDetail`, {
        params : {demNum: demNum}
    });
    return res.data;
}

// 현재 달의 시작일 / 마지막날 / 상품 정보를 받아가 현재 달의 예약된 날짜를 가져오기 위한 요청
export const getResDate = async (startDate, endDate, demNum) => {
    const res = await axios.get(`${demonstration}/demResCon`, {
        params: { startDate, endDate, demNum }
    });
    return res.data;
}

// 상세 페이지에서 날짜 선택 후, 예약하기 위한 요청
export const postRes = async (startDate, endDate, demNum,itemNum) => {
 const res = await jwtAxios.post(`${demonstration}/ReservationRes`, {
        startDate: startDate,
        endDate: endDate,
        demNum: demNum,
        itemNum:itemNum
    });
    return res.data;
}

// 상세 페이지에서 예약 신청 클릭 시, 백에서 컨트롤러에서 아이디 확인해, 이미 해당 물품에 예약을 한 상태일경우 예약 못하게함
export const getReserveCheck =async (demNum) => {
 const res = await jwtAxios.get(`${demonstration}/demReserveCheck`, {
        params: {demNum:demNum }
    });
    return res.data;
}


// 물품 대여 현황 페이지에서 여러가지 정보를 가져오기 위한 요청
export const getRental =async (pageCount,sort,sortBy) => {
 const res = await jwtAxios.get(`${demonstration}/demRental`, {
     params: {pageCount:pageCount,
            sort:sort,
            sortBy:sortBy
     }
    });
    return res.data;
}
// get요청은 param, requestParma
// post요청은 json형태로 responsebody
// 물품 대여 현황 페이지에서 여러가지 정보를 가져오기 위한 요청 (검색어 포함해서)
export const getRentalSearch=async (search,type,pageCount,sortBy,sort) => {
 const res = await jwtAxios.get(`${demonstration}/demRental`, {
    params: {
    search:search,
    type:type,   
    pageCount:pageCount,
    sortBy:sortBy,
    sort:sort,
    }
    });
    return res.data;
}