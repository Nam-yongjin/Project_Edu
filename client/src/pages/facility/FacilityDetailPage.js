import FacilityDetailComponent from "../../components/facility/FacilityDetailComponent";
import { useParams } from "react-router-dom";
import "react-datepicker/dist/react-datepicker.css";

const FacilityDetailPage = () => {
  const { facRevNum } = useParams();

  return (
    <div className="w-full">
      <FacilityDetailComponent facRevNum={facRevNum} />
    </div>
  );
};
export default FacilityDetailPage;
