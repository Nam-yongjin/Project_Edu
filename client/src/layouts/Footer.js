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
            
            <footer className="bg-gray-600 py-6">
                <div className="max-w-screen-xl mx-auto flex flex-col md:flex-row justify-between items-center md:items-start gap-6">
                    
                    {/* 왼쪽: 정책 + 주소 */}
                    <div className='space-y-4 text-center md:text-left min-blank'>
                        {/* 정책 */}
                        <nav className='flex flex-wrap justify-center md:justify-start gap-2 newText-sm text-white font-medium'>
                            <span 
                                className='hover:underline cursor-pointer' 
                                onClick={() => openModal("register.txt", "이용약관")}
                            >
                                이용약관
                            </span>
                            <span>|</span>
                            <span 
                                className='hover:underline cursor-pointer' 
                                onClick={() => openModal("process.txt", "개인정보처리방침")}
                            >
                                개인정보처리방침
                            </span>
                            <span>|</span>
                            <Link to="/about/direction" className='hover:underline cursor-pointer'>
                                오시는길
                            </Link>
                        </nav>
                        {/* 주소 */}
                        <address className="newText-base not-italic space-y-1 text-gray-400">
                            <p className='text-gray-300 font-semibold'>서울에듀테크소프트랩</p>
                            <p>주소 : 서울특별시 광진구 능동로 120 신공학관 1F</p>
                            <p>Tel : 02-450-0698 / 02-450-0699</p>
                            <p>E-mail : seouledtech@konkuk.ac.kr</p>
                        </address>
                    </div>

                    {/* 오른쪽: 로고 + 저작권 */}
                    <div className='text-center md:text-right min-blank'>
                        <img src={logo} alt="로고" className="mx-auto md:ml-8 w-[180px] md:w-[240px] h-auto flex-shrink-0" />
                        {/* https://www.keris.or.kr/main/cm/cntnts/cntntsViewPop.do?cntntsId=1681 */}
                        <p className='pt-4 newText-xs font-light text-gray-300'>
                            Copyright © 2024 서울에듀테크소프트랩 <br className="md:hidden" />
                            All Right Reserved.
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