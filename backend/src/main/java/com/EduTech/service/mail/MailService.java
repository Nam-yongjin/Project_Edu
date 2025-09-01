package com.EduTech.service.mail;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
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

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;
    
    @Autowired
    private java.util.concurrent.Executor mailTaskExecutor;

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

    // HTML 컨텐츠 처리 메서드 (정렬 및 테이블 변환)
    private String processHtmlContent(String content) {
        String contentHtml = content.replaceAll("(\r\n|\n|\r)", "<br/>");
        
        // 이미지 정렬 처리 (고정된 정규식 사용)
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
        
        // 텍스트 정렬 처리
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
        
        contentHtml = contentHtml.replaceAll("</p>", "</td></tr></table>");
        return contentHtml;
    }

    // 병렬로 HTML+첨부파일 메일 발송 (통합 처리)
    @Async("mailTaskExecutor")
    public CompletableFuture<Void> sendMimeMessage(AdminMessageDTO adminMessageDTO) {
        try {
            List<String> memberList = adminMessageDTO.getMemberList();

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

            // 병렬로 각 수신자에게 메일 발송
            List<CompletableFuture<Void>> futures = memberList.stream()
                .map(email -> CompletableFuture.runAsync(() -> {
                    try {
                        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
                        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

                        helper.setTo(email);
                        helper.setSubject(adminMessageDTO.getTitle());

                        // HTML 본문 처리
                        Context context = new Context();
                        context.setVariable("email", email);
                        
                        String contentHtml = processHtmlContent(adminMessageDTO.getContent());
                        context.setVariable("content", contentHtml);
                        
                        String htmlContent = templateEngine.process("mailTemplate", context);
                        helper.setText(htmlContent, true);

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
                        
                    } catch (Exception e) {
                        System.out.println("개별 이메일 발송 실패: " + email + " - " + e.getMessage());
                    }
                }, mailTaskExecutor))
                .collect(Collectors.toList());

            // 모든 이메일 발송 완료 대기
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
            
            System.out.println("전체 이메일 발송 완료: " + memberList.size() + "명");

        } catch (Exception e) {
            throw new RuntimeException("메일 발송 실패", e);
        }

        return CompletableFuture.completedFuture(null);
    }
}