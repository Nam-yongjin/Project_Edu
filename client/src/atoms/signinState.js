import { atom } from "recoil";

const initState = {
    memId: ''
}

const signinState = atom({
    key: 'LoginState',
    default: initState
})

export default signinState