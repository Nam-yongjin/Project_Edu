import { useState } from "react";
import useMove from "../../hooks/useMove";
import { createNotice } from "../../api/noticeApi";

const NoticeAddComponent = () => {
    const { moveToPath } = useMove();
    //초기값 객체
    const initState = {
        title: '',
        content: '',
        isPinned: false,
        files: [] //새로 선택한 파일로 변경
    };

    const [notice, setNotice] = useState(initState);
    //initState는 초기값 객체, useState는 상태 만드는 훅
    //둘 다 쓰는 게 정석 -> 폼 초기화 할 때 편해짐

    //에러 객체(아무것도 안 쓰면 공지 등록이 안 되도록)
    const [errors, setErrors] = useState({
        title: '',
        content: ''
    });

    const getByteLength = (str) => new Blob([str]).size;
    
    //내용 작성칸 자동 조절
    const autoTextareaHeight = (e) => {
        e.target.style.height = 'auto';
        e.target.style.height = e.target.scrollHeight + 'px';
    };

    const validateForm = () => {
        const newErrors = {};
    
        //제목 검사
        if (!notice.title.trim()) {
            newErrors.title = "제목을 입력하세요.";
        } else if (notice.title.length > 100) {
            newErrors.title = "제목은 최대 100자까지 입력 가능합니다.";
        }
    
        //내용 검사
        if (!notice.content.trim()) {
            newErrors.content = "내용을 입력하세요.";
        } else if (getByteLength(notice.content) > 65535) {
            newErrors.content = "내용이 너무 깁니다. 글자 수를 줄여주세요.";
        }
    
        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    //파일 업로드에서 파일 선택했을 때 파일들을 상태에 저장하는 로직
    const handleFileChange = (e) => {
        //e.target.files는 특수한 객체라 .map으로 바로 쓸 수 없어서 Array.from을 활용해 일반배열로 바꿔줌
        const files = Array.from(e.target.files);
        setNotice({ ...notice, files }); //기존 상태 유지하면서 files만 새로 바꿈
        };

    //파일 등록 취소
    const handleRemoveFile = (indexToRemove) => {
            const updatedFiles = [...notice.files];
            updatedFiles.splice(indexToRemove, 1);
            setNotice({ ...notice, files: updatedFiles });
    };

    //등록 버튼
    const handleAdd = async () => {
        //유효성 검사 실행
        if (!validateForm()){
            return;
        }

        try {
            const formData = new FormData();
            //dto 객체를 JSON으로 감싸서 Blob로 만들기(하나의 개체로 구성된 바이너리 데이터의 집합)
            const dto = {
                title: notice.title || "",
                content: notice.content || "",
                isPinned: notice.isPinned ?? false,
                files: []  // 빈 배열로 초기화
            };
            const dtoBlob = new Blob([JSON.stringify(dto)], { type: "application/json" });
            formData.append("dto", dtoBlob); //백엔드 @RequestPart("dto")와 일치해야 함

            //파일 추가
            if (notice.files && Array.isArray(notice.files)) {
                notice.files.forEach(file => {
                    if (file) {  // null 체크
                        formData.append("files", file);
                    }
                });
            }

            //전송
            await createNotice(formData); //경로 포함된 함수

            alert("공지사항이 등록되었습니다.");
            moveToPath("/notice/NoticeList");

        } catch (error) {
            console.error("공지사항 등록 실패:", error);

             // 백엔드 유효성 에러 처리
            if (error.response?.status === 400 && error.response?.data?.errors) {
                const backendErrors = {};
                error.response.data.errors.forEach(err => {
                    backendErrors[err.field] = err.defaultMessage;
                });
                setErrors(backendErrors);
            } else {
                alert("등록 중 오류가 발생했습니다.");
            }
        } 
    };

    //취소 버튼
    const handleCancel = () => {
        if (window.confirm("작성을 취소하고 목록으로 돌아가시겠습니까?")) {
            moveToPath("/notice/NoticeList");
        }
    }   

   return (
        <div className="max-w-screen-xl mx-auto my-10">
            <div className="min-blank mt-4 px-10 p-6 bg-white page-shadow space-y-6">
                <h2 className="newText-2xl my-4 font-bold">공지사항 등록</h2>
                <hr className="border-gray-200 my-4" />
                {/* 제목 */}
                <div>
                    <label className="block font-medium mb-1">제목</label>
                    <input
                        type="text"
                        value={notice.title}
                        onChange={(e) => setNotice({ ...notice, title: e.target.value })}
                        placeholder="제목을 입력하세요"
                        className="w-full input-focus"
                    />
                    {errors.title && (
                        <p className="text-red-500 newText-sm mt-1">{errors.title}</p>
                    )}
                </div>
                {/* 내용 */}
                <div>
                    <label className="block font-medium mb-1">내용</label>
                    <textarea
                        value={notice.content}
                        onChange={(e) => {
                            setNotice({ ...notice, content:e.target.value });
                            autoTextareaHeight(e);
                        }}
                        placeholder="내용을 입력하세요"
                        rows={6} //아무것도 입력하지 않았을 때 보이는 줄의 개수
                        className="w-full border border-gray-300 rounded px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-400 resize-none"
                        style={{ minHeight: '144px' }}
                    />
                    {errors.content && (
                        <p className="text-red-500 newText-sm mt-1">{errors.content}</p>
                    )}
                </div>
                {/* 고정 설정 */}
                <div className="p-4 bg-blue-50 border-l-4 border-blue-400 rounded-md">
                    <label className="flex items-center cursor-pointer">
                        <input
                            type="checkbox"
                            checked={notice.isPinned}
                            onChange={(e) => setNotice({ ...notice, isPinned: e.target.checked })}
                            className="w-4 h-4 text-blue-600 bg-gray-100 border-gray-300 rounded focus:ring-blue-500 focus:ring-2"
                        />
                        <span className="ml-3 newText-sm font-medium text-gray-700">
                            📌고정 게시물로 설정
                        </span>
                    </label>
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
                                        <div className="flex items-center justify-center h-full text-center px-2 newText-sm">
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
                        className="positive-button newText-sm"
                        onClick={handleAdd} >등록</button>
                    <button
                        className="nagative-button newText-sm"
                        onClick={handleCancel}>취소</button>
                </div>
            </div>
        </div>
    );
}

export default NoticeAddComponent;