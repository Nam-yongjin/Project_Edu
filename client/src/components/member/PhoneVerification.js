import { useState } from 'react';
import { auth } from '../../api/firebaseApi';
import { RecaptchaVerifier, signInWithPhoneNumber } from 'firebase/auth';
import { useEffect } from 'react';

const PhoneVerification = ({ onVerified }) => {
    const [phone, setPhone] = useState('');
    const [otp, setOtp] = useState('');
    const [step, setStep] = useState(0);
    const [cooldown, setCooldown] = useState(0); // 시간 제한 (초 단위)
    const [disable, setDisable] = useState(false)

    // 타이머 관리
    useEffect(() => {
        let timer;
        if (cooldown > 0) {
            timer = setInterval(() => {
                setCooldown(prev => prev - 1);
            }, 1000);
        }
        return () => clearInterval(timer);
    }, [cooldown]);

    // 입력한 전화번호 → +82 형식으로 변환
    const formatToE164 = (rawPhone) => {
        // 예: 01012345678 → +821012345678
        if (rawPhone.startsWith('0')) {
            return '+82' + rawPhone.slice(1);
        }
        return rawPhone; // 이미 국제번호라면 그대로 사용
    };

    // 전화번호 유효성검사
    const isValidPhone = (number) => /^01[016789][0-9]{7,8}$/.test(number);

    // reCAPTCHA 초기화
    useEffect(() => {
        if (!auth) return;
        if (!window.recaptchaVerifier) {
            window.recaptchaVerifier = new RecaptchaVerifier(
                'recaptcha-container',
                { size: 'invisible' },
                auth
            );
        }
    }, []);


    const sendOTP = async () => {

        if (cooldown > 0) {
            alert(`${cooldown}초 후에 다시 시도해주세요.`);
            return;
        }

        const appVerifier = window.recaptchaVerifier;
        const e164Phone = formatToE164(phone);

        try {
            const result = await signInWithPhoneNumber(auth, e164Phone, appVerifier);
            window.confirmationResult = result;
            setStep(1);
            setCooldown(180); // 3분 제한 시작
            alert('인증번호가 전송되었습니다.');
        } catch (error) {
            if (error.code === 'auth/too-many-requests') {
                alert('요청이 너무 많습니다. 잠시 후 다시 시도해주세요.');
            } else {
                alert('인증번호 전송 실패: ' + error.message);
            }
        }
    };

    const verifyOTP = async () => {
        try {
            const result = await window.confirmationResult.confirm(otp);
            alert('인증이 완료되었습니다!');
            setDisable(true)
            onVerified(phone, result.user);
        } catch (error) {
            if (error.code === 'auth/invalid-verification-code') {
                alert('인증번호가 올바르지 않습니다.');
            } else {
                alert('인증 실패: ' + error.message);
            }
        }
    };

    return (
        <div>
    <input
      type="text"
      value={phone}
      placeholder="전화번호 입력 ('-' 없이)"
      onChange={(e) => setPhone(e.target.value)}
    />
    {!isValidPhone(phone) && <p style={{ color: 'red' }}>유효한 전화번호를 입력해주세요.</p>}

    <button type="button" onClick={sendOTP} disabled={cooldown > 0}>
      {cooldown > 0 ? `${cooldown}초 후 재요청 가능` : '인증번호 전송'}
    </button>

    {step === 1 && (
      <>
        <input
          type="text"
          value={otp}
          placeholder="인증번호 입력"
          onChange={(e) => setOtp(e.target.value)}
          disabled={disable}
        />
        <button type="button" onClick={verifyOTP} disabled={disable}>인증번호 확인</button>
      </>
    )}

    <div id="recaptcha-container" />
  </div>
);
};

export default PhoneVerification;