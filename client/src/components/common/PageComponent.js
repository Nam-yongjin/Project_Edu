// props 전달값: 전체 페이지, 현재 페이지, 현재 페이지 set함수(현재 페이지는 사용하는 컴포넌트에서 setState로 설정해야함)
const PageComponent = ({ totalPages, current, setCurrent }) => {
    const currentGroup = Math.floor(current / 10); // 현재 페이지 그룹 (0:0~9 1:10~19)
    const startPage = currentGroup * 10; // 현재 그룹의 시작 페이지
    const endPage = Math.min(startPage + 10, totalPages); // 현재 그룹의 마지막 페이지
    const lastGroup = Math.floor((totalPages - 1) / 10); // 마지막 페이지 그룹

    return (
        <div className="flex sm:gap-x-2 gap-x-1 items-center">
            <button
                onClick={() => setCurrent(0)}
                disabled={current === 0}
                className={`newText-sm px-4 py-2 rounded-md font-semibold normal-button cursor-pointer
                    flex justify-center items-center lg:w-[36px] sm:w-[32px] w-[28px] lg:h-[36px] sm:h-[32px] h-[28px]`}
            >
                &lt;&lt;
            </button>
            <button
                onClick={() => currentGroup === 0 ? setCurrent(0) : setCurrent(Math.max((currentGroup - 1) * 10, 0))}
                disabled={current === 0}
                className={`newText-sm px-4 py-2 rounded-md font-semibold normal-button cursor-pointer
                    flex justify-center items-center lg:w-[36px] sm:w-[32px] w-[28px] lg:h-[36px] sm:h-[32px] h-[28px]`}
            >
                &lt;
            </button>
            {Array.from({ length: endPage - startPage }).map((_, i) => {
                const pageIndex = startPage + i;
                return (
                    <button
                        key={pageIndex}
                        onClick={() => setCurrent(pageIndex)}
                        className={`newText-sm px-4 py-2 rounded-md font-semibold flex justify-center items-center
                            lg:w-[36px] sm:w-[32px] w-[28px] lg:h-[36px] sm:h-[32px] h-[28px]
                            ${pageIndex === current
                                ? "positive-button"
                                : "normal-button"
                            }`}

                    >
                        {pageIndex + 1}
                    </button>
                );
            })}
            <button
                onClick={() => currentGroup === lastGroup ? setCurrent(totalPages - 1) : setCurrent((currentGroup + 1) * 10)}
                disabled={current === totalPages - 1}
                className={`newText-sm px-4 py-2 rounded-md font-semibold normal-button cursor-pointer
                    flex justify-center items-center lg:w-[36px] sm:w-[32px] w-[28px] lg:h-[36px] sm:h-[32px] h-[28px]`}
            >
                &gt;
            </button>
            <button
                onClick={() => setCurrent(totalPages - 1)}
                disabled={current === totalPages - 1}
                className={`newText-sm px-4 py-2 rounded-md font-semibold normal-button cursor-pointer
                    flex justify-center items-center lg:w-[36px] sm:w-[32px] w-[28px] lg:h-[36px] sm:h-[32px] h-[28px]`}
            >
                &gt;&gt;
            </button>
        </div>
    );
};
export default PageComponent;