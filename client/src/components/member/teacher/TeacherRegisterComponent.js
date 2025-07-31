import { useState } from 'react';
import PhoneVerification from '../PhoneVerification';
import useMove from '../../../hooks/useMove';
import { registerTeacher, checkDuplicateId } from '../../../api/memberApi';
import AddressSearch from '../AddressSearch';

const initState = {
    memId: '',
    pw: '',
    pwCheck: '',
    name: '',
    email: '',
    birthDate: '',
    gender: '',
    addr: '',
    addrDetail: '',
    checkSms: false,
    checkEmail: false,
    schoolName: '',
    role: 'TEACHER'
};

const TeacherRegisterComponent = () => {
    const [verifiedPhone, setVerifiedPhone] = useState(null);
    const { moveToLogin } = useMove();
    const [idCheck, setIdCheck] = useState(false);
    const [form, setForm] = useState({ ...initState });

    const [errors, setErrors] = useState({});

    const handleChange = (e) => {
        const { name, value, type, checked } = e.target;

        setForm((prev) => ({
            ...prev,
            [name]: type === 'checkbox' ? checked : value
        }));

        if (name === 'memId') {
            setIdCheck(false);
        };
    };

    const handleAddressSelected = (address) => {
        setForm(prev => ({
            ...prev,
            addr: address
        }));
    };

    const validate = () => {
        const errs = {};

        if (!/^[A-Za-z0-9]{6,16}$/.test(form.memId)) {
            errs.memId = '아이디는 6~16자 영문/숫자만 가능합니다.';
        };

        if (!/^[A-Za-z0-9!@#$.]{6,16}$/.test(form.pw)) {
            errs.pw = '비밀번호는 6~16자, 특수문자(!@#$.) 사용 가능.';
        };

        if (form.pw !== form.pwCheck) {
            errs.pwCheck = '비밀번호가 일치하지 않습니다.';
        };

        if (!/^[가-힣]{1,6}$/.test(form.name)) {
            errs.name = '이름은 한글 1~6자여야 합니다.';
        };

        if (!form.email || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email)) {
            errs.email = '유효한 이메일을 입력해주세요.';
        };

        if (!form.birthDate) {
            errs.birthDate = '생년월일을 선택해주세요.';
        };

        if (!form.gender) {
            errs.gender = '성별을 선택해주세요.';
        };

        if (!verifiedPhone) {
            errs.phone = '휴대폰 인증이 필요합니다.';
        };

        if (!form.schoolName) {
            errs.schoolName = '학교명을 입력해주세요.';
        };

        return errs;
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        const validationErrors = validate();
        setErrors(validationErrors);

        if (Object.keys(validationErrors).length > 0) {
            return;
        };

        // pwCheck를 제거한 객체
        const { pwCheck, ...dataToSubmit } = {
            ...form,
            phone: verifiedPhone
        };
        if (!idCheck) {
            alert("아이디 중복 확인을 해야합니다.");
            return;
        };

        registerTeacher(dataToSubmit).then((response) => {
            alert('회원가입 완료');
            // 초기화
            setForm({ ...initState });
            setVerifiedPhone(null);
            setErrors({});
            moveToLogin();
        }).catch((error) => {
            alert('회원가입 실패');
        });

    };

    const handleCheckDuplicateId = async () => {
        if (!form.memId) {
            alert('아이디를 입력해주세요.');
            return;
        } else if (form.memId.length < 6) {
            alert('아이디를 6자 이상 입력해주세요.');
            return;
        };
        checkDuplicateId({ memId: form.memId })
            .then((isDuplicated) => {
                if (isDuplicated) {
                    alert('이미 사용 중인 아이디입니다.');
                    setIdCheck(false)
                } else {
                    alert('사용 가능한 아이디입니다.');
                    setIdCheck(true)
                };
            })
            .catch((error) => {
                alert('중복 확인 중 오류 발생');
                setIdCheck(false)
            });
    };

    return (
        <div className='space-y-5 mt-10 mx-2 pl-4 text-center'>
            <div className='text-3xl'>회원가입</div>
            <div>
                <input
                    name="memId"
                    placeholder="아이디"
                    value={form.memId}
                    onChange={handleChange}
                    autoFocus />
                <button className="border border-black px-1 bg-gray-300 active:bg-gray-400"
                    onClick={handleCheckDuplicateId}>
                    아이디 중복 체크
                </button>
                {errors.memId && <div style={{ color: 'red' }}>{errors.memId}</div>}
            </div>

            <div>
                <input
                    name="pw"
                    type="password"
                    placeholder="비밀번호"
                    value={form.pw}
                    onChange={handleChange} />
                {errors.pw && <div style={{ color: 'red' }}>{errors.pw}</div>}
            </div>

            <div>
                <input
                    name="pwCheck"
                    type="password"
                    placeholder="비밀번호 확인"
                    value={form.pwCheck}
                    onChange={handleChange}
                />
                {errors.pwCheck && <div style={{ color: 'red' }}>{errors.pwCheck}</div>}
            </div>

            <div>
                <input
                    name="name"
                    placeholder="이름"
                    value={form.name}
                    onChange={handleChange} />
                {errors.name && <div style={{ color: 'red' }}>{errors.name}</div>}
            </div>

            <div>
                <PhoneVerification onVerified={setVerifiedPhone} />
                {errors.phone && <div style={{ color: 'red' }}>{errors.phone}</div>}
            </div>

            <div>
                <input
                    name="email"
                    type="email"
                    placeholder="이메일"
                    value={form.email}
                    onChange={handleChange} />
                {errors.email && <div style={{ color: 'red' }}>{errors.email}</div>}
            </div>

            <div>
                <input
                    name="birthDate"
                    type="date"
                    value={form.birthDate}
                    onChange={handleChange} />
                {errors.birthDate && <div style={{ color: 'red' }}>{errors.birthDate}</div>}
            </div>

            <div>
                <div>성별 선택</div>
                <label>
                    <input
                        type="radio"
                        name="gender"
                        value="MALE"
                        checked={form.gender === 'MALE'}
                        onChange={handleChange}
                    />
                    남성
                </label>
                <label className="ml-4">
                    <input
                        type="radio"
                        name="gender"
                        value="FEMALE"
                        checked={form.gender === 'FEMALE'}
                        onChange={handleChange}
                    />
                    여성
                </label>
                {errors.gender && <div style={{ color: 'red' }}>{errors.gender}</div>}
            </div>

            <div>
                <AddressSearch onAddressSelected={handleAddressSelected} />
                <input
                    name="addr"
                    placeholder="주소"
                    value={form.addr}
                    readOnly
                />
            </div>
            <div>
                <input
                    name="addrDetail"
                    placeholder="상세 주소"
                    value={form.addrDetail}
                    onChange={handleChange} />
            </div>

            <div>
                <input
                    name="schoolName"
                    type="text"
                    placeholder="학교명"
                    value={form.schoolName}
                    onChange={handleChange} />
                {errors.schoolName && <div style={{ color: 'red' }}>{errors.schoolName}</div>}
            </div>

            <div>
                <label>
                    <input
                        name="checkSms"
                        type="checkbox"
                        checked={form.checkSms}
                        onChange={handleChange} />
                    SMS 수신 동의
                </label>
                {errors.checkSms && <div style={{ color: 'red' }}>{errors.checkSms}</div>}
            </div>

            <div>
                <label>
                    <input
                        name="checkEmail"
                        type="checkbox"
                        checked={form.checkEmail}
                        onChange={handleChange} />
                    이메일 수신 동의
                </label>
                {errors.checkEmail && <div style={{ color: 'red' }}>{errors.checkEmail}</div>}
            </div>

            <div>
                <button className='rounded p-1 w-[70px] bg-blue-500	text-white active:bg-blue-600' onClick={handleSubmit}>회원가입</button>
            </div>
        </div>
    );
};

export default TeacherRegisterComponent;
