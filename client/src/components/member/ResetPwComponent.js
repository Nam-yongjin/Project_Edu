import { useState } from 'react';
import PhoneVerification from './PhoneVerification';
import { resetPw, checkIdPhone } from '../../api/memberApi';
import useMove from '../../hooks/useMove';

const ResetPwComponent = () => {
    const [step, setStep] = useState('checkId');
    const [memId, setMemId] = useState('');
    const [phone, setPhone] = useState('');
    const [newPassword, setNewPassword] = useState('');
    const [pwCheck, setPwCheck] = useState('');
    const { moveToLogin } = useMove();

    const handleCheckIdPhone = () => {
        if (!memId || !phone) { // 휴대폰 번호 입력 여부도 확인
            alert('아이디와 휴대폰번호를 모두 입력해주세요.');
            return;
        };

        // phone, memId를 직접 전달
        checkIdPhone({ memId, phone })
            .then((res) => {
                if (res) {
                    alert('가입된 아이디와 휴대폰번호입니다. 인증을 시작합니다.');
                    setStep('verifyPhone');
                } else {
                    alert('존재하지 않는 아이디 또는 휴대폰번호입니다.');
                };
            })
            .catch((error) => {
                alert('아이디 확인 중 오류가 발생했습니다.',error);
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
                alert('비밀번호가 재설정되었습니다.');
                moveToLogin();
            })
            .catch((error) => {
                if (error.response?.data) {
                    alert(error.response.data);
                } else {
                    alert('비밀번호 재설정 실패: ' + error.message);
                };
            });
    };

    return (

        <div className='page-shadow my-10 p-10 w-[500px] space-y-6 text-center'>
            <div className='text-2xl font-bold'>비밀번호 찾기 / 재설정</div>

            {step === 'checkId' && (
                <div className="">
                    <div>
                        <input
                            type="text"
                            placeholder="아이디 입력"
                            value={memId}
                            onChange={(e) => setMemId(e.target.value)}
                            className="w-full my-3 input-focus"
                        />
                    </div>
                    <div>
                        <input
                            type="text"
                            placeholder="휴대폰번호 입력 ('-' 없이)"
                            value={phone}
                            onChange={(e) => setPhone(e.target.value)}
                            className="w-full input-focus"
                        />
                    </div>
                    <button className="normal-button p-2 my-3 w-full"
                        onClick={handleCheckIdPhone}>아이디 확인</button>
                </div>

            )}

            {step === 'verifyPhone' && (
                <PhoneVerification
                    onVerified={handleVerified}
                    initialPhone={phone} // `phone` 상태를 `initialPhone` prop으로 전달
                />
            )}

            {step === 'resetPw' && (
                <>
                    <div>
                        <input
                            type="password"
                            placeholder="새 비밀번호 입력"
                            value={newPassword}
                            onChange={(e) => setNewPassword(e.target.value)}
                            className="flex-1 w-full input-focus"
                        />
                    </div>
                    <div>
                        <input
                            type="password"
                            placeholder="새 비밀번호 확인"
                            value={pwCheck}
                            onChange={(e) => setPwCheck(e.target.value)}
                            className="flex-1 w-full input-focus"
                        />
                    </div><div>
                        <button className="positive-button w-full"
                            onClick={handleResetPassword}>비밀번호 재설정</button>
                    </div>
                </>
            )}
        </div>
    );
};

export default ResetPwComponent;
