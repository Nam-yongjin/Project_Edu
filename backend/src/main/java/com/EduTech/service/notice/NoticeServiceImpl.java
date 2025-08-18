package com.EduTech.service.notice;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
import com.EduTech.entity.member.MemberRole;
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
    	//단일 삭제에서 권한 충돌이 나서 주석처리 함 @PreAuthorize("hasRole('ADMIN')")도 같이
    	//프론트에서 관리자만 버튼이 보이도록 설정했기 때문에 없어도 괜찮을 것 같다고 판단.... --> 프론트에서 axios로 직접 경로 지정해서 그런 거였음 (JWTaxios사용하기)
        Member currentMember = getCurrentMember();
        validateAdminRole(currentMember);
        
        Notice notice = noticeRepository.findById(noticeNum)
                .orElseThrow(() -> new EntityNotFoundException("공지사항을 찾을 수 없습니다. ID: " + noticeNum));
        
        // 파일 삭제 (orphanRemoval = true로 인해 자동으로 삭제되지만 물리적 파일은 직접 삭제 필요)
        deleteNoticeFiles(noticeNum);
        
        // 연관관계 끊기(orphanRemoval 반영을 위한 작업)
        notice.getNoticeFiles().clear(); // JPA 영속성 컨텍스트에서 관계 해제
        
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
        
        for (Notice notice : notices) {
        	deleteNoticeFiles(notice.getNoticeNum()); //파일 삭제
        	notice.getNoticeFiles().clear(); //관계 해제
        	noticeRepository.delete(notice); //하나씩 삭제 --> orphanRemoval 정상 작동
        }
        
        // 파일들 삭제
        //noticeNums.forEach(this::deleteNoticeFiles);
        
        // 공지사항들 삭제
        // deleteByNoticeNumIn은 JPA의 delete in 쿼리를 사용하는데 이 방식은 엔티티를 영속성 컨텍스트에 올리지 않아서
        // cascade = CascadeType.ALL, orphanRemoval = true가 동작이 안됨 --> NoticeFile이 DB에 남아있어서 외래키 제약조건 오류 발생
        // noticeRepository.deleteByNoticeNumIn(noticeNums);
    }
    
    // 공지사항 상세조회
    @Override
    @Transactional(readOnly = true)
    public NoticeDetailDTO getNoticeDetail(Long noticeNum) {
        Notice notice = noticeRepository.findByIdWithFiles(noticeNum)
                .orElseThrow(() -> new EntityNotFoundException("공지사항을 찾을 수 없습니다. ID: " + noticeNum));
        
        System.out.println("공지사항 첨부파일 수: " + notice.getNoticeFiles().size());
        //DTO 변환 로직
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
    	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    	System.out.println("권한 목록: " + authentication.getAuthorities());
    	System.out.println("인증된 사용자: " + authentication.getName());
    	System.out.println("현재 로그인한 사용자 권한: " + member.getRole());
    	
    	if (!MemberRole.ADMIN.equals(member.getRole())) {
    	    throw new AccessDeniedException("관리자 권한이 필요합니다.");
    	}
    }
    // 유효한 파일이 있는지 체크
    private boolean hasValidFiles(List<MultipartFile> files) {
        return files != null && !files.isEmpty() && 
               files.stream().anyMatch(file -> file != null && !file.isEmpty());
    }
    //파일 저장
    private List<NoticeFile> processFiles(List<MultipartFile> files, Notice notice) {
        String dirName = "notice-files";
        List<Object> fileDataList = fileUtil.saveFiles(files, dirName);
        
        System.out.println("파일 저장 결과: " + fileDataList.size() + "개");
        
        List<NoticeFile> result = fileDataList.stream()
                .map(obj -> (Map<String, String>) obj)
                .peek(fileData -> System.out.println("파일 데이터: " + fileData))
                .map(fileData -> {
                    //originalName에서 추출
                    String originalName = fileData.get("originalName");
                    String fileType = extractFileType(originalName);
                    
                    return NoticeFile.builder()
                            .originalName(originalName)
                            .filePath(fileData.get("filePath"))
                            .fileType(fileType) //추출한 fileType 사용
                            .notice(notice)
                            .build();
                })
                .filter(noticeFile -> isAllowedFileType(noticeFile.getFileType())) //필터링 로직 변경
                .peek(noticeFile -> System.out.println("허용된 파일: " + noticeFile.getOriginalName()))
                .toList();
		return result;
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

        // 파일 개수 세팅
        listDTO.setFileCount(
            notice.getNoticeFiles() != null ? notice.getNoticeFiles().size() : 0
        );

        return listDTO;
    }
    // 파일 DTO 변환
    private NoticeFileDTO mapToFileDTO(NoticeFile noticeFile) {
        NoticeFileDTO fileDTO = new NoticeFileDTO();
        fileDTO.setNotFileNum(noticeFile.getNotFileNum()); //첨부파일 삭제할 때 필요
        fileDTO.setOriginalName(noticeFile.getOriginalName());
        fileDTO.setFilePath(noticeFile.getFilePath());
        fileDTO.setFileType(noticeFile.getFileType());
        //파일 다운로드 오류로 절대 경로로 변경!! - 백엔드 서버 주소 포함하기
        String downloadUrl = "http://localhost:8090/api/notice/files/" + noticeFile.getNotFileNum() + "/download";
        fileDTO.setDownloadUrl(downloadUrl);
        //프론트엔드랑 백엔드가 다른 포드에서 실행되어서 상대 경로가 프론트엔드 서버로 요청이 가고 있었음
        //네트워크에서 실제 요청 url 확인 -> 프론트, 백엔드 포트가 다를 경우 절대경로 사용
        //브라우저에 직접 url입력해서 API 테스트 하기
        
        System.out.println("생성된 downloadUrl: " + downloadUrl);

     // 파일 경로에서 파일명만 추출
        String savedName = extractSavedNameFromPath(noticeFile.getFilePath());
        fileDTO.setSavedName(savedName);
        
        return fileDTO;
    }
    
    
    //파일 경로에서 저장된 파일명 추출
    private String extractSavedNameFromPath(String filePath) {
        if (filePath == null) return null;
        //"notice-files/UUID_filename.ext" -> "UUID_filename.ext" 추출
        int lastSlashIndex = filePath.lastIndexOf("/");
        if (lastSlashIndex != -1) {
            return filePath.substring(lastSlashIndex + 1);
        }
        return filePath;
    }
    
    //파일 확장자 추출 메서드 추가
    private String extractFileType(String originalName) {
        if (originalName == null || !originalName.contains(".")) {
            return "";
        }
        return originalName.substring(originalName.lastIndexOf(".") + 1).toLowerCase();
    }

    //필터링 메서드 수정
    private boolean isAllowedFileType(String fileType) {
        return fileType != null && ALLOWED_FILE_TYPES.contains(fileType.toLowerCase());
    }
}