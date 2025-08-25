import { useState } from "react";
import { questionAdd } from "../../api/qnaApi";
import useMove from "../../hooks/useMove";
import lock from '../../assets/lock.png';
import lockNO from '../../assets/lockNo.png';

const AddComponent = () => {
  const [content, setContent] = useState("");
  const [title, setTitle] = useState("");
  const [isPrivate, setIsPrivate] = useState(false);
  const { moveToPath } = useMove(); // 원하는 곳으로 이동할 변수


  const addQuestion = async () => {
    if(title.trim()==="")
    {
      alert("글 제목을 입력해주세요");
      return;
    }
    if(content.trim()==="")
    {
      alert("글 내용을 입력해주세요");
      return;
    }
    await questionAdd(title, content, isPrivate);
    alert("글 등록 완료");
    moveToPath("/question/select");
  };

  return (
     <div className="w-full max-w-screen-xl mx-auto px-4 mt-10 sm:px-6 md:px-8 lg:px-12">
      <h2 className="newText-2xl my-4 font-bold">질문 등록</h2>

      <div className="flex justify-between items-center mb-4">
        <input
          type="text"
          onChange={(e) => setTitle(e.target.value)}
          placeholder="글 제목을 입력하세요"
          className="w-full border-b border-gray-300 focus:border-blue-500 focus:outline-none newText-lg p-1"
        />
        <button
          onClick={() => setIsPrivate(!isPrivate)}
          className="ml-3 text-gray-600 hover:text-blue-500 transition"
          title={isPrivate ? "비밀글 해제" : "비밀글 설정"}
        >
          {isPrivate ? (
            <img src={lock} className="w-6 h-6 mr-2 flex-shrink-0" alt="lock" />
            // <a href="https://www.flaticon.com/kr/free-icons/" title="자물쇠 아이콘">자물쇠 아이콘 제작자: Freepik - Flaticon</a>
          ) : (
           <img src={lockNO} className="w-6 h-6 mr-2 flex-shrink-0" alt="lockNO" />
           //<a href="https://www.flaticon.com/kr/free-icons/" title="자물쇠 아이콘">자물쇠 아이콘 제작자: Freepik - Flaticon</a> 
          )}
        </button>
      </div>
      

      <textarea
        value={content}
        onChange={(e) => setContent(e.target.value)}
        placeholder="글 내용을 입력하세요"
        className="newText-base input-focus w-full min-h-[500px] border border-gray-300 p-2 rounded resize-y whitespace-pre-wrap none"
        style={{ whiteSpace: 'pre-wrap' }}
      />
  
      <div className="newText-base my-4 text-right">
        <button
          onClick={() => {
            addQuestion();
          }}
          className="positive-button mr-2"
        >
          등록하기
        </button>
        <button
          onClick={() => {
            moveToPath("/");
          }}
          className="negative-button"
        >
          취소하기
        </button>
      </div>
    </div>
  );
};

export default AddComponent;