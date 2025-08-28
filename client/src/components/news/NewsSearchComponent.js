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

    // 오늘 날짜 구하기 (YYYY-MM-DD 형식)
    const getTodayString = () => {
        const today = new Date();
        return today.toISOString().split('T')[0];
    };

    // 날짜 유효성 검사
    const validateDates = (startDate, endDate) => {
        const today = getTodayString();
        const errors = [];

        // 현재 날짜 이후 날짜 선택 제한
        if (startDate && startDate > today) {
            errors.push("시작일은 오늘 날짜 이후로 선택할 수 없습니다.");
        }
        if (endDate && endDate > today) {
            errors.push("종료일은 오늘 날짜 이후로 선택할 수 없습니다.");
        }

        // 시작일이 종료일보다 이후인지 확인
        if (startDate && endDate && startDate > endDate) {
            errors.push("시작일은 종료일보다 이후일 수 없습니다.");
        }

        // 기간 최대 범위 제한 (1년)
        if (startDate && endDate) {
            const start = new Date(startDate);
            const end = new Date(endDate);
            const diffTime = Math.abs(end - start);
            const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
            
            if (diffDays > 365) {
                errors.push("검색 기간은 최대 1년까지만 설정 가능합니다.");
            }
        }

        return errors;
    };

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setSearchForm(prev => ({
            ...prev,
            [name]: value
        }));
    };

    const handleDateChange = (e) => {
        const { name, value } = e.target;
        
        // 날짜 형식 검증
        if (value && !/^\d{4}-\d{2}-\d{2}$/.test(value)) {
            alert("올바른 날짜 형식(YYYY-MM-DD)을 입력해주세요.");
            return;
        }

        const newSearchForm = {
            ...searchForm,
            [name]: value
        };

        // 실시간 유효성 검사
        const errors = validateDates(
            name === 'startDate' ? value : newSearchForm.startDate,
            name === 'endDate' ? value : newSearchForm.endDate
        );

        if (errors.length > 0) {
            alert(errors.join('\n'));
            return;
        }

        setSearchForm(newSearchForm);
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        
        // 제출 전 최종 유효성 검사
        const errors = validateDates(searchForm.startDate, searchForm.endDate);
        if (errors.length > 0) {
            alert(errors.join('\n'));
            return;
        }

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
                            className="w-full input-focus newText-base"
                        />
                    </div>
                </div>

                {/* 기간 검색 */}
                    <div className="flex flex-wrap items-center gap-4">
                        <div className="flex items-center gap-2">
                            <label className="newText-sm font-medium text-gray-700">기간:</label>
                            <input
                                type="date"
                                name="startDate"
                                value={searchForm.startDate}
                                onChange={handleDateChange}
                                max={getTodayString()} // 오늘 날짜까지만 선택 가능
                                className="border border-gray-300 rounded px-3 py-1.5 newText-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                                title="시작일을 선택하세요 (오늘 날짜까지만 선택 가능)"
                            />
                            <span className="text-gray-500">~</span>
                            <input
                                type="date"
                                name="endDate"
                                value={searchForm.endDate}
                                onChange={handleDateChange}
                                max={getTodayString()} // 오늘 날짜까지만 선택 가능
                                min={searchForm.startDate || undefined} // 시작일 이후만 선택 가능
                                className="border border-gray-300 rounded px-3 py-1.5 newText-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                                title="종료일을 선택하세요 (시작일 이후, 오늘 날짜까지만 선택 가능)"
                            />
                        </div>               
                    </div>

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