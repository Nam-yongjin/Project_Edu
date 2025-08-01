import { useState, useEffect } from 'react';
import useMove from '../../../hooks/useMove';
import { readCompany, leaveMember, modifyCompany } from '../../../api/memberApi';
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

    const fetchCompanyInfo = async () => {
        try {
            const data = await readCompany();

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
        } catch (err) {
            console.error("회원 정보 조회 실패:", err);
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

    const handleClickModify = (e) => {
        e.preventDefault();
        const validationErrors = validate();
        setErrors(validationErrors);

        if (Object.keys(validationErrors).length > 0) {
            return;
        };

        // dataToSubmit: CompanyModifyDTO
        const { memId, pwCheck, birthDate, gender, role, ...dataToSubmit } = {
            ...form,
            phone: verifiedPhone
        };

        modifyCompany(dataToSubmit).then((response) => {
            alert("회원정보가 수정되었습니다.");
            setForm({ ...initState });
            setVerifiedPhone(null);
            setModifying(false);
            setErrors({});
            moveToPath('/');
        }).catch((error) => {
            alert("회원정보 수정실패");
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
                alert("탈퇴에 실패했습니다.");
            }
        } else {
            alert("탈퇴를 취소했습니다.");
        }
    };

    return (
        <div className='space-y-5 mt-10 mx-2 pl-4'>
            <div className='text-3xl'>회원상세정보</div>
            <div>
                <input
                    name="memId"
                    value={form.memId}
                    disabled={true} />
            </div>
            {modifying ?
                <div>
                    <input
                        name="pw"
                        type="password"
                        placeholder="새 비밀번호 입력"
                        value={form.pw}
                        onChange={handleChange} />
                    {errors.pw && <div style={{ color: 'red' }}>{errors.pw}</div>}
                </div>
                :
                <></>}
            {modifying ?
                <div>
                    <input
                        name="pwCheck"
                        type="password"
                        placeholder="새 비밀번호 확인"
                        value={form.pwCheck}
                        onChange={handleChange}
                    />
                    {errors.pwCheck && <div style={{ color: 'red' }}>{errors.pwCheck}</div>}
                </div>
                :
                <></>}
            <div>
                <input
                    name="name"
                    placeholder="이름"
                    value={form.name}
                    disabled={!modifying}
                    onChange={handleChange} />
                {errors.name && <div style={{ color: 'red' }}>{errors.name}</div>}
            </div>

            {modifying ?
                <div>
                    <PhoneVerification onVerified={setVerifiedPhone} />
                    {errors.phone && <div style={{ color: 'red' }}>{errors.phone}</div>}
                </div>
                :
                <div>
                    <input
                        type="text"
                        value={formattedPhoneNumber}
                        disabled={true}
                    />
                </div>
            }


            <div>
                <input
                    name="email"
                    type="email"
                    placeholder="이메일"
                    value={form.email}
                    disabled={!modifying}
                    onChange={handleChange} />
                {errors.email && <div style={{ color: 'red' }}>{errors.email}</div>}
            </div>

            <div>
                <input
                    name="birthDate"
                    type="date"
                    value={form.birthDate}
                    disabled={true} />
            </div>

            <div>
                <input
                    name="gender"
                    value={form.gender}
                    disabled={true} />
            </div>

            {modifying ?
                <div>
                    <AddressSearch onAddressSelected={handleAddressSelected} />
                    <input
                        name="addr"
                        placeholder="주소"
                        value={form.addr}
                        readOnly
                    />
                </div>
                :
                <div>
                    <input
                        name="addr"
                        value={form.addr ?? ''}
                        disabled={true}
                    />
                </div>
            }

            <div>
                <input
                    name="addrDetail"
                    placeholder="상세 주소"
                    value={form.addrDetail ?? ''}
                    disabled={!modifying} 
                    onChange={handleChange}/>
            </div>

            <div>
                <input
                    name="companyName"
                    type="text"
                    placeholder="기업명"
                    value={form.companyName}
                    disabled={!modifying}
                    onChange={handleChange} />
                {errors.companyName && <div style={{ color: 'red' }}>{errors.companyName}</div>}
            </div>

            <div>
                <input
                    name="position"
                    type="text"
                    placeholder="직급"
                    value={form.position}
                    disabled={!modifying}
                    onChange={handleChange} />
                {errors.position && <div style={{ color: 'red' }}>{errors.position}</div>}
            </div>

            <div>
                <label>
                    <input
                        name="checkSms"
                        type="checkbox"
                        checked={form.checkSms}
                        disabled={!modifying}
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
                        disabled={!modifying}
                        onChange={handleChange} />
                    이메일 수신 동의
                </label>
                {errors.checkEmail && <div style={{ color: 'red' }}>{errors.checkEmail}</div>}
            </div>

            <div className=''>
                {modifying
                    ?
                    <div className='flex'>
                        <div className='pr-4'>
                            <button className='rounded p-1 w-[70px] bg-blue-500	text-white active:bg-blue-600 font-bold' onClick={handleClickModify}>수정완료</button>
                        </div>
                        <div className='pr-4'>
                            <button className='rounded p-1 w-[70px] bg-gray-200 active:bg-gray-300 font-bold' onClick={handleModifyCancle}>취소</button>
                        </div>
                        <div>
                            <button className='rounded p-1 w-[70px] bg-red-500	text-white active:bg-red-600 font-bold' onClick={handleMoveLeave}>회원탈퇴</button>
                        </div>
                    </div>
                    :
                    <div className='flex'>
                        <div className='pr-4'>
                            <button className='rounded p-1 w-[70px] bg-blue-500	text-white active:bg-blue-600 font-bold' onClick={handleMoveModify}>정보수정</button>
                        </div>
                        <div>
                            <button className='rounded p-1 w-[70px] bg-gray-300 active:bg-gray-400 font-bold' onClick={moveToReturn}>뒤로가기</button>
                        </div>
                    </div>
                }
            </div>
        </div>
    );
};

export default CompanyInfoModifyComponent;
