import { initializeApp } from "firebase/app";
import { getAnalytics } from "firebase/analytics";

const firebaseConfig = {
  apiKey: `${process.env.REACT_APP_EMAIL_API_KEY}`,
  authDomain: `${process.env.REACT_APP_AUTO_DOMAIN}`,
  projectId: `${process.env.REACT_APP_PROJECT_ID}`,
  storageBucket: `${process.env.REACT_APP_STORAGE_BUCKET}`,
  messagingSenderId: `${process.env.REACT_APP_MESSAGE_SENDER}`,
  appId:`${process.env.REACT_APP_ID}`,
  measurementId: `${process.env.REACT_APP_MEASUREMENT_ID}`
};

export const app = initializeApp(firebaseConfig);
export const analytics = getAnalytics(app);

