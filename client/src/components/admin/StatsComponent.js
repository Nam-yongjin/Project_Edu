import { useEffect, useState } from "react";
import Plot from "react-plotly.js";
import { getVisitorStats, getMemberRoleStats, getEventCategoryStats } from "../../api/adminApi";

const StatsComponent = () => {
    const [visitors, setVisitors] = useState({ total_visitors: 0, daily_visitors: 0, trend: [] });
    const [roles, setRoles] = useState([]);
    const [evtCategorys, setEvtCategorys] = useState([]);

    useEffect(() => {
        getVisitorStats().then(setVisitors)
            .catch(error => {
                console.error("방문자수를 불러올 수 없습니다.:", error);
            });
        getMemberRoleStats().then(setRoles)
            .catch(error => {
                console.error("회원유형을 불러올 수 없습니다.:", error);
            });
        getEventCategoryStats().then(setEvtCategorys)
            .catch(error => {
                console.error("프로그램을 불러올 수 없습니다.:", error);
            });
    }, []);

    return (
        <div className="max-w-screen-xl mx-auto my-10 ">
            <div className="min-blank ">
                {/* 일일 방문자, 총합 방문자 */}
                <div className="mb-10 overflow-x-auto ">
                    <div className="newText-2xl font-semibold mb-4">방문자 수</div>
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
                                    title: "방문자 수",
                                    dtick: 1
                                }
                            }}
                            className="w-full"
                        />
                    </div>
                </div>

                {/* 회원 유형별 가입자 수, 비율 */}
                <div>
                    <div className="newText-2xl font-semibold">회원 유형</div>
                    <div className="flex justify-center">
                        <Plot
                            data={[
                                {
                                    // ADMIN 제외
                                    labels: roles.filter(r => r.role !== "ADMIN").map(r => r.role),
                                    values: roles.filter(r => r.role !== "ADMIN").map(r => r.count),
                                    type: "pie",
                                    textinfo: "label+percent",
                                },
                            ]}
                            layout={{ title: "회원 역할 비율" }}
                        />
                    </div>
                </div>

                {/* 프로그램 카테고리별 등록 수, 비율 */}
                <div>
                    <div className="newText-2xl font-semibold">프로그램 카테고리</div>
                    <div className="flex justify-center">
                        <Plot
                            data={[
                                {
                                    labels: evtCategorys.map(r => r.category),
                                    values: evtCategorys.map(r => r.count),
                                    type: "pie",
                                    textinfo: "label+percent",
                                },
                            ]}
                            layout={{ title: "프로그램 카테고리 비율" }}
                        />
                    </div>
                </div>
            </div>
        </div>
    );
};
export default StatsComponent;
