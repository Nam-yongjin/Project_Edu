import useMove from "../../hooks/useMove";
import { useState } from "react";

const initialChecked = { register: false, agree: false, process: false };

const RegisterTermsComponent = () => {
    const { moveToPath } = useMove();
    const [userType, setUserType] = useState('');
    const [checkedItems, setCheckedItems] = useState({ ...initialChecked });

    const isAllChecked = Object.values(checkedItems).every(Boolean);

    const handleCheckAll = (e) => {
        const checked = e.target.checked;
        setCheckedItems({
            register: checked,
            agree: checked,
            process: checked
        });
    };

    const handleIndividualCheck = (e) => {
        const { name, checked } = e.target;
        setCheckedItems((prev) => ({
            ...prev,
            [name]: checked
        }));
    };

    const handleSubmit = (e) => {
        e.preventDefault();

        if (!userType) {
            alert('회원 유형을 선택해주세요.');
            return;
        }

        if (!isAllChecked) {
            alert('모든 필수 약관에 동의해야 합니다.');
            return;
        }

        moveToPath(`/register/${userType}`);
    };

    return (
        <div className="text-center">
            <div>
                <h2 className="text-xl font-bold mb-4">이용 약관 동의</h2>

                <div>
                    
                    <label>
                        <input
                            type="checkbox"
                            name="register"
                            checked={checkedItems.register}
                            onChange={handleIndividualCheck}
                        />
                        서비스 이용약관 동의 <strong className="text-red-600">[필수]</strong>
                    </label>
                </div>
                <div>
                    <label>
                        <input
                            type="checkbox"
                            name="agree"
                            checked={checkedItems.agree}
                            onChange={handleIndividualCheck}
                        />
                        개인정보 수집 동의 <strong className="text-red-600">[필수]</strong>
                    </label>
                </div>
                <div>
                    <label>
                        <input
                            type="checkbox"
                            name="process"
                            checked={checkedItems.process}
                            onChange={handleIndividualCheck}
                        />
                        개인정보 처리 동의 <strong className="text-red-600">[필수]</strong>
                    </label>
                </div>
                <div className="mt-2">
                    <label>
                        <input
                            type="checkbox"
                            checked={isAllChecked}
                            onChange={handleCheckAll}
                        />
                        전체 동의
                    </label>
                </div>
            </div>

            <div className="mt-6">
                <h2 className="text-xl font-bold mb-4">회원 유형 선택</h2>
                <div className="flex flex-row gap-2 justify-center">
                    {['member', 'student', 'teacher', 'company'].map((type) => (
                        <label key={type}>
                            <input
                                type="radio"
                                value={type}
                                checked={userType === type}
                                onChange={(e) => setUserType(e.target.value)}
                            />
                            {type === 'member' ? '일반'
                                : type === 'student' ? '학생'
                                : type === 'teacher' ? '교사'
                                : '기업'}
                        </label>
                    ))}
                </div>
            </div>

            <button
                className="mt-4 p-2 bg-blue-500 text-white rounded active:bg-blue-600"
                onClick={handleSubmit}
            >
                다음
            </button>
        </div>
    );
};

export default RegisterTermsComponent;
