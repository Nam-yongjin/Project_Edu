import { initializeApp } from 'firebase/app';
import { getAuth } from 'firebase/auth';

// Firebase 초기화
const firebaseConfig = {
    apiKey: `${process.env.REACT_APP_FIREBASE_API_KEY}`,
    authDomain: "seoul-edutech-a256b.firebaseapp.com",
    projectId: "seoul-edutech-a256b",
    messagingSenderId: `${process.env.REACT_APP_FIREBASE_MESSAGESENDER_ID}`,
    appId: `${process.env.REACT_APP_FIREBASE_API_ID}`,
};

const app = initializeApp(firebaseConfig);
const auth = getAuth(app);

export { auth };