import React from "react";
import { useSelector } from "react-redux"
import useMove from "../../hooks/useMove";
import { deleteNotices } from "../../api/noticeApi";

const NoticeButtonsComponent = ({ selectedNotices, onDelete }) => {
  const loginState = useSelector((state) => state.loginState);
  const { moveToPath } = useMove(); //경로 이동

  const handleWrite = (e) => {
    e.preventDefault();
    moveToPath("/notice/AddNotice");
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
    <div className="mt-8 flex justify-end items-center">
      {/* 관리자 전용 버튼들 */}
      {loginState.role === 'ADMIN' ? (
        <div className="flex gap-2">
          {selectedNotices.length > 0 && (
            <button
              onClick={handleDelete}
              className="nagative-button newText-sm"
            >
              선택 삭제 ({selectedNotices.length})
            </button>
          )}

          <button
            onClick={handleWrite}
            className="positive-button newText-sm"
          >
            글쓰기
          </button>

          {selectedNotices.length === 1 && (
            <button
              onClick={() => moveToPath(`/notice/UpdateNotice/${selectedNotices[0]}`)}
              className="green-button newText-sm"
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