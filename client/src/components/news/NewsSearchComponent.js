import React, { useState } from "react";
import { useSelector } from "react-redux";

const NewsSearchComponent = ({ onSearch, initialValues }) => {
    const loginState = useSelector((state) => state.loginState);
    const [searchForm, setSearchForm] = useState({
        keyword: initialValues?.keyword || "",
        searchType: initialValues?.searchType || "ALL",
        startDate: initialValues?.startDate || "",
        endDate: initialValues?.endDate || ""
    });

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setSearchForm(prev => ({
            ...prev,
            [name]: value
        }));
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        const searchParams = {
            ...initialValues,
            ...searchForm,
            page: 0 //검색 시 첫 페이지로
        };
        onSearch(searchParams);
    };

    const handleReset = () => {
        const resetForm = {
            keyword: "",
            searchType: "ALL",
            startDate: "",
            endDate: ""
        };
        setSearchForm(resetForm);
        const searchParams = {
            ...initialValues,
            ...resetForm,
            page: 0
        };
        onSearch(searchParams);
    };

    return (
        <div className="bg-gray-50 border border-gray-200 rounded-lg p-4 mb-6">
            <form onSubmit={handleSubmit} className="space-y-4">
                {/* 검색 타입, 키워드 */}
                <div className="flex flex-wrap items-center gap-4">
                    <div className="flex items-center gap-2">
                        <label className="newText-sm font-medium text-gray-700">검색조건:</label>
                        <select
                            name="searchType"
                            value={searchForm.searchType}
                            onChange={handleInputChange}
                            className="border border-gray-300 rounded px-3 py-1.5 newText-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                        >
                            <option value="ALL">전체</option>
                            <option value="TITLE">제목</option>
                            <option value="CONTENT">내용</option>
                            <option value="WRITER">작성자</option>
                        </select>
                    </div>

                    <div className="flex-1 min-w-0">
                        <input
                            type="text"
                            name="keyword"
                            value={searchForm.keyword}
                            onChange={handleInputChange}
                            placeholder="검색어를 입력하세요"
                            className="w-full input-focus"
                        />
                    </div>
                </div>

                {/* 기간 검색 */}
                {loginState.role === 'ADMIN' ? (
                    <div className="flex flex-wrap items-center gap-4">
                        <div className="flex items-center gap-2">
                            <label className="newText-sm font-medium text-gray-700">기간:</label>
                            <input
                                type="date"
                                name="startDate"
                                value={searchForm.startDate}
                                onChange={handleInputChange}
                                className="border border-gray-300 rounded px-3 py-1.5 newText-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                            />
                            <span className="text-gray-500">~</span>
                            <input
                                type="date"
                                name="endDate"
                                value={searchForm.endDate}
                                onChange={handleInputChange}
                                className="border border-gray-300 rounded px-3 py-1.5 newText-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                            />
                        </div>
                    </div>
                ) : (<></>)}

                {/* 버튼 */}
                <div className="flex justify-center gap-2">
                    <button
                        type="submit"
                        className="dark-button newText-sm"
                    >검색
                    </button>
                    <button
                        type="button"
                        onClick={handleReset}
                        className="dark-button newText-sm"
                    >초기화
                    </button>  
                </div>
            </form>
        </div>
    );
};

export default NewsSearchComponent;