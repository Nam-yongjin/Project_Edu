import { useEffect, useState } from "react";
import SearchComponent from "../../components/demonstration/SearchComponent"
import { getRental, getRentalSearch } from "../../api/demApi";
import PageComponent from "../common/PageComponent";
const RentalComponent = () => {
    const initState = { // 페이지 초기화
        content: [], totalPages: 0, currentPage: 0
    }

    const [search, setSearch] = useState();
    const [type, setType] = useState();
    const [pageData, setPageData] = useState(initState); // 페이지 데이터
    const [current, setCurrent] = useState(pageData.currentPage); // 현재 페이지 값
    const [listData, setListData] = useState([]); // 받아올 content 값


    useEffect(() => {
        if (search === undefined || search === null || search === '') {
              getRental(current).then(data => {
               setListData(data); 
            })
        }
    }, [current])

    const onSearchClick=()=> {
         if (search !== null && search!== '') {
              getRentalSearch(search,type,current).then(data => {
                setListData(data);
            })
        }
    }


    return (
        <>
        <div>
            
        </div>
        <SearchComponent search={search} setSearch={setSearch} type={type} setType={setType} onSearchClick={onSearchClick}/>
        <div className="flex justify-center">
                <PageComponent totalPages={pageData.totalPages} current={current} setCurrent={setCurrent} />
            </div>
        </>
    );
}
export default RentalComponent;