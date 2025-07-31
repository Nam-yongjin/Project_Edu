import { useEffect } from "react";

const DirectionComponent = () => {
    useEffect(() => {
        const loadKakaoMap = () => {
            if (!window.kakao) {
                const script = document.createElement("script");
                script.src =
                    "//dapi.kakao.com/v2/maps/sdk.js?appkey=f8bcf058559032e649a2e99b5c9e4806&libraries=services";
                script.async = true;
                script.onload = initMap;
                document.head.appendChild(script);
            } else {
                initMap();
            }
        };

        const initMap = () => {
            const container = document.getElementById("map");
            const options = {
                center: new window.kakao.maps.LatLng(37.5665, 126.9780),
                level: 3,
            };
            const map = new window.kakao.maps.Map(container, options);

            const geocoder = new window.kakao.maps.services.Geocoder();

            const address = "서울특별시 광진구 능동로 120 신공학관 1F";

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

        loadKakaoMap();
    }, []);

    return (
        <div>
            <h2>오시는 길</h2>
            <div
                id="map"
                style={{ width: "100%", height: "400px", marginTop: "1rem" }}
            ></div>
        </div>
    );
};

export default DirectionComponent;
