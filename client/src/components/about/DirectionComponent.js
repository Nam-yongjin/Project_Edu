import { useEffect } from "react";
import SubAboutHeader from "../../layouts/SubAboutHeader";

const DirectionComponent = () => {
    const address = "서울특별시 광진구 능동로 120 신공학관 1F";

    useEffect(() => {
        const initMap = () => {
            if (!window.kakao || !window.kakao.maps) {
                console.error("카카오 맵 객체가 없습니다.");
                return;
            }

            const container = document.getElementById("map");
            const options = {
                center: new window.kakao.maps.LatLng(35.842453316043404, 128.59189023238966),
                level: 3,
            };

            const map = new window.kakao.maps.Map(container, options);

            const marker = new window.kakao.maps.Marker({
                position: new window.kakao.maps.LatLng(35.842453316043404, 128.59189023238966),
            });
            marker.setMap(map);

            const zoomControl = new window.kakao.maps.ZoomControl();
            map.addControl(zoomControl, window.kakao.maps.ControlPosition.RIGHT);
        };

        // SDK가 이미 로드된 상태라면 load 호출
        if (window.kakao && window.kakao.maps && window.kakao.maps.load) {
            window.kakao.maps.load(initMap);
        } else {
            console.error("Kakao SDK가 아직 로드되지 않았습니다.");
        }
    }, []);

    return (
        <div>
            <SubAboutHeader />

            <div className="mx-auto max-w-screen-xl my-10">
                <div className="text-xl font-bold mb-4">오시는 길</div>
                <div id="map" className="h-[400px] mt-4" />
            </div>

            <div className="mx-auto max-w-screen-xl">
                <div className="text-xl font-bold my-8">주소 및 연락처</div>
                <div className="my-4">{address}</div>
            </div>

            <div className="mx-auto max-w-screen-xl mb-10">
                <div className="text-xl font-bold my-8">교통 안내</div>
            </div>
        </div>
    );
};

export default DirectionComponent;
