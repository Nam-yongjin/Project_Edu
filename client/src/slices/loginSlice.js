import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import { loginPost, readMember } from "../api/memberApi";
import { setCookie, getCookie, removeCookie } from "../util/cookieUtil";
import jwtAxios from "../util/jwtUtil";

import { getMemberWithAccessToken, getAccessToken } from "../api/kakaoApi";
const initState = {
    memId: '',
    email: '',
    role: '',
    accessToken: '',
    refreshToken: ''
};

const loadMemberCookie = () => {
    const memberInfo = getCookie("member");
    if (!memberInfo) return initState;
    try {
        return typeof memberInfo === 'object' ? memberInfo : JSON.parse(memberInfo);
    } catch {
        return initState;
    };
};

// 로그인 후 토큰 저장 및 사용자 정보 가져오기
export const loginPostAsync = createAsyncThunk(
    'login/loginPostAsync',
    async (loginParam, { rejectWithValue }) => {
        try {
            // 로그인 요청 및 accessToken 받기
            const loginRes = await loginPost(loginParam);
            const { accessToken, refreshToken } = loginRes;

            // 토큰만 저장
            setCookie("member", JSON.stringify({ accessToken, refreshToken }), 1);
            // readMember()를 위해 jwtAxios에 Authorization 헤더 설정
            jwtAxios.defaults.headers.common['Authorization'] = `Bearer ${accessToken}`;

            // JWT 헤더로 사용자 정보 요청
            const basicInfo = await readMember();
            const userData = {
                memId: basicInfo.memId,
                email: basicInfo.email,
                role: basicInfo.role,
                accessToken,
                refreshToken
            };

            // 전체 유저 데이터로 다시 저장
            setCookie("member", JSON.stringify(userData), 1);

            // 필요한 정보 리턴
            return userData;
        } catch (error) {
            return rejectWithValue({ error: true, message: "로그인 실패" });
        }
    }
);

// 소셜 로그인 (카카오 등)
export const loginSocialAsync = createAsyncThunk(
    'login/loginSocialAsync',
    async (authCode, { rejectWithValue }) => {
        try {
            const accessToken = await getAccessToken(authCode);
            const memberInfo = await getMemberWithAccessToken(accessToken);

            const userData = {
                memId: memberInfo.memId,
                email: memberInfo.email,
                role: memberInfo.role,
                accessToken: memberInfo.accessToken,
                refreshToken: memberInfo.refreshToken,
            };

            setCookie("member", JSON.stringify(userData), 1);
            jwtAxios.defaults.headers.common['Authorization'] = `Bearer ${userData.accessToken}`;

            return userData;
        } catch (error) {
            return rejectWithValue({ error: true, message: "소셜 로그인 실패" });
        }
    }
);


const loginSlice = createSlice({
    name: 'login',
    initialState: loadMemberCookie(),
    reducers: {
        logout: (state) => {
            alert("로그아웃 되었습니다.");
            removeCookie("member");
            return { ...initState };
        }
    },
    extraReducers: (builder) => {   // 비동기 호출의 상태에 따라 동작
        builder
            .addCase(loginPostAsync.fulfilled, (state, action) => {
                const { memId, email, role, accessToken, refreshToken } = action.payload;
                Object.assign(state, { memId, email, role, accessToken, refreshToken });
            })
            .addCase(loginPostAsync.rejected, (state, action) => {
                console.error("로그인 실패", action.payload?.message);
            });
    }
});

export const { logout } = loginSlice.actions;
export default loginSlice.reducer;
