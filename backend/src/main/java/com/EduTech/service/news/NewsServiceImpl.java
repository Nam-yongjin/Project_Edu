package com.EduTech.service.news;

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

import com.EduTech.dto.news.NewsCreateRegisterDTO;
import com.EduTech.dto.news.NewsDetailDTO;
import com.EduTech.dto.news.NewsFileDTO;
import com.EduTech.dto.news.NewsListDTO;
import com.EduTech.dto.news.NewsSearchDTO;
import com.EduTech.dto.news.NewsUpdateRegisterDTO;
import com.EduTech.entity.member.Member;
import com.EduTech.entity.member.MemberRole;
import com.EduTech.entity.news.News;
import com.EduTech.entity.news.NewsFile;
import com.EduTech.repository.member.MemberRepository;
import com.EduTech.repository.news.NewsFileRepository;
import com.EduTech.repository.news.NewsRepository;
import com.EduTech.repository.news.NewsSpecifications;
import com.EduTech.security.jwt.JWTFilter;
import com.EduTech.util.FileUtil;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class NewsServiceImpl implements NewsService {
    
    private final NewsRepository newsRepository;
    private final NewsFileRepository newsFileRepository;
    private final MemberRepository memberRepository;
    private final FileUtil fileUtil;
    
    private static final List<String> ALLOWED_FILE_TYPES = //파일 유형
            List.of("jpg", "jpeg", "png", "pdf", "hwp", "doc", "docx");
    
    //뉴스 등록
    @Override
    public Long createNews(NewsCreateRegisterDTO registerDto) {
        // 권한 검증(현재 로그인한 사용자)
        Member currentMember = getCurrentMember();
        validateAdminRole(currentMember);
        
        // News 엔티티 생성
        News news = News.builder()
                .title(registerDto.getTitle())
                .content(registerDto.getContent())
                .member(currentMember)
                .build();
        
        // 파일 처리
        if (hasValidFiles(registerDto.getFiles())) {
            List<NewsFile> newsFiles = processFiles(registerDto.getFiles(), news);
            newsFiles.forEach(news::addNewsFile);
        }
        
        News savedNews = newsRepository.save(news);
        return savedNews.getNewsNum();
    }
    //뉴스 수정
    @Override
    public void updateNews(Long newsNum, NewsUpdateRegisterDTO registerDto) {
        // 권한 검증
        Member currentMember = getCurrentMember();
        validateAdminRole(currentMember);
        
        // 기존 뉴스 조회
        News news = newsRepository.findById(newsNum)
                .orElseThrow(() -> new EntityNotFoundException("뉴스를 찾을 수 없습니다. ID: " + newsNum));
        
        // 내용 업데이트
        news.updateContent(registerDto.getTitle(), registerDto.getContent());
        
        // 파일 처리
        handleFileUpdates(news, registerDto);
        
        newsRepository.save(news);
    }
    //뉴스 삭제(단일)
    @Override
    @Transactional
    public void deleteNews(Long newsNum) {
        Member currentMember = getCurrentMember();
        validateAdminRole(currentMember);
        
        News news = newsRepository.findById(newsNum)
                .orElseThrow(() -> new EntityNotFoundException("뉴스를 찾을 수 없습니다. ID: " + newsNum));
        
        // 파일 삭제 (orphanRemoval = true로 인해 자동으로 삭제되지만, 물리적 파일은 직접 삭제 필요)
        deleteNewsFiles(news.getNewsNum());
        
        // 연관관계 끊기(orphanRemoval 반영을 위한 작업)
        news.getNewsFiles().clear(); // JPA 영속성 컨텍스트에서 관계 해제
        
        // 뉴스 삭제
        newsRepository.delete(news);
    }
    //뉴스 삭제(일괄)
    @Override
    @Transactional
    public void deleteNewsByIds(List<Long> newsNums) {
        Member currentMember = getCurrentMember();
        validateAdminRole(currentMember);
        
        // 존재하는 뉴스 조회
        List<News> newsByIds = newsRepository.findAllById(newsNums);
        
        if (newsByIds.size() != newsNums.size()) {
            List<Long> foundIds = newsByIds.stream()
                    .map(News::getNewsNum)
                    .toList();
            List<Long> notFoundIds = newsNums.stream()
                    .filter(id -> !foundIds.contains(id))
                    .toList();
            throw new EntityNotFoundException("존재하지 않는 뉴스: " + notFoundIds);
        }
        
        for (News news : newsByIds) {
        	deleteNewsFiles(news.getNewsNum()); //파일 삭제
        	news.getNewsFiles().clear(); //관계 해제
        	newsRepository.delete(news); //하나씩 삭제 --> orphanRemoval 정상 작동
        }
       
    }
    //뉴스 상세조회
    @Override
    @Transactional(readOnly = true)
    public NewsDetailDTO getNewsDetail(Long newsNum) {
        News news = newsRepository.findByIdWithFiles(newsNum)
                .orElseThrow(() -> new EntityNotFoundException("뉴스를 찾을 수 없습니다. ID: " + newsNum));
        
        System.out.println("뉴스 첨부파일 수: " + news.getNewsFiles().size());
        
        return mapToDetailDTO(news);
    }
    //뉴스 목록 조회
    @Override
    @Transactional(readOnly = true)
    public Page<NewsListDTO> getNewsList(NewsSearchDTO searchDto) {
        // 정렬 설정
//        Sort sort = Sort.by(
//                Sort.Direction.fromString(searchDto.getSortDirection()),
//                searchDto.getSortBy()
//        );
    	// 정렬 설정
        Sort sort = Sort.by(
                Sort.Order.desc("createdAt") //최신순으로 정렬
        );
        
        Pageable pageable = PageRequest.of(searchDto.getPage(), searchDto.getSize(), sort);
        
        // 검색 조건 생성
        Specification<News> spec = NewsSpecifications.createSpecification(searchDto);
        
        // 조회 및 변환
        Page<News> newsByIds = newsRepository.findAll(spec, pageable);
        
        return newsByIds.map(this::mapToListDTO);
    }
    //뉴스 조회수 증가
    @Override
    public void increaseViewCount(Long newsNum) {
        News news = newsRepository.findById(newsNum)
                .orElseThrow(() -> new EntityNotFoundException("뉴스를 찾을 수 없습니다. ID: " + newsNum));
        
        news.increaseViewCount();
        newsRepository.save(news);
    }
    
// 헬퍼메소드
    
    private Member getCurrentMember() {
        return memberRepository.findById(JWTFilter.getMemId())
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다: " + JWTFilter.getMemId()));
    }
    
    private void validateAdminRole(Member member) {
    	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    	System.out.println("권한 목록: " + authentication.getAuthorities());
    	System.out.println("인증된 사용자: " + authentication.getName());
    	System.out.println("현재 로그인한 사용자 권한: " + member.getRole());
    	
    	if (!MemberRole.ADMIN.equals(member.getRole())) {
    	    throw new AccessDeniedException("관리자 권한이 필요합니다.");
    	}
    }
    
    private boolean hasValidFiles(List<MultipartFile> files) {
        return files != null && !files.isEmpty() && 
               files.stream().anyMatch(file -> !file.isEmpty());
    }
    
    private List<NewsFile> processFiles(List<MultipartFile> files, News news) {
        String dirName = "news-files";
        List<Object> fileDataList = fileUtil.saveFiles(files, dirName);
        
        System.out.println("파일 저장 결과: " + fileDataList.size() + "개");
        
        List<NewsFile> result = fileDataList.stream()
                .map(obj -> (Map<String, String>) obj)
                .peek(fileData -> System.out.println("파일 데이터: " + fileData))
                .map(fileData -> {
                    //originalName에서 추출
                    String originalName = fileData.get("originalName");
                    String fileType = extractFileType(originalName);
                    
                    return NewsFile.builder()
                            .originalName(originalName)
                            .filePath(fileData.get("filePath"))
                            .fileType(fileType) //추출한 fileType 사용
                            .news(news)
                            .build();
                })
                .filter(newsFile -> isAllowedFileType(newsFile.getFileType())) //필터링 로직 변경
                .peek(newsFile -> System.out.println("허용된 파일: " + newsFile.getOriginalName()))
                .toList();
		return result;
    }
    
    private boolean isAllowedFileType(Map<String, String> fileData) {
        String fileType = fileData.get("fileType");
        return fileType != null && ALLOWED_FILE_TYPES.contains(fileType.toLowerCase());
    }
    
    private void handleFileUpdates(News news, NewsUpdateRegisterDTO requestDto) {
        // 기존 파일 삭제 (요청에 따라)
        if (requestDto.getDeleteFileIds() != null && !requestDto.getDeleteFileIds().isEmpty()) {
            deleteSpecificFiles(requestDto.getDeleteFileIds());
        }
        
        // 새 파일 추가
        if (hasValidFiles(requestDto.getNewFiles())) {
            List<NewsFile> newFiles = processFiles(requestDto.getNewFiles(), news);
            newFiles.forEach(news::addNewsFile);
        }
    }
    
    private void deleteNewsFiles(Long newsNum) {
        List<NewsFile> files = newsFileRepository.findByNews_NewsNum(newsNum);
        
        if (!files.isEmpty()) {
            // 물리적 파일 삭제
            List<String> filePaths = files.stream()
                    .map(NewsFile::getFilePath)
                    .toList();
            
            fileUtil.deleteFiles(filePaths);
            
            // DB에서 파일 정보 삭제 (orphanRemoval = true로 인해 자동 삭제되지만 명시적으로 처리)
            newsFileRepository.deleteAll(files);
        }
    }
    
    private void deleteSpecificFiles(List<String> fileIds) {
        List<Long> fileIdLongs = fileIds.stream()
                .map(Long::valueOf)
                .toList();
        
        List<NewsFile> filesToDelete = newsFileRepository.findAllById(fileIdLongs);
        
        if (!filesToDelete.isEmpty()) {
            // 물리적 파일 삭제
            List<String> filePaths = filesToDelete.stream()
                    .map(NewsFile::getFilePath)
                    .toList();
            
            fileUtil.deleteFiles(filePaths);
            
            // DB에서 파일 정보 삭제
            newsFileRepository.deleteAll(filesToDelete);
        }
    }
    
    // DTO 매핑 메서드들
    private NewsDetailDTO mapToDetailDTO(News news) {
        List<NewsFileDTO> files = news.getNewsFiles().stream()
                .map(this::mapToFileDTO)
                .toList();
        
        NewsDetailDTO detailDTO = new NewsDetailDTO();
        detailDTO.setNewsNum(news.getNewsNum());
        detailDTO.setTitle(news.getTitle());
        detailDTO.setContent(news.getContent());
        detailDTO.setViewCount(news.getViewCount());
        detailDTO.setName(news.getMember().getName());
        detailDTO.setMem_id(news.getMember().getMemId());
        detailDTO.setCreatedAt(news.getCreatedAt());
        detailDTO.setUpdatedAt(news.getUpdatedAt());
        detailDTO.setFiles(files);
        
        return detailDTO;
    }
    
    private NewsListDTO mapToListDTO(News news) {
        NewsListDTO listDTO = new NewsListDTO();
        listDTO.setNewsNum(news.getNewsNum());
        listDTO.setTitle(news.getTitle());
        listDTO.setName(news.getMember().getName());
        listDTO.setViewCount(news.getViewCount());
        listDTO.setCreatedAt(news.getCreatedAt());
        
        return listDTO;
    }
    
    private NewsFileDTO mapToFileDTO(NewsFile newsFile) {
        NewsFileDTO fileDTO = new NewsFileDTO();
        fileDTO.setNewsFileNum(newsFile.getNewsFileNum()); //첨부파일 삭제할 때 필요
        fileDTO.setOriginalName(newsFile.getOriginalName());
        fileDTO.setFilePath(newsFile.getFilePath());
        fileDTO.setFileType(newsFile.getFileType());
        //파일 다운로드 오류로 절대 경로로 변경!! - 백엔드 서버 주소 포함하기
        String downloadUrl = "http://localhost:8090/api/news/files/" + newsFile.getNewsFileNum() + "/download";
        fileDTO.setDownloadUrl(downloadUrl);
        //프론트엔드랑 백엔드가 다른 포드에서 실행되어서 상대 경로가 프론트엔드 서버로 요청이 가고 있었음
        //네트워크에서 실제 요청 url 확인 -> 프론트, 백엔드 포트가 다를 경우 절대경로 사용
        //브라우저에 직접 url입력해서 API 테스트 하기
        
        System.out.println("생성된 downloadUrl: " + downloadUrl);

     // 파일 경로에서 파일명만 추출
        String savedName = extractSavedNameFromPath(newsFile.getFilePath());
        fileDTO.setSavedName(savedName);
        
        return fileDTO;
    }
        
    //파일 경로에서 저장된 파일명 추출
    private String extractSavedNameFromPath(String filePath) {
        if (filePath == null) return null;

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