package com.EduTech.service.mail;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.UUID;
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

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    public MailService(JavaMailSender javaMailSender, TemplateEngine templateEngine) {
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
    }

    // 비동기로 HTML+첨부파일+이미지 메일 발송
    @Async("mailTaskExecutor")
    public CompletableFuture<Void> sendMimeMessage(AdminMessageDTO adminMessageDTO) {
        try {
            List<String> memberList = adminMessageDTO.getMemberList();

            for (String memId : memberList) {
                MimeMessage mimeMessage = javaMailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

                helper.setTo(memId);
                helper.setSubject(adminMessageDTO.getTitle());

                // HTML 본문 + Quill 이미지 cid 처리
                Context context = new Context();
                context.setVariable("memId", memId);

                String contentHtml = adminMessageDTO.getContent().replaceAll("(\r\n|\n|\r)", "<br/>");
                // Quill 이미지 처리
                List<MultipartFile> images = adminMessageDTO.getImageList();
                if (images != null) {
                    for (MultipartFile image : images) {
                        if (!image.isEmpty()) {
                            String cid = UUID.randomUUID().toString();
                            context.setVariable(image.getOriginalFilename(), "cid:" + cid);

                            // HTML에서 <img src="originalFilename">를 cid로 교체
                            contentHtml = contentHtml.replace("src=\"" + image.getOriginalFilename() + "\"",
                                                              "src='cid:" + cid + "'");

                            // addInline
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

                // 일반 첨부파일 처리 (임시 파일로)
                List<MultipartFile> files = adminMessageDTO.getAttachmentFile();
                if (files != null) {
                    for (MultipartFile file : files) {
                        if (!file.isEmpty()) {
                            File temp = File.createTempFile("attach-", "-" + file.getOriginalFilename());
                            try (FileOutputStream fos = new FileOutputStream(temp)) {
                                fos.write(file.getBytes());
                            }
                            helper.addAttachment(file.getOriginalFilename(), new FileSystemResource(temp));
                            temp.deleteOnExit();
                        }
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
