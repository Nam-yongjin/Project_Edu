import { initializeApp } from 'firebase/app';
import { getAuth } from 'firebase/auth';

// Firebase 초기화
const firebaseConfig = {
    apiKey: "AIzaSyBICYanZm5Mrt72oJBcr-IQcGEhM7y4LB8",
    authDomain: "seoul-edutech-a256b.firebaseapp.com",
    projectId: "seoul-edutech-a256b",
    messagingSenderId: "314519421298",
    appId: "1:314519421298:web:9f6b67b7d6577e48a55deb",
};

const app = initializeApp(firebaseConfig);
const auth = getAuth(app);

export { auth };