import { useState } from 'react';
import PhoneVerification from './PhoneVerification';

const FindIdComponent = () => {
    const [foundId, setFoundId] = useState(null);

    const handleVerified = async (phone) => {
        try {
            const res = await fetch(`/member/findId?phone=${encodeURIComponent(phone)}`);
            const data = await res.json();

            if (res.ok) {
                setFoundId(data.username);
            } else {
                alert(data.message || '아이디를 찾을 수 없습니다.');
            }
        } catch (err) {
            alert('서버 요청 실패: ' + err.message);
        }
    };

    return (
        <div>
            <h2>아이디 찾기</h2>
            {!foundId ? (
                <PhoneVerification onVerified={handleVerified} />
            ) : (
                <p>🔍 찾은 아이디: <strong>{foundId}</strong></p>
            )}
        </div>
    );
};

export default FindIdComponent;
