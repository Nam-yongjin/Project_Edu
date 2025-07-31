import defaultImg from "../../assets/logo.png";
import kakaoIcon from "../../assets/icon-kakao-talk.png";
import searchIcon from "../../assets/search.png";
import sideIcon from "../../assets/side.png";
import { useState, useEffect } from "react";
import { getList } from "../../api/demApi";
import PageComponent from "../common/PageComponent";
const ListComponent = () => {
    const initState = { // 페이지 초기화
        content: [], totalPages: 125, currentPage: 0
    }

    const [current, setCurrent] = useState(0)
    const [listData, setListData] = useState(initState)

    useEffect(() => {
        getList(current).then(data => {

            setListData(data)
        })
    }, [current])

    return (
        <div className="w-3/4">
            <div className="flex gap-2">
                <div className="flex-1 bg-blue-200 p-4 flex flex-col items-center justify-center gap-4 text-center">
                    <img src={defaultImg} alt="default" className="w-40 h-40 object-cover" />
                    <div>
                        장비명 : 장비<br />
                        개수 : 1개<br />
                        제조사: 제조사1
                    </div>
                    <button className="mt-auto bg-blue-600">대여 가능</button>
                </div>

                <div className="flex-1 bg-blue-200 text-center p-4">
                    <img src={kakaoIcon} alt="kakao" className="w-40 h-40 object-cover" />
                    <div>
                        장비명 : 장비2<br />
                        개수 : 2개<br />
                        제조사: 제조사2
                    </div>
                    <button className="mt-auto bg-blue-600">대여 가능</button>
                </div>
                <div className="flex-1 bg-blue-200 text-center p-4">
                    <img src={searchIcon} alt="search" className="w-40 h-40 object-cover" />
                    <div>
                        장비명 : 장비3<br />
                        개수 : 3개<br />
                        제조사: 제조사3
                    </div>
                    <button
                        disabled
                        className="mt-auto bg-gray-400 text-white px-4 py-2 rounded-md shadow hover:bg-gray-500 active:bg-gray-600 disabled:bg-gray-400 disabled:cursor-not-allowed disabled:hover:bg-gray-400 disabled:active:bg-gray-400"
                    >대여 불가능</button>
                </div>
                <div className="flex-1 bg-blue-200 text-center p-4">
                    <img src={sideIcon} alt="side" className="w-40 h-40 object-cover" />
                    <div>
                        장비명 : 장비4<br />
                        개수 : 4개<br />
                        제조사: 제조사4
                    </div>
                    <button className="mt-auto bg-blue-600 text-white px-4 py-2 rounded-md hover:bg-blue-700 active:bg-blue-800 shadow">대여 가능</button>
                </div>
            </div>
            <PageComponent totalPages={listData.totalPages} current={current} setCurrent={setCurrent}></PageComponent>
        </div>
    );
}
export default ListComponent;