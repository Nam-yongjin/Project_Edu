import { useEffect, useState } from "react";
import { getSelect,getSelectSearch } from "../../api/qnaApi";
import useMove from "../../hooks/useMove";
const SelectComponent = () => {
     const initState = {
        content: [],
        totalPages: 0,
        currentPage: 0,
    };
     const [sortBy, setSortBy] = useState("applyAt"); // 정렬 칼럼명
    const [sort, setSort] = useState("asc"); // 정렬 방식
    const [listData, setListData] = useState({ content: [] }); // 받아올 content 데이터
       const [pageData, setPageData] = useState(initState); // 페이지 데이터
       const [search, setSearch] = useState(); // 검색어
    const [type, setType] = useState("companyName"); // 검색 타입
      const [current, setCurrent] = useState(0); // 현재 페이지
      const { moveToPath } = useMove(); // 원하는 곳으로 이동할 변수

     const fetchData = () => {
            if (search && search.trim() !== "") {
                getSelectSearch(search, type, current, sortBy, sort).then((data) => {
                    setListData(data);
                    setPageData(data);
                });
            } else {
                getSelect(current, sort, sortBy).then((data) => {
                    setListData(data);
                    setPageData(data);
                });
            }
        };
    
        useEffect(() => {
            fetchData();
        }, [current, sort, sortBy]);
    return (
        <div>Component입니다.</div>
    )
}
export default SelectComponent;