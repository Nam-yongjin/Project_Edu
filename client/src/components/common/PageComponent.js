// props 전달값: 전체 페이지, 현재 페이지, 현재 페이지 set함수(현재 페이지는 사용하는 컴포넌트에서 setState로 설정해야함)
const PageComponent = ({ totalPages, current, setCurrent }) => { 
    const currentGroup = Math.floor(current / 10); // 현재 페이지 그룹 (0:0~9 1:10~19)
    const startPage = currentGroup * 10; // 현재 그룹의 시작 페이지
    const endPage = Math.min(startPage + 10, totalPages); // 현재 그룹의 마지막 페이지

    return (
        <div className="flex gap-x-2 items-center">
                {startPage>0 ? <div><button onClick={()=>setCurrent(Math.floor(currentGroup-1)*10)}>&lt;</button></div> : <div></div>} 
                {startPage>0 ? <div><button onClick={()=>setCurrent(0)}>&lt;&lt;</button></div> : <div></div>}
            {Array.from({ length: endPage - startPage }).map((_, i) => {
                const pageIndex = startPage + i;
                return (
                    <button
                        key={pageIndex}
                        onClick={() => setCurrent(pageIndex)} className={pageIndex === current ? "font-bold" : ""}
                    >
                        {pageIndex + 1}
                    </button>
                );
            })}
                {currentGroup!==Math.floor(totalPages/10) ? <div><button onClick={()=>setCurrent(Math.floor(currentGroup+1)*10)}>&gt;</button></div> : <div></div>}
                {currentGroup!==Math.floor(totalPages/10) ?<div><button onClick={()=>setCurrent(Math.floor(totalPages/10)*10)}>&gt;&gt;</button></div> : <div></div>}
        </div>
    );
};
export default PageComponent;