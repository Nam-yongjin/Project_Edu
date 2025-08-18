import logo from '../assets/logo.png';
import { Link } from 'react-router-dom';
import { useState } from 'react';

const Footer = () => {
    const [isOpen, setIsOpen] = useState(false);
    const [content, setContent] = useState("");
    const [title, setTitle] = useState("");

    const openModal = async (fileName, modalTitle) => {
        try {
            const response = await fetch(`/terms/${fileName}`);
            const text = await response.text();
            setContent(text);
            setTitle(modalTitle);
            setIsOpen(true);
        } catch (error) {
            console.error("파일 불러오기 실패:", error);
        }
    };

    const closeModal = () => {
        setIsOpen(false);
    };

    return (
        <div>
            <footer className="bg-gray-600 px-8 py-6 text-[14px]">
                <div className="max-w-screen-xl mx-auto flex justify-between items-start flex-wrap">
                    {/* 왼쪽: 정책 + 주소 */}
                    <div className='space-y-4 max-w-xl'>
                        {/* 정책 */}
                        <nav className='space-x-2 text-m text-white font-medium'>
                            <span className='hover:underline cursor-pointer ' onClick={() => openModal("register.txt", "이용약관")}><Link to="">이용약관</Link></span>
                            <span>|</span>
                            <span className='hover:underline cursor-pointer' onClick={() => openModal("process.txt", "개인정보처리방침")}><Link to="">개인정보처리방침</Link></span>
                            <span>|</span>
                            <span className='hover:underline cursor-pointer '><Link to="/about/direction">오시는길</Link></span>
                        </nav>
                        {/* 주소 */}
                        <address className="not-italic space-y-1 text-gray-400">
                            <p className='text-gray-300 font-semibold'>서울에듀테크소프트랩</p>
                            <p>주소 : 서울특별시 광진구 능동로 120 신공학관 1F</p>
                            <p>Tel : 02-450-0698 / 02-450-0699</p>
                            <p>E-mail : seouledtech@konkuk.ac.kr</p>
                        </address>
                    </div>
                    {/* 오른쪽 */}
                    <div className='text-right mt-6 md:mt-0'>
                        <img src={logo} alt="로고" className="ml-8 w-[240px] h-[100px] flex-shrink-0" />
                        {/* https://www.keris.or.kr/main/cm/cntnts/cntntsViewPop.do?cntntsId=1681 */}
                        <p className='pt-5 text-xs font-light text-gray-300'>
                            Copyright © 2024 서울에듀테크소프트랩 All Right Reserved.
                        </p>
                    </div>
                </div>
            </footer>
            {/* 모달 */}
            {isOpen && (
                <div className="fixed inset-0 bg-black bg-opacity-60 flex justify-center items-center z-50" onClick={closeModal}>   {/*화면 밖 클릭시 모달 닫기*/}
                    <div className="min-blank bg-white max-w-2xl p-6 rounded-lg max-h-[80vh] overflow-y-auto" onClick={(e) => e.stopPropagation()}> {/*화면 안 클릭시 모달 닫기 금지*/}
                        <h2 className="newText-lg font-semibold mb-4">{title}</h2>
                        <pre className="nanum-gothic whitespace-pre-wrap newText-sm">{content}</pre>
                        <div className="text-right mt-4">
                            <button
                                onClick={() => setIsOpen(false)}
                                className="newText-base positive-button"
                            >
                                닫기
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};
export default Footer;