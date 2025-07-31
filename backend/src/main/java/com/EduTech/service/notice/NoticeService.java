package com.EduTech.service.notice;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.EduTech.dto.notice.NoticeCreateRegisterDTO;
import com.EduTech.dto.notice.NoticeDetailDTO;
import com.EduTech.dto.notice.NoticeListDTO;
import com.EduTech.dto.notice.NoticeSearchDTO;
import com.EduTech.dto.notice.NoticeUpdateRegisterDTO;

public interface NoticeService {
	
	//공지사항 등록
	Long createNotice(NoticeCreateRegisterDTO requestDto);
	
	//공지사항 수정
	void updateNotice(Long noticeNum, NoticeUpdateRegisterDTO requestDto);
	
	//공지사항 삭제(단일)
	void deleteNotice(Long noticeNum);
	
	//공지사항 삭제(일괄)
	void deleteNotices(List<Long> noticeNums);
	
	//공지사항 상세 조회
	NoticeDetailDTO getNoticeDetail(Long noticeNum);
	
	//공지사항 전체 조회
	Page<NoticeListDTO> getNoticeList(NoticeSearchDTO searchDto);
	
	//고정된 공지만 조회
	List<NoticeListDTO> getPinnedNotices();
	
	//상세 공지 조회 시 조회수 증가
	void increaseViewCount(Long noticeNum);

}