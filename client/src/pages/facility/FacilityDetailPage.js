import FacilityTitleComponent from "../../components/facility/FacilityTitleComponent";
import FacilityDetailComponent from "../../components/facility/FacilityDetailComponent";
import { useParams } from "react-router-dom";
import "react-datepicker/dist/react-datepicker.css";

const FacilityDetailPage = () => {
  const { facRevNum } = useParams();

  return (
    <div className="w-full">
      <FacilityTitleComponent title="시설 상세" />
      <FacilityDetailComponent facRevNum={facRevNum} />
    </div>
  );
};
export default FacilityDetailPage;
