import { useState } from 'react';
import PhoneVerification from './PhoneVerification';
import { findId } from '../../api/memberApi';
import useMove from '../../hooks/useMove';

const FindIdComponent = () => {
    const [foundId, setFoundId] = useState(null);
    const { moveToLogin } = useMove();

    const handleVerified = (phone, user) => {
        findId({ phone })
            .then((memId) => {
                if (memId) {
                    setFoundId(memId);
                } else {
                    alert("아이디를 찾을 수 없습니다.");
                };
            })
            .catch((error) => {
                alert("아이디 찾기 실패: " + (error.response?.data || error.message));
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
                    <button className='rounded p-1 w-18 bg-blue-500	text-white active:bg-blue-600' onClick={moveToLogin}>로그인 페이지로 이동</button>
                </>
            )}
        </div>
    );
};

export default FindIdComponent;
