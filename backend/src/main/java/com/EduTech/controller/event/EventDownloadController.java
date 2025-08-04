package com.EduTech.controller.event;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.EduTech.entity.event.EventFile;
import com.EduTech.entity.event.EventImage;
import com.EduTech.entity.event.EventInfo;
import com.EduTech.repository.event.EventFileRepository;
import com.EduTech.repository.event.EventImageRepository;
import com.EduTech.service.event.EventService;
import com.EduTech.util.FileUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/event/download")
@RequiredArgsConstructor
public class EventDownloadController {

    private final EventService eventService;
    private final EventFileRepository eventFileRepository;
    private final EventImageRepository eventImageRepository;
    private final FileUtil fileUtil;

    // 대표 첨부파일 다운로드
    @GetMapping("/main-file/{eventNum}")
    public ResponseEntity<Resource> downloadMainFile(@PathVariable("eventNum") Long eventNum) {
        EventInfo event = eventService.getEventEntity(eventNum);
        return fileUtil.getDownloadableFile(event.getFilePath(), event.getOriginalName());
    }

    // 대표 이미지 다운로드
    @GetMapping("/main-image/{eventNum}")
    public ResponseEntity<Resource> downloadMainImage(@PathVariable("eventNum") Long eventNum) {
        EventInfo event = eventService.getEventEntity(eventNum);
        return fileUtil.getDownloadableFile(event.getMainImagePath(), event.getMainImageOriginalName());
    }

    // 일반 첨부파일 다운로드
    @GetMapping("/file/{fileId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable("fileId") Long fileId) {
        EventFile file = eventFileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("첨부파일이 존재하지 않습니다."));
        return fileUtil.getDownloadableFile(file.getFilePath(), file.getOriginalName());
    }

    // 일반 이미지 다운로드
    @GetMapping("/image/{imageId}")
    public ResponseEntity<Resource> downloadImage(@PathVariable("imageId") Long imageId) {
        EventImage image = eventImageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("이미지 파일이 존재하지 않습니다."));
        return fileUtil.getDownloadableFile(image.getFilePath(), image.getOriginalName());
    }
}
