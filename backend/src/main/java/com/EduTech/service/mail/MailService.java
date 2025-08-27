package com.EduTech.service.mail;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.EduTech.dto.admin.AdminMessageDTO;

import jakarta.mail.internet.MimeMessage;

@Service
public class MailService {

    private final JavaMailSender javaMailSender; // 스프링에서 제공하는 메일 발송 객체
    private final TemplateEngine templateEngine; // Thymeleaf 템플릿 엔진, 메일 HTML 처리용

    public MailService(JavaMailSender javaMailSender, TemplateEngine templateEngine) {
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
    }

    // 첨부파일 DTO
    public static class Attachment {
        private final String fileName;
        private final byte[] content;
        private final String contentType;

        public Attachment(String fileName, byte[] content, String contentType) {
            this.fileName = fileName;
            this.content = content;
            this.contentType = contentType != null ? contentType : "application/octet-stream";
        }

        public String getFileName() { return fileName; }
        public byte[] getContent() { return content; }
        public String getContentType() { return contentType; }
    }

    // 비동기로 HTML+첨부파일 메일 발송 (Firebase 이미지 처리)
    @Async("mailTaskExecutor")
    public CompletableFuture<Void> sendMimeMessage(AdminMessageDTO adminMessageDTO) {
        try {
            List<String> memberList = adminMessageDTO.getMemberList();

            // 첨부파일을 byte[]로 미리 복사 (비동기 안전)
            List<Attachment> attachments = new ArrayList<>();
            List<MultipartFile> files = adminMessageDTO.getAttachmentFile();
            if (files != null) {
                for (MultipartFile file : files) {
                    try {
                        if (!file.isEmpty()) {
                            attachments.add(new Attachment(
                                file.getOriginalFilename(),
                                file.getBytes(),
                                file.getContentType()
                            ));
                        }
                    } catch (Exception e) {
                        System.out.println("첨부파일 처리 실패: " + file.getOriginalFilename());
                    }
                }
            }

            // 각 수신자별 메일 발송
            for (String email : memberList) {
                MimeMessage mimeMessage = javaMailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8"); // MimeMessageHelper를 사용하면 HTML + 첨부파일 모두 가능

                helper.setTo(email);
                helper.setSubject(adminMessageDTO.getTitle());

                // HTML 본문 처리 - 이메일 클라이언트 호환성을 위한 강력한 처리
                Context context = new Context();
                context.setVariable("email", email);
                
                String contentHtml = adminMessageDTO.getContent().replaceAll("(\r\n|\n|\r)", "<br/>"); // \n을 <br/>로 변환 → 줄바꿈 유지s
                
                
                // 이미지와 텍스트 중앙/좌/우 정렬 처리
                // 1. 모든 img 태그를 찾아서 테이블로 감싸기 (가장 안전한 방법)
                contentHtml = contentHtml.replaceAll(
                    "<p([^>]*?)style=\"([^\"]*?)text-align:\\s*center([^\"]*?)\"([^>]*?)>\\s*(<img[^>]*?)>\\s*</p>",
                    "<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"margin: 10px 0;\"><tr><td align=\"center\">$5 style=\"display: block; margin: 0 auto;\"></td></tr></table>"
                );
                
                contentHtml = contentHtml.replaceAll(
                    "<p([^>]*?)style=\"([^\"]*?)text-align:\\s*left([^\"]*?)\"([^>]*?)>\\s*(<img[^>]*?)>\\s*</p>",
                    "<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"margin: 10px 0;\"><tr><td align=\"left\">$5 style=\"display: block;\"></td></tr></table>"
                );
                
                contentHtml = contentHtml.replaceAll(
                    "<p([^>]*?)style=\"([^\"]*?)text-align:\\s*right([^\"]*?)\"([^>]*?)>\\s*(<img[^>]*?)>\\s*</p>",
                    "<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"margin: 10px 0;\"><tr><td align=\"right\">$5 style=\"display: block;\"></td></tr></table>"
                );
                
                // 2. 일반 텍스트 중앙정렬도 테이블로
                contentHtml = contentHtml.replaceAll(
                    "<p([^>]*?)style=\"([^\"]*?)text-align:\\s*center([^\"]*?)\"([^>]*?)>",
                    "<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"margin: 10px 0;\"><tr><td align=\"center\" style=\"$2$3\">"
                );
                
                contentHtml = contentHtml.replaceAll(
                    "<p([^>]*?)style=\"([^\"]*?)text-align:\\s*left([^\"]*?)\"([^>]*?)>",
                    "<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"margin: 10px 0;\"><tr><td align=\"left\" style=\"$2$3\">"
                );
                
                contentHtml = contentHtml.replaceAll(
                    "<p([^>]*?)style=\"([^\"]*?)text-align:\\s*right([^\"]*?)\"([^>]*?)>",
                    "<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"margin: 10px 0;\"><tr><td align=\"right\" style=\"$2$3\">"
                );
                
                // 3. 닫는 p 태그를 td, tr, table로 변경
                contentHtml = contentHtml.replaceAll("</p>", "</td></tr></table>");
              
                // "content"라는 이름으로 contentHtml(메일 본문 HTML)을 템플릿에 넣음.
                context.setVariable("content", contentHtml);
              
               // "mailTemplate": resources/templates 폴더 안의 mailTemplate.html 파일
                String htmlContent = templateEngine.process("mailTemplate", context);
                helper.setText(htmlContent, true);

                // 로고는 Firebase URL로 처리하므로 별도 첨부 불필요

                // 첨부파일 추가
                for (Attachment att : attachments) {
                    try {
                        helper.addAttachment(att.getFileName(), new ByteArrayResource(att.getContent()), att.getContentType());
                    } catch (Exception e) {
                        System.out.println("첨부파일 추가 실패: " + att.getFileName());
                    }
                }

                // 메일 발송
                javaMailSender.send(mimeMessage);
            }

        } catch (Exception e) {
            throw new RuntimeException("메일 발송 실패", e);
        }

        return CompletableFuture.completedFuture(null); // 비동기 메서드는 종료했으며, 반환값이 없다는 의미
    }
}