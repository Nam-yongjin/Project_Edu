import { useState } from 'react';
import PhoneVerification from './PhoneVerification';

const MemberRegisterComponent = () => {
    const [verifiedPhone, setVerifiedPhone] = useState(null);
    const [form, setForm] = useState({
        memId: '',
        pw: '',
        name: '',
        email: '',
        birthDate: '',
        gender: '',
        addr: '',
        addrDetail: '',
        checkSms: false,
        checkEmail: false
    });
    const handleChange = (e) => {
        const { name, value, type, checked } = e.target;
        setForm((prev) => ({
            ...prev,
            [name]: type === 'checkbox' ? checked : value
        }));
    };
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
            <div className='mt-10 m-2 p-4'>
                
                <h2>회원가입</h2>
                <form onSubmit={handleSubmit}>
                    <input name="memId" type="text" placeholder="아이디" value={form.memId} onChange={handleChange} />
                    <input name="pw" type="password" placeholder="비밀번호" value={form.pw} onChange={handleChange} />
                    <input name="name" type="text" placeholder="이름" value={form.name} onChange={handleChange} />
                    <input name="email" type="email" placeholder="이메일" value={form.email} onChange={handleChange} />
                    <input name="birthDate" type="date" value={form.birthDate} onChange={handleChange} />

                    <select name="gender" value={form.gender} onChange={handleChange}>
                        <option value="">성별</option>
                        <option value="MALE">남성</option>
                        <option value="FEMALE">여성</option>
                    </select>

                    <PhoneVerification onVerified={handleVerified} />

                    <input name="addr" type="text" placeholder="주소" value={form.addr} onChange={handleChange} />
                    <input name="addrDetail" type="text" placeholder="상세 주소" value={form.addrDetail} onChange={handleChange} />

                    <label>
                        <input name="checkSms" type="checkbox" checked={form.checkSms} onChange={handleChange} />
                        SMS 수신 동의
                    </label>

                    <label>
                        <input name="checkEmail" type="checkbox" checked={form.checkEmail} onChange={handleChange} />
                        이메일 수신 동의
                    </label>

                    <button type="submit">회원가입</button>
                </form>
            </div>
        </div>
    );
};

export default MemberRegisterComponent;
