import axios from "axios";
import jwtAxios from "../util/jwtUtil";
import { API_SERVER_HOST } from "./config";
import { API_MAPPING } from "./config";

const host = `${API_SERVER_HOST}/api`;
const mapping = `${API_MAPPING}`;
const member = `${host}${mapping.member}`;
const student = `${host}${mapping.student}`;
const teacher = `${host}${mapping.teacher}`;
const company = `${host}${mapping.company}`;

// 로그인
export const loginPost = async (loginParam) => {
    const header = {
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        }
    };

    const form = new FormData();

    form.append('username', loginParam.memId);
    form.append('password', loginParam.pw);

    const res = await axios.post(`${host}/login`, form, header);

    return res.data;
};

// 회원가입전 이용약관 및 사용자 역할
export const registerUser = async () => {
    const res = await axios.post(`${host}/register`);
    return res.data;
};

// 회원가입
export const registerMember = async (params) => {
    const res = await axios.post(`${host}/register/member`, params);
    return res.data;
};
export const registerStudent = async (params) => {
    const res = await axios.post(`${host}/register/student`, params);
    return res.data;
};
export const registerTeacher = async (params) => {
    const res = await axios.post(`${host}/register/teacher`, params);
    return res.data;
};
export const registerCompany = async (params) => {
    const res = await axios.post(`${host}/register/company`, params);
    return res.data;
};

// 아이디 중복 체크
export const checkDuplicateId = async (params) => {
    const res = await axios.get(`${host}/checkId`, {params: params});
    return res.data;
};

// 회원정보조회
export const readMember = async (params) => {
    const res = await jwtAxios.get(`${member}/myInfo`, {params: params});
    return res.data;
};
export const readStudent = async (params) => {
    const res = await jwtAxios.get(`${student}/myInfo`, {params: params});
    return res.data;
};
export const readTeacher = async (params) => {
    const res = await jwtAxios.get(`${teacher}/myInfo`, {params: params});
    return res.data;
};
export const readCompany = async (params) => {
    const res = await jwtAxios.get(`${company}/myInfo`, {params: params});
    return res.data;
};

// 회원정보수정
export const modifyMember = async (params) => {
    const res = await jwtAxios.put(`${member}/modify`, params);
    return res.data;
};
export const modifyStudent = async (params) => {
    const res = await jwtAxios.put(`${student}/modify`, params);
    return res.data;
};
export const modifyTeacher = async (params) => {
    const res = await jwtAxios.put(`${teacher}/modify`, params);
    return res.data;
};
export const modifyCompany = async (params) => {
    const res = await jwtAxios.put(`${company}/modify`, params);
    return res.data;
};

// 회원정보삭제
export const leaveMember = async () => {
    const res = await jwtAxios.delete(`${host}/leave`);
    return res.data;
};

// 아이디 찾기
export const findId = async (params) => {
    const res = await axios.get(`${host}/findId`, {params: params} );
    return res.data;
};

// 비밀번호 찾기(변경)
export const resetPw = async (params) => {
    const res = await axios.put(`${host}/resetPw`, params);
    return res.data;
};