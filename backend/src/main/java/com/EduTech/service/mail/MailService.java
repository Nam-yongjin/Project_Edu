package com.EduTech.service.mail;

import java.util.List;

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

			// 매개 변수: (메시지, multipart 여부, 인코딩)

			for (String memId : adminMessageDTO.getMemberList()) {
				MimeMessage mimeMessage = javaMailSender.createMimeMessage();
				MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
				// 메일을 받을 수신자 설정
				mimeMessageHelper.setTo(memId);
				// 메일의 제목 설정
				mimeMessageHelper.setSubject(adminMessageDTO.getTitle());

				Context context = new Context();
				context.setVariable("memId", memId); // 예시
				context.setVariable("content", adminMessageDTO.getContent().replaceAll("(\r\n|\n|\r)", "<br/>"));

				// HTML 렌더링
				String htmlContent = templateEngine.process("mailTemplate", context);
				// 메일의 내용 설정
				// true: html형식으로 보여줌 false: 일반 텍스트로 보여줌
				mimeMessageHelper.setText(htmlContent, true);
				// 첫번째 매개변수값: 사용자가 볼 첨부파일 이름
				// 두번째 매개변수값: 서버에서 첨부할 파일 경로
				List<MultipartFile> files = adminMessageDTO.getAttachmentFile();
				if (files != null && !files.isEmpty()) {
					for (MultipartFile file : files) {
						if (!file.isEmpty()) {
							mimeMessageHelper.addAttachment(file.getOriginalFilename(), // 사용자에게 보여질 이름
									file // MultipartFile 자체 전달 가능
							);
						}
					}
				}
				javaMailSender.send(mimeMessage);

			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

}
