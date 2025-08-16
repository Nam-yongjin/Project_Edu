import { useState, useEffect, useRef, useMemo } from "react";
import ReactQuill from "react-quill";
import "react-quill/dist/quill.snow.css";
import { sendEmail,viewMembers} from "../../api/adminApi";

const AdminEmailComponent = () => {
  const [memberList, setMemberList] = useState([]);
  const [selectedMembers, setSelectedMembers] = useState([]);
  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");
  const [imageList, setImageList] = useState([]); // Quill 이미지 파일
  const [attachFiles, setAttachFiles] = useState([]); // 하단 첨부파일

  const quillRef = useRef(null);

  useEffect(() => {
    // 더미 회원 데이터
    setMemberList([
      { id: "tee1694@naver.com", name: "김태근" },
      { id: "tee169412@gmail.com", name: "김태근2" },
      { id: "user3", name: "홍길동" },
    ]);

    // 에디터 CSS
    const style = document.createElement("style");
    style.innerHTML = `
      .ql-editor {
        min-height: 600px;
        max-height: 600px;
        overflow-y: auto;
        padding: 12px;
      }
      .ql-editor img {
        display: inline-block;
        max-width: 100%;
        height: auto;
        vertical-align: middle;
      }
    `;
    document.head.appendChild(style);
  }, []);

  const handleMemberSelect = (e) => {
    const value = Array.from(e.target.selectedOptions, (option) => option.value);
    setSelectedMembers(value);
  };

  // 이미지 리사이즈 함수
  const resizeImageFile = (file, maxWidth = 600, maxHeight = 600) => {
    return new Promise((resolve) => {
      const reader = new FileReader();
      reader.onload = (event) => {
        const img = new Image();
        img.onload = () => {
          let { width, height } = img;
          if (width > maxWidth || height > maxHeight) {
            const ratio = Math.min(maxWidth / width, maxHeight / height);
            width *= ratio;
            height *= ratio;
          }
          const canvas = document.createElement("canvas");
          canvas.width = width;
          canvas.height = height;
          const ctx = canvas.getContext("2d");
          ctx.drawImage(img, 0, 0, width, height);
          canvas.toBlob((blob) => {
            resolve(blob);
          }, file.type || "image/png");
        };
        img.src = event.target.result;
      };
      reader.readAsDataURL(file);
    });
  };

  // Quill 이미지 업로드 핸들러
  const imageHandler = () => {
    const input = document.createElement("input");
    input.type = "file";
    input.accept = "image/*";
    input.click();

    input.onchange = async () => {
      const file = input.files?.[0];
      if (!file) return;

      const resizedFile = await resizeImageFile(file);

      const reader = new FileReader();
      reader.onload = () => {
        const dataUrl = reader.result;
        setImageList((prev) => [...prev, resizedFile]);

        if (quillRef.current) {
          const quill = quillRef.current.getEditor();
          const range = quill.getSelection(true);
          quill.insertEmbed(range.index, "image", dataUrl);
          quill.insertText(range.index + 1, "\u200B");
          quill.setSelection(range.index + 2);
        }
      };
      reader.readAsDataURL(resizedFile);
    };
  };

  // 중앙정렬 핸들러
  const alignCenterHandler = () => {
    if (quillRef.current) {
      const quill = quillRef.current.getEditor();
      const range = quill.getSelection();
      if (range) {
        quill.formatLine(range.index, range.length, { align: "center" });
      }
    }
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
        handlers: {
          image: imageHandler,
          centerAlign: alignCenterHandler,
        },
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

  const handleAttachFiles = (e) => {
    const selected = Array.from(e.target.files);
    setAttachFiles((prev) => [...prev, ...selected]);
  };

  const handleSend = async () => {
    if (!title || !content || selectedMembers.length === 0) {
      alert("제목, 본문, 수신 회원을 모두 입력해주세요.");
      return;
    }

    const formData = new FormData();
    formData.append("title", title);
    formData.append("content", content);
    selectedMembers.forEach((m) => formData.append("memberList", m));
    imageList.forEach((file) => formData.append("imageList", file));
    attachFiles.forEach((file) => formData.append("AttachmentFile", file));

    try {
      await sendEmail(formData);
      alert("메일 전송 성공!");
      setTitle("");
      setContent("");
      setSelectedMembers([]);
      setImageList([]);
      setAttachFiles([]);
    } catch (err) {
      console.error(err);
      alert("메일 전송 실패");
    }
  };

  return (
    <div className="max-w-4xl mx-auto p-6">
      <h2 className="text-xl font-bold mb-4 text-center">이메일 발송 페이지</h2>

      {/* 회원 선택 */}
      <div className="mb-4">
        <label className="font-semibold block mb-1">회원 선택:</label>
        <select
          multiple
          value={selectedMembers}
          onChange={handleMemberSelect}
          className="w-full h-28 border p-2 rounded"
        >
          {memberList.map((member) => (
            <option key={member.id} value={member.id}>
              {member.name} ({member.id})
            </option>
          ))}
        </select>
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

      {/* 첨부파일 input */}
      <div className="mb-4">
        <label className="font-semibold block mb-1">첨부파일 업로드:</label>
        <input
          type="file"
          multiple
          onChange={handleAttachFiles}
          className="w-full border p-2 rounded"
        />
      </div>

      <div className="text-center">
        <button
          onClick={handleSend}
          className="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600"
        >
          이메일 보내기
        </button>
      </div>
    </div>
  );
};

export default AdminEmailComponent;
