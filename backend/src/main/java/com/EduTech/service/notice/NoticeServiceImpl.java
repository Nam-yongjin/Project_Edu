package com.EduTech.service.notice;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.EduTech.dto.notice.NoticeCreateRegisterDTO;
import com.EduTech.dto.notice.NoticeDetailDTO;
import com.EduTech.dto.notice.NoticeFileDTO;
import com.EduTech.dto.notice.NoticeListDTO;
import com.EduTech.dto.notice.NoticeSearchDTO;
import com.EduTech.dto.notice.NoticeUpdateRegisterDTO;
import com.EduTech.entity.member.Member;
import com.EduTech.entity.notice.Notice;
import com.EduTech.entity.notice.NoticeFile;
import com.EduTech.repository.member.MemberRepository;
import com.EduTech.repository.notice.NoticeFileRepository;
import com.EduTech.repository.notice.NoticeRepository;
import com.EduTech.repository.notice.NoticeSpecifications;
import com.EduTech.security.jwt.JWTFilter;
import com.EduTech.util.FileUtil;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class NoticeServiceImpl implements NoticeService {
    
    private final NoticeRepository noticeRepository;
    private final NoticeFileRepository noticeFileRepository;
    private final MemberRepository memberRepository;
    private final FileUtil fileUtil;
    
    private static final List<String> ALLOWED_FILE_TYPES = //파일 유형
            List.of("jpg", "jpeg", "png", "pdf", "hwp", "doc", "docx");
    
    @Override
    public Long createNotice(NoticeCreateRegisterDTO registerDto) {
        // 권한 검증(현재 로그인한 사용자)
        Member currentMember = getCurrentMember();
        validateAdminRole(currentMember);
        
        // Notice 엔티티 생성
        Notice notice = Notice.builder()
                .title(registerDto.getTitle())
                .content(registerDto.getContent())
                .isPinned(registerDto.isPinned())
                .member(currentMember)
                .build();
        
        // 파일 처리
        if (hasValidFiles(registerDto.getFiles())) {
            List<NoticeFile> noticeFiles = processFiles(registerDto.getFiles(), notice);
            noticeFiles.forEach(notice::addNoticeFile);
        }
        
        Notice savedNotice = noticeRepository.save(notice);
        return savedNotice.getNoticeNum();
    }
    
    @Override
    public void updateNotice(Long noticeNum, NoticeUpdateRegisterDTO registerDto) {
        // 권한 검증
        Member currentMember = getCurrentMember();
        validateAdminRole(currentMember);
        
        // 기존 공지사항 조회
        Notice notice = noticeRepository.findById(noticeNum)
                .orElseThrow(() -> new EntityNotFoundException("공지사항을 찾을 수 없습니다. ID: " + noticeNum));
        
        // 내용 업데이트
        notice.updateContent(registerDto.getTitle(), registerDto.getContent(), registerDto.isPinned());
        
        // 파일 처리
        handleFileUpdates(notice, registerDto);
        
        noticeRepository.save(notice);
    }
    
    @Override
    public void deleteNotice(Long noticeNum) {
        Member currentMember = getCurrentMember();
        validateAdminRole(currentMember);
        
        Notice notice = noticeRepository.findById(noticeNum)
                .orElseThrow(() -> new EntityNotFoundException("공지사항을 찾을 수 없습니다. ID: " + noticeNum));
        
        // 파일 삭제 (orphanRemoval = true로 인해 자동으로 삭제되지만, 물리적 파일은 직접 삭제 필요)
        deleteNoticeFiles(notice.getNoticeNum());
        
        // 공지사항 삭제
        noticeRepository.delete(notice);
    }
    
    @Override
    public void deleteNotices(List<Long> noticeNums) {
        Member currentMember = getCurrentMember();
        validateAdminRole(currentMember);
        
        // 존재하는 공지사항들 조회
        List<Notice> notices = noticeRepository.findAllById(noticeNums);
        
        if (notices.size() != noticeNums.size()) {
            List<Long> foundIds = notices.stream()
                    .map(Notice::getNoticeNum)
                    .toList();
            List<Long> notFoundIds = noticeNums.stream()
                    .filter(id -> !foundIds.contains(id))
                    .toList();
            throw new EntityNotFoundException("존재하지 않는 공지사항: " + notFoundIds);
        }
        
        // 파일들 삭제
        noticeNums.forEach(this::deleteNoticeFiles);
        
        // 공지사항들 삭제
        noticeRepository.deleteAll(notices);
    }
    
    @Override
    @Transactional(readOnly = true)
    public NoticeDetailDTO getNoticeDetail(Long noticeNum) {
        Notice notice = noticeRepository.findById(noticeNum)
                .orElseThrow(() -> new EntityNotFoundException("공지사항을 찾을 수 없습니다. ID: " + noticeNum));
        
        return mapToDetailDTO(notice);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<NoticeListDTO> getNoticeList(NoticeSearchDTO searchDto) {
        // 정렬 설정
        Sort sort = Sort.by(
                Sort.Direction.fromString(searchDto.getSortDirection()),
                searchDto.getSortBy()
        );
        
        Pageable pageable = PageRequest.of(searchDto.getPage(), searchDto.getSize(), sort);
        
        // 검색 조건 생성
        Specification<Notice> spec = NoticeSpecifications.createSpecification(searchDto);
        
        // 조회 및 변환
        Page<Notice> notices = noticeRepository.findAll(spec, pageable);
        
        return notices.map(this::mapToListDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<NoticeListDTO> getPinnedNotices() {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        List<Notice> pinnedNotices = noticeRepository.findAllByIsPinned(true, sort);
        
        return pinnedNotices.stream()
                .map(this::mapToListDTO)
                .toList();
    }
    
    @Override
    public void increaseViewCount(Long noticeNum) {
        Notice notice = noticeRepository.findById(noticeNum)
                .orElseThrow(() -> new EntityNotFoundException("공지사항을 찾을 수 없습니다. ID: " + noticeNum));
        
        notice.increaseViewCount();
        noticeRepository.save(notice);
    }
    
// 헬퍼메소드
    
    private Member getCurrentMember() {
        return memberRepository.findById(JWTFilter.getMemId())
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다: " + JWTFilter.getMemId()));
    }
    
    private void validateAdminRole(Member member) {
        if (!"ADMIN".equals(member.getRole())) {
            throw new AccessDeniedException("관리자 권한이 필요합니다.");
        }
    }
    
    private boolean hasValidFiles(List<MultipartFile> files) {
        return files != null && !files.isEmpty() && 
               files.stream().anyMatch(file -> !file.isEmpty());
    }
    
    private List<NoticeFile> processFiles(List<MultipartFile> files, Notice notice) {
        String dirName = "notice-files";
        List<Object> fileDataList = fileUtil.saveFiles(files, dirName);
        
        return fileDataList.stream()
                .map(obj -> (Map<String, String>) obj)
                .filter(this::isAllowedFileType)
                .map(fileData -> NoticeFile.builder()
                        .originalName(fileData.get("originalName"))
                        .filePath(fileData.get("filePath"))
                        .fileType(fileData.get("fileType").toLowerCase())
                        .notice(notice)
                        .build())
                .toList();
    }
    
    private boolean isAllowedFileType(Map<String, String> fileData) {
        String fileType = fileData.get("fileType");
        return fileType != null && ALLOWED_FILE_TYPES.contains(fileType.toLowerCase());
    }
    
    private void handleFileUpdates(Notice notice, NoticeUpdateRegisterDTO requestDto) {
        // 기존 파일 삭제 (요청에 따라)
        if (requestDto.getDeleteFileIds() != null && !requestDto.getDeleteFileIds().isEmpty()) {
            deleteSpecificFiles(requestDto.getDeleteFileIds());
        }
        
        // 새 파일 추가
        if (hasValidFiles(requestDto.getNewFiles())) {
            List<NoticeFile> newFiles = processFiles(requestDto.getNewFiles(), notice);
            newFiles.forEach(notice::addNoticeFile);
        }
    }
    
    private void deleteNoticeFiles(Long noticeNum) {
        List<NoticeFile> files = noticeFileRepository.findByNotice_NoticeNum(noticeNum);
        
        if (!files.isEmpty()) {
            // 물리적 파일 삭제
            List<String> filePaths = files.stream()
                    .map(NoticeFile::getFilePath)
                    .toList();
            
            fileUtil.deleteFiles(filePaths);
            
            // DB에서 파일 정보 삭제 (orphanRemoval = true로 인해 자동 삭제되지만 명시적으로 처리)
            noticeFileRepository.deleteAll(files);
        }
    }
    
    private void deleteSpecificFiles(List<String> fileIds) {
        List<Long> fileIdLongs = fileIds.stream()
                .map(Long::valueOf)
                .toList();
        
        List<NoticeFile> filesToDelete = noticeFileRepository.findAllById(fileIdLongs);
        
        if (!filesToDelete.isEmpty()) {
            // 물리적 파일 삭제
            List<String> filePaths = filesToDelete.stream()
                    .map(NoticeFile::getFilePath)
                    .toList();
            
            fileUtil.deleteFiles(filePaths);
            
            // DB에서 파일 정보 삭제
            noticeFileRepository.deleteAll(filesToDelete);
        }
    }
    
    // DTO 매핑 메서드들
    private NoticeDetailDTO mapToDetailDTO(Notice notice) {
        List<NoticeFileDTO> files = notice.getNoticeFiles().stream()
                .map(this::mapToFileDTO)
                .toList();
        
        NoticeDetailDTO detailDTO = new NoticeDetailDTO();
        detailDTO.setNoticeNum(notice.getNoticeNum());
        detailDTO.setTitle(notice.getTitle());
        detailDTO.setContent(notice.getContent());
        detailDTO.setPinned(notice.isPinned());
        detailDTO.setViewCount(notice.getViewCount());
        detailDTO.setName(notice.getMember().getName());
        detailDTO.setMem_id(notice.getMember().getMemId());
        detailDTO.setCreatedAt(notice.getCreatedAt());
        detailDTO.setUpdatedAt(notice.getUpdatedAt());
        detailDTO.setFiles(files);
        
        return detailDTO;
    }
    
    private NoticeListDTO mapToListDTO(Notice notice) {
        NoticeListDTO listDTO = new NoticeListDTO();
        listDTO.setNoticeNum(notice.getNoticeNum());
        listDTO.setTitle(notice.getTitle());
        listDTO.setPinned(notice.isPinned());
        listDTO.setName(notice.getMember().getName());
        listDTO.setViewCount(notice.getViewCount());
        listDTO.setCreatedAt(notice.getCreatedAt());
        listDTO.setFileCount(notice.getNoticeFiles().size());
        
        return listDTO;
    }
    
    private NoticeFileDTO mapToFileDTO(NoticeFile noticeFile) {
        NoticeFileDTO fileDTO = new NoticeFileDTO();
        fileDTO.setOriginalName(noticeFile.getOriginalName());
        fileDTO.setFilePath(noticeFile.getFilePath());
        fileDTO.setFileType(noticeFile.getFileType());
        fileDTO.setDownloadUrl("/api/notices/files/" + noticeFile.getNotFileNum() + "/download");
        
        return fileDTO;
    }
}