import { Link, useLocation } from "react-router-dom";
import about from "../assets/about.png";

const SubAboutHeader = () => {
    const location = useLocation();

    return (
         <div className="bg-green-100 w-full h-[300px] pt-8 pb-8"> 
            <div className="max-w-screen-xl mx-auto ">
                <div className="min-blank flex items-center justify-between">
                    <div className="newText-4xl font-bold text-gray-800">
                        소프트랩 소개
                    </div>
                    <div className="w-1/4">
                        <img src={about} className="w-[240px]"/>
                    </div>
                </div>
                
                <div className="newText-lg min-blank border-b-2 border-blue-500 pb-4">
                    <ul className="flex">
                        <li className="mr-6 pb-2">
                            <Link 
                                to="/about/greeting" 
                                className={`font-bold text-gray-700 hover:text-blue-400 active:text-blue-600${
                                    location.pathname.includes("/about/greeting") ? "text-blue-600 border-b-2 border-blue-600 active:text-blue-600" : ""
                                }`}
                            >
                                인사말
                            </Link>
                        </li>
                        <li className="mr-6 pb-2">
                            <Link 
                                to="/about/business" 
                                className={`font-bold text-gray-700 hover:text-blue-400 active:text-blue-600${
                                    location.pathname.includes("/about/business") ? "text-blue-600 border-b-2 border-blue-600 active:text-blue-600" : ""
                                }`}
                            >
                                사업소개
                            </Link>
                        </li>
                        <li className="mr-6 pb-2">
                            <Link 
                                to="/about/direction"
                                className={`font-bold text-gray-700 hover:text-blue-400 active:text-blue-600${
                                    location.pathname.includes("/about/direction") ? "text-blue-600 border-b-2 border-blue-600 active:text-blue-600" : ""
                                }`}
                            >
                                오시는 길
                            </Link>
                        </li>
                    </ul>
                </div>
            </div>
        </div>
    );
};

export default SubAboutHeader;