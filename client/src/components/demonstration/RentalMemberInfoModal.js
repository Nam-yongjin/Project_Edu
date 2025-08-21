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

  // 상태 필터링
  const filteredMemberInfo = statusFilter
    ? memberInfo.content.filter((m) => m.state === statusFilter)
    : memberInfo.content;

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 z-40">
      <div className="fixed inset-0 z-50 flex items-center justify-center max-w-screen-xl mx-auto">
        <div className="bg-white rounded-lg p-6 w-full min-h-[400px]">
          <h2 className="newText-xl font-bold mb-1">회원 정보</h2>
          <div className="mb-4 flex w-full max-w-md">
            <SearchComponent
              search={search}
              setSearch={setSearch}
              type={type}
              setType={setType}
              onSearchClick={onSearchClick}
              searchOptions={searchOptions}
            />
          </div>

          <div className="overflow-auto max-h-[300px] ">
            <table className="min-w-full bg-white border border-gray-300 rounded-lg">
              <thead className="bg-gray-100 text-gray-700 newText-sm">
                <tr className="whitespace-nowrap">
                  <th className="w-[8%]">아이디</th>
                  <th className="w-[12%]">전화번호</th>
                  <th className="w-[14%]">주소</th>
                  <th className="w-[12%]">학교명</th>
                  <th className="w-[12%]">신청물품명</th>
                  <th className="w-[12%]">
                    <div>신청상태</div>
                    <select
                      value={statusFilter}
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
                  {[{ label: "시작일", value: "startDate" },
                  { label: "마감일", value: "endDate" },
                  { label: "등록일", value: "applyAt" }].map(({ label, value }) => (
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
                  ))}
                </tr>
              </thead>

              <tbody className="text-gray-600">
                {filteredMemberInfo.length === 0 ? (
                  <tr>
                    <td colSpan={9} className="text-center">
                      <p className="text-gray-500 newText-2xl py-10">신청한 회원이 없습니다.</p>
                    </td>
                  </tr>
                ) : (
                  filteredMemberInfo.map((member, idx) => (
                    <tr key={member.demRevNum || idx} className={`hover:bg-gray-50 newText-sm text-center whitespace-nowrap h-[80px] ${member.state === "CANCEL" ? "bg-gray-100 text-gray-400" : "hover:bg-gray-50"}`}>
                      <td>{member.memId}</td>
                      <td>{member.phone || "-"}</td>
                      <td title={member.addr+" "+member.addrDetail} className="truncate max-w-[100px]">{member.addr+" "+member.addrDetail || "-"}</td>
                      <td title={member.schoolName} className="truncate max-w-[100px]">{member.schoolName || "-"}</td>
                      <td title={member.demName} className="truncate max-w-[100px]">{member.demName || "-"}</td>
                      <td>{member.state || "-"}</td>
                      <td>{member.startDate ? new Date(member.startDate).toLocaleDateString() : "-"}</td>
                      <td>{member.endDate ? new Date(member.endDate).toLocaleDateString() : "-"}</td>
                      <td>{member.applyAt ? new Date(member.applyAt).toLocaleDateString() : "-"}</td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>

          {filteredMemberInfo.length > 0 && (
            <div className="flex justify-center mt-6">
              <PageComponent
                totalPages={pageData.totalPages}
                current={current}
                setCurrent={setCurrent}
              />
            </div>
          )}
          <div className="mt-4 flex justify-end">
            <button className="normal-button rounded newText-base" onClick={onClose}>닫기</button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default RentalMemberInfoModal;
