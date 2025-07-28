import { useState } from "react";
import { ko } from "date-fns/locale";
import DemTitleComponent from "../../components/demonstration/DemTitleComponent";
import useMove from "../../hooks/useMove";
import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";

const AddDemPage = () => {
  const initState = { demName: "", demMfr: "", itemNum: 0, demInfo: "", expDate: new Date() };

  const [fileInputs, setFileInputs] = useState([Date.now()]);
  const [images, setImages] = useState([]);
  const [dem, setDem] = useState({ ...initState });
  const [returnDate, setReturnDate] = useState(new Date());
  const { moveToPath, moveToReturn } = useMove();

  const handleFileChange = (id, e) => {
    const files = Array.from(e.target.files);
    if (files.length === 0) return;

    const newPreviews = files.map((file) => ({
      file,
      url: URL.createObjectURL(file),
      name: file.name,
    }));

    setImages((prev) => [...prev, ...newPreviews]);

    if (fileInputs[fileInputs.length - 1] === id) {
      setFileInputs((prev) => [...prev, Date.now()]);
    }
  };

  const addDem = () => {
    const formData = new FormData();
    for (let i = 0; i < images.length; i++) {
      formData.append("imageList", images[i]);
    }
    formData.append("demName", dem.demName);
    formData.append("demMfr", dem.demMfr);
    formData.append("itemNum", dem.itemNum);
    formData.append("demInfo", dem.demInfo);
    formData.append("expDate", dem.expDate.toISOString().split("T")[0]);
  };

  const handleChangeDem = (e) => {
    dem[e.target.name] = e.target.value;
    setDem({ ...dem });
  };

  const handleReturnDateChange = (date) => {
    setReturnDate(date);
    setDem((prev) => ({ ...prev, expDate: date }));
  };

  return (
    <div className="w-full">
      <DemTitleComponent title="실증 등록" />

      <div className="flex mt-10 max-w-6xl mx-auto">
        <div className="space-y-6 w-2/3">
          <div className="flex items-center">
            <label className="text-xl font-semibold w-[120px]">물품명:</label>
            <input
              type="text"
              placeholder="제품 이름을 입력해주세요."
              className="border p-3 text-lg flex-1 min-w-0 box-border"
              name="demName"
              value={dem.demName}
              onChange={handleChangeDem}
            />
          </div>
          <div className="flex items-center">
            <label className="text-xl font-semibold w-[120px]">제조사:</label>
            <input
              type="text"
              placeholder="제조사를 입력해주세요."
              className="border p-3 text-lg flex-1 min-w-0 box-border"
              name="demMfr"
              value={dem.demMfr}
              onChange={handleChangeDem}
            />
          </div>
          <div className="flex items-center">
            <label className="text-xl font-semibold w-[120px]">개수:</label>
            <input
              type="text"
              placeholder="개수를 입력해주세요."
              className="border p-3 text-lg flex-1 min-w-0 box-border"
              name="itemNum"
              value={dem.itemNum}
              onChange={handleChangeDem}
            />
          </div>
          <div className="flex items-start">
            <label className="text-xl font-semibold w-[120px] pt-3">소개:</label>
            <textarea
              rows={5}
              placeholder="제품 소개를 입력해주세요."
              className="border p-3 text-lg flex-1 resize-y min-w-0 box-border"
              name="demInfo"
              value={dem.demInfo}
              onChange={handleChangeDem}
            />
          </div>

          <div className="flex items-center">
            <label className="text-xl font-semibold w-[120px]">반납 날짜:</label>
            <DatePicker
              className="border p-3 text-lg flex-1 min-w-0 box-border"
              selected={returnDate}
              onChange={handleReturnDateChange}
              dateFormat="yyyy-MM-dd"
              placeholderText="날짜를 선택하세요"
              minDate={new Date()}
              name="expDate"
              locale={ko}
            />
          </div>

          <div>
            {fileInputs.map((id, index) => (
              <div key={id} className="flex items-center mt-3">
                <label className="text-xl font-semibold w-[120px]">이미지{index + 1}:</label>
                <input
                  type="file"
                  multiple
                  accept="image/*"
                  onChange={(e) => handleFileChange(id, e)}
                  className="border p-2 text-base flex-1 cursor-pointer min-w-0 box-border"
                />
              </div>
            ))}

            <div className="mt-4 flex justify-end gap-4 pr-2">
              <button
                className="bg-blue-600 text-white px-4 py-2 rounded-md hover:bg-blue-700 shadow"
                onClick={addDem}
              >
                상품 등록
              </button>
              <button
                className="bg-gray-400 text-white px-4 py-2 rounded-md hover:bg-gray-500 shadow"
                onClick={moveToReturn}
              >
                뒤로가기
              </button>
            </div>
          </div>
        </div>

        <div className="w-1/3 pl-10 flex flex-col gap-4 items-start">
          {images.map((img, index) => (
            <div key={index} className="flex flex-col items-start">
              <img
                src={img.url}
                alt={`선택된 이미지 ${index + 1}`}
                className="w-32 h-32 object-cover rounded-md border shadow mb-1"
              />
              <p className="text-sm text-gray-600 break-all">{img.name}</p>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};

export default AddDemPage;
