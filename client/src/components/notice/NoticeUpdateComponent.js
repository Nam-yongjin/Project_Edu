import { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import useMove from "../../hooks/useMove";
import { updateNotice, NoticeDetail } from "../../api/noticeApi";

const NoticeUpdateComponent = () => {
    const { noticeNum } = useParams(); //url에서 공지사항 번호 가져오기
    const { moveToPath } = useMove();
    const navigate = useNavigate(); //수정 후 수정한 페이지로 이동
    
    //초기값 객체
    const initState = {
        title: '',
        content: '',
        isPinned: false,
        newFiles: [], //새로 선택한 파일(서버 전송용)
        existingFiles: [], //기존 파일들(이미 서버에 저장된 파일로 파일 정보만 필요)-> 삭제할 땐 파일 id만 서버에 전송
        deleteFileIds: [] //삭제할 파일 ID 목록
    };

    const [notice, setNotice] = useState(initState);
    const [isLoading, setIsLoading] = useState(true);

    //에러 객체
    const [error, setErrors] = useState({
        title: '',
        content: ''
    });

    //공지사항 데이터 불러오기
    useEffect(() => {
        console.log("useEffect 실행됨", { noticeNum});

        const fetchNoticeUpdate = async () => {
            if (!noticeNum) {
                alert("공지사항 번호가 없습니다.");
                moveToPath("/notice/NoticeList");
                return;
            }

            try {
                setIsLoading(true);
                console.log("API 호출 시작 - noticeNum:", noticeNum);

                const response = await NoticeDetail(Number(noticeNum));
                console.log("API 응답:", response);
                console.log("noticeFiles:", response.noticeFiles);

                const newState = {
                    ...initState,
                    title: response.title || '',
                    content: response.content || '',
                    isPinned: response.isPinned || false,
                    existingFiles: response.files || []
                };

                console.log("새로운 state:", newState);
                setNotice(newState);

            } catch (error) {
                console.log("공지사항 로드 실패:", error);
                alert("공지사항을 불러오는데 실패했습니다.");
                moveToPath("/notice/NoticeList");
            } finally {
                setIsLoading(false);
                console.log("로딩 완료!!")
            }
        };

        fetchNoticeUpdate();
    }, [noticeNum]);

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

    //새 파일 선택했을 때
    const handleNewFileChange = (e) => {
        const files = Array.from(e.target.files);
        setNotice({ ...notice, newFiles: files });
    };

    //새 파일 등록 취소
    const handleRemoveNewFile = (indexToRemove) => {
        const updatedFiles = [...notice.newFiles];
        updatedFiles.splice(indexToRemove, 1);
        setNotice({ ...notice, newFiles: updatedFiles });
    };

    //기존 파일 삭제(삭제 목록에 추가)
    const handleRemoveExistingFile = (notFileNum) => {
        console.log('삭제할 파일 번호:', notFileNum);
        if (!notFileNum) {
            console.warn('notFileNum이 undefined 또는 null입니다!');
            return;
        }

        //삭제할 파일 번호를 deleteFileIds 배열에 추가(DTO와 매핑)
        const updatedDeleteIds = [...notice.deleteFileIds, notFileNum.toString()];

        //기존 파일 목록에서 해당 파일 제거(화면에서만)
        const updatedExistingFiles = notice.existingFiles.filter(file => file.notFileNum !== notFileNum);

        setNotice({
            ...notice,
            deleteFileIds: updatedDeleteIds,
            existingFiles: updatedExistingFiles
        });
    };

    //수정 버튼
    const handleUpdate = async () => {
        //유효성 검사
        if (!validateForm()) {
            return;
        }

        try {
            const formData = new FormData();

            //dto 객체를 JSON으로 감싸서 Blob로 만들기(이미지, 사운드, 비디오와 같은 멀티미디어 데이터를 다룰 때 사용)
            const dto = {
                title: notice.title || "",
                content: notice.content || "",
                isPinned: notice.isPinned ?? false,
                deleteFileIds: notice.deleteFileIds || [] //삭제할 파일 ID 목록
            };
            const dtoBlob = new Blob([JSON.stringify(dto)], { type: "application/json" });
            formData.append("dto", dtoBlob);

            //새 파일들 추가
            if (notice.newFiles && Array.isArray(notice.newFiles)) {
                notice.newFiles.forEach(file => {
                    if (file) {
                        formData.append("files", file);
                    }
                });
            }

            //전송
            await updateNotice(noticeNum, formData);

            alert("공지사항이 수정되었습니다.")
            navigate(`/notice/NoticeDetail/${Number(noticeNum)}`); //--> noticeNum이 숫자인 걸 명확하게 지정!
            //Long 타입 파라미터에서 오류가 나는 이유
            //자바는 url패스 파라미터(/notice/NoticeDetail/{noticeNum})를 받으면 String -> Long으로 자동 변환
            //여기서 숫자가 아닌 "undefined"나 "NaN"이 들어오면 자동 변환에 실패해서 500에러가 터짐
        } catch (error) {
            console.error("공지사항 수정 실패:", error);

            //백엔드 유효성 에러 처리
            if (error.response?.status === 400 && error.response?.data?.errors) {
                const backendErrors = {};
                error.response.data.errors.forEach(err => {
                    backendErrors[err.field] = err.defaultMessage;
                });
                setErrors(backendErrors);
            } else {
                alert("수정 중 오류가 발생했습니다.");
            }
        }
    };

    //취소 버튼
    const handleCancel = () => {
        if (window.confirm("수정을 취소하고 목록으로 돌아가시겠습니까?")) {
            moveToPath("/notice/NoticeList");
        }
    };

    //이미지 확장자 여부 판단
    const isImage = (type) => {
        const lower = type.toLowerCase();
        return ['jpg', 'jpeg', 'png'].includes(lower);
    };

    if (isLoading) {
        return (
            <div className="w-full px-4 sm:px-6 md:px-8 lg:px-12 max-w-screen-xl mx-auto">
                <div className="max-w-3xl mx-auto mt-4 px-10 p-6 bg-white rounded-xl shadow-md">
                    <div className="text-center py-8">로딩 중...</div>
                </div>
            </div>
        );
    }

    return (
        <div className="w-full px-4 sm:px-6 md:px-8 lg:px-12 max-w-screen-xl mx-auto">
            <div className="max-w-3xl mx-auto my-4 px-10 p-6 bg-white rounded-xl shadow-md space-y-6">
                <h2 className="text-2xl my-4 font-bold">공지사항 수정</h2>
                <hr className="border-gray-200 my-4" />

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
                    {error.title && (
                        <p className="text-red-500 text-sm mt-1">{error.title}</p>
                    )}    
                </div>

                {/* 내용 */}
                <div>
                    <label className="block font-medium mb-1">내용</label>
                    <textarea
                        value={notice.content}
                        onChange={(e) => {
                            setNotice({ ...notice, content: e.target.value });
                            autoTextareaHeight(e);
                        }}
                        placeholder="내용을 입력하세요"
                        rows={6}
                        className="w-full border border-gray-300 rounded px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-400 resize-none"
                        style={{ minHeight: '144px' }}
                    />
                    {error.content && (
                        <p className="text-red-500 text-sm mt-1">{error.content}</p>
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
                        <span className="ml-3 text-sm font-medium text-gray-700">
                            📌고정 게시물로 설정
                        </span>
                    </label>
                </div>

                {/* 기존 첨부파일 */}
                {notice.existingFiles.length > 0 && (
                    <div>
                        <strong className="block mb-2">기존 첨부 파일:</strong>
                        <ul className="flex overflow-x-auto space-x-4">
                            {notice.existingFiles.map((file, index) => (
                                <li key={file.noticeNum || index}
                                className="relative flex-shrink-0 w-[120px] h-[120px] border rounded p-1 bg-gray-50 group">
                                    {/* 삭제 버튼 */}
                                    <button
                                        type="button"
                                        onClick={() => {
                                            console.log("파일 객체 확인", file);
                                            handleRemoveExistingFile(file.notFileNum);
                                        }}
    
                                        className="absolute top-1 right-1 bg-black bg-opacity-50 text-white rounded-full w-6 h-6 text-xs flex items-center justify-center opacity-0 group-hover:opacity-100 transition"
                                        title="삭제"
                                    >✕</button>
                                    {/* 기존에 있는 파일 불러오는 거라 실제 서버 경로가 필요함 */}
                                    {/* {file.fileType && file.fileType.startsWith("image/") ? (
                                        <img
                                            src={file.filePath || `/files/${file.fileName}`}
                                            alt={file.fileName}
                                            className="w-full h-full object-cover rounded"
                                        /> */}
                                    {isImage(file.fileType) ? (
                                        <img
                                            src={file.downloadUrl}
                                            alt={file.originalName}
                                            className="w-full h-full object-cover rounded"
                                        />    
                                    ) : (
                                        <div className="flex items-center justify-center h-full text-center px-2 text-sm">
                                            <span>{file.fileName || file.originalName}</span>
                                        </div>
                                    )}
                                </li>
                            ))}
                        </ul>
                    </div>
                )}

                {/* 새 첨부파일 */}
                <div>
                    <label className="block font-medium mb-1">새 첨부파일 추가</label>
                    <input
                        type="file"
                        multiple
                        accept=".jpg, .jpeg, .png, .pdf, .hwp, .doc, .docx"
                        onChange={handleNewFileChange}
                        className="w-full"
                    />
                </div>

                {/* 새 파일 미리보기 */}
                {notice.newFiles.length > 0 && (
                    <div>
                        <strong className="block mb-2">새로 추가할 파일:</strong>
                        <ul className="flex overflow-x-auto space-x-4">
                            {notice.newFiles.map((file, index) => (
                                <li key={index}
                                    className="relative flex-shrink-0 w-[120px] h-[120px] border rounded p-1 bg-gray-50 group">
                                    {/* 삭제 버튼 */}
                                    <button
                                        type="button"
                                        onClick={() => handleRemoveNewFile(index)}
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
                        className="px-4 py-2 bg-green-500 text-white rounded hover:bg-green-600 transition-colors text-sm font-medium inline-block"
                        onClick={handleUpdate}>수정</button>
                    <button
                        className="px-4 py-2 bg-red-500 text-white rounded hover:bg-red-600 transition-colors text-sm font-medium inline-block"
                        onClick={handleCancel}>취소</button>
                </div> 
            </div>
        </div> 
    )


}
export default NoticeUpdateComponent;