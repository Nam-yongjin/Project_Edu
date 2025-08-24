import { useState, useEffect, useRef, useMemo } from "react";
import ReactQuill from "react-quill";
import "react-quill/dist/quill.snow.css";
//import { getStorage, ref, uploadBytes, getDownloadURL } from "firebase/storage";
//import { getAuth, signInAnonymously } from "firebase/auth";
//import { app } from "../../firebase/firebase"; // Firebase 설정 파일 경로
import { sendEmail } from "../../api/adminApi";
import cancel from "../../assets/cancel.png";

const AdminEmailComponent = ({ members }) => {
  /*
  const [memberList, setMemberList] = useState([]);
  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");
  const [attachFiles, setAttachFiles] = useState([]);
  const [isUploading, setIsUploading] = useState(false);
  const [selectedImage, setSelectedImage] = useState(null);
  const [imageWidth, setImageWidth] = useState("");

  const quillRef = useRef(null);
  const attachInputRef = useRef(null);

  // Firebase 초기화
  const storage = getStorage(app);
  const auth = getAuth(app);

  useEffect(() => {
    if (members.length === 0) return;
    setMemberList(members);

    // Quill 스타일 설정
    const style = document.createElement("style");
    style.innerHTML = `
      .ql-editor { 
        min-height: 600px; 
        max-height: 600px; 
        overflow-y: auto; 
        padding: 16px; 
        line-height: 1.6;
        font-size: 14px;
      }
      .ql-editor img { 
        max-width: 100%;
        height: auto; 
        display: inline-block; 
        margin: 10px 0;
        cursor: pointer;
        border: 2px solid transparent;
        border-radius: 4px;
        transition: border-color 0.2s ease;
      }
      .ql-editor img:hover {
        border-color: #007bff;
      }
      .ql-editor img.selected {
        border-color: #007bff;
        box-shadow: 0 0 8px rgba(0, 123, 255, 0.3);
      }
      .ql-toolbar {
        border-top: 1px solid #ccc;
        border-left: 1px solid #ccc;
        border-right: 1px solid #ccc;
      }
      .ql-container {
        border-bottom: 1px solid #ccc;
        border-left: 1px solid #ccc;
        border-right: 1px solid #ccc;
      }
    `;
    document.head.appendChild(style);
  }, [members]);

  // Firebase 익명 인증
  const authenticateUser = async () => {
    try {
      await signInAnonymously(auth);
      console.log("Firebase 인증 완료");
      return true;
    } catch (error) {
      console.error("Firebase 인증 실패:", error);
      alert("인증에 실패했습니다. 다시 시도해주세요.");
      return false;
    }
  };

  // Firebase에 이미지 업로드
  const uploadImageToFirebase = async (file, fileName) => {
    try {
      const imageRef = ref(storage, `email-images/${Date.now()}_${fileName}`);
      const snapshot = await uploadBytes(imageRef, file);
      const downloadURL = await getDownloadURL(snapshot.ref);
      return downloadURL;
    } catch (error) {
      console.error("이미지 업로드 실패:", error);
      throw error;
    }
  };

  // Base64를 Blob으로 변환
  const base64ToBlob = (base64Data) => {
    const arr = base64Data.split(',');
    const mime = arr[0].match(/:(.*?);/)[1];
    const bstr = atob(arr[1]);
    let n = bstr.length;
    const u8arr = new Uint8Array(n);
    while (n--) {
      u8arr[n] = bstr.charCodeAt(n);
    }
    return new Blob([u8arr], { type: mime });
  };

  // Quill 이미지 업로드 핸들러
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

        // 일단 base64로 미리보기 표시
        quill.insertEmbed(range.index, "image", reader.result);
        quill.setSelection(range.index + 1);

        setTimeout(() => {
          setupImageResizing(quill);
        }, 100);
      };
      reader.readAsDataURL(file);
    };
  };

  // 이미지 크기 조절 설정
  const setupImageResizing = (quill) => {
    const images = quill.root.querySelectorAll("img");
    
    images.forEach((img) => {
      if (img.dataset.resizeSetup) return;
      img.dataset.resizeSetup = "true";

      if (!img.style.width) {
        img.style.width = "300px";
      }
      img.style.display = "inline-block";
      img.style.marginLeft = "0px";

      img.addEventListener("dblclick", (e) => {
        e.preventDefault();
        e.stopPropagation();
        
        const currentWidth = parseInt(img.style.width) || 300;
        setImageWidth(currentWidth.toString());
        setSelectedImage(img);
      });

      img.addEventListener("click", (e) => {
        e.preventDefault();
        e.stopPropagation();
        
        quill.root.querySelectorAll("img").forEach(i => i.classList.remove("selected"));
        img.classList.add("selected");
      });
    });

    quill.root.addEventListener("click", (e) => {
      if (!e.target.closest("img")) {
        quill.root.querySelectorAll("img").forEach(i => i.classList.remove("selected"));
      }
    });
  };

  // 이미지 크기 적용
  const applyImageSize = () => {
    const width = parseInt(imageWidth);
    if (selectedImage && width > 0 && width <= 800) {
      selectedImage.style.width = width + "px";
      selectedImage.style.height = "auto";
    }
    setSelectedImage(null);
    setImageWidth("");
  };

  // Quill 컨텐츠 변경시 이미지 리사이징 설정
  useEffect(() => {
    if (!quillRef.current) return;
    
    const quill = quillRef.current.getEditor();
    
    const handleTextChange = () => {
      setTimeout(() => setupImageResizing(quill), 50);
    };

    quill.on("text-change", handleTextChange);
    setupImageResizing(quill);

    return () => {
      quill.off("text-change", handleTextChange);
    };
  }, []);

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
        ],
        handlers: { 
          image: imageHandler,
        },
      },
    }),
    []
  );

  const formats = [
    "header", "bold", "italic", "underline", "strike",
    "color", "background", "list", "bullet", "link", "image", "align",
  ];

  // 첨부파일 업로드
  const handleAttachFiles = (e) => {
    const files = Array.from(e.target.files);
    const maxSize = 25 * 1024 * 1024; // 25MB
    const currentSize = attachFiles.reduce((total, file) => total + file.size, 0);
    const newFilesSize = files.reduce((total, file) => total + file.size, 0);
    
    if (attachFiles.length + files.length > 10) {
      alert("첨부파일은 최대 10개까지 업로드할 수 있습니다.");
      if (attachInputRef.current) attachInputRef.current.value = "";
      return;
    }

    if (currentSize + newFilesSize > maxSize) {
      alert("첨부파일 총 크기가 25MB를 초과합니다.");
      if (attachInputRef.current) attachInputRef.current.value = "";
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

  // Base64 이미지를 Firebase URL로 변환
  const convertImagesToFirebaseUrls = async (htmlContent) => {
    const parser = new DOMParser();
    const doc = parser.parseFromString(htmlContent, 'text/html');
    const images = doc.querySelectorAll('img');
    
    let updatedContent = htmlContent;
    
    for (let img of images) {
      const src = img.getAttribute('src');
      
      // Base64 이미지인 경우만 처리
      if (src && src.startsWith('data:image/')) {
        try {
          // Base64를 Blob으로 변환
          const blob = base64ToBlob(src);
          const fileName = `quill_image_${Date.now()}.png`;
          
          // Firebase에 업로드
          const firebaseUrl = await uploadImageToFirebase(blob, fileName);
          
          // HTML 콘텐츠에서 Base64 URL을 Firebase URL로 교체
          updatedContent = updatedContent.replace(src, firebaseUrl);
          
          console.log(`이미지 업로드 완료: ${firebaseUrl}`);
        } catch (error) {
          console.error('이미지 업로드 실패:', error);
          throw new Error(`이미지 업로드에 실패했습니다: ${error.message}`);
        }
      }
    }
    
    return updatedContent;
  };

  // 이메일 전송
  const handleSend = async () => {
    if (memberList.length === 0 || !title.trim()) {
      alert("받을 회원과 제목을 모두 입력해주세요.");
      return;
    }

    setIsUploading(true);
    
    try {
      // 1. Firebase 인증
      const isAuthenticated = await authenticateUser();
      if (!isAuthenticated) {
        setIsUploading(false);
        return;
      }

      // 2. Quill 이미지를 Firebase에 업로드하고 URL로 변환
      let finalContent = content;
      if (content.includes('data:image/')) {
        console.log("Base64 이미지를 Firebase URL로 변환 중...");
        finalContent = await convertImagesToFirebaseUrls(content);
      }

      // 3. FormData 생성
      const formData = new FormData();
      formData.append("title", title.trim());
      formData.append("content", finalContent);
      formData.append("memberList", memberList.map(m => m.email).join(','));
      
      // 첨부파일 추가 (백엔드 DTO에 맞춰서 attachmentFile로 전송)
      console.log("첨부파일 개수:", attachFiles.length);
      attachFiles.forEach((file, index) => {
        console.log(`첨부파일 ${index + 1}:`, file.name, file.size);
        formData.append(`attachmentFile`, file); // 백엔드 DTO 필드명에 맞춤
      });

      // FormData 내용 확인
      console.log("FormData 내용:");
      for (let pair of formData.entries()) {
        console.log(pair[0], pair[1]);
      }

      // 4. 이메일 전송
      console.log("이메일 전송 중...");
      await sendEmail(formData);
      
      alert("이메일이 성공적으로 전송되었습니다!");
      
      // 초기화
      setTitle("");
      setContent("");
      setAttachFiles([]);
      setMemberList([]);
      
    } catch (error) {
      console.error("이메일 전송 실패:", error);
      alert(`이메일 전송에 실패했습니다: ${error.message}`);
    } finally {
      setIsUploading(false);
    }
  };

  const bytesToMB = (bytes) => (bytes / 1024 / 1024).toFixed(2);
  const currentSize = attachFiles.reduce((total, file) => total + file.size, 0);
*/

  return (
    <>
    {/* 
    <div className="max-w-5xl mx-auto p-8 bg-white">
      <div className="bg-blue-500 text-white p-4 rounded-t-lg">
        <h2 className="text-xl font-semibold text-center">이메일 발송</h2>
      </div>

      <div className="border-x border-b border-gray-200 p-6 rounded-b-lg shadow-sm">
        <div className="mb-8">
          <div className="flex items-center gap-2 mb-3">
            <label className="font-semibold text-lg text-gray-800">받을 회원</label>
            <span className="bg-blue-100 text-blue-800 px-2 py-1 rounded-full text-sm font-medium">
              {memberList.length}명 선택됨
            </span>
          </div>
          <div className="border-2 border-gray-200 rounded-xl p-4 max-h-[240px] overflow-y-auto bg-gray-50">
            {memberList.length === 0 ? (
              <div className="text-center py-8">
                <p className="text-gray-500 text-lg">선택된 회원이 없습니다</p>
                <p className="text-gray-400 text-sm mt-1">회원 목록에서 받을 회원을 선택해주세요</p>
              </div>
            ) : (
              <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
                {memberList.map((m) => (
                  <div
                    key={m.memId}
                    className="relative flex flex-col bg-white border border-gray-200 rounded-lg px-4 py-3 shadow-sm hover:shadow-md transition-shadow duration-200"
                  >
                    <span className="font-semibold text-gray-800 text-lg">{m.name}</span>
                    <span className="text-gray-600 text-sm mt-1">{m.email}</span>

                    <button
                      onClick={() => handleRemoveMember(m.memId)}
                      className="absolute top-2 right-2 p-1 hover:bg-red-100 rounded-full transition-colors duration-200"
                      title="회원 제외"
                    >
                      <img src={cancel} alt="삭제" className="w-4 h-4" />
                    </button>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>

        <div className="mb-6">
          <label className="font-semibold text-lg text-gray-800 block mb-2">제목</label>
          <input
            type="text"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            className="w-full border-2 border-gray-200 p-3 rounded-lg text-lg focus:border-blue-500 focus:outline-none transition-colors duration-200"
            placeholder="이메일 제목을 입력해주세요"
          />
        </div>

        <div className="mb-6">
          <label className="font-semibold text-lg text-gray-800 block mb-2">본문</label>
          <div className="text-sm text-gray-600 mb-2">
            💡 이미지를 더블클릭하면 크기를 조절할 수 있습니다 | 📤 전송 시 이미지가 자동으로 Firebase에 업로드됩니다
          </div>
          <div className="border-2 border-gray-200 rounded-lg overflow-hidden focus-within:border-blue-500 transition-colors duration-200">
            <ReactQuill
              ref={quillRef}
              theme="snow"
              value={content}
              onChange={setContent}
              modules={modules}
              formats={formats}
              placeholder="이메일 내용을 입력해주세요"
            />
          </div>
        </div>

        {selectedImage && (
          <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
            <div className="bg-white p-6 rounded-lg shadow-xl max-w-md w-full mx-4">
              <h3 className="text-lg font-semibold mb-4">이미지 크기 조절</h3>
              
              <div className="mb-4">
                <label className="block text-sm font-medium mb-2">너비 (px)</label>
                <input
                  type="text"
                  value={imageWidth}
                  onChange={(e) => setImageWidth(e.target.value)}
                  className="w-full border border-gray-300 rounded px-3 py-2"
                  placeholder="예: 300"
                />
                <div className="text-xs text-gray-500 mt-1">
                  권장: 100px ~ 800px
                </div>
              </div>

              <div className="flex gap-2 justify-end">
                <button
                  onClick={() => {
                    setSelectedImage(null);
                    setImageWidth("");
                  }}
                  className="px-4 py-2 bg-gray-300 text-gray-700 rounded hover:bg-gray-400 transition-colors"
                >
                  취소
                </button>
                <button
                  onClick={applyImageSize}
                  className="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600 transition-colors"
                >
                  적용
                </button>
              </div>
            </div>
          </div>
        )}

        <div className="mb-8">
          <div className="flex items-center justify-between mb-2">
            <label className="font-semibold text-lg text-gray-800">첨부파일</label>
            <span className="text-sm text-gray-500">
              {attachFiles.length}/10개 | {bytesToMB(currentSize)}/25.00 MB
            </span>
          </div>
          <input
            type="file"
            multiple
            onChange={handleAttachFiles}
            ref={attachInputRef}
            className="w-full border-2 border-gray-200 p-3 rounded-lg focus:border-blue-500 focus:outline-none transition-colors duration-200"
          />

          {attachFiles.length > 0 && (
            <div className="mt-4 grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
              {attachFiles.map((file, index) => (
                <div
                  key={index}
                  className="relative flex flex-col border-2 border-gray-200 p-4 rounded-lg bg-white hover:shadow-md transition-shadow duration-200"
                >
                  <button
                    onClick={() => handleRemoveFile(index)}
                    className="absolute top-2 right-2 p-1 hover:bg-red-100 rounded-full transition-colors duration-200"
                    title="파일 제거"
                  >
                    <img src={cancel} alt="삭제" className="w-4 h-4" />
                  </button>

                  {file.type.startsWith("image/") ? (
                    <img
                      src={URL.createObjectURL(file)}
                      alt="미리보기"
                      className="mb-3 rounded-lg shadow-sm max-h-32 object-cover"
                    />
                  ) : (
                    <div className="mb-3 flex items-center justify-center bg-gray-100 rounded-lg h-32">
                      <div className="text-center">
                        <div className="text-3xl text-gray-400 mb-2">📄</div>
                        <span className="text-gray-600 text-sm">미리보기 없음</span>
                      </div>
                    </div>
                  )}
                  <span className="text-gray-700 text-sm break-all font-medium">
                    {file.name}
                  </span>
                  <span className="text-gray-500 text-xs mt-1">
                    {bytesToMB(file.size)} MB
                  </span>
                </div>
              ))}
            </div>
          )}
        </div>

        <div className="text-center pt-4 border-t border-gray-200">
          <button 
            onClick={handleSend} 
            disabled={memberList.length === 0 || !title.trim() || isUploading || attachFiles.length > 10 || currentSize > 25 * 1024 * 1024}
            className={`font-medium py-3 px-6 rounded-lg transition-colors duration-200 ${
              isUploading 
                ? 'bg-gray-400 text-white cursor-not-allowed'
                : 'bg-blue-500 hover:bg-blue-600 text-white'
            }`}
          >
            {isUploading ? (
              <span className="flex items-center justify-center gap-2">
                <div className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin"></div>
                업로드 중...
              </span>
            ) : (
              "이메일 발송하기"
            )}
          </button>
          
          {(memberList.length === 0 || !title.trim() || attachFiles.length > 10 || currentSize > 25 * 1024 * 1024) && !isUploading && (
            <p className="text-red-500 text-sm mt-2">
              {memberList.length === 0 && "받을 회원을 선택해주세요"}
              {!title.trim() && "제목을 입력해주세요"}
              {attachFiles.length > 10 && "첨부파일은 최대 10개까지 가능합니다"}
              {currentSize > 25 * 1024 * 1024 && "첨부파일 크기를 25MB 이하로 줄여주세요"}
            </p>
          )}
        </div>
      </div>
    </div>
    */}
    </>
  );
};

export default AdminEmailComponent;
