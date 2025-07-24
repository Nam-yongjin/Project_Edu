package com.EduTech.service;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.EduTech.dto.admin.AdminMessageDTO;
import com.EduTech.service.admin.AdminService;
import com.EduTech.service.mail.MailService;

@SpringBootTest
public class AdminServiceTests {
	@Autowired
	AdminService adminService;
	@Autowired
	MailService mailService;
/*
	// @Test
	@DisplayName("simple mail 테스트")
	public void simpleMailTest() {
		List<String> memberList = new ArrayList<>();
		memberList.add("tee1694@naver.com");
		memberList.add("tee169412@gmail.com");
		AdminMessageDTO adminMessageDTO = new AdminMessageDTO();
		adminMessageDTO.setContent("안녕하십니까 이것은 메시지 테스트의 내용 입니다.");
		adminMessageDTO.setTitle("안녕하세요? 이것은 메시지 테스트의 제목 입니다.");
		adminMessageDTO.setMemberList(memberList);
		mailService.sendSimpleMailMessage(adminMessageDTO);
	}

	@Test
	@DisplayName("타임리프를 이용한 memMail 테스트")
	public void memMailTest() {
		List<String> memberList = new ArrayList<>();
		List<MultipartFile> fileList=new ArrayList<>();
		Path imagePath = Path.of("C:\\바지이미지.jpg");
		Path txtFilePath = Path.of("C:\\깃허브 순서REAL최종.txt");
		
		try {
		byte[] imageBytes = Files.readAllBytes(imagePath);
		byte[] fileBytes = Files.readAllBytes(txtFilePath);
		
		MockMultipartFile file1 = new MockMultipartFile("AttachmentFile", // form 필드명 (DTO에서 사용하는 필드명)
				"testfile1.txt", // 업로드되는 파일명
				"text/plain", // MIME 타입
				fileBytes // 실제 파일 내용
		);

		MockMultipartFile file2 = new MockMultipartFile("file", // form field name
				"duke.png", // 원래 파일명
				"image/png", // content type
				imageBytes // 실제 파일 내용 (byte 배열)
		);
		memberList.add("tee1694@naver.com");
		memberList.add("tee169412@gmail.com");
		fileList.add(file1);
		fileList.add(file2);
		AdminMessageDTO adminMessageDTO = new AdminMessageDTO();
		adminMessageDTO.setContent(
			    "안녕하십니까, 회원님.\n" +
			    "EduTech 시스템을 이용해주셔서 진심으로 감사드립니다.\n" +
			    "더 나은 서비스 제공을 위해 아래와 같이 시스템 점검이 예정되어 있습니다.\n" +
			    "✅ 점검 일시: 2025년 8월 1일(목) 00:00 ~ 06:00\n" +
			    "✅ 점검 내용: 서버 성능 개선 및 보안 패치 적용\n" +
			    "점검 시간 동안 서비스 이용이 일시적으로 제한될 수 있으니 양해 부탁드립니다.\n" +
			    "감사합니다.\n" +
			    "EduTech 드림."
			);

		adminMessageDTO.setTitle("📢 [EduTech] 시스템 점검 안내드립니다");
		adminMessageDTO.setMemberList(memberList);
		adminMessageDTO.setAttachmentFile(fileList);
		mailService.sendMimeMessage(adminMessageDTO);
		}
		catch(Exception e)
		{
			
		}
	}
*/
}
