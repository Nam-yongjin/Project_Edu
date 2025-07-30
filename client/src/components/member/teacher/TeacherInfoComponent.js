import { useState, useEffect } from 'react';
import useMove from '../../../hooks/useMove';
import { readTeacher, leaveMember } from '../../../api/memberApi';
import useLogin from '../../../hooks/useLogin';

const initState = {
    memId: '',
    name: '',
    email: '',
    phone: '',
    birthDate: '',
    gender: '',
    addr: '',
    addrDetail: '',
    checkSms: false,
    checkEmail: false,
    role: 'TEACHER',
    schoolName: ''
};

const TeacherInfoComponent = () => {
    const { moveToPath } = useMove();
    const [form, setForm] = useState({ ...initState });
    const { doLogout } = useLogin();

    useEffect(() => {
        const fetchCompanyInfo = async () => {
            try {
                const data = await readTeacher();

                // gender 값 변환 처리
                const translatedGender = data.gender === "MALE"
                    ? "남성"
                    : data.gender === "FEMALE"
                        ? "여성"
                        : "";

                setForm(prev => ({
                    ...prev,
                    ...data,
                    gender: translatedGender
                }));
            } catch (err) {
                console.error("회원 정보 조회 실패:", err);
            };
        };

        fetchCompanyInfo();
    }, []);

    const handleMoveModify = () => {
        moveToPath(`/teacher/modify`);
    };
    const handleMoveLeave = async () => {
        if (window.confirm("정말 탈퇴하시겠습니까?")) {
            try {
                await leaveMember();
                alert("탈퇴 처리 되었습니다.");
                doLogout();
                moveToPath('/');
            } catch (err) {
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

            <div>
                <input
                    name="name"
                    value={form.name}
                    disabled={true} />
            </div>

            <div>
                <input
                    type="text"
                    value={form.phone}
                    disabled={true}
                />
            </div>

            <div>
                <input
                    name="email"
                    type="email"
                    value={form.email}
                    disabled={true} />
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

            <div>
                <input
                    name="addr"
                    value={form.addr ?? ''}
                    disabled={true}
                />
            </div>
            <div>
                <input
                    name="addrDetail"
                    value={form.addrDetail ?? ''}
                    disabled={true} />
            </div>

            <div>
                <input
                    name="schoolName"
                    type="text"
                    value={form.schoolName}
                    disabled={true} />
            </div>

            <div>
                <label>
                    <input
                        name="checkSms"
                        type="checkbox"
                        checked={form.checkSms}
                        disabled={true} />
                    SMS 수신 동의
                </label>
            </div>

            <div>
                <label>
                    <input
                        name="checkEmail"
                        type="checkbox"
                        checked={form.checkEmail}
                        disabled={true} />
                    이메일 수신 동의
                </label>
            </div>

            <div className=''>
                <div>
                    <button className='rounded p-1 w-18 bg-blue-500	text-white active:bg-blue-600' onClick={handleMoveModify}>정보수정</button>
                </div>
                <div>
                    <button className='rounded p-1 w-18 bg-red-500	text-white active:bg-red-600' onClick={handleMoveLeave}>회원탈퇴</button>
                </div>
            </div>
        </div>
    );
};

export default TeacherInfoComponent;
