import React, { useEffect, useState } from "react";
import PageComponent from "../common/PageComponent";
import SearchComponent from "../../components/demonstration/SearchComponent";
import { getResAdminSearch, getResAdmin, updateResState, updateReqState } from "../../api/demApi";
import useMove from "../../hooks/useMove";
import { useSelector } from "react-redux";
const AdminResComponent = () => {
    const isAdmin = useSelector((state) => state.loginState?.role === "ADMIN");
    const initState = {
        content: [],
        totalPages: 0,
        currentPage: 0,
    };

 useEffect(() => {
        if (!isAdmin) {
            alert("권한이 없습니다.");
            moveToPath("/");
        }

    }, []);

    const [search, setSearch] = useState("");
    const [type, setType] = useState("memId");
    const searchOptions = [
        { value: "memId", label: "아이디" },
        { value: "demName", label: "상품명" },
        { value: "schoolName", label: "학교명" },
    ];

    const [sortBy, setSortBy] = useState("applyAt");
    const [sort, setSort] = useState("desc");
    const { moveToPath } = useMove();
    const [resInfo, setResInfo] = useState({ content: [] });
    const [current, setCurrent] = useState(0);
    const [pageData, setPageData] = useState(initState);
    const [statusFilter, setStatusFilter] = useState("");

    useEffect(() => {
        fetchData();
    }, [current, sortBy, sort, statusFilter]);

    const fetchData = () => {
        if (search && search.trim() !== "") {
            getResAdminSearch(current, search, type, sortBy, sort, statusFilter).then((data) => {
                setResInfo(data);
                setPageData(data);
            });
        } else {
            getResAdmin(current, sort, sortBy, statusFilter).then((data) => {
                setResInfo(data);
                setPageData(data);
                console.log(data);
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

    const handleAcceptRental = async (demRevNum, state) => {
        try {
            alert("대여 신청이 수락되었습니다.");
            await updateResState(demRevNum, state);
            window.location.reload();
        } catch (error) {
            console.error(error);
            alert("대여 신청 수락 중 오류가 발생했습니다.");
        }
    };

    const handleRejectRental = async (demRevNum, state) => {
        try {
            alert("대여 신청이 거절되었습니다.");
            await updateResState(demRevNum, state);
            window.location.reload();
        } catch (error) {
            console.error(error);
            alert("대여 신청 거절 중 오류가 발생했습니다.");
        }
    };

    const handleAcceptRequest = async (demRevNum, state, type) => {
        try {
            alert("요청이 수락되었습니다.");
            await updateReqState(demRevNum, state, type);
            window.location.reload();
        } catch (error) {
            console.error(error);
            alert("요청 수락 중 오류가 발생했습니다.");
        }
    };

    const handleRejectRequest = async (demRevNum, state, type) => {
        try {
            alert("요청이 거절되었습니다.");
            await updateReqState(demRevNum, state, type);
            window.location.reload();
        } catch (error) {
            console.error(error);
            alert("요청 거절 중 오류가 발생했습니다.");
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

    // statusFilter 적용 + 요청별로 행 나누기
    // content 각 아이템에서 requestDTO 배열을 분리해 각각 독립 행으로 분리
    const flattenedContent = resInfo.content
        .filter((item) => (statusFilter ? item.state === statusFilter : true))
        .flatMap((item) => {
            if (item.requestDTO && item.requestDTO.length > 0) {
                return item.requestDTO.map((req, idx) => ({
                    ...item,
                    requestDTO: req,
                    _rowKey: `${item.demRevNum}-${idx}`, // 유니크 키용
                }));
            } else {
                // 요청이 없으면 requestDTO를 null 또는 빈 객체로 둠
                return [{ ...item, requestDTO: null, _rowKey: `${item.demRevNum}-noReq` }];
            }
        });

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
                                <th className="py-3 px-2 border-b whitespace-nowrap w-[120px] text-center truncate">학교명</th>
                                <th className="py-3 px-2 border-b whitespace-nowrap w-[130px] text-center truncate">신청상품명</th>
                                <th className="py-3 px-2 border-b whitespace-nowrap w-[80px] text-center">신청 갯수</th>

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

                                {[{ label: "시작일", value: "startDate" }, { label: "마감일", value: "endDate" }, { label: "등록일", value: "applyAt" }].map(
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
                                <th className="py-3 px-2 border-b text-center whitespace-nowrap min-w-[130px] w-[130px]">반납/연장 신청</th>
                            </tr>
                        </thead>

                        <tbody className="text-gray-600 text-xs">
                            {flattenedContent.map((member) => {
                                const mainImage = member.imageList?.find((img) => img.isMain === true);

                                return (
                                    <tr key={member._rowKey} className="border-b hover:bg-gray-50">
                                        <td className="py-2 px-2 whitespace-nowrap text-center">
                                            {mainImage ? (
                                                <img
                                                    src={`http://localhost:8090/view/${mainImage.imageUrl}`}
                                                    alt={member.demName}
                                                    onClick={() => moveToPath(`../detail/${member.demNum}`)}
                                                    className="w-16 h-16 object-contain rounded-md shadow-sm hover:scale-105 transition-transform cursor-pointer mx-auto"
                                                />
                                            ) : (
                                                <div className="w-16 h-16 flex items-center justify-center bg-gray-100 text-gray-400 rounded-md mx-auto text-[10px]">
                                                    이미지 없음
                                                </div>
                                            )}
                                        </td>

                                        <td className="py-1 px-2 whitespace-nowrap text-center">{member.memId}</td>
                                        <td className="py-1 px-2 whitespace-nowrap text-center">{member.phone || "-"}</td>
                                        <td className="py-1 px-2 whitespace-nowrap text-center truncate max-w-[120px]" title={member.addr}>
                                            {member.addr || "-"}
                                        </td>
                                        <td className="py-1 px-2 whitespace-nowrap text-center truncate max-w-[120px]" title={member.schoolName}>
                                            {member.schoolName || "-"}
                                        </td>
                                        <td className="py-1 px-2 whitespace-nowrap text-center truncate max-w-[130px]" title={member.demName}>
                                            {member.demName || "-"}
                                        </td>
                                        <td className="py-1 px-2 whitespace-nowrap text-center">{member.bitemNum || "-"}</td>

                                        <td className="py-1 px-2 font-semibold whitespace-nowrap text-center">
                                            <div>{getStateLabel(member.state)}</div>
                                            {member.state === "WAIT" && (
                                                <div className="mt-1 whitespace-nowrap">
                                                    <button
                                                        className="inline-block min-w-[50px] px-2 py-0.5 bg-green-500 text-white rounded text-[10px] mr-1"
                                                        onClick={() => handleAcceptRental(member.demRevNum, "ACCEPT")}
                                                    >
                                                        수락
                                                    </button>
                                                    <button
                                                        className="inline-block min-w-[50px] px-2 py-0.5 bg-red-500 text-white rounded text-[10px]"
                                                        onClick={() => handleRejectRental(member.demRevNum, "REJECT")}
                                                    >
                                                        거절
                                                    </button>
                                                </div>
                                            )}
                                        </td>

                                        <td className="py-1 px-2 whitespace-nowrap text-center">
                                            {member.startDate ? new Date(member.startDate).toLocaleDateString() : "-"}
                                        </td>
                                        <td className="py-1 px-2 whitespace-nowrap text-center">
                                            {member.endDate ? new Date(member.endDate).toLocaleDateString() : "-"}
                                        </td>
                                        <td className="py-1 px-2 whitespace-nowrap text-center">
                                            {member.applyAt ? new Date(member.applyAt).toLocaleDateString() : "-"}
                                        </td>

                                        <td className="py-1 px-2 whitespace-nowrap text-center">
                                            {member.requestDTO && member.requestDTO.state === "WAIT" ? (
                                                <>
                                                    <div className="font-semibold mb-1 whitespace-nowrap text-xs">
                                                        {member.requestDTO.type === "EXTEND" && "연장 신청"}
                                                        {member.requestDTO.type === "RENTAL" && "반납 신청"}
                                                    </div>
                                                     <div className="font-semibold mb-1 whitespace-nowrap text-xs">
                                                        {member.requestDTO.type === "EXTEND"?member.requestDTO.updateDate:<></>}
                                                    </div>
                                                    <div className="whitespace-nowrap">
                                                        <button
                                                            className="inline-block min-w-[50px] px-2 py-0.5 bg-green-500 text-white rounded text-[10px] mr-1"
                                                            onClick={() => handleAcceptRequest(member.demRevNum, "ACCEPT", member.requestDTO.type)}
                                                        >
                                                            수락
                                                        </button>
                                                        <button
                                                            className="inline-block min-w-[50px] px-2 py-0.5 bg-red-500 text-white rounded text-[10px]"
                                                            onClick={() => handleRejectRequest(member.demRevNum, "REJECT", member.requestDTO.type)}
                                                        >
                                                            거절
                                                        </button>
                                                    </div>
                                                </>
                                            ) : member.requestDTO && member.requestDTO.state === "ACCEPT" ? (
                                                "처리 완료"
                                            ) : (
                                                ""
                                            )}
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
};

export default AdminResComponent;
