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

            </div>
            <CalendarComponent />
        </>
    );
}
export default DetailComponent;