import { useEffect, useState } from "react";
import { getSelect, getSelectSearch } from "../../api/qnaApi";
import { useSelector } from "react-redux";
import { Link } from "react-router-dom";
import SearchComponent from "./SearchComponent";
import ButtonsComponent from "./ButtonsComponent";
import PageComponent from "../common/PageComponent";
import lock from '../../assets/lock.png';

const SelectComponent = () => {
    const pageSize = 10; // 페이지당 글 개수 (API와 동일해야 함)

    const initState = {
        content: [],
        totalPages: 0,
        currentPage: 0,
        totalElements: 0,
    };

    const [searchParams, setSearchParams] = useState({
        page: 0,
        size: 10,
        keyword: "",
        searchType: "ALL",
        startDate: "",
        endDate: "",
        sortBy: "createdAt",
        sortDirection: "DESC",
    });

    const loginState = useSelector((state) => state.loginState); // 로그인 상태 확인
    const [loading, setLoading] = useState(false);
    const [listData, setListData] = useState(initState.content);
    const [pageData, setPageData] = useState(initState);
    const [current, setCurrent] = useState(0);
    const [selectedQuestion, setSelectedQuestion] = useState([]);

    // 데이터 가져오기
    const fetchData = (params = searchParams) => {
        console.log(params);
        setLoading(true);
        if (params.keyword && params.keyword.trim() !== "") {
            getSelectSearch(
                params.keyword,
                params.searchType,
                params.page,
                params.sortBy,
                params.sortDirection,
                params.startDate,
                params.endDate,
            )
                .then((data) => {
                    setListData(data.content);
                    setPageData(data);
                    setLoading(false);
                })
                .catch(() => setLoading(false));
        } else {
            getSelect(params.page, params.sortDirection, params.sortBy, params.startDate,
                params.endDate)
                .then((data) => {
                    console.log(data);
                    setListData(data.content);
                    setPageData(data);
                    setLoading(false);
                })
                .catch(() => setLoading(false));
        }
    };

    useEffect(() => {
        fetchData();
    }, [current, searchParams.sortBy, searchParams.sortDirection]);

    // 검색 처리
    const handleSearch = (newSearchParams) => {
        const updatedParams = {
            ...searchParams,
            ...newSearchParams,
            page: 0, // 검색 시 첫 페이지로
        };
        setSearchParams(updatedParams);
        setCurrent(0);
        fetchData(updatedParams);
        setSelectedQuestion([]); // 선택 초기화
    };

    // 페이지 변경 (PageComponent용)
    const handlePageChange = (page) => {
        const updatedParams = {
            ...searchParams,
            page: page,
        };
        setSearchParams(updatedParams);
        setCurrent(page);
        fetchData(updatedParams);
        setSelectedQuestion([]); // 페이지 변경 시 선택 초기화
    };

    // 체크박스 변경 처리
    const handleCheckboxChange = (questionNum, isChecked) => {
        if (isChecked) {
            setSelectedQuestion((prev) => [...prev, questionNum]);
        } else {
            setSelectedQuestion((prev) => prev.filter((id) => id !== questionNum));
        }
    };

    // 전체 선택/해제
    const handleSelectAll = (isChecked) => {
        if (isChecked) {
            const allIds = listData.map((q) => q.questionNum);
            setSelectedQuestion(allIds);
        } else {
            setSelectedQuestion([]);
        }
    };

    // 날짜 포맷팅
    const formatDate = (dateString) => {
        if (!dateString) return "-";
        const date = new Date(dateString);
        return date.toLocaleDateString("ko-KR", {
            year: "numeric",
            month: "2-digit",
            day: "2-digit",
        });
    };

    return (
        <div className="max-w-screen-xl mx-auto my-10">
            <div className="min-blank">
                <div className="mb-6">
                    <h1 className="text-2xl font-bold text-gray-900">문의사항</h1>
                    <p className="text-gray-600 mt-1">
                        전체 {pageData.totalElements}건의 문의사항이 있습니다.
                    </p>
                </div>

                {/* 검색 컴포넌트 */}
                <SearchComponent onSearch={handleSearch} initialValues={searchParams} />

                {/* 질문 테이블 */}
                <div className="bg-white shadow-sm border border-gray-200 rounded-lg overflow-hidden">
                    {loading ? (
                        <div className="flex justify-center items-center py-12">
                            <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-500"></div>
                            <span className="ml-2 text-gray-600">로딩중...</span>
                        </div>
                    ) : (
                        <div className="overflow-x-auto">
                            <table className="w-full table-fixed">
                                <thead className="bg-gray-50 border-b border-gray-200">
                                    <tr>
                                        {loginState.role === "ADMIN" && (
                                            <th className="w-12 px-2 py-3 text-center align-middle">
                                                <input
                                                    type="checkbox"
                                                    checked={
                                                        selectedQuestion.length === listData.length &&
                                                        listData.length > 0
                                                    }
                                                    onChange={(e) => handleSelectAll(e.target.checked)}
                                                    className="w-4 h-4 text-blue-600 border-gray-300 rounded focus:ring-blue-500"
                                                />
                                            </th>
                                        )}
                                        <th className="w-16 px-2 py-3 text-center text-sm font-medium text-gray-900">
                                            번호
                                        </th>
                                        <th className="px-3 py-3 text-left text-sm font-medium text-gray-900">
                                            제목
                                        </th>
                                        <th className="w-28 px-3 py-3 text-center text-sm font-medium text-gray-900">
                                            작성자
                                        </th>
                                        <th className="w-36 px-3 py-3 text-center text-sm font-medium text-gray-900">
                                            작성일
                                        </th>
                                        <th className="w-24 px-3 py-3 text-center text-sm font-medium text-gray-900">
                                            조회수
                                        </th>
                                    </tr>
                                </thead>
                                <tbody className="bg-white divide-y divide-gray-200 ">
                                    {listData.length === 0 ? (
                                        <tr>
                                            <td
                                                colSpan={loginState.role === "ADMIN" ? 6 : 5}
                                                className="px-4 py-12 text-center text-gray-500"
                                            >
                                                등록된 문의사항이 없습니다.
                                            </td>
                                        </tr>
                                    ) : (
                                        listData.map((question, index) => (
                                            <tr
                                                key={question.questionNum}
                                            >
                                                {loginState.role === "ADMIN" && (
                                                    <td className="px-4 py-3 text-center">
                                                        <input
                                                            type="checkbox"
                                                            checked={selectedQuestion.includes(question.questionNum)}
                                                            onChange={(e) =>
                                                                handleCheckboxChange(question.questionNum, e.target.checked)
                                                            }
                                                            className="w-4 h-4 text-blue-600 border-gray-300 rounded focus:ring-blue-500"
                                                        />
                                                    </td>
                                                )}
                                                <td className="px-4 py-3 text-center text-sm text-gray-900">
                                                    {/* 1부터 시작하는 번호 */}
                                                    {index + 1 + current * pageSize}
                                                </td>
                                                <td className="px-4 py-3">
                                                    <div className="flex items-center">
                                                        {question.state === true && (
                                                            <img src={lock} className="w-4 h-4 mr-2 flex-shrink-0" alt="lock" />
                                                        )}
                                                        {/*<a href="https://www.flaticon.com/kr/free-icons/" title="자물쇠 아이콘">자물쇠 아이콘 제작자: Freepik - Flaticon</a> */}

                                                        <Link
                                                            to={loginState.role === "ADMIN" || loginState.memId === question.memId ?
                                                                `/question/detail/${question.questionNum}` :
                                                                question.state ? "#" : `/question/detail/${question.questionNum}`}
                                                            className={`text-sm font-medium text-gray-900 transition-colors truncate flex-1
                                                            ${(question.state && loginState.role !== "ADMIN" && loginState.memId !== question.memId) ? "text-gray-400 opacity-50 pointer-events-none" : ""}`
                                                            }
                                                        >
                                                            {question.title}
                                                        </Link>

                                                        {question.answerList && question.answerList.length > 0 ? (
                                                            <span className="ml-2 px-2 py-0.5 text-xs bg-blue-100 text-blue-700 rounded whitespace-nowrap flex-shrink-0">
                                                                답변완료
                                                            </span>
                                                        ) : (
                                                            <span className="ml-2 px-2 py-0.5 text-xs bg-gray-100 text-gray-600 rounded whitespace-nowrap flex-shrink-0">
                                                                답변대기
                                                            </span>
                                                        )}
                                                    </div>
                                                </td>

                                                <td className="px-4 py-3 text-center text-sm text-gray-500">
                                                    {question.memId}
                                                </td>
                                                <td className="px-4 py-3 text-center text-sm text-gray-500">
                                                    {formatDate(question.createdAt)}
                                                </td>
                                                <td className="px-4 py-3 text-center text-sm text-gray-500">
                                                    {question.view?.toLocaleString() ?? 0}
                                                </td>
                                            </tr>
                                        ))
                                    )}
                                </tbody>
                            </table>
                        </div>
                    )}
                </div>

                {/* 페이지네이션 - PageComponent 사용 */}
                {pageData.totalPages > 0 && (
                    <div className="flex justify-center mt-6">
                        <PageComponent
                            totalPages={pageData.totalPages}
                            current={current}
                            setCurrent={handlePageChange}
                        />
                    </div>
                )}

                {/* 하단 버튼 */}
                <div className="mb-8">
                    <ButtonsComponent
                        isAdmin={loginState.role === "ADMIN"}
                        selectedQuestion={selectedQuestion}
                        onDelete={() => {
                            fetchData();
                            setSelectedQuestion([]);
                        }}
                    />
                </div>
            </div>
        </div>
    );
};

export default SelectComponent;