import jwtAxios from "../util/jwtUtil";
import axios from "axios";
import { API_SERVER_HOST } from "./config";
import { API_MAPPING } from "./config";

const host = `${API_SERVER_HOST}/api`
const admin = `${host}${API_MAPPING.admin}`;

// 관리자 회원조회
export const viewMembers = async (params) => {
    const res = await jwtAxios.get(`${admin}/members`, { params });
    return res.data;
};

// 관리자 회원상태(블랙리스트, 탈퇴) 변경
export const memberStateChange = async ({ memId, state }) => {
    const res = await jwtAxios.put(`${admin}/MemberStateChange`, { memId, state });
    return res.data;
};

// 메인페이지 배너 등록
export const uploadBanners = async (params) => {
    const res = await jwtAxios.post(`${admin}/banner/upload`, params, {
        headers: {
            'Content-Type': 'multipart/form-data',
        },
    });
    return res.data;
}

// 배너 조회
export const getAllBanners = async () => {
    const res = await axios.get(`${admin}/banner/list`);
    return res.data;
}

// 배너 삭제
export const deleteBanner = async (params) => {
    const res = await jwtAxios.delete(`${admin}/banner/delete/${params}`);
    return res.data;
}

// 배너 순서 변경
export const updateBannerSequence = async (params) => {
    const res = await jwtAxios.put(`${admin}/banner/sequence`, params);
    return res.data;
}

// 배너 이미지 불러오기
export const getBannerImage = (params) => {
    return `${admin}/banner/view?filePath=${encodeURIComponent(params)}`;
}

// 관리자 물품 대여 관리 페이지에서 회원이 신청한 물품 항목들을 보여주는 요청
export const getResAdminSearch = async (pageCount, search, type, sortBy, sort, statusFilter) => {
    const res = await jwtAxios.get(`${admin}/demRes`, {
        params: {
            search: search,
            type: type,
            statusFilter: statusFilter,
            pageCount: pageCount,
            sortBy: sortBy,
            sort: sort
        }
    });
    return res.data;
};


// 관리자 물품 대여 관리 페이지에서 회원이 신청한 물품 항목들을 보여주는 요청 (검색어 없음)
export const getResAdmin = async (pageCount, sort, sortBy, statusFilter) => {
    const res = await jwtAxios.get(`${admin}/demRes`, {
        params: {
            statusFilter: statusFilter,
            pageCount: pageCount,
            sortBy: sortBy,
            sort: sort
        }
    });
    return res.data;
};

// 물품 대여 현황 페이지에서 예약 날짜를 업데이트 시키는 요청
export const updateResState = async (demRevNum, state) => {
    const res = await jwtAxios.put(`${admin}/ResState`, {
        demRevNum: demRevNum,
        state: state,
    });
    return res.data;
};

// 관리자 물품 대여 페이지에서 반납 요청 / 반납 기한 연장에 대해 수락 / 거절을 업데이트 하는 요청
export const updateReqState = async (demRevNum, state, type) => {
    console.log(type);
    const res = await jwtAxios.put(`${admin}/ReqState`, {
        demRevNum: demRevNum,
        state: state,
        type: type,
    });
    return res.data;
};


// 관리자 물품 대여 관리 페이지에서 회원이 등록한 물품 항목들을 보여주는 요청
export const getRegAdminSearch = async (pageCount, search, type, sortBy, sort, statusFilter) => {
    const res = await jwtAxios.get(`${admin}/demReg`, {
        params: {
            search: search,
            type: type,
            statusFilter: statusFilter,
            pageCount: pageCount,
            sortBy: sortBy,
            sort: sort
        }
    });
    return res.data;
};

// 관리자 물품 대여 관리 페이지에서 회원이 등록한 물품 항목들을 보여주는 요청 (검색어 없음)
export const getRegAdmin = async (pageCount, sort, sortBy, statusFilter) => {
    const res = await jwtAxios.get(`${admin}/demReg`, {
        params: {
            statusFilter: statusFilter,
            pageCount: pageCount,
            sortBy: sortBy,
            sort: sort
        }
    });
    return res.data;
};

// 관리자 물품 대여 페이지에서 물품 대여 신청에 대해 수락 / 거절을 업데이트 하는 요청
export const updateRegstate = async (demRegNum, state) => {
    const res = await jwtAxios.put(`${admin}/RegState`, {
        demRegNum: demRegNum,
        state: state
    });
    return res.data;
};


// FastAPI 통계 API 호출
// 방문자 카운트
export const recordVisitor = async () => {
    const res = await axios.get(`http://127.0.0.1:8000/api/recordVisit`);
    return res.data;
}

// 일일 접속자, 총합 접속자
export const getVisitorStats = async () => {
    const res = await jwtAxios.get(`http://127.0.0.1:8000/api/admin/visitors`);
    return res.data;
};

// 회원 유형별 가입자 수, 비율
export const getMemberRoleStats = async () => {
    const res = await jwtAxios.get(`http://127.0.0.1:8000/api/admin/memberRole`);
    return res.data;
};

// 프로그램 카테고리별 등록 수, 비율
export const getEventCategoryStats = async () => {
    const res = await jwtAxios.get(`http://127.0.0.1:8000/api/admin/eventCategory`);
    return res.data;
};

// 공간 대여 인기 시간대
export const getFacTimesStats = async () => {
    const res = await jwtAxios.get(`http://127.0.0.1:8000/api/admin/popular_facTimes`);
    return res.data;
}

// 실증 카테고리별 등록 수, 비율
export const getDemRegStats = async () => {
    const res = await jwtAxios.get(`http://127.0.0.1:8000/api/admin/demRegCategory`);
    return res.data;
}

// 실증 카테고리별 대여 수, 비율
export const getDemRevStats = async () => {
    const res = await jwtAxios.get(`http://127.0.0.1:8000/api/admin/demRevCategory`);
    return res.data;
}

// 이메일 전송
export const sendEmail = async (formData) => {
    await jwtAxios.post(`${admin}/sendMessage`, formData, {
        headers: { "Content-Type": "multipart/form-data" }
    });
}

// 선택된 회원 ID 배열로 회원 정보 조회
export const getEmailMembers = async (params) => {
  const res = await jwtAxios.get(`${admin}/emailMembers`, 
 {params});
  return res.data;
};
