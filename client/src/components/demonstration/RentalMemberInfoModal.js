import React, { useEffect, useState } from "react";
import { getMemberInfoByDemNum } from "../../api/demApi";
import PageComponent from "../common/PageComponent";
import SearchComponent from "../../components/demonstration/SearchComponent";

const MemberInfoModal = ({ demNum, onClose }) => {
    const [search, setSearch] = useState("");
    const [type, setType] = useState("memId");
    const searchOptions = [
        { value: "memId", label: "회원ID" },
        { value: "demName", label: "신청상품명" },
        { value: "schoolName", label: "학교명" },
    ];

    const [sortBy, setSortBy] = useState("applyAt");
    const [sort, setSort] = useState("desc");

    const [memberInfo, setMemberInfo] = useState([]);
    const [loading, setLoading] = useState(true);
    const [current, setCurrent] = useState(0);
    const [totalPages, setTotalPages] = useState(1);

    const [statusFilter, setStatusFilter] = useState("");

    const statusOptions = [
        { value: "", label: "전체" },
        { value: "wait", label: "대기" },
        { value: "accept", label: "승인" },
        { value: "reject", label: "거절" },
        { value: "cancel", label: "취소" },
    ];

    useEffect(() => {
        if (!demNum) return;
        fetchPage(current, search, type, sortBy, sort);
    }, [demNum, current, sortBy, sort, search, type]);

    const fetchPage = (page, search, type, sortBy, sort) => {
        setLoading(true);
        getMemberInfoByDemNum(demNum, page, search, type, sortBy, sort)
            .then((data) => {
                setMemberInfo(data.content || []);
                setTotalPages(data.totalPages || 1);
                setLoading(false);
            })
            .catch(() => setLoading(false));
    };

    const handleSortChange = (field) => {
        if (sortBy === field) {
            setSort(sort === "asc" ? "desc" : "asc");
        } else {
            setSortBy(field);
            setSort("asc");
        }
        setCurrent(0);
    };

    const onSearchClick = () => {
        setCurrent(0);
    };

    // 상태 필터링
    const filteredMemberInfo = statusFilter
        ? memberInfo.filter((m) => m.state === statusFilter)
        : memberInfo;

    if (!demNum) return null;

    return (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
            <div className="bg-white rounded-lg shadow-lg w-full max-w-6xl p-6 max-h-[80vh] overflow-y-auto relative flex flex-col">
                <button
                    className="absolute top-4 right-4 text-gray-500 hover:text-gray-800 text-2xl font-bold"
                    onClick={onClose}
                    aria-label="Close modal"
                >
                    ×
                </button>

                <h2 className="text-2xl font-semibold mb-4 text-center border-b pb-3">
                    회원 정보
                </h2>

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

                {loading ? (
                    <p className="text-center text-gray-500 mt-20">불러오는 중...</p>
                ) : filteredMemberInfo.length === 0 ? (
                    <p className="text-center text-red-500 mt-20">회원 정보를 불러올 수 없습니다.</p>
                ) : (
                    <>
                        <div className="overflow-x-auto flex-grow">
                            <table className="min-w-full bg-white border border-gray-300 rounded-lg">
                                <thead className="bg-gray-100 text-gray-700 uppercase text-sm">
                                    <tr>
                                        <th
                                            className="py-3 px-4 border-b cursor-pointer select-none"
                                            onClick={() => handleSortChange("memId")}
                                        >
                                            <div className="flex justify-between items-center">
                                                <span>아이디</span>
                                                <span className="flex flex-col ml-1 text-xs">
                                                    <span className={`leading-none ${sortBy === "memId" && sort === "asc" ? "text-black" : "text-gray-300"}`}>▲</span>
                                                    <span className={`leading-none ${sortBy === "memId" && sort === "desc" ? "text-black" : "text-gray-300"}`}>▼</span>
                                                </span>
                                            </div>
                                        </th>
                                        <th className="py-3 px-4 border-b">전화번호</th>
                                        <th className="py-3 px-4 border-b">주소</th>
                                        <th
                                            className="py-3 px-4 border-b cursor-pointer select-none"
                                            onClick={() => handleSortChange("schoolName")}
                                        >
                                            <div className="flex justify-between items-center">
                                                <span>학교명</span>
                                                <span className="flex flex-col ml-1 text-xs">
                                                    <span className={`leading-none ${sortBy === "schoolName" && sort === "asc" ? "text-black" : "text-gray-300"}`}>▲</span>
                                                    <span className={`leading-none ${sortBy === "schoolName" && sort === "desc" ? "text-black" : "text-gray-300"}`}>▼</span>
                                                </span>
                                            </div>
                                        </th>
                                        <th className="py-3 px-4 border-b">신청상품명</th>

                                        {/* 신청상태 헤더 및 select 필터 */}
                                        <th className="py-3 px-4 border-b flex items-center space-x-2">
                                            <span>신청상태</span>
                                            <select
                                                value={statusFilter}
                                                onChange={(e) => {
                                                    setStatusFilter(e.target.value);
                                                    setCurrent(0);
                                                }}
                                                className="border rounded px-2 py-1 text-sm"
                                                aria-label="신청상태 필터"
                                            >
                                                {statusOptions.map((opt) => (
                                                    <option key={opt.value} value={opt.value}>
                                                        {opt.label}
                                                    </option>
                                                ))}
                                            </select>
                                        </th>

                                        <th
                                            className="py-3 px-4 border-b cursor-pointer select-none"
                                            onClick={() => handleSortChange("applyAt")}
                                        >
                                            <div className="flex justify-between items-center">
                                                <span>신청일</span>
                                                <span className="flex flex-col ml-1 text-xs">
                                                    <span className={`leading-none ${sortBy === "applyAt" && sort === "asc" ? "text-black" : "text-gray-300"}`}>▲</span>
                                                    <span className={`leading-none ${sortBy === "applyAt" && sort === "desc" ? "text-black" : "text-gray-300"}`}>▼</span>
                                                </span>
                                            </div>
                                        </th>

                                        <th
                                            className="py-3 px-4 border-b cursor-pointer select-none"
                                            onClick={() => handleSortChange("expDate")}
                                        >
                                            <div className="flex justify-between items-center">
                                                <span>반납예정일</span>
                                                <span className="flex flex-col ml-1 text-xs">
                                                    <span className={`leading-none ${sortBy === "expDate" && sort === "asc" ? "text-black" : "text-gray-300"}`}>▲</span>
                                                    <span className={`leading-none ${sortBy === "expDate" && sort === "desc" ? "text-black" : "text-gray-300"}`}>▼</span>
                                                </span>
                                            </div>
                                        </th>
                                    </tr>
                                </thead>
                                <tbody className="text-gray-600 text-sm">
                                    {filteredMemberInfo.map((member, idx) => (
                                        <tr key={member.demRevNum || idx} className="border-b hover:bg-gray-50">
                                            <td className="py-2 px-4">{member.memId}</td>
                                            <td className="py-2 px-4">{member.phone || "-"}</td>
                                            <td className="py-2 px-4">{member.addr || "-"}</td>
                                            <td className="py-2 px-4">{member.schoolName || "-"}</td>
                                            <td className="py-2 px-4">{member.demName || "-"}</td>
                                            <td className="py-2 px-4 font-semibold">{member.state || "-"}</td>
                                            <td className="py-2 px-4">{member.applyAt ? new Date(member.applyAt).toLocaleDateString() : "-"}</td>
                                            <td className="py-2 px-4">{member.endDate ? new Date(member.endDate).toLocaleDateString() : "-"}</td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        </div>

                        <div className="flex justify-center mt-4">
                            <PageComponent current={current} totalPages={totalPages} setCurrent={setCurrent} />
                        </div>
                    </>
                )}
            </div>
        </div>
    );
};

export default MemberInfoModal;
