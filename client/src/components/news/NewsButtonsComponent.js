import React from "react";
import { useSelector } from "react-redux"
import useMove from "../../hooks/useMove";
import { deleteNewsByIds } from "../../api/newsApi";

const NewsButtonsComponent = ({ selectedArticles, onDelete }) => {
    const loginState = useSelector((state) => state.loginState);
    const { moveToPath } = useMove(); //경로 이동

    const handleWrite = (e) => {
        e.preventDefault();
        moveToPath("/news/AddNews");
    };
  
    // 선택된 뉴스 삭제
    const handleDelete = async () => { //뉴스 선택 삭제
    if (selectedArticles.length === 0) {
      alert("삭제할 뉴스를 선택해주세요.");
      return;
    }
    //삭제 확인
    if (!window.confirm(`선택한 ${selectedArticles.length}개의 뉴스를 삭제하시겠습니까?`)) {
      return;
    }

    try {
      await deleteNewsByIds(selectedArticles);
      console.log("삭제할 뉴스", selectedArticles);
      alert("뉴스가 삭제되었습니다.");
      onDelete(); // 부모 컴포넌트에서 목록 새로고침
    } catch (error) {
      console.error("삭제 실패:", error);
      alert("삭제 중 오류가 발생했습니다.");
    }
  };

  return (
    <div className="mt-8 flex justify-end items-center">
      {/* 관리자 전용 버튼 */}
      {loginState.role === 'ADMIN' ? (
        <div className="flex gap-2">
          {selectedArticles.length > 0 && (
            <button
              onClick={handleDelete}
              className="nagative-button newText-sm"
            >
              선택 삭제 ({selectedArticles.length})
            </button>
          )}
          
          <button
              onClick={handleWrite}
            className="positive-button newText-sm"
          >
            글쓰기
          </button>
          
          {selectedArticles.length === 1 && (
             <button
              onClick={() => moveToPath(`/news/UpdateNews/${selectedArticles[0]}`)}
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

export default NewsButtonsComponent;