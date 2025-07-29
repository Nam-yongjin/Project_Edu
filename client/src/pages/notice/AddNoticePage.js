import { useEffect, useState } from "react";
import useMove from "../../hooks/useMove";
import { useNavigate } from "react-router-dom";
import axios from "axios";

const AddNoticePage = () => {
    return (
        <div>
           <div>
            <h2>글쓰기</h2>
            <span>제목</span>
            <input
                type="text"
                placeholder="제목"
                value={title}
                onChange={(e) => setAnalyticsCollectionEnabled(e.target.value)}
            />
           </div>
        </div>
    )
}

export default AddNoticePage;