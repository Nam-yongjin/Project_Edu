
const Footer = () => {
    return(
        <footer className="bg-gray-700 px-8 py-6 text-[14px]">
            <div className="flex justify-between items-start flex-wrap px-10">
                {/* 왼쪽: 정책 + 주소 */}
                <div className='space-y-4 max-w-xl'>
                    {/* 정책 */}
                    <nav className='space-x-2 text-m text-white font-medium'>
                        {/* 페이지 링크 삽입해야 함 */}
                        <a href="#" className='hover:underline cursor-pointer'>개인정보처리방침</a>
                        <span>|</span>
                        <a href="#" className='hover:underline cursor-pointer'>저작권보호정책</a>
                        <span>|</span>
                        <a href="#" className='hover:underline cursor-pointer'>이메일주소수집거부</a>
                        <span>|</span>
                        <a href="#" className='hover:underline cursor-pointer'>오시는 길</a>
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
                    {/* 저작권 */}
                    <p className='pt-40 text-xs font-light text-gray-300'>
                        Copyright © 2024 서울에듀테크소프트랩 All Right Reserved.
                    </p>
                </div>
            </div>          
        </footer>
            

    )
}
export default Footer