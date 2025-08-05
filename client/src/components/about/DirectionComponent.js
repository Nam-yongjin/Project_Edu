import { useEffect } from "react";
import SubAboutHeader from "../../layouts/SubAboutHeader";

const DirectionComponent = () => {
    const addr = "건국대학교 서울캠퍼스";
    const addrDetail = "서울특별시 광진구 능동로 120 신공학관 1F";
    const tel = "Tel. 02-450-0698,9";
    const Latitude = 37.540404735269824;
    const Longitude = 127.07935535096505;

    useEffect(() => {
        const initMap = () => {
            if (!window.kakao || !window.kakao.maps) {
                console.error("카카오 맵 객체가 없습니다.");
                return;
            }

            const container = document.getElementById("map");
            const options = {
                center: new window.kakao.maps.LatLng(Latitude, Longitude),
                level: 3,
            };

            const map = new window.kakao.maps.Map(container, options);

            const marker = new window.kakao.maps.Marker({
                position: new window.kakao.maps.LatLng(Latitude, Longitude),
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
                <div className="text-2xl font-bold mb-8">오시는 길</div>
                <div id="map" className="h-[600px] mt-4" />
            </div>

            <div className="pb-4 mx-auto max-w-screen-xl border-b border-gray-300">
                <div className="text-2xl font-bold my-8">주소 및 연락처</div>
                <div className="my-4">{addr}<br/>{addrDetail}<br/>{tel}</div>
            </div>

            <div className="pb-4 mx-auto max-w-screen-xl mb-10 ">
                <div className="text-2xl font-bold my-8">교통 안내</div>
            </div>
        </div>
    );
};

export default DirectionComponent;
