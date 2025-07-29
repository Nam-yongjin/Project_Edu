import React from "react";
import { Link } from "react-router-dom";

const NoticeListComponent = ({ notices, selectedNotices, setSelectedNotices, isAdmin }) => {
  const handleCheckboxChange = (noticeNum, isChecked) => {
    if (isChecked) {
      setSelectedNotices([...selectedNotices, noticeNum]);
    } else {
      setSelectedNotices(selectedNotices.filter((num) => num !== noticeNum))
    }
  };
  return (
    <div>
      <table>
        <thead>
          <tr>
            {isAdmin && <th>선택</th>}
            <th>번호</th>
            <th>제목</th>
            <th>작성자</th>
            <th>작성일</th>
            <th>조회수</th>
          </tr>
        </thead>
        <tbody>
           <tr>
              <th>1</th>
              <th>게시글1</th>
              <th>admin1</th>
              <th>2015-07-20</th>
              <th>34</th>
            </tr>
            <tr>
              <th>2</th>
              <th>게시글2</th>
              <th>admin2</th>
              <th>2015-07-23</th>
              <th>120</th>
            </tr>
            <tr>
              <th>3</th>
              <th>게시글3</th>
              <th>admin3</th>
              <th>2015-07-29</th>
              <th>10</th>
            </tr>
          {notices.length === 0 ? (
            <tr>
              <td colSpan={isAdmin ? 6 : 5}>작성된 공지사항이 없습니다.</td>
            </tr>
          ) : (
            notices.map((notice) => (
              <tr key={notice.noticeNum}>
              {isAdmin && (
                <td>
                  <input 
                    type="checkbox"
                    checked={selectedNotices.includes(notice.noticeNum)}
                    onChange={(e) => 
                      handleCheckboxChange(notice.noticeNum, e.target.checked)
                    }
                  />
                </td>
              )}
              <td>{notice.noticeNum}</td>
              <td>
                <Link
                  to={`/notice/${notice.noticeNum}`}
                >
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