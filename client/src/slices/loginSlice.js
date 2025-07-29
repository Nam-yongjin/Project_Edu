import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import { loginPost, readMember, readStudent, readTeacher, readCompany } from "../api/memberApi";
import { setCookie, getCookie, removeCookie } from "../util/cookieUtil";
import jwtAxios from "../util/jwtUtil";

const initState = {
    memId: '',
    email: '',
    role: ''
};
const loadMemberCookie = () => {
    const memberInfo = getCookie("member");

    // 이미 객체일 경우 대비 처리
    if (!memberInfo) return initState;

    if (typeof memberInfo === 'object') {
        return memberInfo;
    }

    try {
        return JSON.parse(memberInfo);
    } catch (e) {
        console.error("쿠키 파싱 실패", e);
        return initState;
    }
};

// 역할별 myInfo 호출
const fetchUserInfoByRole = async (role) => {
    switch (role) {
        case "STUDENT":
            return await readStudent();
        case "TEACHER":
            return await readTeacher();
        case "COMPANY":
            return await readCompany();
        default:
            return await readMember();
    }
};

// 로그인 후 토큰 저장 및 사용자 정보(메인 + 상세) 가져오기
export const loginPostAsync = createAsyncThunk(
    'loginPostAsync',
    async (loginParam) => {
        // 1) 로그인 요청 및 accessToken 받기
        const loginRes = await loginPost(loginParam);
        const token = loginRes.accessToken;

        // 2) 토큰 쿠키에 저장 및 axios 헤더 설정
        setCookie("accessToken", token, 1);
        jwtAxios.defaults.headers.common['Authorization'] = `Bearer ${token}`;

        // 3) 기본 회원정보 조회 (role 포함)
        const basicInfo = await readMember();
        const role = basicInfo.role;

        // 4) role별 상세 정보 조회 함수 호출
        const detailedInfo = await fetchUserInfoByRole(role);

        // 5) 필요한 정보 리턴
        return {
            memId: detailedInfo.memId,
            email: detailedInfo.email,
            role: role,
        };
    }
);

const loginSlice = createSlice({
    name: 'loginSlice',
    initialState: loadMemberCookie() || initState, // 쿠키가 없다면 초기값 사용 초기 상태(state)를 설정
    reducers: {
        login: (state, action) => { //  dispatch(login(memberInfo)) 여기서 action 호출
            // action.payload로 데이터 받은 후 return 시 ,state에 값 저장
            alert("로그인 되었습니다.")
            const payload = action.payload;

            if (!payload.error) {
                setCookie("member", JSON.stringify(payload), 1);    // 쿠키 저장
                state.memId = payload.memId || '';
                state.email = payload.email || '';
                state.role = payload.role || '';
            }
        },
        logout: (state, action) => {
            alert("로그아웃 되었습니다.")
            removeCookie("member")
            return { ...initState };
        }
    },
    extraReducers: (builder) => { // 비동기 호출의 상태에 따라 동작하는 extraReducers
        builder
            .addCase(loginPostAsync.pending, (state, action) => {
                console.log("pending"); // 비동기 요청 시작
            })
            .addCase(loginPostAsync.fulfilled, (state, action) => {
                const payload = action.payload;

                setCookie('member', JSON.stringify(payload), 1);

                state.memId = payload.memId;
                state.email = payload.email;
                state.role = payload.role;
            })
            .addCase(loginPostAsync.rejected, () => {
                console.log("로그인 실패");
            });
    }
});

export const { login, logout } = loginSlice.actions;
export default loginSlice.reducer;
