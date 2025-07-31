import jwtAxios from "../util/jwtUtil";

export const urlToFile = async (url, fileName, mimeType) => {
  const res = await jwtAxios.get(url, { responseType: "blob" });  // jwtAxios로 URL에 GET 요청을 보내고, response를 Blob 형태로 받음
  return new File([res.data], fileName, { type: mimeType });  // 받은 Blob 데이터를 File 객체로 변환하여 반환
};

export const urlListToFileList = async (urlList,imageNameList) => {
  return Promise.all(
    // 비동기 요청을 위해 async를 사용
    urlList.map(async (url, index) => {
      const ext = url.split(".").pop().toLowerCase(); // URL에서 파일 확장자 추출 (예: jpg, png)
      const mime = ext === "png" ? "image/png" : "image/jpeg"; // 확장자에 따라 MIME 타입 결정 (png면 image/png, 아니면 image/jpeg로 간주)
      const name=imageNameList[index];   // imageNameList에서 같은 인덱스 위치의 이름 가져오기
      const file = await urlToFile(url, name, mime);   // urlToFile 함수 호출해 파일 객체 생성
      return { file, url, name: file.name }; // 반환 객체: file (File 객체), url (원본 URL), name (파일명)
    })
  );
};
