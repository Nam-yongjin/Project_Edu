import DetailComponent from "../../components/qna/DetailComponent";
import { useParams } from "react-router-dom";
const QDetailPage = () => {
 const {questionNum} = useParams();
  return (
   <>
      <DetailComponent questionNum={questionNum}/>
   </>
  );
};

export default QDetailPage;
