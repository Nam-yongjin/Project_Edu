import { initializeApp } from 'firebase/app';
import { getAuth } from 'firebase/auth';

// Firebase 초기화
const firebaseConfig = {
    apiKey: "AIzaSyCoCXcA78AU743LgXqCCubGrN3SycfUxYk",
    authDomain: "seoul-edutech.firebaseapp.com",
    projectId: "seoul-edutech",
    storageBucket: "seoul-edutech.firebasestorage.app",
    messagingSenderId: "1075499387098",
    appId: "1:1075499387098:web:8a20c909977eaac17f3de3",
    measurementId: "G-PYBZWJTW9X"
};

const app = initializeApp(firebaseConfig);
export const auth = getAuth(app);
