import { useState, useEffect } from 'react';
import { auth } from '../../api/firebaseApi';
import { signInWithPhoneNumber, RecaptchaVerifier } from 'firebase/auth';

const PhoneVerification = ({ onVerified }) => {
    const [phone, setPhone] = useState('');
    const [otp, setOtp] = useState('');
    const [step, setStep] = useState(0);
    const [cooldown, setCooldown] = useState(0);
    const [disable, setDisable] = useState(false);

    // 타이머: cooldown 감소
    useEffect(() => {
        if (cooldown <= 0) return;
        const timer = setInterval(() => setCooldown(c => c - 1), 1000);
        return () => clearInterval(timer);
    }, [cooldown]);

    // 전화번호 형식 변환 (국제번호 +82)
    const formatToE164 = raw => raw.startsWith('0') ? '+82' + raw.slice(1) : raw;

    // 한국 휴대폰 번호 유효성 검사
    const isValidPhone = number => /^01[016789][0-9]{7,8}$/.test(number);

    // reCAPTCHA 한 번만 초기화
    useEffect(() => {
        if (!window.recaptchaVerifier) {
            window.recaptchaVerifier = new RecaptchaVerifier(
                auth,
                'recaptcha-container',
                { size: 'invisible' },
            );
            window.recaptchaVerifier.render().catch(console.error);
        };

        // 컴포넌트 언마운트 시 reCAPTCHA 제거 로직 추가 (선택 사항이지만 문제 방지에 도움)
        return () => {
            if (window.recaptchaVerifier) {
                const container = document.getElementById('recaptcha-container');
                if (container) {
                    container.innerHTML = ''; // 내용을 비워줌
                };
                window.recaptchaVerifier = null; // 인스턴스 참조 제거
            };
        };
    }, []);

    const sendOTP = async () => {
        if (cooldown > 0) {
            alert(`${cooldown}초 후에 다시 시도해주세요.`);
            return;
        };
        if (!isValidPhone(phone)) {
            alert('유효한 휴대폰번호를 입력해주세요.');
            return;
        };

        const e164Phone = formatToE164(phone);
        const appVerifier = window.recaptchaVerifier;

        try {
            const confirmationResult = await signInWithPhoneNumber(auth, e164Phone, appVerifier);
            window.confirmationResult = confirmationResult;
            setStep(1);
            setCooldown(120);
            alert('인증번호가 전송되었습니다.');
        } catch (error) {
            if (error.code === 'auth/cancelled') {
                alert('reCAPTCHA 인증이 취소되었습니다. 다시 시도해주세요.');
            } else if (error.code === 'auth/too-many-requests') {
                alert('요청이 너무 많습니다. 나중에 다시 시도해주세요.');
            } else {
                alert('인증번호 전송 실패: ' + error.message);
            };
        };
    };

    const verifyOTP = async () => {
        try {
            const result = await window.confirmationResult.confirm(otp);
            alert('인증이 완료되었습니다!');
            setDisable(true);
            onVerified(phone, result.user);
        } catch (error) {
            if (error.code === 'auth/invalid-verification-code') {
                alert('인증번호가 올바르지 않습니다.');
            } else if (error.code === 'auth/code-expired') {
                alert('인증번호가 만료되었습니다. 다시 전송해주세요.');
            } else {
                alert('인증 실패: ' + error.message);
            };
        };
    };

    return (
        <div>
            <div id="recaptcha-container"></div>
            <input
                type="text"
                placeholder="휴대폰번호 입력 ('-' 없이)"
                value={phone}
                onChange={e => setPhone(e.target.value)}
                disabled={disable}
            />
            {!isValidPhone(phone) && phone.length > 0 && (
                <p style={{ color: 'red' }}>유효한 전화번호를 입력해주세요.</p>
            )}

            <button
                onClick={sendOTP}
                className="border border-black px-1 bg-gray-300 active:bg-gray-400" 
                disabled={cooldown > 0 || disable || !isValidPhone(phone)}
            >
                {cooldown > 0 ? `${cooldown}초 후 재요청 가능` : '인증번호 전송'}
            </button>
            

            {step === 1 && (
                <>
                    <input
                        type="text"
                        placeholder="인증번호 입력"
                        value={otp}
                        onChange={e => setOtp(e.target.value)}
                        disabled={disable}
                    />
                    <button className="border border-black px-1 bg-gray-300 active:bg-gray-400" onClick={verifyOTP} disabled={disable}>
                        인증번호 확인
                    </button>
                </>
            )}

            
        </div>
    );
};

export default PhoneVerification;
