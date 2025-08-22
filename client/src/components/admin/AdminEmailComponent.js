import { useState, useEffect, useRef, useMemo } from "react";
import ReactQuill from "react-quill";
import "react-quill/dist/quill.snow.css";
import { sendEmail } from "../../api/adminApi";
import cancel from "../../assets/cancel.png";

const AdminEmailComponent = ({ members }) => {
  const [memberList, setMemberList] = useState([]);
  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");
  const [attachFiles, setAttachFiles] = useState([]);

  const quillRef = useRef(null);
  const attachInputRef = useRef(null);

  useEffect(() => {
    if (members.length === 0) return;
    setMemberList(members);

    // Quill 스타일: 에디터 높이 및 이미지 초기 크기
    const style = document.createElement("style");
    style.innerHTML = `
      .ql-editor { min-height:600px; max-height:600px; overflow-y:auto; padding:12px; }
      .ql-editor img { width:300px; height:auto; display:inline-block; vertical-align:middle; }
    `;
    document.head.appendChild(style);
  }, [members]);

  // 이미지 업로드 핸들러
  const imageHandler = () => {
    const input = document.createElement("input");
    input.type = "file";
    input.accept = "image/*";
    input.click();

    input.onchange = () => {
      const file = input.files?.[0];
      if (!file) return;

      const reader = new FileReader();
      reader.onload = () => {
        const quill = quillRef.current.getEditor();
        const range = quill.getSelection(true);

        quill.insertEmbed(range.index, "image", reader.result);
        quill.insertText(range.index + 1, "\u200B");
        quill.setSelection(range.index + 2);

        // 이미지 크기 초기값
        setTimeout(() => {
          const imgs = quill.root.querySelectorAll("img");
          const img = imgs[imgs.length - 1]; // 마지막 삽입 이미지 선택
          if (img) {
            img.style.width = "300px";
            img.style.height = "auto";
            img.style.marginLeft = "0px";
          }
        }, 100);
      };
      reader.readAsDataURL(file);
    };
  };

  // 이미지 오른쪽으로 한 칸씩 이동: 스페이스바
  useEffect(() => {
    const quill = quillRef.current.getEditor();
    quill.keyboard.bindings[32] = []; // 스페이스바 기본 바인딩 제거

    const handler = (e) => {
      if (e.key === " ") {
        const sel = quill.getSelection();
        if (!sel) return;

        // 선택 직전 위치에 이미지가 있으면 이동
        const [blot] = quill.scroll.descendant(
          (b) => b.domNode && b.domNode.tagName === "IMG",
          sel.index - 1
        );
        if (blot) {
          const img = blot.domNode;
          const currentMargin = parseInt(img.style.marginLeft || "0");
          img.style.marginLeft = currentMargin + 10 + "px"; // 한 칸 이동
          e.preventDefault();
        }
      }
    };

    quill.root.addEventListener("keydown", handler);
    return () => quill.root.removeEventListener("keydown", handler);
  }, []);

  const alignCenterHandler = () => {
    if (!quillRef.current) return;
    const quill = quillRef.current.getEditor();
    const range = quill.getSelection();
    if (range) quill.formatLine(range.index, range.length, { align: "center" });
  };

  const modules = useMemo(
    () => ({
      toolbar: {
        container: [
          [{ header: [1, 2, 3, false] }],
          ["bold", "italic", "underline", "strike"],
          [{ color: [] }, { background: [] }],
          [{ list: "ordered" }, { list: "bullet" }],
          ["link", "image"],
          ["clean"],
          [{ align: [] }],
          ["centerAlign"],
        ],
        handlers: { image: imageHandler, centerAlign: alignCenterHandler },
      },
    }),
    []
  );

  const formats = [
    "header",
    "bold",
    "italic",
    "underline",
    "strike",
    "color",
    "background",
    "list",
    "bullet",
    "link",
    "image",
    "align",
  ];

  // 첨부파일 업로드 (최대 8개)
  const handleAttachFiles = (e) => {
    const files = Array.from(e.target.files);
    if (attachFiles.length + files.length > 8) {
      alert("첨부파일은 최대 8개까지 업로드할 수 있습니다.");
      return;
    }
    setAttachFiles((prev) => [...prev, ...files]);
  };

  const handleRemoveFile = (index) => {
    setAttachFiles((prev) => prev.filter((_, i) => i !== index));
  };

  const handleRemoveMember = (memId) => {
    setMemberList((prev) => prev.filter((m) => m.memId !== memId));
  };

  const handleSend = async () => {
    if (!title || !content) {
      alert("제목, 본문을 모두 입력해주세요.");
      return;
    }

    const formData = new FormData();
    formData.append("title", title);
    formData.append("content", content);
    memberList.forEach((m) => formData.append("memberList", m.email));
    attachFiles.forEach((file) => formData.append("attachmentFile", file));

    try {
      await sendEmail(formData);
      alert("메일 전송 성공!");
      setTitle("");
      setContent("");
      setAttachFiles([]);
      if (attachInputRef.current) attachInputRef.current.value = null;
    } catch (err) {
      console.error(err);
      alert("메일 전송 실패");
    }
  };

  return (
    <div className="max-w-4xl mx-auto p-6">
      <h2 className="text-xl font-bold mb-4 text-center">이메일 발송 페이지</h2>

      {/* 선택된 회원 */}
      <div className="mb-6">
        <label className="font-semibold block mb-2">선택된 회원</label>
        <div className="border rounded-lg p-3 max-h-[200px] overflow-y-auto bg-gray-50">
          {memberList.length === 0 ? (
            <p className="text-gray-500 text-sm text-center">선택된 회원이 없습니다.</p>
          ) : (
            <ul className="space-y-2">
              {memberList.map((m) => (
                <li
                  key={m.memId}
                  className="relative flex flex-col bg-white border rounded-lg px-4 py-2 shadow-sm hover:shadow-md transition"
                >
                  <span className="font-semibold text-gray-800">{m.name}</span>
                  <span className="text-gray-500 text-sm">{m.email}</span>

                  {/* 삭제 버튼 */}
                  <button
                    onClick={() => handleRemoveMember(m.memId)}
                    className="absolute top-2 right-2 hover:opacity-80 transition"
                  >
                    <img src={cancel} alt="삭제" className="w-4 h-4" />
                  </button>
                </li>
              ))}
            </ul>
          )}
        </div>
      </div>

      {/* 제목 */}
      <div className="mb-4">
        <label className="font-semibold block mb-1">제목:</label>
        <input
          type="text"
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          className="w-full border p-2 rounded"
        />
      </div>

      {/* 본문 */}
      <div className="mb-4">
        <label className="font-semibold block mb-1">본문:</label>
        <ReactQuill
          ref={quillRef}
          theme="snow"
          value={content}
          onChange={setContent}
          modules={modules}
          formats={formats}
        />
      </div>

      {/* 첨부파일 업로드 */}
      <div className="mb-4">
        <label className="font-semibold block mb-1">첨부파일 (최대 8개)</label>
        <input
          type="file"
          multiple
          onChange={handleAttachFiles}
          ref={attachInputRef}
          className="w-full border p-2 rounded mb-3"
        />

        {attachFiles.length > 0 && (
          <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
            {attachFiles.map((file, index) => (
              <div
                key={index}
                className="relative flex flex-col border p-2 rounded-md bg-gray-50"
              >
                {/* 삭제 버튼 우측 상단 */}
                <button
                  onClick={() => handleRemoveFile(index)}
                  className="absolute top-1 right-1 hover:opacity-80"
                >
                  <img src={cancel} alt="삭제" className="w-4 h-4" />
                </button>

                {file.type.startsWith("image/") ? (
                  <img
                    src={URL.createObjectURL(file)}
                    alt="미리보기"
                    className="mb-2 rounded shadow"
                    style={{ width: "100px", height: "auto" }}
                  />
                ) : (
                  <div className="mb-2 flex items-center justify-center bg-gray-200 rounded h-[100px]">
                    <span className="text-gray-600 text-sm">미리보기 없음</span>
                  </div>
                )}
                <span className="text-gray-700 text-sm break-all">{file.name}</span>
              </div>
            ))}
          </div>
        )}
      </div>

      {/* 전송 버튼 */}
      <div className="text-center">
        <button onClick={handleSend} className="positive-button newText-base">
          이메일 보내기
        </button>
      </div>
    </div>
  );
};

export default AdminEmailComponent;
