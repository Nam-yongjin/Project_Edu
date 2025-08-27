import { Link } from "react-router-dom";
import { getKakaoLoginLink } from "../../api/kakaoApi";
import { getNaverLoginLink } from "../../api/naverApi"
import kakaoLogo from '../../assets/icon-kakao-talk.png';
import naverLogo from '../../assets/icon-naver.png';

const SocialLoginComponent = () => {
    const kakaoLink = getKakaoLoginLink();
    const naverLink = getNaverLoginLink();

    return (
        <div className="flex flex-col mt-8">
            <div className="newText-base text-center font-bold text-blue-600">로그인시 자동 가입</div>
            <div className="flex justify-center	w-full">
                <div className="m-3">
                    <Link to={kakaoLink}><img className="active:bg-gray-200" src={kakaoLogo} /></Link>
                    {/* <a href="https://www.flaticon.com/kr/free-icons/-" title="카카오 톡 아이콘">카카오 톡 아이콘 제작자: Fathema Khanom - Flaticon</a> */}
                </div>
                <div className="m-3">
                    <Link to={naverLink}><img className="w-[32px] h-[32px] active:bg-gray-200" src={naverLogo} /></Link>
                </div>
            </div>
        </div>
    );
};
export default SocialLoginComponent;
