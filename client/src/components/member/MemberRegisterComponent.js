import { useState } from 'react';
import PhoneVerification from './PhoneVerification';
import BasicLayout from '../../layouts/BasicLayout';

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
        checkEmail: false,
        role: 'MEMBER'
    });

    const [errors, setErrors] = useState({});

    const handleChange = (e) => {
        const { name, value, type, checked } = e.target;
        setForm((prev) => ({
            ...prev,
            [name]: type === 'checkbox' ? checked : value
        }));
    };

        const validate = () => {
        const errs = {};

        if (!/^[A-Za-z0-9]{6,16}$/.test(form.memId)) {
            errs.memId = '아이디는 6~16자 영문/숫자만 가능합니다.';
        }

        if (!/^[A-Za-z0-9!@#$.]{6,16}$/.test(form.pw)) {
            errs.pw = '비밀번호는 6~16자, 특수문자(!@#$.) 사용 가능.';
        }

        if (!/^[가-힣]{1,6}$/.test(form.name)) {
            errs.name = '이름은 한글 1~6자여야 합니다.';
        }

        if (!form.email || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email)) {
            errs.email = '유효한 이메일을 입력해주세요.';
        }

        if (!form.birthDate) {
            errs.birthDate = '생년월일을 선택해주세요.';
        }

        if (!form.gender) {
            errs.gender = '성별을 선택해주세요.';
        }

        if (!verifiedPhone) {
            errs.phone = '휴대폰 인증이 필요합니다.';
        }

        return errs;
    };

    const handleVerified = (phone) => {
        setVerifiedPhone(phone);
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        const validationErrors = validate();
        setErrors(validationErrors);

        if (Object.keys(validationErrors).length > 0) {
            return;
        }

        const dataToSubmit = {
            ...form,
            phone: verifiedPhone
        };

        console.log('제출:', dataToSubmit);
        alert('회원가입 성공!');
    };

    return (
        <div>
            <div className='mt-10 m-2 p-4'>

                <h2>회원가입</h2>
                <form onSubmit={handleSubmit}>
                <input name="memId" placeholder="아이디" value={form.memId} onChange={handleChange} />
                {errors.memId && <div style={{ color: 'red' }}>{errors.memId}</div>}

                <input name="pw" type="password" placeholder="비밀번호" value={form.pw} onChange={handleChange} />
                {errors.pw && <div style={{ color: 'red' }}>{errors.pw}</div>}

                <input name="name" placeholder="이름" value={form.name} onChange={handleChange} />
                {errors.name && <div style={{ color: 'red' }}>{errors.name}</div>}

                <input name="email" type="email" placeholder="이메일" value={form.email} onChange={handleChange} />
                {errors.email && <div style={{ color: 'red' }}>{errors.email}</div>}

                <input name="birthDate" type="date" value={form.birthDate} onChange={handleChange} />
                {errors.birthDate && <div style={{ color: 'red' }}>{errors.birthDate}</div>}

                <select name="gender" value={form.gender} onChange={handleChange}>
                    <option value="">성별 선택</option>
                    <option value="MALE">남성</option>
                    <option value="FEMALE">여성</option>
                </select>
                {errors.gender && <div style={{ color: 'red' }}>{errors.gender}</div>}

                <input name="addr" placeholder="주소" value={form.addr} onChange={handleChange} />
                <input name="addrDetail" placeholder="상세 주소" value={form.addrDetail} onChange={handleChange} />

                <label>
                    <input name="checkSms" type="checkbox" checked={form.checkSms} onChange={handleChange} />
                    SMS 수신 동의
                </label>
                {errors.checkSms && <div style={{ color: 'red' }}>{errors.checkSms}</div>}

                <label>
                    <input name="checkEmail" type="checkbox" checked={form.checkEmail} onChange={handleChange} />
                    이메일 수신 동의
                </label>
                {errors.checkEmail && <div style={{ color: 'red' }}>{errors.checkEmail}</div>}

                <PhoneVerification onVerified={setVerifiedPhone} />
                {errors.phone && <div style={{ color: 'red' }}>{errors.phone}</div>}

                <button type="submit">회원가입</button>
            </form>
            </div>
        </div>
    );
};

export default MemberRegisterComponent;
