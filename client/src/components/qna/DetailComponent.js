import { useSelector } from "react-redux";
import { useEffect, useState } from "react";
import { getDetail, answerAdd, answerUpdate, deleteAnswer, deleteQuestions } from "../../api/qnaApi";
import useMove from "../../hooks/useMove";
const DetailComponent = ({ questionNum }) => {
  const loginState = useSelector((state) => state.loginState);
  const [listData, setListData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [answerContent, setAnswerContent] = useState("");
  const { moveToPath } = useMove(); // 원하는 곳으로 이동할 변수
  // 수정 상태
  const [editingAnswerNum, setEditingAnswerNum] = useState(null);
  const [editingAnswerContent, setEditingAnswerContent] = useState("");

  useEffect(() => {
    getDetail(questionNum).then((data) => {
      setListData(data);
      setLoading(false);
    });
  }, [questionNum]);

  const handleAnswerSubmit = () => {
    if (answerContent === "") {
      alert("답변을 입력해주세요.");
      return;
    }
    alert("답변이 등록되었습니다.");
    answerAdd(answerContent, questionNum).then(() => {
      window.location.reload();
    });
  };

  const handleEditClick = (answer) => {
    setEditingAnswerNum(answer.answerNum);
    setEditingAnswerContent(answer.content);
  };

  const handleCancelEdit = () => {
    setEditingAnswerNum(null);
    setEditingAnswerContent("");
  };

  const handleSaveEdit = () => {
    if (editingAnswerContent === "") {
      alert("내용을 입력해주세요.");
      return;
    }
    alert("답변이 수정되었습니다.");
    ; answerUpdate(editingAnswerNum, editingAnswerContent).then(() => {
      window.location.reload();
    });
  };

  const handleDelete = (type, num) => {
    const confirmed = window.confirm("정말 삭제하시겠습니까?");
    if (!confirmed) return;
    if (type === "ANSWER") {
      alert("답변이 삭제되었습니다.");
      deleteAnswer(num);
      window.location.reload();
    }
    else if (type === "QUESTION") {
      alert("질문이 삭제되었습니다.");
      deleteQuestions([num]);
      moveToPath("/question/select");
    }

  };

  if (loading) return <div>로딩중...</div>;
  if (!listData) return <div>질문글을 불러오지 못했습니다.</div>;

  return (
    <div className="w-full max-w-screen-xl mx-auto px-4 mt-10 sm:px-6 md:px-8 lg:px-12">
      {/* 질문글 */}
      <div className="border border-gray-300 rounded-lg p-6 mb-6">
        <h2 className="text-2xl font-bold mb-4">{listData.title}</h2>
        <div className="flex justify-between text-sm text-gray-500 mb-4">
          <span>작성자: {listData.memId}</span>
          <div className="flex gap-4">
            <span>작성일: {new Date(listData.createdAt).toLocaleString()}</span>
            <span>조회수: {listData.view}</span>
          </div>
        </div>
        <p className="border border-gray-300 rounded-lg p-4 bg-gray-50 h-[400px] overflow-auto">
          {listData.content}
        </p>

        {(loginState.memId === listData.memId || loginState.role === "ADMIN") && (
          <div className="mt-4 flex justify-end gap-2">
            <button
              onClick={() => moveToPath(`../update/${questionNum}`)}
              className="px-3 py-1 border border-gray-300 rounded hover:bg-gray-100"
            >
              수정
            </button>
            <button
              onClick={() => handleDelete("QUESTION", listData.questionNum)}
              className="px-3 py-1 border border-gray-300 rounded hover:bg-gray-100"
            >
              삭제
            </button>
          </div>
        )}
      </div>

      {/* 답변글 */}
      <div className="mb-6">
        {listData.answerList && listData.answerList.length > 0 ? (
          listData.answerList.map((ans) => (
            <div
              key={ans.answerNum}
              className="border border-gray-200 rounded-lg p-4 mb-3 bg-gray-50"
            >
              {/* 수정 모드 */}
              {editingAnswerNum === ans.answerNum ? (
                <div>
                  <textarea
                    value={editingAnswerContent}
                    onChange={(e) => setEditingAnswerContent(e.target.value)}
                    rows={3}
                    className="w-full p-2 border border-gray-300 rounded mb-2"
                  />
                  <div className="flex gap-2">
                    <button
                      onClick={handleSaveEdit}
                      className="px-3 py-1 border border-gray-300 rounded hover:bg-gray-100"
                    >
                      저장
                    </button>
                    <button
                      onClick={handleCancelEdit}
                      className="px-3 py-1 border border-gray-300 rounded hover:bg-gray-100"
                    >
                      취소
                    </button>
                  </div>
                </div>
              ) : (
                <>
                  <p>{ans.content}</p>
                  <div className="flex justify-between items-start text-sm text-gray-500 mt-2">
                    <span>작성자: {ans.memId || "관리자"}</span>
                    <div className="text-right">
                      <span>{new Date(ans.createdAt).toLocaleString()}</span>
                      {loginState.role === "ADMIN" && (
                        <div className="flex justify-end gap-2 mt-1">
                          <button
                            onClick={() => handleEditClick(ans)}
                            className="px-2 py-1 border border-gray-300 rounded hover:bg-gray-100"
                          >
                            수정
                          </button>
                          <button
                            onClick={() => handleDelete("ANSWER", ans.answerNum)}
                            className="px-2 py-1 border border-gray-300 rounded hover:bg-gray-100"
                          >
                            삭제
                          </button>
                        </div>
                      )}
                    </div>
                  </div>
                </>
              )}
            </div>
          ))
        ) : (
          loginState.role !== "ADMIN" && (
            <div className="border border-gray-300 rounded-lg p-4 bg-gray-50 text-center h-[100px]  text-gray-500">
              등록된 답변이 없습니다.
            </div>
          )
        )}
      </div>

      {/* ADMIN 답변 작성 */}
      {loginState.role === "ADMIN" && (
        <div className="mb-10">
          <h3 className="text-xl font-semibold mb-2">답변 작성</h3>
          <textarea
            value={answerContent}
            onChange={(e) => setAnswerContent(e.target.value)}
            rows={4}
            className="w-full p-3 border border-gray-300 rounded bg-gray-50 mb-2"
          />
          <div className="flex justify-end">
            <button
              onClick={handleAnswerSubmit}
              className="px-4 py-2 border border-gray-300 rounded hover:bg-gray-100"
            >
              등록
            </button>
          </div>
        </div>
      )}
    </div>
  );
};

export default DetailComponent;
