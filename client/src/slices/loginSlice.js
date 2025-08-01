import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import { loginPost, readMember } from "../api/memberApi";
import { setCookie, getCookie, removeCookie } from "../util/cookieUtil";
import jwtAxios from "../util/jwtUtil";
import { getMemberWithAccessToken, getAccessToken } from "../api/kakaoApi";
import { getMemberWithNaverCode } from "../api/naverApi";

const initState = {
    memId: '',
    email: '',
    role: '',
    state: '',
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

            // 블랙리스트 또는 탈퇴 회원 처리
            if (basicInfo.state === 'BEN') {
                removeCookie("member");
                return rejectWithValue({ error: true, message: "블랙리스트 회원은 로그인이 불가능합니다." });
            }
            if (basicInfo.state === 'LEAVE') {
                removeCookie("member");
                return rejectWithValue({ error: true, message: "탈퇴처리된 회원은 로그인이 불가능합니다." });
            }

            const userData = {
                memId: basicInfo.memId,
                email: basicInfo.email,
                role: basicInfo.role,
                state: basicInfo.state,
                accessToken,
                refreshToken
            };

            // 전체 유저 데이터로 다시 저장
            setCookie("member", JSON.stringify(userData), 1);

            // 필요한 정보 리턴
            return userData;
        } catch (error) {
            return rejectWithValue({ error: true, message: "로그인에 실패했습니다." });
        }
    }
);

// 소셜 로그인
export const loginSocialAsync = createAsyncThunk(
    'login/loginSocialAsync',
    async ({ provider, code, state }, { rejectWithValue }) => {
        try {
            let memberInfo;

            if (provider === "kakao") {
                const accessToken = await getAccessToken(code);
                memberInfo = await getMemberWithAccessToken(accessToken);
            } else if (provider === "naver") {
                memberInfo = await getMemberWithNaverCode(code, state);
            };

            // 블랙리스트 또는 탈퇴 회원 처리
            if (memberInfo.state === 'BEN') {
                removeCookie("member");
                return rejectWithValue({ error: true, message: "블랙리스트 회원은 로그인이 불가능합니다." });
            }
            if (memberInfo.state === 'LEAVE') {
                removeCookie("member");
                return rejectWithValue({ error: true, message: "탈퇴처리된 회원은 로그인이 불가능합니다." });
            };

            const userData = {
                memId: memberInfo.memId,
                email: memberInfo.email,
                role: memberInfo.role,
                state: memberInfo.state,
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
        logout: (State) => {
            alert("로그아웃 되었습니다.");
            removeCookie("member");
            return { ...initState };
        }
    },
    extraReducers: (builder) => {   // 비동기 호출의 상태에 따라 동작
        builder
            .addCase(loginPostAsync.fulfilled, (State, action) => {
                const { memId, email, role, state, accessToken, refreshToken } = action.payload;
                Object.assign(State, { memId, email, role, state, accessToken, refreshToken });
            })
            .addCase(loginPostAsync.rejected, (State, action) => {
            });
    }
});

export const { logout } = loginSlice.actions;
export default loginSlice.reducer;
