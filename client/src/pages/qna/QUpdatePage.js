import UpdateComponent from "../../components/qna/UpdateComponent";
import { useParams } from "react-router-dom";
const QUpdatePage = () => {
 const {questionNum} = useParams();
  return (
   <>
      <UpdateComponent questionNum={questionNum}/>
   </>
  );
};

export default QUpdatePage;
