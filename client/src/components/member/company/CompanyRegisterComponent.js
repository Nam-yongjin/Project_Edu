import { useState } from 'react';
import PhoneVerification from '../PhoneVerification';
import useMove from '../../../hooks/useMove';
import { registerCompany, checkDuplicateId, checkEmail, checkPhone } from '../../../api/memberApi';
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
    companyName: '',
    position: '',
    role: 'COMPANY'
};

const CompanyRegisterComponent = () => {
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
            errs.name = '이름은 한글 6자 이하여야 합니다.';
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

        if (!form.companyName) {
            errs.companyName = '기업명을 입력해주세요.';
        };

        if (!form.position) {
            errs.position = '직급을 입력해주세요.';
        };

        return errs;
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        const validationErrors = validate();
        setErrors(validationErrors);

        if (Object.keys(validationErrors).length > 0) return;

        if (!idCheck) {
            alert("아이디 중복 확인을 해야합니다.");
            return;
        }

        // 이메일, 휴대폰번호 중복 체크
        try {
            const isDuplicatedEmail = await checkEmail({ email: form.email });
            if (isDuplicatedEmail) {
                alert('이미 등록 된 이메일입니다.');
                return;
            }
            const isDuplicatedPhone = await checkPhone({ phone: verifiedPhone });
            if (isDuplicatedPhone) {
                alert('이미 등록 된 휴대폰번호입니다.');
                return;
            }

            // pwCheck 제거
            const { pwCheck, ...dataToSubmit } = {
                ...form,
                phone: verifiedPhone
            };

            registerCompany(dataToSubmit)
                .then(() => {
                    alert('회원가입을 완료했습니다.');
                    setForm({ ...initState });
                    setVerifiedPhone(null);
                    setErrors({});
                    moveToLogin();
                })
                .catch((error) => {
                    alert('회원가입 중 오류 발생',error);
                });

        } catch (error) {
            alert('중복 확인 중 오류 발생',error);
        }
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
                alert('중복 확인 중 오류 발생',error);
                setIdCheck(false)
            });
    };

    return (
        <div className='max-w-screen-md mx-auto'>
            <div className="page-shadow min-blank my-10 p-10 space-y-6 text-center">
                <div className="newText-3xl font-bold">회원가입</div>

                {/* 아이디 */}
                <div>
                    <div className="newText-base flex items-center gap-4">
                        <label className="w-32 text-left font-medium">아이디<span className='text-red-600'> *</span></label>
                        <input
                            name="memId"
                            placeholder="아이디"
                            value={form.memId}
                            onChange={handleChange}
                            autoFocus
                            className="flex-1 input-focus"
                        />
                        <button
                            className="text-center normal-button lg:w-[100px] w-[90px]"
                            onClick={handleCheckDuplicateId}
                        >
                            중복 체크
                        </button>
                    </div>
                    {errors.memId && <div className="text-red-500 newText-sm text-left ml-36">{errors.memId}</div>}
                </div>

                {/* 비밀번호 */}
                <div>
                    <div className="newText-base flex items-center">
                        <label className="w-36 text-left font-medium">비밀번호<span className='text-red-600'> *</span></label>
                        <input
                            name="pw"
                            type="password"
                            placeholder="비밀번호"
                            value={form.pw}
                            onChange={handleChange}
                            className="flex-1 input-focus"
                        />
                    </div>
                    {errors.pw && <div className="text-red-500 newText-sm text-left ml-36">{errors.pw}</div>}
                </div>

                {/* 비밀번호 확인 */}
                <div>
                    <div className="newText-base flex items-center">
                        <label className="w-36 text-left font-medium">비밀번호 확인<span className='text-red-600'> *</span></label>
                        <input
                            name="pwCheck"
                            type="password"
                            placeholder="비밀번호 확인"
                            value={form.pwCheck}
                            onChange={handleChange}
                            className="flex-1 input-focus"
                        />
                    </div>
                    {errors.pwCheck && <div className="text-red-500 newText-sm text-left ml-36">{errors.pwCheck}</div>}
                </div>

                {/* 이름 */}
                <div>
                    <div className="newText-base flex items-center">
                        <label className="w-36 text-left font-medium">이름<span className='text-red-600'> *</span></label>
                        <input
                            name="name"
                            placeholder="이름"
                            value={form.name}
                            onChange={handleChange}
                            className="flex-1 input-focus"
                        />
                    </div>
                    {errors.name && <div className="text-red-500 newText-sm text-left ml-36">{errors.name}</div>}
                </div>

                {/* 휴대폰 인증 */}
                <div>
                    <div className="newText-base flex items-start">
                        <div className="w-36 text-left font-medium pt-4">휴대폰<span className='text-red-600'> *</span></div>
                        <PhoneVerification onVerified={setVerifiedPhone} />
                    </div>
                    {errors.phone && <div className="text-red-500 newText-sm text-left ml-36">{errors.phone}</div>}
                </div>

                {/* 이메일 */}
                <div>
                    <div className="newText-base flex items-center">
                        <label className="w-36 text-left font-medium">이메일<span className='text-red-600'> *</span></label>
                        <input
                            name="email"
                            type="email"
                            placeholder="이메일"
                            value={form.email}
                            onChange={handleChange}
                            className="flex-1 input-focus"
                        />
                    </div>
                    {errors.email && <div className="text-red-500 newText-sm text-left ml-36">{errors.email}</div>}
                </div>

                {/* 생년월일 */}
                <div>
                    <div className="newText-base flex items-center">
                        <label className="w-36 text-left font-medium">생년월일<span className='text-red-600'> *</span></label>
                        <input
                            name="birthDate"
                            type="date"
                            value={form.birthDate}
                            onChange={handleChange}
                            min="1900-01-01"
                            max={new Date().toISOString().split("T")[0]} // 오늘 날짜까지 제한
                            className="flex-1 input-focus"
                        />
                    </div>
                    {errors.birthDate && <div className="text-red-500 newText-sm text-left ml-36">{errors.birthDate}</div>}
                </div>

                {/* 성별 */}
                <div>
                    <div className="newText-base flex items-center">
                        <div className="w-36 text-left font-medium">성별<span className='text-red-600'> *</span></div>
                        <div className="flex gap-6">
                            <label className="inline-flex items-center gap-1">
                                <input
                                    type="radio"
                                    name="gender"
                                    value="MALE"
                                    checked={form.gender === 'MALE'}
                                    onChange={handleChange}
                                    className="accent-blue-500"
                                />
                                남성
                            </label>
                            <label className="inline-flex items-center gap-1">
                                <input
                                    type="radio"
                                    name="gender"
                                    value="FEMALE"
                                    checked={form.gender === 'FEMALE'}
                                    onChange={handleChange}
                                    className="accent-pink-500"
                                />
                                여성
                            </label>
                        </div>
                    </div>
                    {errors.gender && <div className="text-red-500 newText-sm text-left ml-36">{errors.gender}</div>}
                </div>

                {/* 주소 */}
                <div>
                    <div className="newText-base flex items-start">
                        <div className="w-36 text-left font-medium pt-2">주소</div>
                        <div className="flex-1">
                            <AddressSearch onAddressSelected={handleAddressSelected} />
                            <input
                                name="addr"
                                placeholder="주소"
                                value={form.addr}
                                readOnly
                                className="w-full mt-2 bg-gray-100 input-focus"
                            />
                        </div>
                    </div>
                </div>

                {/* 상세 주소 */}
                <div>
                    <div className="newText-base flex items-center">
                        <label className="w-36 text-left font-medium">상세 주소</label>
                        <input
                            name="addrDetail"
                            placeholder="상세 주소"
                            value={form.addrDetail}
                            onChange={handleChange}
                            className="flex-1 input-focus"
                        />
                    </div>
                </div>

                {/* 기업명 */}
                <div>
                    <div className="newText-base flex items-center">
                        <label className="w-36 text-left font-medium">기업명<span className='text-red-600'> *</span></label>
                        <input
                            name="companyName"
                            placeholder="기업명"
                            value={form.companyName}
                            onChange={handleChange}
                            className="flex-1 input-focus"
                        />
                    </div>
                    {errors.companyName && <div className="text-red-500 newText-sm text-left ml-36">{errors.companyName}</div>}
                </div>

                {/* 직급 */}
                <div>
                    <div className="newText-base flex items-center">
                        <label className="w-36 text-left font-medium">직급<span className='text-red-600'> *</span></label>
                        <input
                            name="position"
                            placeholder="직급"
                            value={form.position}
                            onChange={handleChange}
                            className="flex-1 input-focus"
                        />
                    </div>
                    {errors.position && <div className="text-red-500 newText-sm text-left ml-36">{errors.position}</div>}
                </div>

                {/* 수신 동의 체크박스 */}
                <div className="newText-base flex items-center">
                    <label className="w-36 text-left font-medium">SMS 수신</label>
                    <input
                        name="checkSms"
                        type="checkbox"
                        checked={form.checkSms}
                        onChange={handleChange}
                        className="mr-2"
                    />
                </div>

                <div className="newText-base flex items-center">
                    <label className="w-36 text-left font-medium">이메일 수신</label>
                    <input
                        name="checkEmail"
                        type="checkbox"
                        checked={form.checkEmail}
                        onChange={handleChange}
                        className="mr-2"
                    />
                </div>

                {/* 제출 버튼 */}
                <div className='pt-4'>
                    <button
                        className="newText-base positive-button"
                        onClick={handleSubmit}
                    >
                        회원가입
                    </button>
                </div>
            </div>
        </div>
    );
};

export default CompanyRegisterComponent;
