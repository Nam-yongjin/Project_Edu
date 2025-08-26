import React, { useEffect, useState } from "react";
import PageComponent from "../common/PageComponent";
import SearchComponent from "../../components/demonstration/SearchComponent";
import { getResAdminSearch, getResAdmin, updateResState, updateReqState } from "../../api/adminApi";
import useMove from "../../hooks/useMove";
import { useSelector } from "react-redux";
import defaultImage from '../../assets/default.jpg';
import { deleteRental } from "../../api/demApi";
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

  // 체크박스 관련 상태
  const [selectedItems, setSelectedItems] = useState([]);
  const [isAllSelected, setIsAllSelected] = useState(false);

  const searchOptions = [
    { value: "memId", label: "아이디" },
    { value: "demName", label: "물품명" },
    { value: "schoolName", label: "학교명" },
  ];

  useEffect(() => {
    if (!isAdmin) {
      alert("권한이 없습니다.");
      moveToPath("/");
    }
  }, [isAdmin]);

  useEffect(() => { fetchData(); }, [current, sortBy, sort, statusFilter]);

  useEffect(() => {
    setSelectedItems([]);
    setIsAllSelected(false);
  }, [resInfo]);

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
      await updateResState([demRevNum], state);
      window.location.reload();
    } catch (error) {
      console.error(error);
      alert("대여 신청 처리 중 오류가 발생했습니다.");
    }
  };

  const handleRequest = async (demRevNum, state, type) => {
    try {
      alert(state === "ACCEPT" ? "요청이 수락되었습니다." : "요청이 거부되었습니다.");
      await updateReqState([demRevNum], state, type);
      window.location.reload();
    } catch (error) {
      console.error(error);
      alert("요청 처리 중 오류가 발생했습니다.");
    }
  };

  const isItemSelectable = (member) => {
    return member.state === "WAIT" ||
      (member.requestDTO && member.requestDTO.some(req => req.state === "WAIT"));
  };

  const handleSelectAll = (checked) => {
    setIsAllSelected(checked);
    if (checked) {
      const selectableItems = resInfo.content
        .filter(item => isItemSelectable(item))
        .map(item => ({
          demRevNum: item.demRevNum,
          hasWaitState: item.state === "WAIT",
          hasWaitRequest: item.requestDTO && item.requestDTO.some(req => req.state === "WAIT"),
          waitRequestTypes: item.requestDTO ? item.requestDTO.filter(req => req.state === "WAIT").map(req => req.type) : []
        }));
      setSelectedItems(selectableItems);
    } else {
      setSelectedItems([]);
    }
  };

  const handleItemSelect = (member, checked) => {
    const itemData = {
      demRevNum: member.demRevNum,
      hasWaitState: member.state === "WAIT",
      hasWaitRequest: member.requestDTO && member.requestDTO.some(req => req.state === "WAIT"),
      waitRequestTypes: member.requestDTO ? member.requestDTO.filter(req => req.state === "WAIT").map(req => req.type) : []
    };

    let newSelectedItems;
    if (checked) {
      newSelectedItems = [...selectedItems, itemData];
    } else {
      newSelectedItems = selectedItems.filter(item => item.demRevNum !== member.demRevNum);
    }
    setSelectedItems(newSelectedItems);

    const selectableItems = resInfo.content.filter(item => isItemSelectable(item));
    const allSelectableItemsSelected = selectableItems.every(item =>
      newSelectedItems.some(selected => selected.demRevNum === item.demRevNum)
    );
    setIsAllSelected(allSelectableItemsSelected && selectableItems.length > 0);
  };

  // 일괄 수락 처리 (순차적으로 호출)
  const handleCheckedAccept = async () => {
    if (selectedItems.length === 0) return;

    try {
      for (const item of selectedItems) {
        if (item.hasWaitState) {
          await updateResState([item.demRevNum], "ACCEPT");
        }
        if (item.hasWaitRequest && item.waitRequestTypes.length > 0) {
          for (const reqType of item.waitRequestTypes) {
            await updateReqState([item.demRevNum], "ACCEPT", reqType);
          }
        }
      }
      alert(`${selectedItems.length}개의 항목이 수락되었습니다.`);
      window.location.reload();
    } catch (error) {
      console.error(error);
      alert("수락 처리 중 오류가 발생했습니다.");
    }
  };

  // 일괄 거부 처리 (순차적으로 호출)
  const handleCheckedReject = async () => {
    if (selectedItems.length === 0) return;

    try {
      for (const item of selectedItems) {
        if (item.hasWaitState) {
          await updateResState([item.demRevNum], "REJECT");
        }
        if (item.hasWaitRequest && item.waitRequestTypes.length > 0) {
          for (const reqType of item.waitRequestTypes) {
            await updateReqState([item.demRevNum], "REJECT", reqType);
          }
        }
      }
      alert(`${selectedItems.length}개의 항목이 거부되었습니다.`);
      window.location.reload();
    } catch (error) {
      console.error(error);
      alert("거부 처리 중 오류가 발생했습니다.");
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

  const selectableItemsCount = resInfo.content.filter(item => isItemSelectable(item)).length;
return (
    <>
        <div className="max-w-screen-xl mx-auto my-10">
            <div className="min-blank">
                <div className="mx-auto text-center">
                    <div className="newText-3xl font-bold ">실증 대여 관리</div>
                    <div className="py-2 flex justify-center">
                        <SearchComponent
                            search={search} setSearch={setSearch}
                            type={type} setType={setType}
                            onSearchClick={onSearchClick}
                            searchOptions={searchOptions}
                        />
                    </div>
                </div>
                <p className="text-gray-700 my-3 newText-base px-4 py-2 rounded-md inline-block">
                    전체 <span className="font-bold text-blue-600">{pageData.totalElements}</span>건의 대여내역이 있습니다.
                </p>

                <div className="overflow-x-auto page-shadow">
                    <table className="w-full">
                        {/* 테이블 헤더 */}
                        <thead className="bg-gray-100 text-gray-700 newText-base border border-gray-300">
                            <tr className="newText-base whitespace-nowrap">
                                <th className="w-[5%] px-3">
                                    {selectableItemsCount > 0 && (
                                        <input
                                            type="checkbox"
                                            checked={isAllSelected}
                                            onChange={(e) => handleSelectAll(e.target.checked)}
                                            className="w-4 h-4"
                                        />
                                    )}
                                </th>
                                {/* 
                                <th className="w-[10%]">이미지</th>*/}
                                <th className="w-[14%]">아이디</th>
                                <th className="w-[14%]">전화번호</th>
                                <th className="w-[14%]">주소</th>
                                <th className="w-[14%]">학교명</th>
                                <th className="w-[14%]">물품명</th>
                                <th className="w-[10%]">신청 갯수</th>
                                <th className="w-[12%]">
                                    <div className="mb-1">신청 상태</div>
                                    <select
                                        value={statusFilter}
                                        className="input-focus"
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
                                        <th key={value} onClick={() => handleSortChange(value)} className="cursor-pointer w-[8%] px-3">
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
                                <th className="w-[10%] px-3 min-w-[100px]">반납/연장</th>
                                <th className="w-[10%] px-3 min-w-[100px]">삭제</th>
                            </tr>
                        </thead>

                        {/* 테이블 바디 */}
                        <tbody className="text-gray-600 border border-gray-300">
                            {resInfo.content.length === 0 ? (
                                <tr>
                                    <td colSpan={14} className="text-center">
                                        <p className="text-gray-500 newText-3xl mt-20 min-h-[300px]">등록된 신청이 없습니다.</p>
                                    </td>
                                </tr>
                            ) : (
                                resInfo.content.map((member) => {
                                    const mainImage = member.imageList?.find((img) => img.isMain === true);
                                    const memberState = member.state;
                                    const isSelectable = isItemSelectable(member);
                                    const isSelected = selectedItems.some(item => item.demRevNum === member.demRevNum);

                                    return (
                                        <tr key={`${member.demRevNum}_${member.startDate}_${member.endDate}_${member.applyAt}_${member.state}`}
                                            className={`h-[100px] hover:bg-gray-50 newText-sm text-center whitespace-nowrap border border-gray-300 ${memberState === "CANCEL" ? "bg-gray-100 text-gray-400" : "hover:bg-gray-50"}`}>
                                            <td className="py-2">
                                                {isSelectable && (
                                                    <input
                                                        type="checkbox"
                                                        checked={isSelected}
                                                        onChange={(e) => handleItemSelect(member, e.target.checked)}
                                                        className="w-4 h-4 px-3"
                                                    />
                                                )}
                                            </td>
                                            {/* 
                                            <td className="py-2">
                                                <div className="flex justify-center">
                                                    {mainImage ? (
                                                        <img
                                                            src={`http://localhost:8090/view/${mainImage.imageUrl}`}
                                                            alt={member.demName}
                                                            onClick={() => moveToPath(`../../demonstration/detail/${member.demNum}`)}
                                                            className="w-16 h-16 object-cover rounded-md cursor-pointer flex-shrink-0"
                                                        />
                                                    ) : (
                                                        <img
                                                            src={defaultImage}
                                                            alt="default"
                                                            className="w-16 h-16 object-cover rounded-md cursor-pointer flex-shrink-0"
                                                        />
                                                    )}
                                                </div>
                                            </td>
                                            */}
                                            <td className="py-2">{member.memId}</td>
                                            <td className="py-2">{member.phone || "-"}</td>
                                            <td className="truncate max-w-[100px] py-2" title={member.addr + " " + member.addrDetail}>{member.addr + " " + member.addrDetail || "-"}</td>
                                            <td className="truncate max-w-[100px] py-2" title={member.schoolName}>{member.schoolName || "-"}</td>
                                            <td className="truncate max-w-[100px] py-2" title={member.demName}>{member.demName || "-"}</td>
                                            <td className="py-2">{member.bitemNum ?? "-"}</td>
                                            <td className="py-2 min-w-[120px]">
                                                <div className="flex flex-col items-center gap-1">
                                                    <div>{getStateLabel(member.state)}</div>
                                                    {member.state === "WAIT" && (
                                                        <div className="flex gap-1">
                                                            <button
                                                                className="green-button px-2 py-1 text-xs whitespace-nowrap"
                                                                onClick={() => handleRental(member.demRevNum, "ACCEPT")}
                                                            >수락</button>
                                                            <button
                                                                className="negative-button px-2 py-1 text-xs whitespace-nowrap"
                                                                onClick={() => handleRental(member.demRevNum, "REJECT")}
                                                            >거절</button>
                                                        </div>
                                                    )}
                                                </div>
                                            </td>
                                            <td className="py-2">{member.startDate ? new Date(member.startDate).toLocaleDateString() : "-"}</td>
                                            <td className="py-2">{member.endDate ? new Date(member.endDate).toLocaleDateString() : "-"}</td>
                                            <td className="py-2">{member.applyAt ? new Date(member.applyAt).toLocaleDateString() : "-"}</td>
                                            <td className="px-3 py-2 max-w-[80px]
                                            ">
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
                                                                    <div className="flex gap-1">
                                                                        <button
                                                                            className="green-button text-xs px-2 py-1 whitespace-nowrap"
                                                                            onClick={() => handleRequest(member.demRevNum, "ACCEPT", req.type)}
                                                                        >수락</button>
                                                                        <button
                                                                            className="negative-button text-xs px-2 py-1 whitespace-nowrap"
                                                                            onClick={() => handleRequest(member.demRevNum, "REJECT", req.type)}
                                                                        >거절</button>
                                                                    </div>
                                                                </div>
                                                            ))}
                                                        </div>
                                                    ) : (
                                                        <button
                                                            className="positive-button"
                                                            onClick={() => openModal(member.requestDTO)}
                                                        >내역 확인</button>
                                                    )
                                                ) : "-"}
                                            </td>
                                            <td className="px-3 py-2 max-w-[80px]">
                                                {member.state === "ACCEPT" || member.state === "EXPIRED" ? (
                                                    <button
                                                        className="negative-button"
                                                        onClick={() => {
                                                            deleteRental([member.demRevNum]);
                                                            alert("삭제되었습니다.");
                                                            window.location.reload();
                                                        }}
                                                    >
                                                        삭제 하기
                                                    </button>
                                                ) : "-"}
                                            </td>
                                        </tr>
                                    );
                                })
                            )}
                        </tbody>
                    </table>
                </div>

                {/* 일괄 처리 버튼 */}
                <div className="flex justify-end mt-5 mr-15 newText-base">
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
                            ? "negative-button"
                            : "disable-button"
                            }`}
                        onClick={handleCheckedReject}
                    >
                        신청 거부 ({selectedItems.length})
                    </button>
                </div>
            </div>
        </div>

        <div className="flex justify-center my-6">
            <PageComponent totalPages={pageData.totalPages} current={current} setCurrent={setCurrent} />
        </div>

        {/* 모달 */}
        {showModal && (
            <div className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50">
                <div className="bg-white rounded-xl page-shadow p-6 max-w-lg w-full min-blank">
                    {/* 모달 헤더 */}
                    <div className="flex items-center justify-between mb-6">
                        <div className="flex items-center gap-3">
                            <div className="w-10 h-10 bg-blue-100 rounded-full flex items-center justify-center">
                                <svg className="w-6 h-6 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                                </svg>
                            </div>
                            <h2 className="newText-2xl font-bold text-gray-800">신청 내역</h2>
                        </div>
                    </div>

                    {/* 모달 콘텐츠 */}
                    <div className="space-y-4 max-h-96 overflow-y-auto pr-2">
                        {modalData.length === 0 ? (
                            <div className="text-center py-12">
                                <div className="w-16 h-16 bg-gray-100 rounded-full flex items-center justify-center mx-auto mb-4">
                                    <svg className="w-8 h-8 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                                    </svg>
                                </div>
                                <p className="text-gray-500 newText-lg">신청 내역이 없습니다</p>
                            </div>
                        ) : (
                            modalData.map((req, idx) => (
                                <div key={idx} className="bg-gradient-to-r from-gray-50 to-gray-100 rounded-lg p-4 border border-gray-200 hover:shadow-md transition-all duration-200">
                                    <div className="flex items-start justify-between">
                                        <div className="flex-1">
                                            <div className="flex items-center gap-3 mb-2">
                                                <div className={`w-8 h-8 rounded-full flex items-center justify-center ${
                                                    req.type === "EXTEND" 
                                                        ? "bg-green-100 text-green-600" 
                                                        : "bg-orange-100 text-orange-600"
                                                }`}>
                                                    {req.type === "EXTEND" ? (
                                                        <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                                                        </svg>
                                                    ) : (
                                                        <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M3 10h10a8 8 0 018 8v2M3 10l6 6m-6-6l6-6" />
                                                        </svg>
                                                    )}
                                                </div>
                                                <h3 className="newText-lg font-semibold text-gray-800">
                                                    {req.type === "EXTEND" ? "연장 신청" : "반납 신청"}
                                                </h3>
                                            </div>
                                            
                                            {req.type === "EXTEND" && req.updateDate && (
                                                <div className="flex items-center gap-2 mb-2 text-gray-600">
                                                    <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                                                    </svg>
                                                    <span className="newText-sm">신청 날짜: {req.updateDate}</span>
                                                </div>
                                            )}
                                            
                                            <div className="flex items-center gap-2">
                                                <span className="newText-sm text-gray-500">상태:</span>
                                                <span className={`px-3 py-1 rounded-full newText-xs font-medium ${
                                                    req.state === "ACCEPT" 
                                                        ? "bg-green-100 text-green-800" 
                                                        : req.state === "REJECT"
                                                        ? "bg-red-100 text-red-800"
                                                        : req.state === "WAIT"
                                                        ? "bg-yellow-100 text-yellow-800"
                                                        : "bg-gray-100 text-gray-800"
                                                }`}>
                                                    {getStateLabel(req.state)}
                                                </span>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            ))
                        )}
                    </div>

                    {/* 모달 푸터 */}
                    <div className="mt-6 pt-4 border-t border-gray-200">
                        <div className="flex justify-end">
                            <button 
                                className="normal-button px-4 py-2 rounded-lg"
                                onClick={closeModal}
                            >
                                닫기
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        )}
    </>
);
};

export default AdminResComponent;
