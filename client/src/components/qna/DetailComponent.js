import { useSelector } from "react-redux";
import { useEffect, useState } from "react";
import { getDetail, answerAdd, answerUpdate, deleteAnswer, deleteQuestions } from "../../api/qnaApi";
import useMove from "../../hooks/useMove";

const DetailComponent = ({ questionNum }) => {
  const loginState = useSelector((state) => state.loginState);
  const [listData, setListData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [answerContent, setAnswerContent] = useState("");
  const { moveToPath } = useMove();
  const [editingAnswerNum, setEditingAnswerNum] = useState(null);
  const [editingAnswerContent, setEditingAnswerContent] = useState("");

  useEffect(() => {
    getDetail(questionNum).then((data) => {
      setListData(data);
      setLoading(false);
    });
  }, [questionNum]);

  const handleAnswerSubmit = () => {
    if (loginState.role !== "ADMIN") {
      alert("관리자만 글 작성 가능합니다.");
      return;
    }
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
    answerUpdate(editingAnswerNum, editingAnswerContent).then(() => {
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
    } else if (type === "QUESTION") {
      alert("질문이 삭제되었습니다.");
      deleteQuestions([num]);
      moveToPath("/question/select");
    }
  };

  if (loading) return <div className="text-center py-10">로딩중...</div>;
  if (!listData) return <div className="text-center py-10 text-red-500">질문글을 불러오지 못했습니다.</div>;

  return (
  <div className="max-w-screen-xl mx-auto my-10">
      <div className="min-blank">
      {/* 질문글 */}
      <div className="bg-white border border-gray-200 rounded-lg shadow-sm mb-8 page-shadow">
        <div className="border-b border-gray-200 p-6">
          <h2 className="newText-2xl font-semibold mb-4 text-gray-800">{listData.title}</h2>
          <div className="flex justify-between items-center newText-base text-gray-600">
            <span>작성자: <b>{listData.memId}</b></span>
            <div className="text-right">
              <div>작성일: {new Date(listData.createdAt).toLocaleString()}</div>
              <div>수정일: {new Date(listData.updatedAt).toLocaleString()}</div>
              <div>조회수: {listData.view}</div>
            </div>
          </div>
        </div>
        
        <div className="p-6">
          <div className="bg-gray-50 border border-gray-200 rounded-lg p-4 min-h-[300px]">
            <p className="text-gray-700 leading-relaxed whitespace-pre-wrap newText-base">
              {listData.content}
            </p>
          </div>

          {(loginState.memId === listData.memId || loginState.role === "ADMIN") && (
            <div className="mt-4 flex justify-end gap-2">
              <button
                onClick={() => moveToPath(`../update/${questionNum}`)}
                className="positive-button"
              >
                수정
              </button>
              <button
                onClick={() => handleDelete("QUESTION", listData.questionNum)}
                className="nagative-button"
              >
                삭제
              </button>
            </div>
          )}
        </div>
      </div>

      {/* 답변 목록 */}
      <div className="mb-8">
        <h3 className="newText-xl font-semibold mb-4 text-gray-800">
          답변 ({listData.answerList ? listData.answerList.length : 0})
        </h3>
        
        <div className="space-y-4">
          {listData.answerList && listData.answerList.length > 0 ? (
            listData.answerList.map((ans) => (
              <div
                key={ans.answerNum}
                className="bg-blue-50 border border-blue-200 rounded-lg p-4"
              >
                {editingAnswerNum === ans.answerNum ? (
                  <div>
                    <textarea
                      value={editingAnswerContent}
                      onChange={(e) => setEditingAnswerContent(e.target.value)}
                      rows={4}
                      className="w-full p-3 border border-gray-300 rounded mb-3 whitespace-pre-wrap resize-none newText-base"
                    />
                    <div className="flex gap-2">
                      <button
                        onClick={handleSaveEdit}
                        className="positive-button"
                      >
                        저장
                      </button>
                      <button
                        onClick={handleCancelEdit}
                        className="normal-button"
                      >
                        취소
                      </button>
                    </div>
                  </div>
                ) : (
                  <>
                    <p className="text-gray-700 mb-3 whitespace-pre-wrap leading-relaxed newText-base">
                      {ans.content}
                    </p>
                    <div className="flex justify-between items-center newText-sm text-gray-600">
                      <span>답변자: {ans.memId || "관리자"}</span>
                      <div className="flex items-center gap-4">
                        <span>{new Date(ans.createdAt).toLocaleString()}</span>
                        {loginState.role === "ADMIN" && (
                          <div className="flex gap-2">
                            <button
                              onClick={() => handleEditClick(ans)}
                              className="positive-button"
                            >
                              수정
                            </button>
                            <button
                              onClick={() => handleDelete("ANSWER", ans.answerNum)}
                              className="nagative-button"
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
              <div className="bg-gray-50 border border-gray-200 rounded-lg p-8 text-center text-gray-500 newText-base">
                등록된 답변이 없습니다.
              </div>
            )
          )}
        </div>
      </div>

      {/* ADMIN 답변 작성 */}
      {loginState.role === "ADMIN" && (
        <div className="bg-white border border-gray-200 rounded-lg shadow-sm page-shadow">
          <div className="border-b border-gray-200 p-4">
            <h3 className="newText-xl font-semibold text-gray-800">답변 작성</h3>
          </div>
          <div className="p-6">
            <textarea
              value={answerContent}
              onChange={(e) => setAnswerContent(e.target.value)}
              rows={5}
              className="w-full p-4 border border-gray-300 rounded mb-4 whitespace-pre-wrap resize-none newText-base"
              placeholder="답변을 입력하세요..."
            />
            <div className="flex justify-end">
              <button
                onClick={handleAnswerSubmit}
                className="positive-button"
              >
                답변 등록
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
    </div>
  );
};

export default DetailComponent;
