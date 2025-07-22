import { Link } from "react-router-dom";
import { getKakaoLoginLink } from "../../api/kakaoApi";
import kakaoLogo from '../../assets/icon-kakao-talk.png'

const KakaoLoginComponent = () => {
    const link = getKakaoLoginLink()
    return (
        <div className="flex flex-col">
            <div className="text-center text-blue-500">로그인시 자동 가입처리 됩니다</div>
            <div className="flex justify-center	w-full">
                <div className="m-6">
                    <Link to={link}><img src={kakaoLogo}  /></Link>
                    {/* <a href="https://www.flaticon.com/kr/free-icons/-" title="카카오 톡 아이콘">카카오 톡 아이콘 제작자: Fathema Khanom - Flaticon</a> */}
                </div>
            </div>
        </div>
    )
}
export default KakaoLoginComponent
