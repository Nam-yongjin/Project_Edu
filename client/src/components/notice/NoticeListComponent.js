import React from "react";
import { Link } from "react-router-dom";
import { useSelector } from "react-redux";

const NoticeListComponent = ({ notices, selectedNotices, setSelectedNotices}) => {
  const loginState = useSelector((state) => state.loginState);
  const handleCheckboxChange = (noticeNum, isChecked) => {
    if (isChecked) {
      setSelectedNotices([...selectedNotices, noticeNum]);
    } else {
      setSelectedNotices(selectedNotices.filter((num) => num !== noticeNum));
    }
  };

  return (
    <div>
      <table>
        <thead>
          <tr>
            {loginState.role === 'ADMIN' ? (
            <th>선택</th>
            ) : (<></>)}
            <th>번호</th>
            <th>제목</th>
            <th>작성자</th>
            <th>작성일</th>
            <th>조회수</th>
          </tr>
        </thead>
        <tbody>
          {notices.length === 0 ? (
            <tr>
              <td colSpan={loginState.role === "ADMIN" ? 6 : 5}>작성된 공지사항이 없습니다.</td>
            </tr>
          ) : (
            notices.map((notice) => (
              <tr key={notice.noticeNum}>
                {loginState.role === 'ADMIN' ? (
                  <td>
                    <input
                      type="checkbox"
                      checked={selectedNotices.includes(notice.noticeNum)}
                      onChange={(e) =>
                        handleCheckboxChange(notice.noticeNum, e.target.checked)
                      }
                    />
                  </td>
                ) : (<></>)}
                <td>{notice.noticeNum}</td>
                <td>
                  <Link to={`/notice/${notice.noticeNum}`}>
                    {notice.title}
                  </Link>
                </td>
                <td>{notice.name}</td>
                <td>{notice.createdAt}</td>
                <td>{notice.view}</td>
              </tr>
            ))
          )}
        </tbody>
      </table>
    </div>
  );
};

export default NoticeListComponent;