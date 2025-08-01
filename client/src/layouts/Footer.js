import logo from '../assets/logo.png';
import { Link } from 'react-router-dom';

const Footer = () => {
    return(
        <footer className="mt-10 bg-gray-600 px-8 py-6 text-[14px]"> 
            <div className="max-w-screen-xl mx-auto flex justify-between items-start flex-wrap">
                {/* 왼쪽: 정책 + 주소 */}
                <div className='space-y-4 max-w-xl'>
                    {/* 정책 */}
                    <nav className='space-x-2 text-m text-white font-medium'>
                        {/* 페이지 링크 삽입해야 함 */}
                        <span className='hover:underline cursor-pointer'><Link to="">개인정보처리방침</Link></span>
                        <span>|</span>
                        <span className='hover:underline cursor-pointer'><Link to="">저작권보호정책</Link></span>
                        <span>|</span>
                        <span className='hover:underline cursor-pointer'><Link to="">이메일주소수집거부</Link></span>
                        <span>|</span>
                        <span className='hover:underline cursor-pointer'><Link to="/about/direction">오시는길</Link></span>
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
                    <img src={logo} alt="로고" className="ml-8 w-[240px] h-[100px] flex-shrink-0"/>
                    {/* https://www.keris.or.kr/main/cm/cntnts/cntntsViewPop.do?cntntsId=1681 */}
                    <p className='pt-5 text-xs font-light text-gray-300'>
                        Copyright © 2024 서울에듀테크소프트랩 All Right Reserved.
                    </p>
                </div>
            </div>          
        </footer>
            

    )
}
export default Footer