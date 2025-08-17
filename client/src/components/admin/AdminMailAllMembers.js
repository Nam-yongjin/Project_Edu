import { useEffect, useState } from "react";
import { viewMembers, memberStateChange } from "../../api/adminApi";
import PageComponent from "../common/PageComponent";
import { useNavigate } from "react-router-dom"; // react-router-dom 사용 가정

const AdminMembersComponent = () => {
  const navigate = useNavigate();

  const [members, setMembers] = useState([]);
  const [selectedIds, setSelectedIds] = useState([]);
  const [selectedState, setSelectedState] = useState("");
  const [searchParams, setSearchParams] = useState({
    memId: "",
    name: "",
    email: "",
    phone: "",
    role: "",
    state: "",
  });
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [sortField, setSortField] = useState("createdAt");
  const [sortDirection, setSortDirection] = useState("DESC");

  // 회원 목록 불러오기
  const loadMembers = () => {
    viewMembers({ ...searchParams, pageCount: page, sortField, sortDirection })
      .then((data) => {
        setMembers(Array.isArray(data?.content) ? data.content : []);
        setTotalPages(data?.totalPages ?? 0);
      })
      .catch((error) => {
        alert("회원 목록 불러오기 실패:", error);
        setMembers([]);
      });
  };

  useEffect(() => {
    loadMembers();
  }, [page]);

  // 체크박스 토글
  const handleCheckboxChange = (id) => {
    setSelectedIds((prev) =>
      prev.includes(id) ? prev.filter((i) => i !== id) : [...prev, id]
    );
  };

  const handleSelectAll = (e) => {
    const isChecked = e.target.checked;
    setSelectedIds(isChecked ? members.map((m) => m.memId) : []);
  };

  const isAllSelected = members.length > 0 && selectedIds.length === members.length;

  // 상태 변경 처리
  const handleChangeState = async () => {
    if (!selectedState || selectedIds.length === 0) {
      alert("변경할 상태와 회원을 선택하세요.");
      return;
    }
    try {
      await memberStateChange({ memId: selectedIds, state: selectedState });
      alert("상태 변경 완료");
      setSelectedIds([]);
      loadMembers();
    } catch (error) {
      alert("상태 변경 실패:", error);
    }
  };

  const handleSearchChange = (e) => {
    const { name, value } = e.target;
    setSearchParams((prev) => ({ ...prev, [name]: value }));
  };

  const handleSearch = () => {
    setPage(0);
    loadMembers();
  };

  // 선택한 회원 이메일 발송 페이지로 전달
  const handleSendEmailPage = () => {
    if (selectedIds.length === 0) {
      alert("회원 선택 후 이동하세요.");
      return;
    }
    // react-router-dom navigate로 query나 state 전달
    navigate("/admin/email", { state: { selectedMembers: selectedIds } });
  };

  return (
    <div className="max-w-screen-xl mx-auto my-10">
      <div className="newText-2xl min-blank font-bold mb-4">회원 관리</div>

      {/* 검색 및 정렬 필터 */}
      <div className="min-blank newText-base grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 mb-6 p-4 bg-gray-100 rounded-lg shadow-sm">
        <input name="memId" placeholder="회원 ID" onChange={handleSearchChange} className="input-focus" />
        <input name="name" placeholder="이름" onChange={handleSearchChange} className="input-focus" />
        <input name="email" placeholder="이메일" onChange={handleSearchChange} className="input-focus" />
        <input name="phone" placeholder="휴대폰번호" onChange={handleSearchChange} className="input-focus" />
        <select name="role" onChange={handleSearchChange} className="input-focus">
          <option value="">역할 선택</option>
          <option value="STUDENT">학생</option>
          <option value="TEACHER">교사</option>
          <option value="COMPANY">기업</option>
        </select>
        <select name="state" onChange={handleSearchChange} className="input-focus">
          <option value="">상태 선택</option>
          <option value="NORMAL">일반</option>
          <option value="BEN">블랙리스트</option>
          <option value="LEAVE">탈퇴</option>
        </select>

        {/* 정렬 기준 + 방향 버튼 */}
        <div className="flex gap-2">
          <select value={sortField} onChange={(e) => setSortField(e.target.value)} className="flex-grow input-focus">
            <option value="createdAt">가입일</option>
            <option value="name">이름</option>
            <option value="memId">아이디</option>
          </select>
          <button
            onClick={() => setSortDirection(sortDirection === "ASC" ? "DESC" : "ASC")}
            className="normal-button"
          >
            {sortDirection === "ASC" ? "▲ 오름차순" : "▼ 내림차순"}
          </button>
        </div>

        <button onClick={handleSearch} className="positive-button">
          검색
        </button>
      </div>

      {/* 회원 목록 테이블 */}
      <div className="page-shadow min-blank overflow-x-auto">
        <table className="newText-sm w-full text-center min-w-[1024px]">
          <thead className="bg-gray-200">
            <tr>
              <th className="p-4">
                <input type="checkbox" onChange={handleSelectAll} checked={isAllSelected} className="w-4 h-4" />
              </th>
              <th>ID</th>
              <th>이름</th>
              <th>이메일</th>
              <th>휴대폰번호</th>
              <th>가입일</th>
              <th>역할</th>
              <th>상태</th>
            </tr>
          </thead>
          <tbody>
            {members.length > 0 ? (
              members.map((m) => (
                <tr key={m.memId} className="bg-white border-b hover:bg-gray-50">
                  <td>
                    <input type="checkbox" checked={selectedIds.includes(m.memId)} onChange={() => handleCheckboxChange(m.memId)} />
                  </td>
                  <td>{m.memId}</td>
                  <td>{m.name}</td>
                  <td>{m.email}</td>
                  <td>{m.phone}</td>
                  <td>{new Date(m.createdAt).toLocaleDateString()}</td>
                  <td>{m.role}</td>
                  <td>{m.state}</td>
                </tr>
              ))
            ) : (
              <tr>
                <td colSpan="8" className="text-center">
                  회원 정보가 없습니다.
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>

      {/* 상태 변경 드롭다운 + 이메일 페이지 이동 */}
      <div className="min-blank newText-base flex items-center gap-4 mt-10 mb-6">
        <select value={selectedState} onChange={(e) => setSelectedState(e.target.value)} className="p-2 border rounded-md">
          <option value="">상태 선택</option>
          <option value="NORMAL">일반</option>
          <option value="BEN">블랙리스트</option>
          <option value="LEAVE">탈퇴</option>
        </select>
        <button onClick={handleChangeState} className="nagative-button">
          선택 회원 상태 변경
        </button>
        <button onClick={handleSendEmailPage} className="positive-button">
          선택 회원 이메일 발송 페이지로 이동
        </button>
      </div>

      {/* 페이지네이션 */}
      <div className="flex justify-center mt-10">
        <PageComponent totalPages={totalPages} current={page} setCurrent={setPage} />
      </div>
    </div>
  );
};

export default AdminMembersComponent;
