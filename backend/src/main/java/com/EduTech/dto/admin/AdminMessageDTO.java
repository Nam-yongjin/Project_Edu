package com.EduTech.dto.admin;

import java.util.ArrayList;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import com.EduTech.entity.member.Member;
import lombok.Data;


//관리자 -> 회원에게 보내는 메시지 알림 dto (반방향)
@Data
public class AdminMessageDTO { 
	private String title; // 메시지 보낼 제목
	private String content; // 메시지 보낼 내용
	private List<MultipartFile> AttachmentFile; // 메시지 보낼 첨부파일 (여기 dto는 db에 저장하지 않으므로 file을 dto에 넣었음)
	private List<String> memberList; // 메시지 보낼 멤버 리스트 (멤버 아이디가 담겨잇을곳)
	private List<MultipartFile> imageList;
	
}
