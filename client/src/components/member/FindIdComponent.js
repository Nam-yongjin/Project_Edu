import { useState } from 'react';
import PhoneVerification from './PhoneVerification';
import { findId } from '../../api/memberApi';
import useMove from '../../hooks/useMove';

const FindIdComponent = () => {
    const [foundId, setFoundId] = useState(null);
    const { moveToLogin } = useMove();

    const handleVerified = (phone, user) => {
        console.log(phone)
        findId({ phone })
            .then((data) => {
                if (data) {
                    setFoundId(data);
                } else {
                    alert('해당 전화번호로 등록된 아이디가 없습니다.');
                }
            })
            .catch((error) => {
                const errData = error.response?.data;
                if (typeof errData === 'string') {
                    alert(errData);
                } else if (errData?.message) {
                    alert(errData.message);
                } else {
                    alert('아이디 찾기 실패: ' + error.message);
                }
            });
    };

    return (
        <div>
            <h2>아이디 찾기</h2>
            {!foundId ? (
                <PhoneVerification onVerified={handleVerified} />
            ) : (
                <>
                    <p>찾은 아이디: <strong>{foundId}</strong></p>
                    <button onClick={moveToLogin}>로그인 페이지로 이동</button>
                </>
            )}
        </div>
    );
};

export default FindIdComponent;
