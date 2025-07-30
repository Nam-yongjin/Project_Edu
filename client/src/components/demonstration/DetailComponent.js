import CalendarComponent from "./CalendarComponent";
import defaultImg from "../../assets/logo.png";
const DetailComponent = () => {

    return (
        <>
            <div>
                <div className="flex items-start gap-4">
                    <img src={defaultImg} alt="default" className="w-40 h-40 object-cover" />

                    <div className="space-y-1 mt-3">
                        <h3>상품명: 상품명 입니다.</h3>
                        <h3>제조사: 레노버입니다.</h3>
                        <h3>물품소개: 물품소개 입니다.</h3>
                    </div>
                </div>
            <div> 
                <h2>날짜 선택</h2> (신청기간은 연속으로 최소 30일부터 신청 가능합니다.(최대90일)) 
                <div className="w-10 h-5 rounded-full bg-purple-400 flex items-center justify-center border-2 border-black">불가</div>
                <div className="w-10 h-5 rounded-full bg-red-400 flex items-center justify-center border-2 border-black">가능</div>
                <div className="w-10 h-5 rounded-full bg-blue-400 flex items-center justify-center border-2 border-black">선택</div>
            </div>
            </div>
            <CalendarComponent />
        </>
    );
}
export default DetailComponent;