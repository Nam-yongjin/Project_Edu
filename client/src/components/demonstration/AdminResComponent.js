import React, { useEffect, useState } from "react";
import PageComponent from "../common/PageComponent";
import SearchComponent from "../../components/demonstration/SearchComponent";
import { getResAdminSearch, getResAdmin, updateResState, updateReqState } from "../../api/adminApi";
import useMove from "../../hooks/useMove";
import { useSelector } from "react-redux";
import defaultImage from '../../assets/default.jpg';
const AdminResComponent = () => {
  const isAdmin = useSelector((state) => state.loginState?.role === "ADMIN");
  const initState = { totalPages: 0, currentPage: 0 };
  const { moveToPath } = useMove();

  const [search, setSearch] = useState("");
  const [type, setType] = useState("memId");
  const [sortBy, setSortBy] = useState("applyAt");
  const [sort, setSort] = useState("desc");
  const [resInfo, setResInfo] = useState({ content: [] });
  const [current, setCurrent] = useState(initState.currentPage);
  const [pageData, setPageData] = useState(initState);
  const [statusFilter, setStatusFilter] = useState("");
  const [showModal, setShowModal] = useState(false);
  const [modalData, setModalData] = useState([]);

  const searchOptions = [
    { value: "memId", label: "아이디" },
    { value: "demName", label: "상품명" },
    { value: "schoolName", label: "학교명" },
  ];

  useEffect(() => {
    if (!isAdmin) {
      alert("권한이 없습니다.");
      moveToPath("/");
    }
  }, [isAdmin]);

  useEffect(() => { fetchData(); }, [current, sortBy, sort, statusFilter]);

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
      });
    }
  };

  const handleSortChange = (column) => {
    if (sortBy === column) setSort((prev) => (prev === "asc" ? "desc" : "asc"));
    else { setSortBy(column); setSort("asc"); }
  };

  const onSearchClick = () => { setCurrent(0); fetchData(); };

  const handleRental = async (demRevNum, state) => {
    try {
      alert(state === "ACCEPT" ? "대여 신청이 수락되었습니다." : "대여 신청이 거부되었습니다.");
      await updateResState(demRevNum, state);
      window.location.reload();
    } catch (error) {
      console.error(error);
      alert("대여 신청 처리 중 오류가 발생했습니다.");
    }
  };

  const handleRequest = async (demRevNum, state, type) => {
    try {
      alert(state === "ACCEPT" ? "요청이 수락되었습니다." : "요청이 거부되었습니다.");
      await updateReqState(demRevNum, state, type);
      window.location.reload();
    } catch (error) {
      console.error(error);
      alert("요청 처리 중 오류가 발생했습니다.");
    }
  };

  const getStateLabel = (state) => {
    switch (state) {
      case "ACCEPT": return "수락";
      case "REJECT": return "거부";
      case "WAIT": return "대기";
      case "CANCEL": return "취소";
      case "EXPIRED": return "만료";
      default: return state || "-";
    }
  };

  const openModal = (data) => {
    setModalData(data);
    setShowModal(true);
  };
  const closeModal = () => setShowModal(false);

  return (
    <>
    <div className="max-w-screen-xl mx-auto my-10 overflow-x-auto">
      <div className="min-blank">
        <div className="newText-3xl font-bold ">실증 물품 대여 관리</div>
        <div className="py-2">
          <SearchComponent
            search={search} setSearch={setSearch}
            type={type} setType={setType}
            onSearchClick={onSearchClick}
            searchOptions={searchOptions}
          />
        </div>

        <table className="min-w-full">
          <thead className="bg-gray-100 text-gray-700 newText-base">
            <tr className="newText-base">
              <th className="w-[10%]">이미지</th>
              <th className="w-[5%]">아이디</th>
              <th className="w-[10%]">전화번호</th>
              <th className="w-[14%]">주소</th>
              <th className="w-[10%]">학교명</th>
              <th className="w-[10%]">상품명</th>
              <th className="w-[10%]">신청 갯수</th>
              <th className="w-[10%]">
                <div className="mb-1">신청 상태</div>
                <select
                  value={statusFilter}
                  onChange={(e) => {
                    setStatusFilter(e.target.value);
                    setCurrent(0);
                  }}>
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
                  <th key={value} onClick={() => handleSortChange(value)} className="cursor-pointer w-[8%]">
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
              <th className="min-w-[14%]">반납/연장 신청</th>
            </tr>
          </thead>

          <tbody className="text-gray-600">
            {resInfo.content.length === 0 ? (
              <tr>
                <td colSpan={13} className="text-center">
                  <p className="text-gray-500 newText-3xl mt-20">등록된 신청이 없습니다.</p>
                </td>
              </tr>
            ) : (
              resInfo.content.map((member) => {
                const mainImage = member.imageList?.find((img) => img.isMain === true);

                return (
                  <tr key={member.demRevNum} className="hover:bg-gray-50 newText-sm text-center whitespace-nowrap">
                    <td className="py-2 px-2 whitespace-nowrap text-center">
                      {mainImage ? (
                        <img
                          src={`http://localhost:8090/view/${mainImage.imageUrl}`}
                          alt={member.demName}
                          onClick={() => moveToPath(`../../demonstration/detail/${member.demNum}`)}
                          className="min-w-20 min-h-20 rounded-md hover:scale-105 transition-transform cursor-pointer"
                        />
                      ) : (
                        <img
                          src={defaultImage}
                          alt="default"
                          className="min-w-20 min-h-20 rounded-md hover:scale-105 transition-transform cursor-pointer"
                        />
                      )}
                    </td>
                    <td title={member.memId}>{member.memId}</td>
                    <td title={member.phone}>{member.phone || "-"}</td>
                    <td title={member.addr} className="truncate max-w-[14%]">{member.addr || "-"}</td>
                    <td title={member.schoolName}>{member.schoolName || "-"}</td>
                    <td title={member.demName}>{member.demName || "-"}</td>
                    <td>{member.bitemNum ?? "-"}</td>
                    <td>
                      <div>{getStateLabel(member.state)}</div>
                      {member.state === "WAIT" ?
                      <div>
                          <button
                            button className="inline-block green-button text-[10px] px-2 py-1 leading-none mr-1"
                            onClick={() => handleRental(member.demRevNum, "ACCEPT")}
                          >
                            수락
                          </button>
                          <button
                            className="inline-block nagative-button text-[10px] px-2 py-1 leading-none"
                            onClick={() => handleRental(member.demRevNum, "REJECT")}
                          >
                            거절
                          </button>
                        </div>: <></>}
                    </td>
                    <td>{member.startDate ? new Date(member.startDate).toLocaleDateString() : "-"}</td>
                    <td>{member.endDate ? new Date(member.endDate).toLocaleDateString() : "-"}</td>
                    <td>{member.applyAt ? new Date(member.applyAt).toLocaleDateString() : "-"}</td>
                    <td>
                      {member.requestDTO && member.requestDTO.length > 0 ? (
                        member.requestDTO.some(req => req.state === "WAIT") ? (
                          <div className="flex flex-col items-center space-y-1">
                            {member.requestDTO.filter(req => req.state === "WAIT").map((req, idx) => (
                              <div key={idx} className="flex flex-col items-center space-y-1">
                                {req.type === "EXTEND" && (
                                  <div className="newText-sm text-gray-600">연장일: {req.updateDate}</div>
                                )}
                                <div className="font-semibold newText-sm">
                                  {req.type === "EXTEND" ? "연장 신청" : "반납 신청"}
                                </div>
                                <div>
                                  <button
                                    className="inline-block green-button text-[10px] px-2 py-1 leading-none mr-1"
                                    onClick={() => handleRequest(member.demRevNum, "ACCEPT", req.type)}
                                  >
                                    수락
                                  </button>
                                  <button
                                    className="inline-block nagative-button text-[10px] px-2 py-1 leading-none"
                                    onClick={() => handleRequest(member.demRevNum, "REJECT", req.type)}
                                  >
                                    거절
                                  </button>
                                </div>
                              </div>
                            ))}
                          </div>
                        ) : (
                          <button
                            className="positive-button"
                            onClick={() => openModal(member.requestDTO)}
                          >
                            내역 확인
                          </button>
                        )
                      ) : "-"}
                    </td>
                  </tr>
                );
              })
            )}
          </tbody>
        </table>
      </div>
    </div>
    <div className="flex justify-center my-6">
        <PageComponent totalPages={pageData.totalPages} current={current} setCurrent={setCurrent} />
      </div>

      {/* 모달 */}
      {showModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50">
          <div className="bg-white rounded-lg p-6 max-w-md w-full">
            <h2 className="newText-3xl font-bold mb-1">신청 내역</h2>
            <div className="space-y-2 max-h-80 overflow-y-auto">
              {modalData.map((req, idx) => (
                <div key={idx} className="border-b pb-2">
                  <div className="newText-xl font-bold">
                    {req.type === "EXTEND" ? "연장 신청" : "반납 신청"}
                  </div>
                  {req.type === "EXTEND" && (
                    <div className="newText-base text-gray-600">신청 날짜: {req.updateDate || "-"}</div>
                  )}
                  <div className="newText-sm">상태: {getStateLabel(req.state)}</div>
                </div>
              ))}
            </div>
            <div className="mt-4 flex justify-end">
              <button className="normal-button rounded" onClick={closeModal}>닫기</button>
            </div>
          </div>
        </div>
      )}
      </>
  );
};

export default AdminResComponent;
