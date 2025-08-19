import FacilityTitleComponent from "../../components/facility/FacilityTitleComponent";
import "react-datepicker/dist/react-datepicker.css";
import FacilityUpdateComponent from "../../components/facility/FacilityUpdateComponent";
import { useParams } from "react-router-dom";

const FacilityUpdatePage = () => {
  const { facRevNum } = useParams();
  const parsedFacilityNum = parseInt(facRevNum, 10);

  return (
    <div className="w-full">
      <FacilityTitleComponent title="프로그램 수정" />
      <FacilityUpdateComponent facRevNum={parsedFacilityNum} />
    </div>
  );
};

export default FacilityUpdatePage;
