package com.EduTech.service.mail;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.ArrayList;

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
import java.io.File;

@Service
public class MailService {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

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

    // 비동기로 HTML+첨부파일+이미지 메일 발송
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
            for (String memId : memberList) {
                MimeMessage mimeMessage = javaMailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

                helper.setTo(memId);
                helper.setSubject(adminMessageDTO.getTitle());

                // HTML 본문 + Quill 이미지 CID 처리
                Context context = new Context();
                context.setVariable("memId", memId);

                String contentHtml = adminMessageDTO.getContent().replaceAll("(\r\n|\n|\r)", "<br/>");

                // Quill 이미지 처리
                List<MultipartFile> images = adminMessageDTO.getImageList();
                if (images != null) {
                    for (MultipartFile image : images) {
                        if (!image.isEmpty()) {
                            String cid = UUID.randomUUID().toString();

                            // 본문에 모든 src 치환
                            contentHtml = contentHtml.replaceAll(
                                "src=[\"']" + image.getOriginalFilename() + "[\"']",
                                "src='cid:" + cid + "'"
                            );

                            // 메일에 inline 추가
                            helper.addInline(cid, new ByteArrayResource(image.getBytes()), image.getContentType());
                        }
                    }
                }

                context.setVariable("content", contentHtml);
                String htmlContent = templateEngine.process("mailTemplate", context);
                helper.setText(htmlContent, true);

                // 로고 첨부 (선택 사항)
                File logoFile = new File("C:/Users/tee16/git/Project_Edu/backend/src/main/resources/static/images/logo.png");
                if (logoFile.exists()) {
                    FileSystemResource res = new FileSystemResource(logoFile);
                    helper.addInline("logo", res);
                }

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

        return CompletableFuture.completedFuture(null);
    }
}
