import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import { loginPost } from "../api/memberApi";
import { setCookie, getCookie, removeCookie } from "../util/cookieUtil";

const initState = {
    email: ''
};
const loadMemberCookie = () => {
    const memberInfo = getCookie("member")

    return memberInfo
}
// 서버에 로그인 요청을 보내는 비동기 thunk 함수
export const loginPostAsync = createAsyncThunk('loginPostAsync', (param) => {
    return loginPost(param); // createAsyncThunk()를 사용해서 비동기 통신을 호출하는 함수
});

const loginSlice = createSlice({
    name: 'loginSlice',
    initialState: loadMemberCookie() || initState, // 쿠키가 없다면 초기값 사용 초기 상태(state)를 설정하는 부분이에요.
    reducers: {
        login: (state, action) => { //  dispatch(login(memberInfo)) 여기서 action 호출
            // action.payload로 데이터 받은 후 return 시 ,state에 값 저장됨. 
            // 여기선 추가로 쿠키에도 저장
            console.log("login.....");
            const payload = action.payload;

            setCookie("member", JSON.stringify(payload), 1) // 쿠키 만료시간
            return payload;
        },
        logout: (state, action) => {
            console.log("logout....");
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
                console.log("fulfilled"); // 요청 성공

                const payload = action.payload;

                // 정상적인 로그인시에만 쿠키 저장
                if (!payload.error) {
                    setCookie('member', JSON.stringify(payload), 1); // 1일
                }

                return payload;
            })
            .addCase(loginPostAsync.rejected, (state, action) => {
                console.log("rejected"); // 요청 실패 
            });
    }
});

export const { login, logout } = loginSlice.actions;
export default loginSlice.reducer;
