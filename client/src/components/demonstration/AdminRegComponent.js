import PageComponent from "../common/PageComponent";
import SearchComponent from "../../components/demonstration/SearchComponent";
import React, { useEffect, useState } from "react";
import { getRegAdminSearch, getRegAdmin, updateRegstate } from "../../api/adminApi";
import useMove from "../../hooks/useMove";
import { useSelector } from "react-redux";
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
        { value: "memId", label: "아이디" },
        { value: "demName", label: "상품명" },
        { value: "companyName", label: "기업명" },
    ];
    const [search, setSearch] = useState(""); // 검색 state 변수
    const [type, setType] = useState("memId"); // 검색 칼럼 state 변수 
    const [sortBy, setSortBy] = useState("applyAt"); // 정렬 칼럼 state 변수 
    const [sort, setSort] = useState("desc"); // 정렬 방식 
    const { moveToPath } = useMove(); // 다른 페이지로 이동 위한 함수
    const [regInfo, setRegInfo] = useState({ content: [] }); // 실증 신청 정보를 담는 state변수
    const [current, setCurrent] = useState(initState.currentPage); // 현재 페이지 정보를 담는 state 변수
    const [pageData, setPageData] = useState(initState); // 페이지 정보를 담는 state 변수
    const [statusFilter, setStatusFilter] = useState(""); // status값으로 필터링 위한 state 변수

    useEffect(() => { // 현재 페이지, 정렬 칼럼, 정렬 방식, 상태값이 변하면 리랜더링
        fetchData();
    }, [current, sortBy, sort, statusFilter]);

    const fetchData = () => { // fetchData 함수 실행 시, 백으로 부터 페이지 객체를 받아옴
        if (search && search.trim() !== "") { // 검색어가 존재할 경우,
            getRegAdminSearch(current, search, type, sortBy, sort, statusFilter).then((data) => {
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
            setSort((prev) => (prev === "asc" ? "desc" : "asc"));
        } else { // 다른 칼럼을 정렬 햇을 경우, 칼럼 및 정렬 상태 변경
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
            await updateRegstate(demRegNum, state);
            window.location.reload();
        } catch (error) {
            console.error(error);
            alert("실증 신청 처리 중 오류가 발생했습니다.");
        }
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

    return (

        <div className="max-w-screen-xl mx-auto my-10 ">
            <div className="newText-2xl min-blank font-bold mb-4">실증 신청 관리</div>

            <div className="mb-4 flex justify-start w-full max-w-md">
                <SearchComponent
                    search={search} // 검색어
                    setSearch={setSearch} // 검색어 set
                    type={type} // 검색어 칼럼
                    setType={setType} // 검색어 칼럼 set
                    onSearchClick={onSearchClick} // 검색어 클릭 함수
                    searchOptions={searchOptions} // search compnent에게 전달할 option의 select 객체
                />
            </div>

            <div className="overflow-x-auto">
                <table className="min-w-full border-collapse table-fixed">
                    <thead className="bg-gray-100 text-gray-700 uppercase text-sm">
                        <tr>
                            <th className="py-3 px-2 text-center rounded-tl-lg whitespace-nowrap w-[70px]">이미지</th>
                            <th
                                className="py-3 px-2 border-b cursor-pointer select-none whitespace-nowrap w-[100px] text-center"
                                onClick={() => handleSortChange("memId")}
                            >
                                아이디
                            </th>
                            <th className="py-3 px-2 border-b whitespace-nowrap w-[110px] text-center">전화번호</th>
                            <th className="py-3 px-2 border-b whitespace-nowrap w-[150px] text-center truncate">주소</th>
                            <th className="py-3 px-2 border-b whitespace-nowrap w-[120px] text-center truncate">기업명</th>
                            <th className="py-3 px-2 border-b whitespace-nowrap w-[130px] text-center truncate">상품명</th>
                            <th className="py-3 px-2 border-b whitespace-nowrap w-[80px] text-center">상품 갯수</th>

                            <th className="py-3 px-2 border-b text-center w-[90px]">
                                <div className="mb-1">신청상태</div>
                                <select
                                    value={statusFilter}
                                    onChange={(e) => {
                                        setStatusFilter(e.target.value);
                                        setCurrent(0);
                                    }}
                                    className="border rounded px-1 text-xs w-full"
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
                                        className="cursor-pointer text-center select-none py-3 px-2 whitespace-nowrap w-[100px]"
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
                        </tr>
                    </thead>

                    <tbody className="text-gray-600 text-xs">
                        {regInfo.content.length === 0 ? (
                            <tr>
                                <td colSpan={10} className="text-center">
                                    <p className="text-gray-500 text-lg mt-20">등록된 신청이 없습니다.</p>
                                </td>
                            </tr>
                        ) : (
                            regInfo.content.map((data) => {
                                const mainImage = data.imageList?.find((img) => img.isMain);

                                return (
                                    <tr key={data.demRegNum} className="border-b hover:bg-gray-50">
                                        <td className="py-2 px-2 whitespace-nowrap text-center">
                                            {mainImage ? (
                                                <img
                                                    src={`http://localhost:8090/view/${mainImage.imageUrl}`}
                                                    alt={data.demName}
                                                    onClick={() => moveToPath(`../../demonstration/detail/${data.demNum}`)}
                                                    className="w-16 h-16 object-contain rounded-md shadow-sm hover:scale-105 transition-transform cursor-pointer mx-auto"
                                                />
                                            ) : (
                                                <div className="w-16 h-16 flex items-center justify-center bg-gray-100 text-gray-400 rounded-md mx-auto text-[10px]">
                                                    이미지 없음
                                                </div>
                                            )}
                                        </td>
                                        <td className="py-1 px-2 whitespace-nowrap text-center">{data.memId}</td>
                                        <td className="py-1 px-2 whitespace-nowrap text-center">{data.phone || "-"}</td>
                                        <td className="py-1 px-2 whitespace-nowrap text-center truncate max-w-[120px]" title={data.addr}>
                                            {data.addr || "-"}
                                        </td>
                                        <td className="py-1 px-2 whitespace-nowrap text-center truncate max-w-[120px]" title={data.companyName}>
                                            {data.companyName || "-"}
                                        </td>
                                        <td className="py-1 px-2 whitespace-nowrap text-center truncate max-w-[130px]" title={data.demName}>
                                            {data.demName || "-"}
                                        </td>
                                        <td className="py-1 px-2 whitespace-nowrap text-center">{data.itemNum ?? "-"}</td>

                                        <td className="py-1 px-2 font-semibold whitespace-nowrap text-center">
                                            <div>{getStateLabel(data.state)}</div>
                                            {data.state === "WAIT" && (
                                                <div className="mt-1 whitespace-nowrap">
                                                    <button
                                                        className="inline-block min-w-[50px] px-2 py-0.5 bg-green-500 text-white rounded text-[10px] mr-1"
                                                        onClick={() => handleRental(data.demRegNum, "ACCEPT")}
                                                    >
                                                        수락
                                                    </button>
                                                    <button
                                                        className="inline-block min-w-[50px] px-2 py-0.5 bg-red-500 text-white rounded text-[10px]"
                                                        onClick={() => handleRental(data.demRegNum, "REJECT")}
                                                    >
                                                        거절
                                                    </button>
                                                </div>
                                            )}
                                        </td>

                                        <td className="py-1 px-2 whitespace-nowrap text-center">
                                            {data.expDate ? new Date(data.expDate).toLocaleDateString() : "-"}
                                        </td>
                                        <td className="py-1 px-2 whitespace-nowrap text-center">
                                            {data.regDate ? new Date(data.regDate).toLocaleDateString() : "-"}
                                        </td>
                                    </tr>
                                );
                            })
                        )}
                    </tbody>
                </table>
            </div>

            <div className="flex justify-center mt-6">
                <PageComponent totalPages={pageData.totalPages} current={current} setCurrent={setCurrent} />
            </div>
        </div>
    );
}
export default AdminRegComponent;