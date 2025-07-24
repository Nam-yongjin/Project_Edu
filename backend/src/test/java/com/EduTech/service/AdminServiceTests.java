package com.EduTech.service;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.EduTech.dto.Page.PageResponseDTO;
import com.EduTech.dto.admin.AdminMemberViewReqDTO;
import com.EduTech.dto.admin.AdminMemberViewResDTO;
import com.EduTech.dto.demonstration.DemonstrationApprovalRegDTO;
import com.EduTech.dto.demonstration.DemonstrationApprovalResDTO;
import com.EduTech.entity.demonstration.DemonstrationState;
import com.EduTech.entity.member.MemberRole;
import com.EduTech.entity.member.MemberState;
import com.EduTech.service.admin.AdminService;
import com.EduTech.service.mail.MailService;

@SpringBootTest
public class AdminServiceTests {
	@Autowired
	AdminService adminService;
	@Autowired
	MailService mailService;
	
	//@Test
	@DisplayName("res 상태 업데이트 테스트")
	public void ResStateUpdateTest() {
		DemonstrationApprovalResDTO demonstrationApprovalResDTO=new DemonstrationApprovalResDTO();
		demonstrationApprovalResDTO.setDemonstrationState(DemonstrationState.REJECT);
		demonstrationApprovalResDTO.setDemRevNum(Long.valueOf(201));
		demonstrationApprovalResDTO.setMemId("user0");
		adminService.approveOrRejectDemRes(demonstrationApprovalResDTO);
	}
	
	//@Test
	@DisplayName("reg 상태 다중 업데이트 테스트")
	public void RegStateUpdateTest() {
		DemonstrationApprovalRegDTO demonstrationApprovalRegDTO=new DemonstrationApprovalRegDTO();
		demonstrationApprovalRegDTO.setDemonstrationState(DemonstrationState.ACCEPT);
		demonstrationApprovalRegDTO.setDemRegNum(Long.valueOf(313));
		demonstrationApprovalRegDTO.setMemId("user1");
		adminService.approveOrRejectDemReg(demonstrationApprovalRegDTO);
	}
		
	//@Test
	@DisplayName("회원 상태 업데이트 테스트") 
	public void MemStateUpdateTest() {
		List<String> memId=new ArrayList<>();
		memId.add("user3");
		memId.add("user10");
		memId.add("user1");
		MemberState state=MemberState.BEN;
		adminService.MemberStateChange(memId,state);
	}
	
	//@Test
	@DisplayName("회원 상태 다중 업데이트 테스트") 
	public void MemStateUpdateOneTest() {
		List<String> memId=new ArrayList<>();
		memId.add("user1");
		MemberState state=MemberState.NORMAL;
		adminService.MemberStateChange(memId,state);
	}
	
	
	//@Test
	@DisplayName("관리자 회원 목록 페이지 조회 기능 테스트")
	public void adminMemberListTest() {
		AdminMemberViewReqDTO adminMemberViewReqDTO=new AdminMemberViewReqDTO();
		adminMemberViewReqDTO.setState(MemberState.BEN);
		int pageCount=0;
		
		PageResponseDTO<AdminMemberViewResDTO> members=adminService.adminViewMembers(adminMemberViewReqDTO,pageCount);
		
		System.out.println(members);
		
	}
	
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
