import PageComponent from "../common/PageComponent";
import SearchComponent from "../../components/demonstration/SearchComponent";
import React, { useEffect, useState } from "react";
import { getRegAdminSearch, getRegAdmin, updateRegstate } from "../../api/demApi";
import useMove from "../../hooks/useMove";
import { useSelector } from "react-redux";
const AdminRegComponent = () => {
    const isAdmin = useSelector((state) => state.loginState?.role === "ADMIN");
    // 권한 체크 useEffect
    useEffect(() => {
        if (!isAdmin) {
            alert("권한이 없습니다.");
            moveToPath("/");
        }

    }, []);
    const initState = {
        content: [],
        totalPages: 0,
        currentPage: 0,
    };

    const [search, setSearch] = useState("");
    const [type, setType] = useState("memId");
    const searchOptions = [
        { value: "memId", label: "아이디" },
        { value: "demName", label: "상품이름" },
        { value: "companyName", label: "기업명" },
    ];

    const [sortBy, setSortBy] = useState("applyAt");
    const [sort, setSort] = useState("desc");
    const { moveToPath } = useMove();
    const [regInfo, setRegInfo] = useState({ content: [] });
    const [current, setCurrent] = useState(0);
    const [pageData, setPageData] = useState(initState);
    const [statusFilter, setStatusFilter] = useState("");

    useEffect(() => {
        fetchData();
    }, [current, sortBy, sort, statusFilter]);

    const fetchData = () => {
        if (search && search.trim() !== "") {
            getRegAdminSearch(current, search, type, sortBy, sort, statusFilter).then((data) => {
                setRegInfo(data);
                setPageData(data);
            });
        } else {
            getRegAdmin(current, sort, sortBy, statusFilter).then((data) => {
                console.log(data);
                setRegInfo(data);
                setPageData(data);
            });
        }
    };

    const handleSortChange = (column) => {
        if (sortBy === column) {
            setSort((prev) => (prev === "asc" ? "desc" : "asc"));
        } else {
            setSortBy(column);
            setSort("asc");
        }
    };

    const onSearchClick = () => {
        setCurrent(0);
        fetchData();
    };

    const handleAcceptRental = async (demRegNum, state) => {
        try {
            alert("대여 신청이 수락되었습니다.");
            await updateRegstate(demRegNum, state);
            window.location.reload();
        } catch (error) {
            console.error(error);
            alert("대여 신청 수락 중 오류가 발생했습니다.");
        }
    };

    const handleRejectRental = async (demRevNum, state) => {
        try {
            alert("대여 신청이 거절되었습니다.");
            await updateRegstate(demRevNum, state);
            window.location.reload();
        } catch (error) {
            console.error(error);
            alert("대여 신청 거절 중 오류가 발생했습니다.");
        }
    };

    const getStateLabel = (state) => {
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
        <div className="w-full flex justify-center mt-6">
            <div className="bg-white rounded-lg shadow-lg w-full max-w-6xl p-6 flex flex-col">
                <h2 className="text-2xl font-semibold mb-4 text-center border-b pb-3">회원 정보</h2>

                <div className="mb-4 flex justify-start w-full max-w-md">
                    <SearchComponent
                        search={search}
                        setSearch={setSearch}
                        type={type}
                        setType={setType}
                        onSearchClick={onSearchClick}
                        searchOptions={searchOptions}
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

                                <th className="py-3 px-2 border-b flex flex-col items-center space-y-1 whitespace-nowrap w-[90px]">
                                    <span>신청상태</span>
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
                            {regInfo.content.map((data) => {
                                const mainImage = data.imageList?.find((img) => img.isMain === true);

                                return (
                                    <tr key={data._rowKey} className="border-b hover:bg-gray-50">
                                        <td className="py-2 px-2 whitespace-nowrap text-center">
                                            {mainImage ? (
                                                <img
                                                    src={`http://localhost:8090/view/${mainImage.imageUrl}`}
                                                    alt={data.demName}
                                                    onClick={() => moveToPath(`../detail/${data.demNum}`)}
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
                                        <td className="py-1 px-2 whitespace-nowrap text-center">{data.itemNum || "-"}</td>

                                        <td className="py-1 px-2 font-semibold whitespace-nowrap text-center">
                                            <div>{getStateLabel(data.state)}</div>
                                            {data.state === "WAIT" && (
                                                <div className="mt-1 whitespace-nowrap">
                                                    <button
                                                        className="inline-block min-w-[50px] px-2 py-0.5 bg-green-500 text-white rounded text-[10px] mr-1"
                                                        onClick={() => handleAcceptRental(data.demRegNum, "ACCEPT")}
                                                    >
                                                        수락
                                                    </button>
                                                    <button
                                                        className="inline-block min-w-[50px] px-2 py-0.5 bg-red-500 text-white rounded text-[10px]"
                                                        onClick={() => handleRejectRental(data.demRegNum, "REJECT")}
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
                            })}
                        </tbody>
                    </table>
                </div>

                <div className="flex justify-center mt-6">
                    <PageComponent totalPages={pageData.totalPages} current={current} setCurrent={setCurrent} />
                </div>
            </div>
        </div>
    );
}
export default AdminRegComponent;