import SubAboutHeader from "../../layouts/SubAboutHeader";
import { useState, useEffect } from "react";

const BusinessComponent = () => {
    const [businessText, setBusinessText] = useState("");

    useEffect(() => {
        fetch('/terms/business.txt')
            .then((res) => res.text())
            .then((text) => setBusinessText(text))
            .catch((error) => {
                setBusinessText("사업소개를 불러올 수 없습니다.");
            });
    }, []);

    return (
        <div className="">
            <SubAboutHeader />
            <div className="mx-auto max-w-screen-xl my-10 border-b border-gray-300">
                <div className="text-2xl font-bold">사업소개</div>
                <div className="text-xl font-semibold py-8">에듀테크 소프트랩이란?</div>
                <div className="text-lg pb-8" style={{ whiteSpace: 'pre-wrap' }}>{businessText}</div>
            </div>
            <div className="mx-auto max-w-screen-xl my-10">
                <div className="text-2xl font-bold pb-8">주요사업</div>
                <div className="flex flex-col lg:flex-row justify-center gap-10 text-center items-center">
                    <div className="text-xl shadow-2xl shadow-gray-500 rounded-3xl px-20 py-16 font-semibold w-[400px] h-[400px]">
                        에듀테크 프로그램 운영
                        <ul className="text-base space-y-8 mt-16 font-normal">
                            <li>
                                에듀테크 수요자 맞춤형 학생 대상 교육 프로그램 운영
                            </li>
                            <li>
                                교원 대상 에듀테크 연수 운영
                            </li>
                            <li>
                                에듀테크 기업 대상 연수프로그램 운영
                            </li>
                        </ul>
                    </div>
                    <div className="text-xl shadow-2xl shadow-gray-500 rounded-3xl px-20 py-16 font-semibold w-[400px] h-[400px]">
                        연구회 운영 및 기업 지원
                        <ul className="text-base space-y-8 mt-16 font-normal">
                            <li>
                                에듀테크 실증 관련 연구
                            </li>
                            <li>
                                공교육 현장 활용 사례 발굴
                            </li>
                            <li>
                                에듀테크 개발 · 상용화 지원
                            </li>
                        </ul>
                    </div>
                    <div className="text-xl shadow-2xl shadow-gray-500 rounded-3xl px-20 py-16 font-semibold w-[400px] h-[400px]">
                        실증 지원
                        <ul className="text-base space-y-8 mt-16 font-normal">
                            <li>
                                교육성 중심의 실증 체계 전환
                            </li>
                            <li>
                                교육 효과 분석 프로그램 도입
                            </li>
                            <li>
                                교사와 기업 간 건전한 교류 지원
                            </li>
                        </ul>
                    </div>
                </div>
            </div>
        </div>
    );
};
export default BusinessComponent;