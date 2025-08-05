import { useEffect, useRef, useState } from "react";
import { useLocation } from "react-router-dom";
import SubAboutHeader from "../../layouts/SubAboutHeader";
import defaultImage from "../../assets/default.jpg";

const DirectionComponent = () => {
    const location = useLocation();
    const mapInitialized = useRef(false);

    const addr = "건국대학교 서울캠퍼스";
    const addrDetail = "서울특별시 광진구 능동로 120 신공학관 1F";
    const tel = "Tel. 02-123-0698,9";
    const Latitude = 37.540404735269824;
    const Longitude = 127.07935535096505;

    const [mapError, setMapError] = useState(false);

    useEffect(() => {
        const isDirectionPage = location.pathname === "/about/direction";
        if (!isDirectionPage || mapInitialized.current) return;

        mapInitialized.current = true;

        const loadKakaoMapScript = () => {
            return new Promise((resolve, reject) => {
                if (window.kakao && window.kakao.maps) {
                    resolve(true);
                    return;
                }

                const script = document.createElement("script");
                script.src = `https://dapi.kakao.com/v2/maps/sdk.js?appkey=${process.env.REACT_APP_KAKAOMAP_API_KEY}&autoload=false`;
                script.async = true;
                script.onload = () => resolve(true);
                script.onerror = () => reject("Kakao Map load error");
                document.head.appendChild(script);
            });
        };

        const initMap = () => {
            try {
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
            } catch (error) {
                setMapError(true);
            }
        };

        loadKakaoMapScript()
            .then(() => {
                if (window.kakao && window.kakao.maps && window.kakao.maps.load) {
                    window.kakao.maps.load(initMap);
                } else {
                    throw new Error("Kakao map object not available");
                }
            })
            .catch(error => {
                setMapError(true);
            });
    }, [location]);

    return (
        <div>
            <SubAboutHeader />

            <div className="mx-auto max-w-screen-xl my-10">
                <div className="text-2xl font-bold mb-8">오시는 길</div>
                {mapError ? (
                    <img src={defaultImage} alt="지도 로딩 실패" className="h-[600px] w-full object-cover" />
                ) : (
                    <div id="map" className="h-[600px] mt-4" />
                )}
            </div>

            <div className="pb-4 mx-auto max-w-screen-xl border-b border-gray-300">
                <div className="text-2xl font-bold my-8">주소 및 연락처</div>
                <div className="my-4">
                    {addr}
                    <br />
                    {addrDetail}
                    <br />
                    {tel}
                </div>
            </div>
        </div>
    );
};

export default DirectionComponent;
