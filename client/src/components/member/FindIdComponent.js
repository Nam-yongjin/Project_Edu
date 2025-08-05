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
                alert((error.response?.data || error.message));
                moveToLogin();
            });
    };

    return (
        <div>
        <div className='my-10 p-10 w-[500px] space-y-6 text-center shadow-2xl shadow-gray-500'>
            <div className='text-3xl font-bold'>아이디 찾기</div>
            {!foundId ? (
                <PhoneVerification onVerified={handleVerified} />
            ) : (
                <>
                    <div>찾은 아이디: <strong className='text-lg'>{foundId}</strong></div>
                    <button className="rounded-md p-2 w-18 bg-blue-500 hover:bg-blue-600 active:bg-blue-700 font-semibold text-white" onClick={moveToLogin}>로그인 페이지로 이동</button>
                </>
            )}
        </div>
        </div>
    );
};

export default FindIdComponent;
