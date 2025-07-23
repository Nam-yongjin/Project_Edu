package com.EduTech.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import com.EduTech.repository.member.MemberRepository;
import com.EduTech.repository.notice.NoticeFileRepository;
import com.EduTech.repository.notice.NoticeRepository;
import com.EduTech.service.notice.NoticeService;
import com.EduTech.util.FileUtil;

public class NoiceServiceTests {
	
	@Autowired
	private NoticeRepository noticeRepository;
	
	@Autowired
	private NoticeFileRepository noticeFileRepository;
	
	@Autowired
	private NoticeService noticeService;
	
	@Autowired
	private MemberRepository memberRepository;
	
	@Autowired
	private FileUtil fileUtil;
	
	@Autowired
	private ModelMapper modelMapper;
	
	//등록(첨부파일 등록, 작성자 조회)
	
	//수정(기존 파일 삭제 후 새로 저장, 존재하지 않는 공지번호, 기존 공지 업데이트)
	
	//삭제(단일, 일괄, 없는 번호 삭제 시 예외, 없는 공지 예외)
	
	//상세 조회(첨부파일 리스트, 없는 공지 예외)
	
	//목록 조회(검색조건 적용)
	
	//고정(true만 조회, 작성일 기준 내림차순)
	
	//조회수 증가(1씩 증가, 없는 공지 예외)
	
	//회원정보가 일치하지 않을 때
	
	//파일 유형이 다를 때
	
	//
	

}
