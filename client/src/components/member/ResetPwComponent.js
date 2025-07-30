import { useState } from 'react';
import PhoneVerification from './PhoneVerification';
import { resetPw, checkDuplicateId } from '../../api/memberApi';
import useMove from '../../hooks/useMove';

const ResetPwComponent = () => {
    const [step, setStep] = useState('checkId');
    const [memId, setMemId] = useState('');
    const [phone, setPhone] = useState('');
    const [newPassword, setNewPassword] = useState('');
    const [pwCheck, setPwCheck] = useState('');
    const [resetComplete, setResetComplete] = useState(false);
    const {moveToLogin} = useMove();

    const handleCheckId = () => {
        if (!memId) {
            alert('아이디를 입력해주세요.');
            return;
        };

        checkDuplicateId({ memId })
            .then((res) => {
                if (res) {
                    setStep('verifyPhone');
                } else {
                    alert('존재하지 않는 아이디입니다.');
                };
            })
            .catch(() => {
                alert('아이디 확인 중 오류가 발생했습니다.');
            });
    };

    const handleVerified = (verifiedPhone) => {
        setPhone(verifiedPhone);
        setStep('resetPw');
    };

    const handleResetPassword = () => {
        if (newPassword !== pwCheck) {
            alert('비밀번호가 일치하지 않습니다.');
            return;
        };

        resetPw({
            memId,
            phone,
            pw: newPassword,
        })
            .then(() => {
                alert('비밀번호 재설정 성공!');
                setResetComplete(true);
            })
            .catch((error) => {
                if (error.response?.data) {
                    alert(error.response.data);
                } else {
                    alert('비밀번호 재설정 실패: ' + error.message);
                };
            });
    };

    if (resetComplete) {
        alert("비밀번호가 재설정되었습니다.");
        moveToLogin();
    };

    return (
        <div>
            <div>비밀번호 찾기 / 재설정</div>

            {step === 'checkId' && (
                <>
                    <input
                        type="text"
                        placeholder="아이디 입력"
                        value={memId}
                        onChange={(e) => setMemId(e.target.value)}
                    />
                    <button className="border border-black px-1 bg-gray-300 active:bg-gray-400" onClick={handleCheckId}>아이디 확인</button>
                </>
            )}

            {step === 'verifyPhone' && (
                <PhoneVerification onVerified={handleVerified} />
            )}

            {step === 'resetPw' && (
                <>
                    <input
                        type="password"
                        placeholder="새 비밀번호 입력"
                        value={newPassword}
                        onChange={(e) => setNewPassword(e.target.value)}
                    />
                    <input
                        type="password"
                        placeholder="비밀번호 확인"
                        value={pwCheck}
                        onChange={(e) => setPwCheck(e.target.value)}
                    />
                    <button className="border border-black px-1 bg-gray-300 active:bg-gray-400" onClick={handleResetPassword}>비밀번호 재설정</button>
                </>
            )}
        </div>
    );
};

export default ResetPwComponent;
