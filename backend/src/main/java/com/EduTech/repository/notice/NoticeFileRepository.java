package com.EduTech.repository.notice;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.EduTech.entity.notice.Notice;
import com.EduTech.entity.notice.NoticeFile;

public interface NoticeFileRepository extends JpaRepository<NoticeFile, Long>{ //첨부파일과 관련된 DB담당
	
	List<NoticeFile> findByNotice_NoticeNum(Long noticeNum); //공지사항 번호로 첨부파일 조회
    
    void deleteByNotice_NoticeNum(Long noticeNum); //첨부파일 삭제

}
