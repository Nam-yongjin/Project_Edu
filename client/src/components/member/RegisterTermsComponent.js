import useMove from "../../hooks/useMove";
import { useState, useEffect } from "react";

const initialChecked = { register: false, agree: false, process: false };

const RegisterTermsComponent = () => {
    const { moveToPath } = useMove();
    const [userType, setUserType] = useState('');
    const [checkedItems, setCheckedItems] = useState({ ...initialChecked });
    const [registerText, setRegisterText] = useState("");
    const [agreeText, setAgreeText] = useState("");
    const [processText, setProcessText] = useState("");

    const isAllChecked = Object.values(checkedItems).every(Boolean);

    useEffect(() => {
        fetch('/terms/register.txt')
            .then((res) => res.text())
            .then((text) => setRegisterText(text))
            .catch((error) => {
                console.error("이용약관 로드 실패", error);
                setRegisterText("이용약관을 불러올 수 없습니다.");
            });
    }, []);

    useEffect(() => {
        fetch('/terms/agree.txt')
            .then((res) => res.text())
            .then((text) => setAgreeText(text))
            .catch((error) => {
                console.error("이용약관 로드 실패", error);
                setAgreeText("이용약관을 불러올 수 없습니다.");
            });
    }, []);

    useEffect(() => {
        fetch('/terms/process.txt')
            .then((res) => res.text())
            .then((text) => setProcessText(text))
            .catch((error) => {
                console.error("이용약관 로드 실패", error);
                setProcessText("이용약관을 불러올 수 없습니다.");
            });
    }, []);

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
        <div className="mb-20 pt-20">
            <div className="max-w-screen-md">

                <div className="text-center text-xl font-bold mb-4">이용 약관 동의</div>

                <div className="mt-20">
                    <div className="text-lg font-bold mb-4">서울 에듀테크 소프트랩 이용약관</div>

                    <div className="p-4 border border-gray-400 h-64 overflow-y-scroll text-left whitespace-pre-wrap bg-gray-100 rounded">
                        {registerText}
                    </div>
                    <label className="float-right pt-4">
                        <input
                            type="checkbox"
                            name="register"
                            checked={checkedItems.register}
                            onChange={handleIndividualCheck}
                            className="mr-1"
                        />
                        서비스 이용약관 동의 <strong className="text-red-600">[필수]</strong>
                    </label>
                </div>
                <div className="mt-20">
                    <div className="text-lg font-bold mb-4">개인 정보 수집 및 이용 동의</div>
                    <div className="p-4 border border-gray-400 h-64 overflow-y-scroll text-left whitespace-pre-wrap bg-gray-100 rounded">
                        {agreeText}
                    </div>
                    <label className="float-right pt-4">
                        <input
                            type="checkbox"
                            name="agree"
                            checked={checkedItems.agree}
                            onChange={handleIndividualCheck}
                            className="mr-1"
                        />
                        개인정보 수집 동의 <strong className="text-red-600">[필수]</strong>
                    </label>
                </div>
                <div className="mt-20">
                    <div className="text-lg font-bold mb-4">개인정보 처리 방침</div>
                    <div className="p-4 border border-gray-400 h-64 overflow-y-scroll text-left whitespace-pre-wrap bg-gray-100 rounded">
                        {processText}
                    </div>
                    <label className="float-right pt-4">
                        <input
                            type="checkbox"
                            name="process"
                            className="mr-1"
                            checked={checkedItems.process}
                            onChange={handleIndividualCheck}
                        />
                        개인정보 처리 동의 <strong className="text-red-600">[필수]</strong>
                    </label>
                </div>
                <div className="py-10">
                    <label className="float-right pt-8">
                        <input
                            type="checkbox"
                            checked={isAllChecked}
                            onChange={handleCheckAll}
                            className="mr-1"
                        />
                        전체 동의
                    </label>
                </div>
            </div>

            <div className="mt-20 py-2 text-center">
                <h2 className="text-xl font-bold mb-4">회원 유형 선택</h2>
                <div className="border border-gray-400 flex flex-row gap-3 justify-center bg-gray-200 mx-auto w-1/3 p-2 rounded">
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
            <div className="text-center">
                <button
                    className="mt-4 p-2 w-[60px] bg-blue-500 text-white rounded active:bg-blue-600 font-bold"
                    onClick={handleSubmit}
                >
                    다음
                </button>
            </div>
        </div>
    );
};

export default RegisterTermsComponent;
