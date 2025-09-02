import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import { loginPost, readMember } from "../api/memberApi";
import { setCookie, getCookie, removeCookie } from "../util/cookieUtil";
import jwtAxios from "../util/jwtUtil";
import { getMemberWithAccessToken, getAccessToken } from "../api/kakaoApi";
import { getMemberWithNaverCode } from "../api/naverApi";

// 쿠키에 들어갈 사용자정보 기본값
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

// 일반 로그인
// 로그인 후 토큰 저장 및 사용자 정보 가져오기
export const loginPostAsync = createAsyncThunk(
    'login/loginPostAsync',
    async (loginParam, { rejectWithValue }) => {
        try {
            // 로그인 요청 및 Token 받기
            const loginRes = await loginPost(loginParam);
            const { accessToken, refreshToken } = loginRes;

            // 'member' 라는 이름의 쿠키에 토큰만 저장
            setCookie("member", JSON.stringify({ accessToken, refreshToken }), 1);
            // readMember()를 위해 jwtAxios에 Authorization 헤더 설정
            jwtAxios.defaults.headers.common['Authorization'] = `Bearer ${accessToken}`;

            // 사용자 정보 요청
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

            // 'member' 라는 이름의 쿠키에 전체 유저 데이터도 함께 저장
            setCookie("member", JSON.stringify(userData), 1);

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
    initialState: loadMemberCookie(), // 앱 시작 시 쿠키에 저장된 로그인 정보를 Redux 초기 상태로 불러옴.
    reducers: {
        logout: (State) => {
            alert("로그아웃 되었습니다.");
            removeCookie("member");
            return { ...initState };
        }
    },
    extraReducers: (builder) => {   // 비동기 호출의 상태에 따라 동작
        builder
        // pending : API 요청이 시작될 때
        // fulfilled : 성공적으로 API 응답을 받아 완료되었을 때
        // rejected : 실패했을 때 (API 오류, 블랙리스트, 탈퇴 등)
        // 일반 로그인
        .addCase(loginPostAsync.pending, (state) => {
            state.loading = true;
            state.error = null;
        })
        .addCase(loginPostAsync.fulfilled, (state, action) => {
            const { memId, email, role, state: userState, accessToken, refreshToken } = action.payload;
            Object.assign(state, { memId, email, role, state: userState, accessToken, refreshToken });
            state.loading = false;
            state.error = null;
        })
        .addCase(loginPostAsync.rejected, (state, action) => {
            state.loading = false;
            state.error = action.payload?.message || "로그인 실패";
        })

        // 소셜 로그인
        .addCase(loginSocialAsync.pending, (state) => {
            state.loading = true;
            state.error = null;
        })
        .addCase(loginSocialAsync.fulfilled, (state, action) => {
            const { memId, email, role, state: userState, accessToken, refreshToken } = action.payload;
            Object.assign(state, { memId, email, role, state: userState, accessToken, refreshToken });
            state.loading = false;
            state.error = null;
        })
        .addCase(loginSocialAsync.rejected, (state, action) => {
            state.loading = false;
            state.error = action.payload?.message || "소셜 로그인 실패";
        });
    }
});

export const { logout } = loginSlice.actions;
export default loginSlice.reducer;
