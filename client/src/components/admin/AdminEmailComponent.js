import { useState, useEffect, useRef, useMemo } from "react";
import ReactQuill from "react-quill";
import "react-quill/dist/quill.snow.css";
import { getStorage, ref, uploadBytes, getDownloadURL } from "firebase/storage";
import { getAuth, signInAnonymously } from "firebase/auth";
import { app } from "../../api/emailFirebaseApi";
import { sendEmail } from "../../api/adminApi";
import cancel from "../../assets/cancel.png";
import useMove from "../../hooks/useMove";

const AdminEmailComponent = ({ members }) => {
  
  const [memberList, setMemberList] = useState([]); // 받아올 회원 리스트
  const [title, setTitle] = useState(""); // 제목
  const [content, setContent] = useState(""); // quil 내용
  const [attachFiles, setAttachFiles] = useState([]); // 첨부파일
  const [isUploading, setIsUploading] = useState(false); // 업로드 여부
  const [selectedImage, setSelectedImage] = useState(null); // 이미지 더블 클릭 했을때 selectedImage에 등록, 리사이즈 가능
  const [imageWidth, setImageWidth] = useState(""); // 이미지 길이
  const [imageHeight, setImageHeight] = useState(""); // 이미지 높이
  const { moveToPath } = useMove(); // 이동 변수
  const quillRef = useRef(null); // quil 접근 변수
  const attachInputRef = useRef(null); // 첨부파일 접근 변수

  // Firebase 초기화
  const storage = getStorage(app);
  const auth = getAuth(app);

  useEffect(() => {
    if (members.length === 0) return;
    setMemberList(members);

    // Quill 스타일 설정
    const style = document.createElement("style");
   // 외부 라이브러리는 tailwind가 먹히지 않아 직접 스타일을 넣어줌
    style.innerHTML = `
      .ql-editor { 
        min-height: 400px; 
        max-height: 600px; 
        overflow-y: auto; 
        padding: 16px; 
        line-height: 1.6;
        font-size: 14px;
      }
      .ql-editor img { 
        max-width: 600px;
        height: auto; 
        display: inline-block; 
        margin: 10px 0;
        cursor: pointer;
        border: 2px solid transparent;
        border-radius: 4px;
        transition: border-color 0.2s ease;
      }
      .ql-editor img:hover {
        border-color: #3b82f6;
      }
      .ql-editor img.selected {
        border-color: #3b82f6;
        box-shadow: 0 0 8px rgba(59, 130, 246, 0.3);
      }
    `;
    document.head.appendChild(style);
  }, [members]);

  // Firebase 익명 인증
  const authenticateUser = async () => {
    try {
      await signInAnonymously(auth);
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
      const imageRef = ref(storage, `email-images/${Date.now()}_${fileName}`); // 이미지 경로
      const snapshot = await uploadBytes(imageRef, file); // 파이어베이스에 이미지 업로드
      const downloadURL = await getDownloadURL(snapshot.ref); // content에 업로드한 이미지 경로 변경하기 위해 url을 받아옴
      return downloadURL;
    } catch (error) {
      console.error("이미지 업로드 실패:", error);
      throw error;
    }
  };

  // Base64를 Blob으로 변환(quil은 자동으로 이미지 업로드 시, base64파일로 변환함,
  // 즉, 파이어 베이스에 업로드 위해 blob 파일로 변환함)
  const base64ToBlob = (base64Data) => {
    // 1. "data:image/png;base64,실제데이터" 분리
    const arr = base64Data.split(',');
    // 2. MIME 타입 추출: "image/png"
    const mime = arr[0].match(/:(.*?);/)[1];
    // 3. Base64 문자열을 바이너리 문자열로 디코딩
    const bstr = atob(arr[1]);
    // 4. 바이너리 문자열을 바이트 배열로 변환
    let n = bstr.length;
    const u8arr = new Uint8Array(n);
    while (n--) {
      u8arr[n] = bstr.charCodeAt(n); // 각 문자를 바이트로
    }
    // 5. Blob 객체 생성
    return new Blob([u8arr], { type: mime });
  };

  // Quill 이미지 업로드 핸들러
  const imageHandler = () => {
    const input = document.createElement("input");
    input.type = "file"; // 파일 타임
    input.accept = "image/*"; // 이미지만 업로드 가능
    input.click(); // 파일 선택창 띄움

    input.onchange = () => {
      const file = input.files?.[0];
      if (!file) return;

      const reader = new FileReader();
      reader.onload = () => { // 이미지 변환이 끝나면 실행
        const quill = quillRef.current.getEditor();
        const range = quill.getSelection(true); // 현재 커서 위치
        quill.insertEmbed(range.index, "image", reader.result); // quil에 이미지 삽입
        quill.setSelection(range.index + 1); // 다음 커서로 이동
        setTimeout(() => setupImageResizing(quill), 100); // 이미지 태그 삽입 시간 부여
      };
      reader.readAsDataURL(file); // quil은 base64만 지원하므로 해당 타입으로 변환
    };
  };

  // 이미지 크기 조절 설정
  const setupImageResizing = (quill) => {
    const images = quill.root.querySelectorAll("img"); // quil에 업로드된 이미지 불러옴
    
    images.forEach((img) => {
      if (img.dataset.resizeSetup) return; // 내가 원하는 커스텀 데이터를 자유롭게 붙였다가 꺼내 쓸 수 있는 기능
                                            // 여기선 해당 이미지에 이미 전처리를 했을 경우, 전처리 하지않기 위해 사용한다.
      img.dataset.resizeSetup = "true";

      if (!img.style.width) {
        img.style.width = "300px";
      }
      img.style.display = "inline-block";

      // 더블 클릭 이벤트 리스너 설정
      img.addEventListener("dblclick", (e) => {
        e.preventDefault(); // 이벤트 실행 시, 브라우저가 다른 행동을 하지 않도록 막음
        e.stopPropagation(); // 이벤트가 다른 곳으로 전파되지 않게 막는 역할
        
        const currentWidth = parseInt(img.style.width) || 300;
        const currentHeight = parseInt(img.style.height) || '';
        setImageWidth(currentWidth.toString());
        setImageHeight(currentHeight.toString());
        setSelectedImage(img);
      });

      // 일반 클릭 이벤트 리스너 설정
      img.addEventListener("click", (e) => {
        e.preventDefault();
        e.stopPropagation();
        
        quill.root.querySelectorAll("img").forEach(i => i.classList.remove("selected"));
        img.classList.add("selected");
      });
    });

    quill.root.addEventListener("click", (e) => {
      if (!e.target.closest("img")) { // 이미지가 아닌 다른 곳을 선택 했을 경우. 선택 상태를 해제
        quill.root.querySelectorAll("img").forEach(i => i.classList.remove("selected"));
      }
    });
  };

  // 이미지 크기 적용
  const applyImageSize = () => {
    const width = parseInt(imageWidth);
    const height = parseInt(imageHeight);
    
    // content가 없을 경우 return
    if (!content.trim()) {
      alert("본문 내용이 없습니다.");
      return;
    }
    
    // 가로: 100px ~ 600px 범위 체크
    if (width && (width < 100 || width > 600)) {
      alert("너비는 100px에서 600px 사이의 값을 입력해주세요.");
      return;
    }
    
    // 세로: 100px ~ 300px 범위 체크 (값이 있을 경우에만)
    if (height && (height < 100 || height > 300)) {
      alert("높이는 100px에서 300px 사이의 값을 입력해주세요.");
      return;
    }
    
    if (selectedImage) {
      if (width > 0 && width <= 600) {
        selectedImage.style.width = width + "px";
      }
      if (height > 0 && height <= 300) {
        selectedImage.style.height = height + "px";
      } else if (!height) {
        selectedImage.style.height = "auto";
      }
    }
    setSelectedImage(null);
    setImageWidth("");
    setImageHeight("");
  };

  // Quill 컨텐츠 변경시 이미지 리사이징 설정
  useEffect(() => {
    if (!quillRef.current) return;
    
    const quill = quillRef.current.getEditor();
    const handleTextChange = () => {
      setTimeout(() => setupImageResizing(quill), 50); // 내용이 바뀔때마다 실행
    };

    quill.on("text-change", handleTextChange);
    setupImageResizing(quill);

    return () => { // 다른 페이지 이동시 이벤트 리스너 제거
      quill.off("text-change", handleTextChange);
    };
  }, []);

  // 메뉴 선택창
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

  // Quill이 실제 적용할 수 있는 스타일/포맷을 지정하는 배열
  const formats = [
    "header", "bold", "italic", "underline", "strike",
    "color", "background", "list", "bullet", "link", "image", "align",
  ];

  // 첨부파일 업로드
  const handleAttachFiles = (e) => {
    const files = Array.from(e.target.files);
    const maxSize = 25 * 1024 * 1024; // 25mb 설정
    // array.reduce((누적값, 현재값) 배열의 값을 합해서 리턴하는 함수
    const currentSize = attachFiles.reduce((total, file) => total + file.size, 0);
    const newFilesSize = files.reduce((total, file) => total + file.size, 0);
    
    if (attachFiles.length + files.length > 8) {
      alert("첨부파일은 최대 8개까지 업로드할 수 있습니다.");
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

  // 파일 제거
  const handleRemoveFile = (index) => {
    setAttachFiles((prev) => prev.filter((_, i) => i !== index));
  };

  // 회원 제거
  const handleRemoveMember = (memId) => {
    setMemberList((prev) => prev.filter((m) => m.memId !== memId));
  };

  // Base64 이미지를 Firebase URL로 변환
  const convertImagesToFirebaseUrls = async (htmlContent) => {
    const parser = new DOMParser(); // img 태그를 찾기위해 parser를 사용해 html로 파싱
    const doc = parser.parseFromString(htmlContent, 'text/html');
    const images = doc.querySelectorAll('img');
    
    let updatedContent = htmlContent;
    
    for (let img of images) {
      const src = img.getAttribute('src');
      
      if (src && src.startsWith('data:image/')) { // base64방식의 이미지를 찾음
        try {
          const blob = base64ToBlob(src); 
          const fileName = `quill_image_${Date.now()}.png`;
          const firebaseUrl = await uploadImageToFirebase(blob, fileName); // 파일 업로드
          updatedContent = updatedContent.replace(src, firebaseUrl);
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
    if (memberList.length === 0) {
      alert("받을 회원을 선택해주세요.");
      return;
    }
    
    if (!title.trim()) {
      alert("제목을 입력해주세요.");
      return;
    }

    // content 검증 추가
    if (!content.trim()) {
      alert("본문 내용을 입력해주세요.");
      return;
    }

    setIsUploading(true);
    
    try {
      const isAuthenticated = await authenticateUser();
      if (!isAuthenticated) {
        setIsUploading(false);
        return;
      }

      let finalContent = content;
      if (content.includes('data:image/')) { // base64가 존재한다면,
        finalContent = await convertImagesToFirebaseUrls(content);
      }

      const formData = new FormData();
      formData.append("title", title.trim());
      formData.append("content", finalContent);
      formData.append("memberList", memberList.map(m => m.email).join(','));
      
      attachFiles.forEach((file) => {
        formData.append(`attachmentFile`, file);
      });

      await sendEmail(formData);
      
      alert("이메일이 성공적으로 전송되었습니다!");
      moveToPath("/admin/adminSelectEmail");
      
    } catch (error) {
      console.error("이메일 전송 실패:", error);
      alert(`이메일 전송에 실패했습니다: ${error.message}`);
    } finally {
      setIsUploading(false);
    }
  };

  const bytesToMB = (bytes) => (bytes / 1024 / 1024).toFixed(2);
  const currentSize = attachFiles.reduce((total, file) => total + file.size, 0);

  return (
    <>

    <div className="max-w-screen-xl mx-auto my-10">
      <div className="min-blank">
        <div className="page-shadow bg-white">
          <div className=" p-4 rounded-t-lg border-b">
            <h2 className="newText-2xl font-semibold text-center text-gray-800">메일 전송</h2>
          </div>

          <div className="p-6">
            <div className="mb-8">
              <div className="flex items-center gap-2 mb-3">
                <label className="newText-lg font-semibold text-gray-800">받을 회원</label>
                <span className="bg-blue-100 text-blue-800 px-2 py-1 rounded-full newText-sm font-medium">
                  {memberList.length}명 선택됨
                </span>
              </div>
              
              <div className="border rounded-lg p-4 max-h-60 overflow-y-auto bg-gray-50">
                {memberList.length === 0 ? (
                  <div className="text-center py-8">
                    <p className="text-gray-500 newText-base">선택된 회원이 없습니다</p>
                    <p className="text-gray-400 newText-sm mt-1">회원 목록에서 받을 회원을 선택해주세요</p>
                  </div>
                ) : (
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
                    {memberList.map((m) => (
                      <div key={m.memId} className="relative bg-white border rounded-lg p-3 shadow-sm">
                        <div className="newText-base font-semibold text-gray-800">{m.name}</div>
                        <div className="newText-sm text-gray-600">{m.memId}</div>
                        <div className="newText-sm text-gray-600">{m.email}</div>
                        
                        <button
                          onClick={() => handleRemoveMember(m.memId)}
                          className="absolute top-2 right-2 p-1 hover:bg-red-100 rounded-full transition-colors"
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
              <label className="newText-lg font-semibold text-gray-800 block mb-2">제목</label>
              <input
                type="text"
                value={title}
                onChange={(e) => setTitle(e.target.value)}
                className="input-focus w-full newText-base"
                placeholder="이메일 제목을 입력해주세요"
              />
            </div>

            <div className="mb-6">
              <label className="newText-lg font-semibold text-gray-800 block mb-2">본문</label>
              <div className="newText-sm text-gray-600 mb-2">
                💡 이미지를 더블클릭하면 크기를 조절할 수 있습니다 (최대 600px)
              </div>
              <div className="border rounded-lg overflow-hidden">
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
                  <h3 className="newText-lg font-semibold mb-4">이미지 크기 조절</h3>
                  
                  <div className="mb-4">
                    <label className="block newText-sm font-medium mb-2">너비 (px)</label>
                    <input
                      type="text"
                      value={imageWidth}
                      onChange={(e) => setImageWidth(e.target.value)}
                      className="input-focus w-full"
                      placeholder="예: 300"
                    />
                  </div>

                  <div className="mb-4">
                    <label className="block newText-sm font-medium mb-2">높이 (px)</label>
                    <input
                      type="text"
                      value={imageHeight}
                      onChange={(e) => setImageHeight(e.target.value)}
                      className="input-focus w-full"
                      placeholder="예: 200 (비워두면 자동)"
                    />
                    <div className="newText-xs text-gray-500 mt-1">
                      너비: 100px ~ 600px, 높이: 100px ~ 300px
                    </div>
                  </div>

                  <div className="flex gap-2 justify-end">
                    <button
                      onClick={() => {
                        setSelectedImage(null);
                        setImageWidth("");
                        setImageHeight("");
                      }}
                      className="normal-button newText-sm px-4 py-2"
                    >
                      취소
                    </button>
                    <button
                      onClick={applyImageSize}
                      className="positive-button newText-sm px-4 py-2"
                    >
                      적용
                    </button>
                  </div>
                </div>
              </div>
            )}

            <div className="mb-8">
              <div className="flex items-center justify-between mb-2">
                <label className="newText-lg font-semibold text-gray-800">첨부파일</label>
                <span className="newText-sm text-gray-500">
                  {attachFiles.length}/8개 | {bytesToMB(currentSize)}/25.00 MB
                </span>
              </div>
              <input
                type="file"
                multiple
                onChange={handleAttachFiles}
                ref={attachInputRef}
                className="input-focus w-full"
              />

              {attachFiles.length > 0 && (
                <div className="mt-4 grid grid-cols-4 gap-3">
                  {attachFiles.map((file, index) => (
                    <div key={index} className="relative border rounded-lg p-2 bg-white shadow-sm aspect-square">
                      <button
                        onClick={() => handleRemoveFile(index)}
                        className="absolute -top-2 -right-2 p-1 bg-red-500 hover:bg-red-600 rounded-full transition-colors z-10"
                      >
                        <img src={cancel} alt="삭제" className="w-3 h-3 filter brightness-0 invert" />
                      </button>

                      {file.type.startsWith("image/") ? (
                        <img
                          src={URL.createObjectURL(file)}
                          alt="미리보기"
                          className="w-full h-full object-cover rounded-lg"
                          style={{
                            minHeight: '80px',
                            maxHeight: '120px'
                          }}
                        />
                      ) : (
                        <div className="w-full h-full flex flex-col items-center justify-center bg-gray-100 rounded-lg">
                          <div className="newText-2xl text-gray-400 mb-1">📄</div>
                          <span className="text-gray-600 newText-xs text-center leading-tight break-all px-1">
                            {file.name.length > 12 ? file.name.substring(0, 12) + '...' : file.name}
                          </span>
                        </div>
                      )}
                      
                      <div className="absolute bottom-0 left-0 right-0 bg-black bg-opacity-75 text-white newText-xs p-1 rounded-b-lg text-center">
                        <div className="truncate" title={file.name}>
                          {file.name.length > 10 ? file.name.substring(0, 10) + '...' : file.name}
                        </div>
                        <div className="text-gray-300">
                          {bytesToMB(file.size)} MB
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </div>

            <div className="text-center pt-4 ">
              <button 
                onClick={handleSend} 
                disabled={isUploading || attachFiles.length > 8 || currentSize > 25 * 1024 * 1024}
                className={`newText-base px-6 py-3 rounded-lg font-semibold transition-colors ${
                  isUploading 
                    ? 'disable-button'
                    : 'positive-button'
                }`}
              >
                {isUploading ? (
                  <span className="flex items-center justify-center gap-2">
                    <div className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin"></div>
                    업로드 중...
                  </span>
                ) : (
                  "메일 전송하기"
                )}
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    </>
  );
};

export default AdminEmailComponent;