import { useEffect } from "react";
import SubAboutHeader from "../../layouts/SubAboutHeader";

const DirectionComponent = () => {
    useEffect(() => {
        const checkKakaoLoaded = setInterval(() => {
            if (window.kakao && window.kakao.maps) {
                clearInterval(checkKakaoLoaded); // 더 이상 체크 안함
                initMap(); // 지도를 그리는 함수 실행
            }
        }, 300); // 0.3초마다 확인
    }, []);
    const address = "서울특별시 광진구 능동로 120 신공학관 1F";
    const initMap = () => {
        const container = document.getElementById("map");
        const options = {
            center: new window.kakao.maps.LatLng(37.5665, 126.9780),
            level: 3,
        };
        const map = new window.kakao.maps.Map(container, options);

        const geocoder = new window.kakao.maps.services.Geocoder();


        geocoder.addressSearch(address, function (result, status) {
            if (status === window.kakao.maps.services.Status.OK) {
                const coords = new window.kakao.maps.LatLng(result[0].y, result[0].x);

                const marker = new window.kakao.maps.Marker({
                    map: map,
                    position: coords,
                });

                const infowindow = new window.kakao.maps.InfoWindow({
                    content: `<div style="padding:5px;font-size:14px;">${address}</div>`,
                });
                infowindow.open(map, marker);

                map.setCenter(coords);
            } else {
                console.error("주소 검색 실패:", status);
            }
        });
    };

    return (
        <div className="">

            <SubAboutHeader />

            <div className="mx-auto max-w-screen-xl">
                <div className="text-xl font-bold mb-4">오시는 길</div>
                <div className="h-[400px] mt-4"
                    id="map"
                ></div>
            </div>
            <div className="mx-auto max-w-screen-xl">
                <div className="text-xl font-bold my-8">주소 및 연락처</div>
                <div className="my-4">{address}</div>
            </div>
            <div className="mx-auto max-w-screen-xl">
                <div className="text-xl font-bold my-8">교통 안내</div>
            </div>
        </div>
    );
};

export default DirectionComponent;
