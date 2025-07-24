import { useState } from 'react';
import PhoneVerification from './PhoneVerification';

const MemberRegisterComponent = () => {
    const [verifiedPhone, setVerifiedPhone] = useState(null);
    const [form, setForm] = useState({ username: '', password: '' });

    const handleVerified = (phone) => {
        setVerifiedPhone(phone);
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        if (!verifiedPhone) {
            alert('휴대폰 인증을 먼저 완료하세요.');
            return;
        }

        console.log('회원가입 정보:', {
            ...form,
            phone: verifiedPhone
        });
        alert('회원가입 완료!');
    };

    return (
        <div>
            <h2>회원가입</h2>
            <form onSubmit={handleSubmit}>
                <input
                    type="text"
                    placeholder="아이디"
                    value={form.username}
                    onChange={(e) => setForm({ ...form, username: e.target.value })}
                />
                <input
                    type="password"
                    placeholder="비밀번호"
                    value={form.password}
                    onChange={(e) => setForm({ ...form, password: e.target.value })}
                />
                <PhoneVerification onVerified={handleVerified} />
                <button type="submit">회원가입</button>
            </form>
        </div>
    );
};

export default MemberRegisterComponent;
