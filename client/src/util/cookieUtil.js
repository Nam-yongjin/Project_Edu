import { Cookies } from "react-cookie";

const cookies = new Cookies();

export const setCookie = (name, value, days) => {
    const expires = new Date();
    expires.setDate(expires.getDate() + days);  // 보관기한
    cookies.set(name, value, { path: "/", expires });
};

export const getCookie = (name) => cookies.get(name);
export const removeCookie = (name) => cookies.remove(name, { path: "/" });
