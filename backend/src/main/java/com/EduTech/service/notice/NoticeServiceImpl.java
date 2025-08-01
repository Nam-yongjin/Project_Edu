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
    // 의존성 주입
    private final NoticeRepository noticeRepository;
    private final NoticeFileRepository noticeFileRepository;
    private final MemberRepository memberRepository;
    private final FileUtil fileUtil;
    // 허용된 파일 유형
    private static final List<String> ALLOWED_FILE_TYPES =
            List.of("jpg", "jpeg", "png", "pdf", "hwp", "doc", "docx");
    // 공지사항 생성
    @Override
    public Long createNotice(NoticeCreateRegisterDTO registerDto) {
        // 권한 검증(현재 로그인한 사용자 -> 관리자 권한 체크)
        Member currentMember = getCurrentMember();
        validateAdminRole(currentMember);
        
        // Notice 엔티티 생성(dto를 entity로 변환)
        Notice notice = Notice.builder()
                .title(registerDto.getTitle())
                .content(registerDto.getContent())
                .isPinned(registerDto.getIsPinned())
                .member(currentMember)
                .build();
        
        // 첨부파일 처리
        if (hasValidFiles(registerDto.getFiles())) {
            List<NoticeFile> noticeFiles = processFiles(registerDto.getFiles(), notice);
            noticeFiles.forEach(notice::addNoticeFile); // 양방향 연결
        }
        
        Notice savedNotice = noticeRepository.save(notice);
        return savedNotice.getNoticeNum();
    }
    // 공지사항 수정
    @Override
    public void updateNotice(Long noticeNum, NoticeUpdateRegisterDTO registerDto) {
        // 권한 검증
        Member currentMember = getCurrentMember();
        validateAdminRole(currentMember);
        
        // 기존 공지사항 조회
        Notice notice = noticeRepository.findById(noticeNum)
                .orElseThrow(() -> new EntityNotFoundException("공지사항을 찾을 수 없습니다. ID: " + noticeNum));
        
        // 내용 업데이트(제목, 내용, 고정 여부)
        notice.updateContent(registerDto.getTitle(), registerDto.getContent(), registerDto.getIsPinned());
        
        // 파일 처리
        handleFileUpdates(notice, registerDto);
        
        noticeRepository.save(notice);
    }
    // 공지사항 삭제(단일)
    @Override
    @Transactional
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
    // 공지사항 삭제(일괄)
    @Override
    @Transactional
    public void deleteNotices(List<Long> noticeNums) {
    	//권한 검증
        Member currentMember = getCurrentMember();
        validateAdminRole(currentMember);
        
        // 존재하는 공지사항들 조회
        List<Notice> notices = noticeRepository.findAllById(noticeNums);
        // 누락된 id가 있는지 검증
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
        noticeRepository.deleteByNoticeNumIn(noticeNums);
    }
    // 공지사항 상세조회
    @Override
    @Transactional(readOnly = true)
    public NoticeDetailDTO getNoticeDetail(Long noticeNum) {
        Notice notice = noticeRepository.findById(noticeNum)
                .orElseThrow(() -> new EntityNotFoundException("공지사항을 찾을 수 없습니다. ID: " + noticeNum));
        
        return mapToDetailDTO(notice);
    }
    // 공지사항 목록조회
    @Override
    @Transactional(readOnly = true)
    public Page<NoticeListDTO> getNoticeList(NoticeSearchDTO searchDto) {
    	// 검색 조건 생성
    	Specification<Notice> spec = NoticeSpecifications.createSpecification(searchDto);
    	
        // 정렬 설정
        Sort sort = Sort.by(
                Sort.Order.desc("isPinned"), //isPinned가 true인 공지가 먼저
                Sort.Order.desc("createdAt") //isPinned가 같을 경우 최신순으로 정렬
        );
        
        // Pageable 객체 생성 (정렬 조건 포함)
        Pageable pageable = PageRequest.of(searchDto.getPage(), searchDto.getSize(), sort);
                
        // 조회 및 변환
        Page<Notice> notices = noticeRepository.findAll(spec, pageable);
        
        return notices.map(this::mapToListDTO);
    }
    //고정 공지사항 목록 조회(통합된 NoticeList가 있지만 고정된 공지만 따로 볼 수 있도록 남겨둠...!)
    @Override
    @Transactional(readOnly = true)
    public List<NoticeListDTO> getPinnedNotices() {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        List<Notice> pinnedNotices = noticeRepository.findAllByIsPinned(true, sort);

        return pinnedNotices.stream()
                .map(this::mapToListDTO)
                .toList();
    }
    // 조회수 증가
    @Override
    public void increaseViewCount(Long noticeNum) {
        Notice notice = noticeRepository.findById(noticeNum)
                .orElseThrow(() -> new EntityNotFoundException("공지사항을 찾을 수 없습니다. ID: " + noticeNum));
        
        notice.increaseViewCount();
        noticeRepository.save(notice);
    }
    
    // 헬퍼메소드
    
    // 현재 로그인한 회원 정보 조회
    private Member getCurrentMember() {
        return memberRepository.findById(JWTFilter.getMemId())
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다: " + JWTFilter.getMemId()));
    }
    // 관리자 권한 체크
    private void validateAdminRole(Member member) {
        if (!"ADMIN".equals(member.getRole())) {
            throw new AccessDeniedException("관리자 권한이 필요합니다.");
        }
    }
    // 유효한 파일이 있는지 체크
    private boolean hasValidFiles(List<MultipartFile> files) {
        return files != null && !files.isEmpty() && 
               files.stream().anyMatch(file -> !file.isEmpty());
    }
    //파일 저장
    private List<NoticeFile> processFiles(List<MultipartFile> files, Notice notice) {
        String dirName = "notice-files";
        List<Object> fileDataList = fileUtil.saveFiles(files, dirName);
        //noticeFile 리스트 생성
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
    // 허용된 파일 유형인지 체크
    private boolean isAllowedFileType(Map<String, String> fileData) {
        String fileType = fileData.get("fileType");
        return fileType != null && ALLOWED_FILE_TYPES.contains(fileType.toLowerCase());
    }
    // 수정 시 파일 처리
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
    // 공지사항에 속한 모든 파일 삭제
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
    // 특정 파일 삭제
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
    
    // 상세보기용 DTO 변환
    private NoticeDetailDTO mapToDetailDTO(Notice notice) {
        List<NoticeFileDTO> files = notice.getNoticeFiles().stream()
                .map(this::mapToFileDTO)
                .toList();
        
        NoticeDetailDTO detailDTO = new NoticeDetailDTO();
        detailDTO.setNoticeNum(notice.getNoticeNum());
        detailDTO.setTitle(notice.getTitle());
        detailDTO.setContent(notice.getContent());
        detailDTO.setIsPinned(notice.isPinned());
        detailDTO.setViewCount(notice.getViewCount());
        detailDTO.setName(notice.getMember().getName());
        detailDTO.setMem_id(notice.getMember().getMemId());
        detailDTO.setCreatedAt(notice.getCreatedAt());
        detailDTO.setUpdatedAt(notice.getUpdatedAt());
        detailDTO.setFiles(files);
        
        return detailDTO;
    }
    // 목록용 DTO 변환
    private NoticeListDTO mapToListDTO(Notice notice) {
        NoticeListDTO listDTO = new NoticeListDTO();
        listDTO.setNoticeNum(notice.getNoticeNum());
        listDTO.setTitle(notice.getTitle());
        listDTO.setIsPinned(notice.isPinned());
        listDTO.setName(notice.getMember().getName());
        listDTO.setViewCount(notice.getViewCount());
        listDTO.setCreatedAt(notice.getCreatedAt());
        listDTO.setFileCount(notice.getNoticeFiles().size());
        
        return listDTO;
    }
    // 파일 DTO 변환
    private NoticeFileDTO mapToFileDTO(NoticeFile noticeFile) {
        NoticeFileDTO fileDTO = new NoticeFileDTO();
        fileDTO.setOriginalName(noticeFile.getOriginalName());
        fileDTO.setFilePath(noticeFile.getFilePath());
        fileDTO.setFileType(noticeFile.getFileType());
        fileDTO.setDownloadUrl("/api/notices/files/" + noticeFile.getNotFileNum() + "/download");
        
        return fileDTO;
    }
}