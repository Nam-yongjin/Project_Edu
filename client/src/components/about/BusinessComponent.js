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
        <div className="mx-auto max-w-screen-xl">
            <div className="min-blank my-10 border-b border-gray-300">
                <div className="newText-2xl font-bold">사업소개</div>
                <div className="newText-xl font-medium py-8">에듀테크 소프트랩이란?</div>
                <div className="newText-lg pb-8" style={{ whiteSpace: 'pre-wrap' }}>{businessText}</div>
            </div>
            <div className="min-blank my-10">
                <div className="newText-2xl font-bold pb-8">주요사업</div>
                <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 lg:flex-row gap-10 text-center items-center justify-items-center">
                    <div className="newText-xl shadow-2xl shadow-gray-500 rounded-3xl px-16 py-16 font-semibold 
                    lg:w-[330px] sm:w-[310px] w-[290px] lg:h-[400px] sm:h-[370px] h-[340px] hover:scale-105 ease-in-out duration-300">
                        에듀테크 프로그램 운영
                        <ul className="newText-base space-y-8 mt-12 font-normal">
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
                    <div className="newText-xl shadow-2xl shadow-gray-500 rounded-3xl px-16 py-16 font-semibold 
                    lg:w-[330px] sm:w-[310px] w-[290px] lg:h-[400px] sm:h-[370px] h-[340px] hover:scale-105 ease-in-out duration-300">
                        연구회 운영 및 기업지원
                        <ul className="newText-base space-y-8 mt-12 font-normal">
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
                    <div className="newText-xl shadow-2xl shadow-gray-500 rounded-3xl px-16 py-16 font-semibold 
                    lg:w-[330px] sm:w-[310px] w-[290px] lg:h-[400px] sm:h-[370px] h-[340px] hover:scale-105 ease-in-out duration-300">
                        실증 지원
                        <ul className="newText-base space-y-8 mt-12 font-normal">
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