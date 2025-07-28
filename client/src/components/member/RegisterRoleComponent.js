import useMove from "../../hooks/useMove";
import { useState } from "react";

const RegisterRoleComponent = () => {
    const { moveToPath } = useMove()
    const [userType, setUserType] = useState('');

    const handleSubmit = (e) => {
        e.preventDefault();
        if (!userType) {
            alert('회원 유형을 선택해주세요.');
            return;
        }
        moveToPath(`/register/${userType}`);
    };
    return (
        <div>
            <h2 className="text-xl font-bold mb-4">회원 유형 선택</h2>
            <div className="flex flex-col gap-2">
                <label>
                    <input
                        type="radio"
                        value="member"
                        checked={userType === 'member'}
                        onChange={(e) => setUserType(e.target.value)}
                    />
                    일반
                </label>
                <label>
                    <input
                        type="radio"
                        value="student"
                        checked={userType === 'student'}
                        onChange={(e) => setUserType(e.target.value)}
                    />
                    학생
                </label>
                <label>
                    <input
                        type="radio"
                        value="teacher"
                        checked={userType === 'teacher'}
                        onChange={(e) => setUserType(e.target.value)}
                    />
                    교사
                </label>
                <label>
                    <input
                        type="radio"
                        value="company"
                        checked={userType === 'company'}
                        onChange={(e) => setUserType(e.target.value)}
                    />
                    기업
                </label>
            </div>
            <button className="mt-4 p-2 bg-blue-500 text-white rounded active:bg-blue-600"
                onClick={handleSubmit}>
                다음
            </button>

        </div>
    )
}

export default RegisterRoleComponent