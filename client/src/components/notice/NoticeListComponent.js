import React from "react";
import { Link } from "react-router-dom";

const NoticeListComponent = ({ notices }) => {
  return (
    <div>
      <table>
        <thead>
          <tr>
            <th>번호</th>
            <th>제목</th>
            <th>작성자</th>
            <th>작성일</th>
            <th>조회수</th>
          </tr>
        </thead>
        <tbody>
          {notices.length === 0? (
            <tr>
              <td>작성된 공지사항이 없습니다.</td>
            </tr>
            ) : (
            notices.map((notice) => (
              <tr key={notice.noticeNum}>
                <Link to={`/notice/${notice.noticeNum}`}>{notice.title}</Link>
                <td>{notice.Name}</td>
                <td>{notice.CreatedAt}</td>
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