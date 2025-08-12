import { useState } from "react";
import {postAdd} from "../../api/qnaApi";
import useMove from "../../hooks/useMove";
const AddComponent = () => {
  const [content, setContent] = useState("");
  const [title, setTitle] = useState("");
  const [isPrivate, setIsPrivate] = useState(false);
    const { moveToPath } = useMove(); // 원하는 곳으로 이동할 변수

    const addQuestion=async()=> {
         await postAdd(title,content,isPrivate);
         alert("글 등록 완료");
         moveToPath("/question/select");
    };

    return (
    <div className="w-full mx-auto p-6 bg-white shadow rounded-lg">
      <h2 className="text-2xl my-4 font-bold">질문 등록</h2>

      <div className="flex justify-between items-center mb-4">
        <input
          type="text"
          onChange={(e) => setTitle(e.target.value)}
          placeholder="글 제목을 입력하세요"
          className="w-1/2 border-b border-gray-300 focus:border-blue-500 focus:outline-none text-lg p-1"
        />
        <button
          onClick={() => setIsPrivate(!isPrivate)}
          className="ml-3 text-gray-600 hover:text-blue-500 transition"
          title={isPrivate ? "비밀글 해제" : "비밀글 설정"}
        >
          {isPrivate ? (
            <svg
              xmlns="http://www.w3.org/2000/svg"
              className="w-7 h-7"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
              strokeWidth={2}
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                d="M8 11V8a4 4 0 018 0v3"
              />
              <rect
                x="5"
                y="11"
                width="14"
                height="10"
                rx="2"
                ry="2"
                stroke="currentColor"
                fill="none"
              />
            </svg>
          ) : (
            <svg
              xmlns="http://www.w3.org/2000/svg"
              className="w-7 h-7"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
              strokeWidth={2}
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                d="M15 8a4 4 0 00-8 0v3"
              />
              <rect
                x="5"
                y="11"
                width="14"
                height="10"
                rx="2"
                ry="2"
                stroke="currentColor"
                fill="none"
              />
            </svg>
          )}
        </button>
      </div>

      <textarea
        value={content}
        onChange={(e) => setContent(e.target.value)}
        placeholder="글 내용을 입력하세요..."
        className="w-full min-h-[300px] border border-gray-300 p-2 rounded resize-y"
      />

      <div className="mt-4 text-right">
        <button
          onClick={() => {
          addQuestion();
          }}
          className="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600 transition"
        >
          등록하기
        </button>
        <button
          onClick={() => {
           moveToPath("/");
          }}
          className="bg-red-500 text-white px-4 py-2 rounded hover:bg-red-600 transition ml-2"
        >
          취소하기
        </button>
      </div>
    </div>
  );
};

export default AddComponent;
