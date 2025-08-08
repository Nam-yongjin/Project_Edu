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
                alert("아이디 찾기 중 오류 발생",error);
                moveToLogin();
            });
    };

    return (
        <div className='max-w-screen-md mx-auto w-[500px]'>
            <div className='page-shadow min-blank my-10 p-10 space-y-6 text-center'>
                <div className='newText-2xl font-bold'>아이디 찾기</div>
                {!foundId ? (
                    <PhoneVerification onVerified={handleVerified} />
                ) : (
                    <>
                        <div>찾은 아이디: <strong className='newText-lg'>{foundId}</strong></div>
                        <button className="newText-lg positive-button w-full" onClick={moveToLogin}>로그인 페이지로 이동</button>
                    </>
                )}
            </div>
        </div>
    );
};

export default FindIdComponent;
