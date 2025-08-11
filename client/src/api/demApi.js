import axios from "axios";
import jwtAxios from "../util/jwtUtil";
import { API_SERVER_HOST } from "./config";
import { API_MAPPING } from "./config";
const host = `${API_SERVER_HOST}/api`
const demonstration = `${host}${API_MAPPING.demonstration}`;
const admin = `${host}${API_MAPPING.admin}`;
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

export const getResExceptDate = async (demNum) => {
    const res = await jwtAxios.get(`${demonstration}/demResConExcept`, {
        params: {demNum }
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
export const getRental =async (pageCount,sort,sortBy,statusFilter) => {
 const res = await jwtAxios.get(`${demonstration}/demRental`, {
     params: {pageCount:pageCount,
            sort:sort,
            sortBy:sortBy,
            statusFilter:statusFilter,
     }
    });
    return res.data;
}
// get요청은 param, requestParma
// post요청은 json형태로 responsebody
// 물품 대여 현황 페이지에서 여러가지 정보를 가져오기 위한 요청 (검색어 포함해서)
export const getRentalSearch=async (search,type,pageCount,sortBy,sort,statusFilter) => {
 const res = await jwtAxios.get(`${demonstration}/demRental`, {
    params: {
    search:search,
    type:type,   
    pageCount:pageCount,
    sortBy:sortBy,
    sort:sort,
    statusFilter:statusFilter,
    }
    });
    return res.data;
}

// 물품 대여 현황 페이지에서 항목 선택 후, 예약 취소 버튼 클릭 시, 상품 삭제
export const deleteRental = async (demNum) => {
    const res = await jwtAxios.delete(`${demonstration}/CancelRes`, {
        params: { demNum }
    });
    return res.data;
};


// 물품 대여 현황 페이지에서 예약 날짜를 업데이트 시키는 요청
export const updateRental = async (startDate,endDate,demNum,itemNum) => {
  const res = await jwtAxios.put(`${demonstration}/ChangeRes`, {
      startDate: startDate,
        endDate: endDate,
        demNum: demNum,
        itemNum:itemNum
  });
  return res.data;
};

// 물품 대여 현황 페이지에서 반납 및 대여 연장을 시키는 요청
export const addRequest = async (demNum,type) => {
  const res = await jwtAxios.post(`${demonstration}/AddRequest`, {
        demNum: demNum,
        type:type
  });
  return res.data;
};

// 실증 물품 현황 페이지에서 등록 물품 정보를 받아오는 요청
export const getBorrowSearch= async (search,type,pageCount,sortBy,sort,statusFilter) => {
  const res = await jwtAxios.get(`${demonstration}/getBorrow`,{
    params: {
    search:search,
    type:type,   
    pageCount:pageCount,
    sortBy:sortBy,
    sort:sort,
    statusFilter:statusFilter,
    }
    });
  return res.data;
};

// 실증 물품 현황 페이지에서 등록 물품 정보를 받아오는 요청
export const getBorrow= async (pageCount, sort, sortBy, statusFilter) => {
  const res = await jwtAxios.get(`${demonstration}/getBorrow`,{
    params: {
    pageCount:pageCount,
    sortBy:sortBy,
    sort:sort,
    statusFilter:statusFilter,
    }
    });
  return res.data;
};

// 실증 상품 목록 페이지에서 실증 상품 삭제하기 위한 요청 
export const delDem = async (demNum) => {
    const res = await jwtAxios.delete(`${demonstration}/DeleteDem`, {
        params: { demNum } // 쿼리 파라미터로 전송
    });
    return res.data;
};

// 실증 등록 물품 페이지에서 해당 물품을 신청한 회원을 보게 해주는 요청(검색어 있음)
export const getBorrowResInfoSearch  = async (demNum, pageCount, search, type, sortBy, sort,statusFilter) => {
    const res = await jwtAxios.get(`${demonstration}/borrowRes`,{
    params: {
    search:search,
    type:type,
    statusFilter:statusFilter,
    pageCount:pageCount,
    sortBy:sortBy,
    sort:sort,
    demNum:demNum,
    }
    });
    return res.data;
};

// 실증 등록 물품 페이지에서 해당 물품을 신청한 회원을 보게 해주는 요청(검색어 없음)
export const getBorrowResInfo  = async (demNum, pageCount, sort, sortBy, statusFilter) => {
    const res = await jwtAxios.get(`${demonstration}/borrowRes`,{
    params: {
    statusFilter:statusFilter,
    pageCount:pageCount,
    sortBy:sortBy,
    sort:sort,
    demNum:demNum,
    }
    });
    return res.data;
};

// 관리자 물품 대여 관리 페이지에서 회원이 신청한 물품 항목들을 보여주는 요청
export const getResAdminSearch  = async (pageCount, search, type, sortBy, sort,statusFilter) => {
    const res = await jwtAxios.get(`${admin}/demRes`,{
    params: {
    search:search,
    type:type,
    statusFilter:statusFilter,
    pageCount:pageCount,
    sortBy:sortBy,
    sort:sort
    }
    });
    return res.data;
};

// 실증 등록 물품 페이지에서 해당 물품을 신청한 회원을 보게 해주는 요청(검색어 없음)
export const getResAdmin  = async (pageCount, sort, sortBy, statusFilter) => {
    const res = await jwtAxios.get(`${admin}/demRes`,{
    params: {
    statusFilter:statusFilter,
    pageCount:pageCount,
    sortBy:sortBy,
    sort:sort
    }
    });
    return res.data;
};


