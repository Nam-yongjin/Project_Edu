import { useEffect, useState } from "react";
import { viewMembers, memberStateChange } from "../../api/adminApi";

const AdminMembersComponent = () => {
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
        viewMembers({ ...searchParams, pageCount: page, sortField: sortField, sortDirection: sortDirection })
            .then(data => {
                setMembers(Array.isArray(data?.content) ? data.content : []);
                setTotalPages(data?.totalPages ?? 0);
            })
            .catch(error => {
                console.error("회원 목록 불러오기 실패:", error);
                setMembers([]);
            });
    };

    useEffect(() => {
        loadMembers();
    }, [page]); // 페이지가 변경될 때 재호출

    // 체크박스 토글
    const handleCheckboxChange = (id) => {
        setSelectedIds((prev) =>
            prev.includes(id) ? prev.filter((i) => i !== id) : [...prev, id]
        );
    };

    // 전체 선택/해제
    const handleSelectAll = (e) => {
        const isChecked = e.target.checked;
        if (isChecked) {
            const allMemberIds = members.map(m => m.memId);
            setSelectedIds(allMemberIds);
        } else {
            setSelectedIds([]);
        }
    };

    // 모든 회원이 선택되었는지 확인
    const isAllSelected = members.length > 0 && selectedIds.length === members.length;

    // 상태 변경 처리
    const handleChangeState = async () => {
        if (!selectedState || selectedIds.length === 0) {
            alert("변경할 상태와 회원을 선택하세요.");
            return;
        }

        memberStateChange({ memId: selectedIds, state: selectedState })
            .then(() => {
                alert("상태 변경 완료");
                setSelectedIds([]);
                loadMembers();
            })
            .catch((error) => {
                console.error("상태 변경 실패:", error);
            });
    };

    // 검색 입력 처리
    const handleSearchChange = (e) => {
        const { name, value } = e.target;
        setSearchParams((prev) => ({ ...prev, [name]: value }));
    };

    // 검색 버튼 클릭 시
    const handleSearch = () => {
        setPage(0); // 검색 시 첫 페이지로 이동
        loadMembers();
    };

    return (
        <div className="max-w-screen-xl mx-auto my-10">
            <div className="text-2xl font-bold text-gray-900 mb-4">회원 관리</div>

            {/* 검색 및 정렬 필터 */}
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 mb-6 p-4 bg-gray-100 rounded-lg shadow-sm">
                <input
                    name="memId"
                    placeholder="회원 ID"
                    onChange={handleSearchChange}
                    className="p-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
                <input
                    name="name"
                    placeholder="이름"
                    onChange={handleSearchChange}
                    className="p-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
                <input
                    name="email"
                    placeholder="이메일"
                    onChange={handleSearchChange}
                    className="p-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
                <input
                    name="phone"
                    placeholder="휴대폰 번호"
                    onChange={handleSearchChange}
                    className="p-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
                <select
                    name="role"
                    onChange={handleSearchChange}
                    className="p-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                >
                    <option value="">역할 선택</option>
                    <option value="STUDENT">학생</option>
                    <option value="TEACHER">교사</option>
                    <option value="COMPANY">기업</option>
                    <option value="ADMIN">관리자</option>
                </select>
                <select
                    name="state"
                    onChange={handleSearchChange}
                    className="p-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                >
                    <option value="">상태 선택</option>
                    <option value="NORMAL">정상</option>
                    <option value="BEN">정지</option>
                    <option value="LEAVE">탈퇴</option>
                </select>

                {/* 정렬 기준 + 방향 버튼 */}
                <div className="flex gap-2">
                    <select
                        value={sortField}
                        onChange={(e) => setSortField(e.target.value)}
                        className="p-2 flex-grow border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                    >
                        <option value="createdAt">가입일</option>
                        <option value="name">이름</option>
                        <option value="memId">아이디</option>
                    </select>
                    <button
                        onClick={() => setSortDirection(sortDirection === "ASC" ? "DESC" : "ASC")}
                        className="rounded-md p-2 bg-gray-300 hover:bg-gray-400 active:bg-gray-500 font-semibold"
                    >
                        {sortDirection === "ASC" ? "▲ 오름차순" : "▼ 내림차순"}
                    </button>
                </div>

                <button
                    onClick={handleSearch}
                    className="rounded-md p-2 bg-blue-500 hover:bg-blue-600 active:bg-blue-700 text-white font-semibold"
                >
                    검색
                </button>
            </div>

            {/* 회원 목록 테이블 */}
            <div className="overflow-x-auto shadow-lg shadow-gray-400 rounded-lg">
                <table className="w-full text-sm text-center">
                    <thead className="bg-gray-200">
                        <tr>
                            <th scope="col" className="p-4">
                                <input
                                    type="checkbox"
                                    onChange={handleSelectAll}
                                    checked={isAllSelected}
                                    className="w-4 h-4 text-blue-600 bg-gray-100 border-gray-300 rounded focus:ring-blue-500"
                                />
                            </th>
                            <th scope="col" className="px-6 py-3">ID</th>
                            <th scope="col" className="px-6 py-3">이름</th>
                            <th scope="col" className="px-6 py-3">전화번호</th>
                            <th scope="col" className="px-6 py-3">이메일</th>
                            <th scope="col" className="px-6 py-3">가입일</th>
                            <th scope="col" className="px-6 py-3">역할</th>
                            <th scope="col" className="px-6 py-3">상태</th>
                        </tr>
                    </thead>
                    <tbody>
                        {members.length > 0 ? (
                            members.map((m) => (
                                <tr key={m.memId} className="bg-white border-b hover:bg-gray-50">
                                    <td className="w-4 p-4">
                                        <input
                                            type="checkbox"
                                            checked={selectedIds.includes(m.memId)}
                                            onChange={() => handleCheckboxChange(m.memId)}
                                            className="w-4 h-4"
                                        />
                                    </td>
                                    <td className="px-6 py-4 font-medium text-gray-900 whitespace-nowrap">{m.memId}</td>
                                    <td className="px-6 py-4">{m.name}</td>
                                    <td className="px-6 py-4">{m.phone}</td>
                                    <td className="px-6 py-4">{m.email}</td>
                                    <td className="px-6 py-4">
                                        {new Date(m.createdAt).toLocaleDateString()}
                                    </td>
                                    <td className="px-6 py-4">{m.role}</td>
                                    <td className="px-6 py-4">{m.state}</td>
                                </tr>
                            ))
                        ) : (
                            <tr>
                                <td colSpan="8" className="px-6 py-4 text-center">
                                    회원 정보가 없습니다.
                                </td>
                            </tr>
                        )}
                    </tbody>
                </table>
            </div>

            {/* 상태 변경 드롭다운 */}
            <div className="flex items-center gap-4 mt-10 mb-6">
                <select
                    value={selectedState}
                    onChange={(e) => setSelectedState(e.target.value)}
                    className="p-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                >
                    <option value="">상태 선택</option>
                    <option value="NORMAL">정상</option>
                    <option value="BEN">정지</option>
                    <option value="LEAVE">탈퇴</option>
                </select>
                <button
                    onClick={handleChangeState}
                    className="rounded-md p-2 bg-red-500 hover:bg-red-600 active:bg-red-700 text-white font-semibold"
                >
                    선택 회원 상태 변경
                </button>
            </div>

            {/* 페이지네이션 */}
            <div className="flex justify-center items-center gap-2">
                {Array.from({ length: totalPages }, (_, idx) => (
                    <button
                        key={idx}
                        className={`px-4 py-2 rounded-md text-sm font-semibold ${page === idx
                            ? "bg-blue-600 text-white"
                            : "bg-gray-200 hover:bg-gray-300 active:bg-gray-400"
                            }`}
                        onClick={() => setPage(idx)}
                    >
                        {idx + 1}
                    </button>
                ))}
            </div>
        </div>
    );
};

export default AdminMembersComponent;