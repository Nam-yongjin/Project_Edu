import React from "react";

const ResRequestModal = ({ show, data, onClose, getStateLabel }) => {
    if (!show) return null;

    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50">
            <div className="bg-white rounded-xl page-shadow p-6 max-w-lg w-full min-blank">
                {/* 헤더 */}
                <div className="flex items-center justify-between mb-6">
                    <div className="flex items-center gap-3">
                        <div className="w-10 h-10 bg-blue-100 rounded-full flex items-center justify-center">
                            <svg className="w-6 h-6 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                            </svg>
                        </div>
                        <h2 className="newText-2xl font-bold text-gray-800">신청 내역</h2>
                    </div>
                </div>

                {/* 내용 */}
                <div className="space-y-4 max-h-96 overflow-y-auto pr-2">
                    {data.length === 0 ? (
                        <div className="text-center py-12">
                            <p className="text-gray-500 newText-lg">신청 내역이 없습니다</p>
                        </div>
                    ) : (
                        data.map((req, idx) => (
                            <div key={idx} className="bg-gradient-to-r from-gray-50 to-gray-100 rounded-lg p-4 border border-gray-200 hover:shadow-md transition-all duration-200">
                                <div className="flex items-start justify-between">
                                    <div className="flex-1">
                                        <div className="flex items-center gap-3 mb-2">
                                            <div className={`w-8 h-8 rounded-full flex items-center justify-center ${req.type === "EXTEND"
                                                ? "bg-green-100 text-green-600"
                                                : "bg-orange-100 text-orange-600"
                                                }`}>
                                                {req.type === "EXTEND" ? "⏳" : "↩️"}
                                            </div>
                                            <h3 className="newText-lg font-semibold text-gray-800">
                                                {req.type === "EXTEND" ? "연장 신청" : "반납 신청"}
                                            </h3>
                                        </div>

                                        {req.type === "EXTEND" && req.updateDate && (
                                            <div className="flex items-center gap-2 mb-2 text-gray-600">
                                                <span className="newText-sm">신청 날짜: {req.updateDate}</span>
                                            </div>
                                        )}

                                        <div className="flex items-center gap-2">
                                            <span className="newText-sm text-gray-500">상태:</span>
                                            <span className={`px-3 py-1 rounded-full newText-xs font-medium ${req.state === "ACCEPT"
                                                ? "bg-green-100 text-green-800"
                                                : req.state === "REJECT"
                                                    ? "bg-red-100 text-red-800"
                                                    : req.state === "WAIT"
                                                        ? "bg-yellow-100 text-yellow-800"
                                                        : "bg-gray-100 text-gray-800"
                                                }`}>
                                                {getStateLabel(req.state)}
                                            </span>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        ))
                    )}
                </div>

                {/* 푸터 */}
                <div className="mt-6 pt-4 border-t border-gray-200 flex justify-end">
                    <button className="normal-button px-4 py-2 rounded-lg" onClick={onClose}>
                        닫기
                    </button>
                </div>
            </div>
        </div>
    );
};

export default ResRequestModal;
