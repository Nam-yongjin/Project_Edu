import { useState, useEffect } from 'react';
import useMove from '../../../hooks/useMove';
import { readCompany, leaveMember, modifyCompany, checkEmail, checkPhone } from '../../../api/memberApi';
import useLogin from '../../../hooks/useLogin';
import PhoneVerification from '../PhoneVerification';
import AddressSearch from '../AddressSearch';

const initState = {
    memId: '',
    pw: '',
    pwCheck: '',
    name: '',
    email: '',
    phone: '',
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

const CompanyInfoModifyComponent = () => {
    const { moveToPath, moveToReturn } = useMove();
    const [form, setForm] = useState({ ...initState });
    const { doLogout } = useLogin();
    const [verifiedPhone, setVerifiedPhone] = useState(null);
    const [modifying, setModifying] = useState(false);
    const [errors, setErrors] = useState({});
    const [originalEmail, setOriginalEmail] = useState('');
    const [originalPhone, setOriginalPhone] = useState('');

    const fetchCompanyInfo = async () => {
        try {
            const data = await readCompany();

            setOriginalEmail(data.email);
            setOriginalPhone(data.phone);

            // gender 값 변환 처리
            const translatedGender = data.gender === "MALE"
                ? "남성"
                : data.gender === "FEMALE"
                    ? "여성"
                    : "";

            setForm(prev => ({
                ...prev,
                ...data,
                pw: '',
                gender: translatedGender
            }));
        } catch (error) {
            alert("회원 정보 조회 실패:", error);
        };
    };
    useEffect(() => {
        fetchCompanyInfo();
    }, []);

    const phoneNumber = form.phone;
    const formattedPhoneNumber = phoneNumber.replace(/(\d{3})(\d{4})(\d{4})/, '$1-$2-$3');

    const handleMoveModify = () => {
        setModifying(true);
    };

    const handleChange = (e) => {
        const { name, value, type, checked } = e.target;

        setForm((prev) => ({
            ...prev,
            [name]: type === 'checkbox' ? checked : value
        }));
    };

    const handleAddressSelected = (address) => {
        setForm(prev => ({
            ...prev,
            addr: address
        }));
    };

    const validate = () => {
        const errs = {};

        if (!/^[A-Za-z0-9!@#$.]{6,16}$/.test(form.pw)) {
            errs.pw = '🚫 비밀번호는 6~16자, 영문/숫자, 특수문자(!@#$.)만 사용 가능합니다.';
        };

        if (form.pw !== form.pwCheck) {
            errs.pwCheck = '🚫 비밀번호가 일치하지 않습니다.';
        };

        if (!/^[가-힣]{1,6}$/.test(form.name)) {
            errs.name = '🚫 이름은 한글 6자 이하여야 합니다.';
        };

        if (!form.email || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email)) {
            errs.email = '🚫 유효한 이메일을 입력해주세요.';
        };

        if (!verifiedPhone) {
            errs.phone = '🚫 휴대폰 인증이 필요합니다.';
        };

        if (!form.companyName) {
            errs.companyName = '🚫 기업명을 입력해주세요.';
        };

        if (!form.position) {
            errs.position = '🚫 직급을 입력해주세요.';
        };

        return errs;
    };

    const handleClickModify = async (e) => {
        e.preventDefault();
        const validationErrors = validate();
        setErrors(validationErrors);

        if (Object.keys(validationErrors).length > 0) return;

        if (form.email !== originalEmail) {
            let isDuplicated = false;
            try {
                isDuplicated = await checkEmail({ email: form.email });
            } catch (error) {
                alert("이메일 중복 확인 중 오류가 발생했습니다.", error);
                return;
            }

            if (isDuplicated) {
                alert('이미 등록 된 이메일입니다.');
                return;
            }
        }

        if (form.phone !== originalPhone) {
            let isDuplicated = false;
            try {
                isDuplicated = await checkPhone({ phone: form.phone });
            } catch (error) {
                alert("휴대본번호 중복 확인 중 오류가 발생했습니다.",error);
                return;
            }

            if (isDuplicated) {
                alert('이미 등록 된 휴대폰번호입니다.');
                return;
            }
        }

        const { memId, pwCheck, birthDate, gender, role, ...dataToSubmit } = {
            ...form,
            phone: verifiedPhone
        };

        modifyCompany(dataToSubmit)
            .then(() => {
                alert("회원정보가 수정되었습니다.");
                setForm({ ...initState });
                setVerifiedPhone(null);
                setModifying(false);
                setErrors({});
                moveToPath('/company/myInfo');
            })
            .catch((error) => {
                alert("회원정보 수정 실패", error);
            });
    };

    const handleModifyCancle = (e) => {
        setForm({ ...initState });
        setVerifiedPhone(null);
        setModifying(false);
        setErrors({});
        fetchCompanyInfo();
        moveToPath('/company/myInfo');
    }

    const handleMoveLeave = async () => {
        if (window.confirm("정말 탈퇴하시겠습니까?")) {
            try {
                await leaveMember();
                alert("탈퇴 처리 되었습니다.");
                setForm({ ...initState });
                setVerifiedPhone(null);
                setModifying(false);
                setErrors({});
                doLogout();
                moveToPath('/');
            } catch (error) {
                if (error.response && error.response.data) {
                    alert(error.response.data);
                } else {
                    alert("탈퇴에 실패했습니다.", error);
                }
            }
        } else {
            alert("탈퇴를 취소했습니다.");
        }
    };

    return (
        <div className={`mx-auto ${modifying ? "max-w-screen-md" : "max-w-screen-sm"}`}>
            <div className={`page-shadow min-blank my-10 p-10 space-y-6 text-center`}>
                {modifying ?
                    <div className="newText-3xl font-bold">회원정보수정</div>
                    :
                    <div className="newText-3xl font-bold">회원상세정보</div>
                }
                <div>
                    <div className="newText-base flex items-center">
                        <label className="w-36 text-left font-medium">아이디<span className='text-red-600' hidden={!modifying}> *</span></label>
                        <input
                            name="memId"
                            value={form.memId}
                            disabled={true}
                            className={`flex-1 input-focus ${modifying? "cursor-not-allowed":""}`} />
                    </div>
                </div>
                {modifying ?
                    <div>
                        <div className="newText-base flex items-center">
                            <label className="w-36 text-left font-medium">새 비밀번호<span className='text-red-600' hidden={!modifying}> *</span></label>
                            <input
                                name="pw"
                                type="password"
                                placeholder="새 비밀번호 입력"
                                value={form.pw}
                                onChange={handleChange}
                                className="flex-1 input-focus" />
                        </div>
                        {errors.pw && <div className="text-red-500 newText-sm text-left ml-36">{errors.pw}</div>}
                    </div>
                    :
                    <></>}
                {modifying ?
                    <div>
                        <div className="newText-base flex items-center">
                            <label className="w-36 text-left font-medium">새 비밀번호 확인<span className='text-red-600' hidden={!modifying}> *</span></label>
                            <input
                                name="pwCheck"
                                type="password"
                                placeholder="새 비밀번호 확인"
                                value={form.pwCheck}
                                onChange={handleChange}
                                className="flex-1 input-focus"
                            />
                        </div>
                        {errors.pwCheck && <div className="text-red-500 newText-sm text-left ml-36">{errors.pwCheck}</div>}
                    </div>
                    :
                    <></>}
                <div>
                    <div className="newText-base flex items-center">
                        <label className="w-36 text-left font-medium">이름<span className='text-red-600' hidden={!modifying}> *</span></label>
                        <input
                            name="name"
                            placeholder="이름"
                            value={form.name}
                            disabled={!modifying}
                            onChange={handleChange}
                            className="flex-1 input-focus" />
                    </div>
                    {errors.name && <div className="text-red-500 newText-sm text-left ml-36">{errors.name}</div>}
                </div>

                {modifying ?
                    <div>
                        <div className="newText-base flex items-start">
                            <div className="xl:w-[182px] sm:w-[188px] w-[234px] text-left font-medium pt-4">휴대폰<span className='text-red-600' hidden={!modifying}> *</span></div>
                            <PhoneVerification onVerified={setVerifiedPhone} />
                        </div>
                        {errors.phone && <div className="text-red-500 newText-sm text-left ml-36">{errors.phone}</div>}
                    </div>
                    :
                    <div>
                        <div className="newText-base flex items-start">
                            <div className="w-36 text-left font-medium pt-2">휴대폰<span className='text-red-600' hidden={!modifying}> *</span></div>
                            <input
                                type="text"
                                value={formattedPhoneNumber}
                                disabled={true}
                                className="flex-1 input-focus"
                            />
                        </div>
                    </div>
                }

                <div>
                    <div className="newText-base flex items-center">
                        <label className="w-36 text-left font-medium">이메일<span className='text-red-600' hidden={!modifying}> *</span></label>
                        <input
                            name="email"
                            type="email"
                            placeholder="이메일"
                            value={form.email}
                            disabled={!modifying}
                            onChange={handleChange}
                            className="flex-1 input-focus" />
                    </div>
                    {errors.email && <div className="text-red-500 newText-sm text-left ml-36">{errors.email}</div>}
                </div>

                <div>
                    <div className="newText-base flex items-center">
                        <label className="w-36 text-left font-medium">생년월일<span className='text-red-600' hidden={!modifying}> *</span></label>
                        <input
                            name="birthDate"
                            type="date"
                            value={form.birthDate}
                            disabled={true}
                            className={`flex-1 input-focus ${modifying? "cursor-not-allowed":""}`} />
                    </div>
                </div>

                <div>
                    <div className="newText-base flex items-center">
                        <div className="w-36 text-left font-medium">성별<span className='text-red-600' hidden={!modifying}> *</span></div>
                        <input
                            name="gender"
                            value={form.gender}
                            disabled={true}
                            className={`flex-1 input-focus ${modifying? "cursor-not-allowed":""}`} />
                    </div>
                </div>

                {modifying ?
                    <div>
                        <div className="newText-base flex items-center gap-4">
                            <div className="w-32 text-left font-medium">주소</div>
                            <input
                                name="addr"
                                placeholder="주소"
                                value={form.addr}
                                readOnly
                                className="flex-1 w-full bg-gray-100 input-focus"
                            />
                            <AddressSearch onAddressSelected={handleAddressSelected} />
                        </div>
                    </div>
                    :
                    <div>
                        <div className="newText-base flex items-center">
                            <label className="w-36 text-left font-medium">주소</label>
                            <input
                                name="addr"
                                value={form.addr ?? ''}
                                disabled={true}
                                className="flex-1 input-focus"
                            />
                        </div>
                    </div>
                }

                <div>
                    <div className="newText-base flex items-center">
                        <label className="w-36 text-left font-medium">상세 주소</label>
                        <input
                            name="addrDetail"
                            placeholder="상세 주소"
                            value={form.addrDetail ?? ''}
                            disabled={!modifying}
                            onChange={handleChange}
                            className="flex-1 input-focus" />
                    </div>
                </div>

                <div>
                    <div className="newText-base flex items-center">
                        <label className="w-36 text-left font-medium">기업명<span className='text-red-600' hidden={!modifying}> *</span></label>
                        <input
                            name="companyName"
                            type="text"
                            placeholder="기업명"
                            value={form.companyName}
                            disabled={!modifying}
                            onChange={handleChange}
                            className="flex-1 input-focus" />
                    </div>
                    {errors.companyName && <div className="text-red-500 newText-sm text-left ml-36">{errors.companyName}</div>}
                </div>
                <div>
                    <div className="newText-base flex items-center">
                        <label className="w-36 text-left font-medium">직급<span className='text-red-600' hidden={!modifying}> *</span></label>
                        <input
                            name="position"
                            type="text"
                            placeholder="직급"
                            value={form.position}
                            disabled={!modifying}
                            onChange={handleChange}
                            className="flex-1 input-focus" />
                    </div>
                    {errors.position && <div className="text-red-500 newText-sm text-left ml-36">{errors.position}</div>}
                </div>

                <div className="newText-base flex items-center">
                    <label className="w-36 text-left font-medium">SMS 수신</label>
                    <input
                        name="checkSms"
                        type="checkbox"
                        checked={form.checkSms}
                        disabled={!modifying}
                        onChange={handleChange}
                        className="mr-2" />
                </div>

                <div className="newText-base flex items-center">
                    <label className="w-36 text-left font-medium">이메일 수신</label>
                    <input
                        name="checkEmail"
                        type="checkbox"
                        checked={form.checkEmail}
                        disabled={!modifying}
                        onChange={handleChange}
                        className="mr-2" />
                </div>

                <div className='newText-base pt-4'>
                    {modifying
                        ?
                        <div className='flex justify-center'>
                            <div className='pr-4'>
                                <button className='positive-button' onClick={handleClickModify}>수정완료</button>
                            </div>
                            <div className='pr-4'>
                                <button className='normal-button' onClick={handleModifyCancle}>수정취소</button>
                            </div>
                            <div>
                                <button className='nagative-button' onClick={handleMoveLeave}>회원탈퇴</button>
                            </div>
                        </div>
                        :
                        <div className='flex justify-center'>
                            <div className='pr-4'>
                                <button className="positive-button" onClick={handleMoveModify}>정보수정</button>
                            </div>
                            <div>
                                <button className='normal-button' onClick={moveToReturn}>뒤로가기</button>
                            </div>
                        </div>
                    }
                </div>
            </div>
        </div>
    );
};

export default CompanyInfoModifyComponent;
