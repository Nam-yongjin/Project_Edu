import { useState } from 'react';
import PhoneVerification from './PhoneVerification';
import { resetPw } from '../../api/memberApi'; // 실제 경로에 맞게 조정

const ResetPwComponent = () => {
    const [phone, setPhone] = useState(null);
    const [memId, setMemId] = useState('');
    const [newPassword, setNewPassword] = useState('');
    const [pwCheck, setPwCheck] = useState('');
    const [resetComplete, setResetComplete] = useState(false);

    const handleVerified = (verifiedPhone) => {
        setPhone(verifiedPhone);
    };

    const handleResetPassword = async () => {
        if (newPassword !== pwCheck) {
            alert('비밀번호가 일치하지 않습니다.');
            return;
        }

        try {
            await resetPw({
                memId,
                phone,
                pw: newPassword,
            });

            alert('비밀번호 재설정 성공!');
            setResetComplete(true);
        } catch (error) {
            if (error.response?.data) {
                alert(error.response.data);
            } else {
                alert('비밀번호 재설정 실패: ' + error.message);
            }
        }
    };

    if (resetComplete) return <p>비밀번호가 재설정되었습니다. 로그인해 주세요.</p>;

    return (
        <div>
            <h2>비밀번호 찾기 / 재설정</h2>
            {!phone ? (
                <PhoneVerification onVerified={handleVerified} />
            ) : (
                <>
                    <input
                        type="text"
                        placeholder="아이디 입력"
                        value={memId}
                        onChange={(e) => setMemId(e.target.value)}
                    />
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
                    <button onClick={handleResetPassword}>비밀번호 재설정</button>
                </>
            )}
        </div>
    );
};

export default ResetPwComponent;
