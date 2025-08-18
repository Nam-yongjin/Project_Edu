import { useEffect, useState } from "react";
import Plot from "react-plotly.js";
import { getVisitorStats, getMemberRoleStats, getEventCategoryStats, getFacTimesStats } from "../../api/adminApi";

const StatsComponent = () => {
    const [visitors, setVisitors] = useState({ total_visitors: 0, daily_visitors: 0, trend: [] });
    const [members, setMembers] = useState({ total_member: 0, trend: [] });
    const [evtCategorys, setEvtCategorys] = useState([]);
    const [popularFacTimes, setPopularFacTimes] = useState([]);

    useEffect(() => {
        getVisitorStats().then(setVisitors)
            .catch(error => {
                console.error("방문자수를 불러올 수 없습니다.:", error);
            });
        getMemberRoleStats().then(setMembers)
            .catch(error => {
                console.error("회원유형을 불러올 수 없습니다.:", error);
            });
        getEventCategoryStats().then(setEvtCategorys)
            .catch(error => {
                console.error("프로그램을 불러올 수 없습니다.:", error);
            });
        getFacTimesStats().then(setPopularFacTimes)
            .catch(error => {
                console.error("공간내역을 불러올 수 없습니다.:", error);
            });
    }, []);

    return (
        <div className="max-w-screen-xl mx-auto my-10 text-center">
            <div className="min-blank ">
                {/* 일일 방문자, 총합 방문자 */}
                <div className="mb-10 overflow-x-auto border-b border-gray-300">
                    <div className="newText-2xl font-semibold mb-4">방문자</div>
                    <div className="newText-lg">
                        <div>오늘 방문자: {visitors.daily_visitors} 명</div>
                        <div>총 방문자: {visitors.total_visitors} 명</div>
                    </div>
                    <div className="">
                        <Plot
                            data={[
                                {
                                    x: visitors.trend.map(item => item.date),
                                    y: visitors.trend.map(item => item.count),
                                    type: "scatter",
                                    mode: "lines+markers",
                                    marker: { color: "blue" }
                                }
                            ]}
                            layout={{
                                title: "일별 방문자 추세",
                                xaxis: {
                                    title: "날짜",
                                    tickformat: "%Y-%m-%d",
                                    type: 'date'
                                },
                                yaxis: {
                                    title: "방문자 수"
                                }
                            }}
                            className="w-full"
                        />
                    </div>
                </div>

                {/* 회원 유형별 가입자 수, 비율 */}
                <div className="border-b border-gray-300 pb-10">
                    <div className="newText-2xl font-semibold my-4">회원</div>
                    <div className="newText-lg">
                        <div>총 회원 수: {members.total_member} 명</div>
                    </div>
                    <div className="flex justify-center mt-4">
                        <div className="page-shadow pt-10 ">
                            <div className="newText-xl font-semibold">
                                회원 유형별 수 및 비율
                            </div>
                            <Plot
                                data={[
                                    {
                                        // ADMIN 제외
                                        labels: members.trend.filter(item => item.role !== "ADMIN").map(item => item.role),
                                        values: members.trend.filter(item => item.role !== "ADMIN").map(item => item.count),
                                        type: "pie",
                                        textinfo: "label+percent",
                                    },
                                ]}
                                layout={{ title: "회원 유형 비율" }}
                            />
                        </div>
                    </div>
                </div>

                {/* 프로그램 카테고리별 등록 수, 비율 */}
                <div className="">
                    <div className="newText-2xl font-semibold my-10">프로그램</div>
                    <div className="flex flex-wrap justify-center overflow-x-auto border-b border-gray-300">
                        <div className="page-shadow pt-10">
                            <div className="newText-xl font-semibold">
                                프로그램별 등록 수
                            </div>
                            <Plot
                                data={[
                                    {
                                        labels: evtCategorys.map(item => item.category),
                                        values: evtCategorys.map(item => item.count),
                                        type: "pie",
                                        textinfo: "label+percent",
                                    },
                                ]}
                                layout={{ title: "프로그램 수 비율" }}
                            />
                        </div>
                        <div className="page-shadow my-10 pt-10">
                            <div className="newText-xl font-semibold">
                                프로그램별 신청자 수
                            </div>
                            <Plot
                                data={[
                                    {
                                        labels: evtCategorys.map(item => item.category),
                                        values: evtCategorys.map(item => item.curr_capacity),
                                        type: "pie",
                                        textinfo: "label+percent",
                                    },
                                ]}
                                layout={{ title: "프로그램 신청자 수 비율" }}

                            />
                        </div>
                    </div>
                </div>

                {/* 공간 대여 인기 시간대 */}
                <div className="my-10 overflow-x-auto border-b border-gray-300">
                    <div className="newText-2xl font-semibold mb-4">공간</div>
                    <div className="newText-lg font-semibold">
                        시간대별 공간 예약 수
                    </div>
                    <div className="">
                        <Plot
                            data={[
                                {
                                    x: popularFacTimes.map(item => item.label),
                                    y: popularFacTimes.map(item => item.count),
                                    type: "bar",
                                    marker: { color: "orange" }
                                }
                            ]}
                            layout={{
                                title: "공간 인기 예약 시간대",
                                xaxis: {
                                    title: "시간대",
                                },
                                yaxis: { title: "예약 건수" }
                            }}
                            className="w-full"
                        />
                    </div>
                </div>
            </div>
        </div >
    );
};
export default StatsComponent;
