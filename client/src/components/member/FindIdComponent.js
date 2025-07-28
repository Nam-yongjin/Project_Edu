import { useState } from 'react';
import PhoneVerification from './PhoneVerification';
import { findId } from '../../api/memberApi';

const FindIdComponent = () => {
    const [foundId, setFoundId] = useState(null);

    const handleVerified = async (phone) => {
        try {
            const data = await findId({ params: { phone } });
            setFoundId(data);
        } catch (error) {
            if (error.response?.data) {
                alert(error.response.data);
            } else {
                alert('아이디 찾기 실패: ' + error.message);
            }
        }
    };

    return (
        <div>
            <h2>아이디 찾기</h2>
            {!foundId ? (
                <PhoneVerification onVerified={handleVerified} />
            ) : (
                <p>찾은 아이디: <strong>{foundId}</strong></p>
            )}
        </div>
    );
};

export default FindIdComponent;
