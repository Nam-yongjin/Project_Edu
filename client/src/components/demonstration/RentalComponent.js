import { useEffect, useState } from "react";
import SearchComponent from "../../components/demonstration/SearchComponent";
import { getRental, getRentalSearch } from "../../api/demApi";
import PageComponent from "../common/PageComponent";

const RentalComponent = () => {
    const initState = {
        content: [], totalPages: 0, currentPage: 0
    };

    const [search, setSearch] = useState();
    const [type, setType] = useState();
    const [pageData, setPageData] = useState(initState);
    const [current, setCurrent] = useState(0);
    const [listData, setListData] = useState({ content: [] });

    const [sortBy, setSortBy] = useState("applyAt");
    const [sort, setSort] = useState("desc");

    const fetchData = () => {
        if (search && search.trim() !== "") {
            getRentalSearch(search, type, current, sortBy, sort).then(data => {
                setListData(data);
                setPageData(data); 
            });
        } else {
            getRental(current).then(data => {
                setListData(data);
                 setPageData(data); 
            });
        }
    };

    useEffect(() => {
        fetchData(); // 최초 로딩, 페이지 변경, 정렬 변경에 따라
    }, [current, sortBy, sort]);

    const onSearchClick = () => {
        setCurrent(0); // 검색 시 페이지 초기화
        fetchData();   // 검색 실행
    };

    const handleSortChange = (column) => {
        if (sortBy === column) {
            setSort(prev => prev === "asc" ? "desc" : "asc");
        } else {
            setSortBy(column);
            setSort("");
        }
    };

    return (
        <>
            <table border="1" cellPadding="8" style={{ borderCollapse: 'collapse', width: '100%' }}>
                <thead>
                    <tr>
                        <th>대표 이미지</th>
                        <th onClick={() => handleSortChange("demName")}>demName</th>
                        <th onClick={() => handleSortChange("companyName")}>companyName</th>
                        <th onClick={() => handleSortChange("itemNum")}>itemNum</th>
                        <th onClick={() => handleSortChange("startDate")}>startDate</th>
                        <th onClick={() => handleSortChange("endDate")}>endDate</th>
                        <th onClick={() => handleSortChange("applyAt")}>applyAt</th>
                    </tr>
                </thead>
                <tbody>
                    {listData.content.map((item) => {
                        const mainImage = item.imageList?.find(img => img.isMain === true);
                        return (
                            <tr key={item.demNum}>
                                <td>
                                    {mainImage
                                        ? <img
                                            src={mainImage.imageUrl}
                                            alt={mainImage.imageName}
                                            style={{ width: '80px', height: 'auto', objectFit: 'contain' }}
                                        />
                                        : '이미지 없음'}
                                </td>
                                <td>{item.demName}</td>
                                <td>{item.companyName}</td>
                                <td>{item.itemNum}</td>
                                <td>{item.startDate}</td>
                                <td>{item.endDate}</td>
                                <td>{item.applyAt}</td>
                            </tr>
                        );
                    })}
                </tbody>
            </table>

            <SearchComponent
                search={search}
                setSearch={setSearch}
                type={type}
                setType={setType}
                onSearchClick={onSearchClick}
            />

            <div className="flex justify-center">
                <PageComponent
                    totalPages={pageData.totalPages}
                    current={current}
                    setCurrent={setCurrent}
                />
            </div>
        </>
    );
};

export default RentalComponent;
