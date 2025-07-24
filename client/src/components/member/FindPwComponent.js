import { useState } from 'react';
import PhoneVerification from './PhoneVerification';

const FindPwComponent = () => {
    const [phone, setPhone] = useState(null);
    const [newPassword, setNewPassword] = useState('');
    const [resetComplete, setResetComplete] = useState(false);

    const handleVerified = (verifiedPhone) => {
        setPhone(verifiedPhone);
    };

    const handleResetPassword = async () => {
        try {
            const res = await fetch('/member/resetPw', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    phone,
                    newPassword,
                }),
            });

            const data = await res.json();

            if (res.ok) {
                alert('비밀번호 재설정 성공!');
                setResetComplete(true);
            } else {
                alert(data.message || '재설정 실패');
            }
        } catch (err) {
            alert('요청 실패: ' + err.message);
        }
    };

    if (resetComplete) return <p>✅ 비밀번호가 재설정되었습니다. 로그인해 주세요.</p>;

    return (
        <div>
            <h2>비밀번호 찾기</h2>
            {!phone ? (
                <PhoneVerification onVerified={handleVerified} />
            ) : (
                <>
                    <input
                        type="password"
                        placeholder="새 비밀번호 입력"
                        value={newPassword}
                        onChange={(e) => setNewPassword(e.target.value)}
                    />
                    <button onClick={handleResetPassword}>비밀번호 재설정</button>
                </>
            )}
        </div>
    );
};

export default FindPwComponent;
