import React, { useEffect, useState } from "react";
import { getBorrowResInfoSearch, getBorrowResInfo } from "../../api/demApi";
import PageComponent from "../common/PageComponent";
import SearchComponent from "../../components/demonstration/SearchComponent";

const RentalMemberInfoModal = ({ demNum, onClose }) => {
  const initState = {
    content: [],
    totalPages: 0,
    currentPage: 0,
  };

  const [search, setSearch] = useState("");
  const [type, setType] = useState("memId");
  const searchOptions = [
    { value: "memId", label: "아이디" },
    { value: "demName", label: "상품명" },
    { value: "schoolName", label: "학교명" },
  ];

  const [sortBy, setSortBy] = useState("applyAt");
  const [sort, setSort] = useState("desc");

  const [memberInfo, setMemberInfo] = useState({ content: [] });
  const [current, setCurrent] = useState(0);
  const [pageData, setPageData] = useState(initState);
  const [statusFilter, setStatusFilter] = useState("");

  useEffect(() => {
    fetchData(current, search, type, sortBy, sort);
  }, [current, sortBy, sort, statusFilter]);

  const fetchData = () => {
    if (search && search.trim() !== "") {
      getBorrowResInfoSearch(demNum, current, search, type, sortBy, sort, statusFilter).then((data) => {
        setMemberInfo(data);
        setPageData(data);
      });
    } else {
      getBorrowResInfo(demNum, current, sort, sortBy, statusFilter).then((data) => {
        setMemberInfo(data);
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
    fetchData();
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

  // 상태 필터링
  const filteredMemberInfo = statusFilter
    ? memberInfo.content.filter((m) => m.state === statusFilter)
    : memberInfo.content;

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
      <div className="bg-white rounded-lg p-6 w-full max-w-7xl mx-auto min-h-[400px] max-h-[90vh] overflow-hidden flex flex-col">
        <div className="mx-auto text-center">
          <h2 className="newText-xl font-bold mb-4">회원 정보</h2>

          <div className="mb-4 flex w-full max-w-md justify-center">
            <SearchComponent
              search={search}
              setSearch={setSearch}
              type={type}
              setType={setType}
              onSearchClick={onSearchClick}
              searchOptions={searchOptions}
            />
          </div>
        </div>
        <div className="flex-1 overflow-auto">
          <table className="min-w-full bg-white border border-gray-300 rounded-lg">
            <thead className="bg-gray-100 text-gray-700 text-sm sticky top-0">
              <tr className="whitespace-nowrap">
                <th className="w-[8%] p-2">아이디</th>
                <th className="w-[12%] p-2">전화번호</th>
                <th className="w-[14%] p-2">주소</th>
                <th className="w-[12%] p-2">학교명</th>
                <th className="w-[12%] p-2">신청물품명</th>
                <th className="w-[12%] p-2">
                  <div>신청상태</div>
                  <select
                    value={statusFilter}
                    onChange={(e) => {
                      setStatusFilter(e.target.value);
                      setCurrent(0);
                    }}
                    className="mt-1 newText-xs input-focus"
                  >
                    <option value="">전체</option>
                    <option value="REJECT">거부</option>
                    <option value="ACCEPT">수락</option>
                    <option value="WAIT">대기</option>
                    <option value="CANCEL">취소</option>
                    <option value="EXPIRED">만료</option>
                  </select>
                </th>
                {[{ label: "시작일", value: "startDate" },
                { label: "마감일", value: "endDate" },
                { label: "등록일", value: "applyAt" }].map(({ label, value }) => (
                  <th
                    key={value}
                    onClick={() => handleSortChange(value)}
                    className="cursor-pointer w-[8%] p-2"
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
                ))}
              </tr>
            </thead>

            <tbody className="text-gray-600">
              {filteredMemberInfo.length === 0 ? (
                <tr>
                  <td colSpan={9} className="text-center p-8 min-h-[300px]">
                    <p className="text-gray-500 newText-xl">신청한 회원이 없습니다.</p>
                  </td>
                </tr>
              ) : (
                filteredMemberInfo.map((member, idx) => (
                  <tr key={member.demRevNum || idx} className={`hover:bg-gray-50 newText-sm text-center whitespace-nowrap h-[60px] border border-gray-300 ${member.state === "CANCEL" ? "bg-gray-100 text-gray-400" : "hover:bg-gray-50"}`}>
                    <td className="p-2">{member.memId}</td>
                    <td className="p-2">{member.phone || "-"}</td>
                    <td title={member.addr + " " + member.addrDetail} className="truncate max-w-[120px] p-2">{member.addr + " " + member.addrDetail || "-"}</td>
                    <td title={member.schoolName} className="truncate max-w-[120px] p-2">{member.schoolName || "-"}</td>
                    <td title={member.demName} className="truncate max-w-[120px] p-2">{member.demName || "-"}</td>
                    <td className="p-2">{getStateLabel(member.state) || "-"}</td>
                    <td className="p-2">{member.startDate ? new Date(member.startDate).toLocaleDateString() : "-"}</td>
                    <td className="p-2">{member.endDate ? new Date(member.endDate).toLocaleDateString() : "-"}</td>
                    <td className="p-2">{member.applyAt ? new Date(member.applyAt).toLocaleDateString() : "-"}</td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>

        {filteredMemberInfo.length > 0 && (
          <div className="flex justify-center mt-4">
            <PageComponent
              totalPages={pageData.totalPages}
              current={current}
              setCurrent={setCurrent}
            />
          </div>
        )}

        <div className="mt-4 flex justify-end">
          <button className="normal-button rounded newText-base px-4 py-2" onClick={onClose}>닫기</button>
        </div>
      </div>
    </div>
  );
};

export default RentalMemberInfoModal;