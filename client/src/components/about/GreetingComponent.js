import { useEffect, useState } from "react";
import logo from "../../assets/logo.png";

const GreetingComponent = () => {
    const [greetText, setGreetText] = useState("");

    useEffect(() => {
        fetch('/terms/greet.txt')
            .then((res) => res.text())
            .then((text) => setGreetText(text))
            .catch((error) => {
                setGreetText("인사말을 불러올 수 없습니다.");
            });
    }, []);

    return (
        <div className="">
            <div className="">
                <img
                    src={logo}
                    alt="로고"
                    className="float-right w-1/2"
                />
                {/* https://www.keris.or.kr/main/cm/cntnts/cntntsViewPop.do?cntntsId=1681 */}
            </div>
            <div className="mx-auto max-w-screen-xl my-10">
                <div className="text-2xl font-bold mb-4">인사말</div>
                <div className="text-lg py-4" style={{ whiteSpace: 'pre-wrap' }}>{greetText}</div>
            </div>

        </div>
    );
};
export default GreetingComponent;