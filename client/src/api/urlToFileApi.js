import jwtAxios from "../util/jwtUtil";

export const urlToFile = async (url, fileName, mimeType) => {
  const res = await jwtAxios.get(url, { responseType: "blob" });
  return new File([res.data], fileName, { type: mimeType });
};

export const urlListToFileList = async (urlList,imageNameList) => {
  return Promise.all(
    urlList.map(async (url, index) => {
      const ext = url.split(".").pop().toLowerCase();
      const mime = ext === "png" ? "image/png" : "image/jpeg";
      const name=imageNameList[index];
      const file = await urlToFile(url, name, mime);
      return { file, url, name: file.name };
    })
  );
};
