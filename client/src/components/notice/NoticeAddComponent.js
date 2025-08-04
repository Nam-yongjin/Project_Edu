import { useRef, useState } from "react";

const NoticeAddComponent = ({notice, setNotice, onAdd, onCancel}) => {
    //파일 업로드에서 파일 선택했을 때 파일들을 상태에 저장하는 로직
    const handleFileChange = (e) => {
        //e.target.files는 특수한 객체라 .map으로 바로 쓸 수 없어서 Array.from을 활용해 일반배열로 바꿔줌
        const files = Array.from(e.target.files);
        setNotice({ ...notice, files }); //기존 상태 유지하면서 files만 새로 바꿈
        };
    const handleRemoveFile = (indexToRemove) => {
            const updatedFiles = [...notice.files];
            updatedFiles.splice(indexToRemove, 1);
            setNotice({ ...notice, files: updatedFiles });
    };

    return (
        <div className="max-w-3xl mx-auto px-10 p-6 bg-white rounded-xl shadow-md space-y-6">
            <h2 className="text-2xl font-bold">공지사항 등록</h2>
            {/* 제목 */}
            <div>
                <label className="block font-medium mb-1">제목</label>
                <input
                    type="text"
                    value={notice.title}
                    onChange={(e) => setNotice({ ...notice, title: e.target.value })}
                    placeholder="제목을 입력하세요"
                    className="w-full border border-gray-300 rounded px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-400"
                />
            </div>
            {/* 내용 */}
            <div>
                <label className="block font-medium mb-1">내용</label>
                <textarea
                    value={notice.content}
                    onChange={(e) => setNotice({ ...notice, content:e.target.value })}
                    placeholder="내용을 입력하세요"
                    rows={6} //아무것도 입력하지 않았을 때 보이는 줄의 개수
                    className="w-full border border-gray-300 rounded px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-400"
                />
            </div>
            {/* 첨부파일 */}
            <div>
                <label className="block font-medium mb-1">첨부파일</label>
                <input
                    type="file"
                    multiple //여러 개의 파일을 한 번에 선택
                    accept=".jpg, .jpeg, .png, .pdf, .hwp, .doc, .docx" //업로드 파일 유형 제한
                    onChange={handleFileChange}
                    className="w-full"
                />
            </div>

            {/* 파일 미리 보기 */}
            {notice.files.length > 0 && (
                <div>
                    <strong className="block mb-2">첨부 파일:</strong>
                    <ul className="flex overflow-x-auto space-x-4">
                        {notice.files.map((file, index) => (
                            <li key={index}
                                className="relative flex-shrink-0 w-[120px] h-[120px] border rounded p-1 bg-gray-50 group">
                                {/* 삭제 버튼 */}
                                <button
                                    type="button"
                                    onClick={() => handleRemoveFile(index)}
                                    className="absolute top-1 right-1 bg-black bg-opacity-50 text-white rounded-full w-6 h-6 text-xs flex items-center justify-center opacity-0 group-hover:opacity-100 transition"
                                    title="삭제"
                                >✕</button>

                                {file.type.startsWith("image/") ? (
                                    <img
                                        src={URL.createObjectURL(file)}
                                        alt={`preview-${index}`}
                                        className="w-full h-full object-cover rounded"
                                    />
                                ) : (
                                    <div className="flex items-center justify-center h-full text-center px-2 text-sm">
                                        <span>{file.name}</span>
                                    </div>
                                )}
                            </li>
                        ))}
                    </ul>
                </div>
            )}

            <div className="flex justify-end space-x-4">
                <button
                    className="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600 transition-colors text-sm font-medium inline-block"
                    onClick={onAdd} >등록</button>
                <button
                    className="px-4 py-2 bg-red-500 text-white rounded hover:bg-red-600 transition-colors text-sm font-medium inline-block"
                    onClick={onCancel}>취소</button>
            </div>
        </div>    
    );
}

export default NoticeAddComponent;
