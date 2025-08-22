import React from "react";
import { useSelector } from "react-redux";
import { deleteQuestions } from "../../api/qnaApi"; // 삭제 API도 question 관련으로 변경 필요
import useMove from "../../hooks/useMove";
const ButtonsComponent = ({ selectedQuestion, onDelete }) => {
  const loginState = useSelector((state) => state.loginState);
  const { moveToPath } = useMove(); // 원하는 곳으로 이동할 변수
  const handleDelete = async () => {
    if (!selectedQuestion || selectedQuestion.length === 0) {
      alert("삭제할 질문을 선택해주세요.");
      return;
    }
    if (!window.confirm(`선택한 ${selectedQuestion.length}개의 질문을 삭제하시겠습니까?`)) {
      return;
    }

    try {
      await deleteQuestions(selectedQuestion);
      alert("질문이 삭제되었습니다.");
      onDelete();
    } catch (error) {
      alert("삭제 중 오류가 발생했습니다.");
    }
  };

  const moveAddPage=()=> {
    if(loginState.role==="") {
      alert('로그인이 필요합니다.');
      moveToPath("../../login");
    }
    else {
      moveToPath("../../question/add")
    }
  }
  return (
    <div className="mt-8 flex justify-end items-center">
      {loginState.role === "ADMIN" ? (
        <div className="flex gap-2">
          {selectedQuestion && selectedQuestion.length > 0 && (
            <button
              onClick={handleDelete}
              className="px-4 py-2 bg-red-500 text-white rounded hover:bg-red-600 transition-colors newText-sm font-medium mr-1"
            >
              선택 삭제 ({selectedQuestion.length})
            </button>
          )}
        </div>
        
      ) : (
        <></>
      )}
    <button
            onClick={() => moveAddPage()}
            className="px-4 py-2 positive-button rounde transition-colors newText-sm font-medium inline-block"
          >
            글쓰기
          </button>
    </div>
  );
};

export default ButtonsComponent;
