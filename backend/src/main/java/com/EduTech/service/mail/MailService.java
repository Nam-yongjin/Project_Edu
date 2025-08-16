package com.EduTech.service.mail;

import java.io.File;
import java.util.List;

import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.EduTech.dto.admin.AdminMessageDTO;

import jakarta.mail.internet.MimeMessage;

@Service
public class MailService {
	private final JavaMailSender javaMailSender;
	private final TemplateEngine templateEngine;

	// 생성자 추가
	public MailService(JavaMailSender javaMailSender, TemplateEngine templateEngine) {
		this.javaMailSender = javaMailSender;
		this.templateEngine = templateEngine;
	}

	// simpleMail을 이용한 메시지 보내기
	public void sendSimpleMailMessage(AdminMessageDTO adminMessageDTO) {
		SimpleMailMessage simpleMailMessage = new SimpleMailMessage();

		try {
			// new String[0]는 객체에 있는 memeberList를 문자열로 변환하여 생성함.
			String[] memberList = adminMessageDTO.getMemberList().toArray(new String[0]);
			// 메일을 받을 수신자 설정
			// setTo메소드는 문자열 배열을 받기에
			// 수신자를 한번에 넣을때는 변환이 필요함.
			simpleMailMessage.setTo(memberList);
			// 메일의 제목 설정
			simpleMailMessage.setSubject(adminMessageDTO.getTitle());
			// 메일의 내용 설정
			simpleMailMessage.setText(adminMessageDTO.getContent());

			javaMailSender.send(simpleMailMessage);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	// memeMessage를 사용하며 타임리프를 이용한 메시지 보내기
	public void sendMimeMessage(AdminMessageDTO adminMessageDTO) {
	    try {
	        // 회원별로 메일 전송
	        for (String memId : adminMessageDTO.getMemberList()) {
	            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
	            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

	            // 수신자 설정
	            mimeMessageHelper.setTo(memId);
	            // 제목 설정
	            mimeMessageHelper.setSubject(adminMessageDTO.getTitle());
	            File logoFile = new File("C:/Users/tee16/git/Project_Edu/backend/src/main/resources/static/images/logo.png");

	         // 로고를 메일에 inline으로 추가
	         mimeMessageHelper.addInline("logo", logoFile);
	            // 타임리프 Context
	            Context context = new Context();
	            context.setVariable("memId", memId); // 예시
	            context.setVariable("content", adminMessageDTO.getContent().replaceAll("(\r\n|\n|\r)", "<br/>"));
	            
	            // HTML 렌더링
	            String htmlContent = templateEngine.process("mailTemplate", context);
	            mimeMessageHelper.setText(htmlContent, true); // HTML 형식

	            // 1️⃣ 일반 첨부파일
	            List<MultipartFile> files = adminMessageDTO.getAttachmentFile();
	            if (files != null && !files.isEmpty()) {
	                for (MultipartFile file : files) {
	                    if (!file.isEmpty()) {
	                        mimeMessageHelper.addAttachment(file.getOriginalFilename(), file);
	                    }
	                }
	            }

	            // 2️⃣ Quill 이미지 첨부 (imageList)
	            List<MultipartFile> images = adminMessageDTO.getImageList();
	            if (images != null && !images.isEmpty()) {
	                for (MultipartFile image : images) {
	                    if (!image.isEmpty()) {
	                        mimeMessageHelper.addAttachment(image.getOriginalFilename(), image);
	                    }
	                }
	            }

	            // 메일 전송
	            javaMailSender.send(mimeMessage);
	        }
	    } catch (Exception e) {
	        throw new RuntimeException(e);
	    }
	}


}
