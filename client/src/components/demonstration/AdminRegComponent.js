import PageComponent from "../common/PageComponent";
import SearchComponent from "../../components/demonstration/SearchComponent";
import React, { useEffect, useState } from "react";
import { getRegAdminSearch, getRegAdmin, updateRegstate } from "../../api/adminApi";
import { delDem } from "../../api/demApi";
import { useSelector } from "react-redux";
import defaultImage from '../../assets/default.jpg';
import useMove from "../../hooks/useMove";
const AdminRegComponent = () => {
    const isAdmin = useSelector((state) => state.loginState?.role === "ADMIN"); // 권한이 admin인지 확인

    useEffect(() => { // 권한 체크 useEffect
        if (!isAdmin) {
            alert("권한이 없습니다.");
            moveToPath("/");
        }
    }, [isAdmin]);

    const initState = { // 페이지 초기화 객체
        totalPages: 0,
        currentPage: 0,
    };

    const searchOptions = [ // search compnent에게 전달할 option의 select 객체
        { value: "total", label: "전체" },
        { value: "memId", label: "아이디" },
        { value: "demName", label: "물품명" },
        { value: "companyName", label: "기업명" },
    ];
    const [search, setSearch] = useState(""); // 검색 state 변수
    const [type, setType] = useState("total"); // 검색 칼럼 state 변수 
    const [sortBy, setSortBy] = useState("regDate"); // 정렬 칼럼 state 변수 
    const [sort, setSort] = useState("desc"); // 정렬 방식 
    const { moveToPath } = useMove(); // 다른 페이지로 이동 위한 함수
    const [regInfo, setRegInfo] = useState({ content: [] }); // 실증 신청 정보를 담는 state변수
    const [current, setCurrent] = useState(initState.currentPage); // 현재 페이지 정보를 담는 state 변수
    const [pageData, setPageData] = useState(initState); // 페이지 정보를 담는 state 변수
    const [statusFilter, setStatusFilter] = useState(""); // status값으로 필터링 위한 state 변수

    // 체크박스 관련 상태
    const [selectedItems, setSelectedItems] = useState([]);
    const [isAllSelected, setIsAllSelected] = useState(false);

    useEffect(() => { // 현재 페이지, 정렬 칼럼, 정렬 방식, 상태값이 변하면 리랜더링
        fetchData();
    }, [current, sortBy, sort, statusFilter]);

    // 데이터가 변경될 때마다 선택된 항목들을 초기화
    useEffect(() => {
        setSelectedItems([]);
        setIsAllSelected(false);
    }, [regInfo]);

    const fetchData = () => { // fetchData 함수 실행 시, 백으로 부터 페이지 객체를 받아옴
        if (search && search.trim() !== "") { // 검색어가 존재할 경우,
            getRegAdminSearch(current, search, type, sortBy, sort, statusFilter).then((data) => {
                console.log(data);
                setRegInfo(data);
                setPageData(data);
            });
        } else { // 검색어가 존재 하지 않을 경우,
            getRegAdmin(current, sort, sortBy, statusFilter).then((data) => {
                console.log(data);
                setRegInfo(data);
                setPageData(data);
            });
        }
    };

    const handleSortChange = (column) => { // 정렬 시, 해당 칼럼값으로 변경하며, 정렬 상태 토글
        if (sortBy === column) { // 같은 것에 대해 정렬 햇을 경우, 정렬상태만 토글
            setCurrent(0);
            setSort((prev) => (prev === "asc" ? "desc" : "asc"));
        } else { // 다른 칼럼을 정렬 햇을 경우, 칼럼 및 정렬 상태 변경
            setCurrent(0);
            setSortBy(column);
            setSort("asc");
        }
    };

    const onSearchClick = () => { // 검색어 클릭 시, 현재 페이지를 0으로 하고 fetchData 실행
        setCurrent(0);
        fetchData();
    };

    const handleRental = async (demRegNum, state) => { // 실증 신청처리
        try {
            if (state === "ACCEPT") {
                alert("실증 신청이 수락되었습니다.");
            }
            else if (state === "REJECT") {
                alert("실증 신청이 거부되었습니다.");
            }
            await updateRegstate([demRegNum], state);
            window.location.reload();
        } catch (error) {
            console.error(error);
            alert("실증 신청 처리 중 오류가 발생했습니다.");
        }
    };

    // 전체 선택/해제
    const handleSelectAll = (checked) => {
        setIsAllSelected(checked);
        if (checked) {
            const waitItems = regInfo.content
                .filter(item => item.state === "WAIT")
                .map(item => item.demRegNum);
            setSelectedItems(waitItems);
        } else {
            setSelectedItems([]);
        }
    };

    // 개별 항목 선택/해제
    const handleItemSelect = (demRegNum, checked) => {
        let newSelectedItems;
        if (checked) {
            newSelectedItems = [...selectedItems, demRegNum];
        } else {
            newSelectedItems = selectedItems.filter(item => item !== demRegNum);
        }
        setSelectedItems(newSelectedItems);

        // 전체 선택 체크박스 상태 업데이트
        const waitItems = regInfo.content.filter(item => item.state === "WAIT");
        const allWaitItemsSelected = waitItems.every(item => newSelectedItems.includes(item.demRegNum));
        setIsAllSelected(allWaitItemsSelected && waitItems.length > 0);
    };

    // 선택된 항목들 일괄 거부
    const handleCheckedReject = () => {
        updateRegstate(selectedItems, "REJECT");
        alert("실증 신청이 수락되었습니다.");
        window.location.reload();
    };

    // 선택된 항목들 일괄 수락
    const handleCheckedAccept = () => {
        if (selectedItems.length === 0) return;

        updateRegstate(selectedItems, "ACCEPT");
        alert("실증 신청이 수락되었습니다.");
        window.location.reload();

    };

    const getStateLabel = (state) => { // 상태값을 한국어로 바꾸어서 보여주는 함수
        switch (state) {
            case "ACCEPT":
                return "수락";
            case "REJECT":
                return "거부";
            case "WAIT":
                return "대기";
            case "CANCEL":
                return "취소";
            case "EXPIRED":
                return "만료"
            default:
                return state || "-";
        }
    };

    // 대기 상태인 항목들의 개수
    const waitItemsCount = regInfo.content.filter(item => item.state === "WAIT").length;

    return (
        <>
            <div className="max-w-screen-xl mx-auto my-10">
                <div className="min-blank">
                    <div className="mx-auto text-center">
                        <div className="newText-3xl font-bold ">실증 등록 관리</div>
                        <div className="py-2 flex justify-center">
                            <SearchComponent
                                search={search} // 검색어
                                setSearch={setSearch} // 검색어 set
                                type={type} // 검색어 칼럼
                                setType={setType} // 검색어 칼럼 set
                                onSearchClick={onSearchClick} // 검색어 클릭 함수
                                searchOptions={searchOptions} // search compnent에게 전달할 option의 select 객체
                            />
                        </div>
                    </div>
                    <p className="text-gray-700 my-3 newText-base px-4 py-2 rounded-md inline-block">
                        전체 <span className="font-bold text-blue-600">{pageData.totalElements}</span>건의 등록내역이 있습니다.
                    </p>
                    <div className="page-shadow overflow-x-auto">
                        <table className="w-full">
                            <thead className="bg-gray-100 text-gray-700 newText-base border border-gray-300">
                                <tr className="newText-base whitespace-nowrap">
                                    <th className="w-[5%]">
                                        {waitItemsCount > 0 && (
                                            <input
                                                type="checkbox"
                                                checked={isAllSelected}
                                                onChange={(e) => handleSelectAll(e.target.checked)}
                                                className="w-4 h-4"
                                            />
                                        )}
                                    </th>
                                    <th className="w-[8%]">이미지</th>
                                    <th className="w-[8%]">아이디</th>
                                    <th className="w-[14%]">전화번호</th>
                                    <th className="w-[14%]">주소</th>
                                    <th className="w-[10%]">기업명</th>
                                    <th className="w-[10%]">물품명</th>
                                    <th className="w-[10%]">물품 갯수</th>

                                    <th className="w-[10%]">
                                        <div className="mb-1">신청상태</div>
                                        <select
                                            value={statusFilter}
                                            className="input-focus"
                                            onChange={(e) => {
                                                setStatusFilter(e.target.value);
                                                setCurrent(0);
                                            }}
                                        >
                                            <option value="">전체</option>
                                            <option value="REJECT">거부</option>
                                            <option value="ACCEPT">수락</option>
                                            <option value="WAIT">대기</option>
                                            <option value="CANCEL">취소</option>
                                            <option value="EXPIRED">만료</option>
                                        </select>
                                    </th>

                                    {[{ label: "만료일", value: "expDate" }, { label: "등록일", value: "regDate" }].map(
                                        ({ label, value }) => (
                                            <th
                                                key={value}
                                                onClick={() => handleSortChange(value)}
                                                className="cursor-pointer w-[8%]"
                                            >
                                                <div className="flex items-center justify-center space-x-1">
                                                    <span>{label}</span>
                                                    <div className="flex flex-col">
                                                        <span className={`text-[10px] leading-none ${sortBy === value && sort === "asc" ? "text-black" : "text-gray-300"}`}>
                                                            ▲
                                                        </span>
                                                        <span className={`text-[10px] leading-none ${sortBy === value && sort === "desc" ? "text-black" : "text-gray-300"}`}>
                                                            ▼
                                                        </span>
                                                    </div>
                                                </div>
                                            </th>
                                        )
                                    )}
                                    <th th className="w-[10%]">수정/삭제</th>
                                </tr>
                            </thead>

                            <tbody className="text-gray-600">
                                {regInfo.content.length === 0 ? (
                                    <tr>
                                        <td colSpan={11} className="text-center">
                                            <p className="text-gray-500 newText-3xl mt-20 min-h-[300px]">등록된 신청이 없습니다.</p>
                                        </td>
                                    </tr>
                                ) : (
                                    regInfo.content.map((data) => {
                                        const mainImage = data.imageList?.find((img) => img.isMain);
                                        const dataState = data.state;
                                        const isWaitState = dataState === "WAIT";

                                        return (
                                            <tr key={data.demRegNum} className={`hover:bg-gray-50 newText-sm text-center whitespace-nowrap border border-gray-300 ${dataState === "CANCEL" ? "bg-gray-100 text-gray-400" : "hover:bg-gray-50"}`}>
                                                <td>
                                                    {isWaitState && (
                                                        <input
                                                            type="checkbox"
                                                            checked={selectedItems.includes(data.demRegNum)}
                                                            onChange={(e) => handleItemSelect(data.demRegNum, e.target.checked)}
                                                            className="w-4 h-4"
                                                        />
                                                    )}
                                                </td>
                                                <td>
                                                    {mainImage ? (
                                                        <img
                                                            src={`http://localhost:8090/view/${mainImage.imageUrl}`}
                                                            alt={data.demName}
                                                            onClick={() => moveToPath(`../../demonstration/detail/${data.demNum}`)}
                                                            className="w-20 h-20 rounded-md hover:scale-105 transition-transform cursor-pointer"
                                                        />
                                                    ) : (
                                                        <img
                                                            src={defaultImage}
                                                            alt="default"
                                                            className="w-20 h-20 rounded-md hover:scale-105 transition-transform cursor-pointer"
                                                        />
                                                    )}
                                                </td>
                                                <td title={data.memId}>{data.memId}</td>
                                                <td title={data.title}>{data.phone || "-"}</td>
                                                <td title={data.addr + " " + data.addrDetail} className="truncate max-w-[100px]" >
                                                    {data.addr + " " + data.addrDetail || "-"}
                                                </td>
                                                <td title={data.companyName} className="truncate max-w-[100px]">
                                                    {data.companyName || "-"}
                                                </td>
                                                <td title={data.demName} className="truncate max-w-[100px]">
                                                    {data.demName || "-"}
                                                </td>
                                                <td>{data.itemNum ?? "-"}</td>

                                                <td>
                                                    <div>{getStateLabel(data.state)}</div>
                                                    {data.state === "WAIT" ? (
                                                        <div>
                                                            <button
                                                                className="inline-block green-button text-[10px] px-2 py-1 leading-none mr-1"
                                                                onClick={() => handleRental(data.demRegNum, "ACCEPT")}
                                                            >
                                                                수락
                                                            </button>

                                                            <button
                                                                className="inline-block nagative-button text-[10px] px-2 py-1 leading-none"
                                                                onClick={() => handleRental(data.demRegNum, "REJECT")}
                                                            >
                                                                거절
                                                            </button>
                                                        </div>
                                                    ) : <></>}
                                                </td>

                                                <td>
                                                    {data.expDate ? new Date(data.expDate).toLocaleDateString() : "-"}
                                                </td>
                                                <td>
                                                    {data.regDate ? new Date(data.regDate).toLocaleDateString() : "-"}
                                                </td>
                                                <div className="flex flex-col gap-2">
                                                    <td>
                                                        {data.state === "ACCEPT" ? (
                                                            <button
                                                                className="positive-button flex-1"
                                                                onClick={() => moveToPath(`/demonstration/update/${data.demNum}`)}
                                                            >
                                                                수정하기
                                                            </button>
                                                        ) : (
                                                            ""
                                                        )}
                                                    </td>
                                                    <td>
                                                        {data.state === "ACCEPT" ? (
                                                            <button
                                                                className="nagative-button flex-1"
                                                                onClick={async () => {
                                                                    try {
                                                                        await delDem([data.demNum]); // 배열로 전달
                                                                        alert("삭제되었습니다.");
                                                                        window.location.reload(); // 페이지 새로고침
                                                                    } catch (error) {
                                                                        console.error(error);
                                                                        alert("삭제 실패");
                                                                    }
                                                                }}
                                                            >
                                                                삭제하기
                                                            </button>
                                                        ) : (
                                                            ""
                                                        )}
                                                    </td>
                                            </div>
                                            </tr>
                            );
                                    })
                                )}
                        </tbody>
                    </table>

                </div>
                {/* 우측 하단 일괄 처리 버튼들 */}
                <div className="flex justify-end mt-4 mr-15">
                    <button
                        disabled={selectedItems.length === 0}
                        className={`px-4 py-2 rounded mr-2 ${selectedItems.length > 0
                            ? "positive-button"
                            : "disable-button"
                            }`}
                        onClick={handleCheckedAccept}
                    >
                        신청 수락 ({selectedItems.length})
                    </button>
                    <button
                        disabled={selectedItems.length === 0}
                        className={`px-4 py-2 rounded ${selectedItems.length > 0
                            ? "nagative-button"
                            : "disable-button"
                            }`}
                        onClick={handleCheckedReject}
                    >
                        신청 거부 ({selectedItems.length})
                    </button>
                </div>
            </div>
        </div >

            <div className="flex justify-between items-center my-6">
                <div className="flex justify-center flex-1">
                    <PageComponent totalPages={pageData.totalPages} current={current} setCurrent={setCurrent} />
                </div>
            </div>
        </>
    );
}
export default AdminRegComponent;