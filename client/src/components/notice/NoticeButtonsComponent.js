import React from "react";
import { useSelector } from "react-redux"
import useMove from "../../hooks/useMove";
import { deleteNotices } from "../../api/noticeApi";

const NoticeButtonsComponent = ({selectedNotices, onDelete }) => {
    const loginState = useSelector((state) => state.loginState);
    const { moveToPath } = useMove(); //경로 이동

    const handleList = (e) => {
        e.preventDefault();
        moveToPath("/notice/list");
    };

    const handleWrite = (e) => {
        e.preventDefault();
        moveToPath("/notice/add");
    };
  
    // 선택된 공지사항 삭제
    const handleDelete = async () => { //공지글 선택 삭제
    if (selectedNotices.length === 0) {
      alert("삭제할 공지사항을 선택해주세요.");
      return;
    }
    //삭제 확인
    if (!window.confirm(`선택한 ${selectedNotices.length}개의 공지사항을 삭제하시겠습니까?`)) {
      return;
    }

    try {
      await deleteNotices(selectedNotices);
      console.log("삭제할 공지사항:", selectedNotices);
      alert("공지사항이 삭제되었습니다.");
      onDelete(); // 부모 컴포넌트에서 목록 새로고침
    } catch (error) {
      console.error("삭제 실패:", error);
      alert("삭제 중 오류가 발생했습니다.");
    }
  };

    return (
        <div className="mt-6 flex justify-between items-center">
        {/* 목록 버튼 */}
        <div>
            <button
                onClick={handleList}
                className="px-4 py-2 bg-gray-500 text-white rounded hover:bg-gray-600 transition-colors text-sm font-medium"
            >
                목록
            </button>
        </div>

      {/* 관리자 전용 버튼들 */}
      {loginState.role === 'ADMIN' ? (
        <div className="flex gap-2">
          {selectedNotices.length > 0 && (
            <button
              onClick={handleDelete}
              className="px-4 py-2 bg-red-500 text-white rounded hover:bg-red-600 transition-colors text-sm font-medium"
            >
              선택 삭제 ({selectedNotices.length})
            </button>
          )}
          
          <button
              onClick={handleWrite}
            className="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600 transition-colors text-sm font-medium inline-block"
          >
            글쓰기
          </button>
          
          {selectedNotices.length === 1 && (
             <button
              onClick={`/notice/update/${selectedNotices[0]}`}
              className="px-4 py-2 bg-green-500 text-white rounded hover:bg-green-600 transition-colors text-sm font-medium inline-block"
            >
              수정하기
            </button>
          )}
        </div>
       ) : (<></>)}
    </div>
  );
};

export default NoticeButtonsComponent;