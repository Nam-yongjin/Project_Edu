package com.EduTech.service.news;

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

import com.EduTech.dto.news.NewsCreateRegisterDTO;
import com.EduTech.dto.news.NewsDetailDTO;
import com.EduTech.dto.news.NewsFileDTO;
import com.EduTech.dto.news.NewsListDTO;
import com.EduTech.dto.news.NewsSearchDTO;
import com.EduTech.dto.news.NewsUpdateRegisterDTO;
import com.EduTech.entity.member.Member;
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
    
    @Override
    public void deleteNews(Long newsNum) {
        Member currentMember = getCurrentMember();
        validateAdminRole(currentMember);
        
        News news = newsRepository.findById(newsNum)
                .orElseThrow(() -> new EntityNotFoundException("뉴스를 찾을 수 없습니다. ID: " + newsNum));
        
        // 파일 삭제 (orphanRemoval = true로 인해 자동으로 삭제되지만, 물리적 파일은 직접 삭제 필요)
        deleteNewsFiles(news.getNewsNum());
        
        // 뉴스 삭제
        newsRepository.delete(news);
    }
    
    @Override
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
        
        // 파일들 삭제
        newsNums.forEach(this::deleteNewsFiles);
        
        // 뉴스 삭제
        newsRepository.deleteAll(newsByIds);
    }
    
    @Override
    @Transactional(readOnly = true)
    public NewsDetailDTO getNewsDetail(Long newsNum) {
        News news = newsRepository.findById(newsNum)
                .orElseThrow(() -> new EntityNotFoundException("뉴스를 찾을 수 없습니다. ID: " + newsNum));
        
        return mapToDetailDTO(news);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<NewsListDTO> getNewsList(NewsSearchDTO searchDto) {
        // 정렬 설정
        Sort sort = Sort.by(
                Sort.Direction.fromString(searchDto.getSortDirection()),
                searchDto.getSortBy()
        );
        
        Pageable pageable = PageRequest.of(searchDto.getPage(), searchDto.getSize(), sort);
        
        // 검색 조건 생성
        Specification<News> spec = NewsSpecifications.createSpecification(searchDto);
        
        // 조회 및 변환
        Page<News> newsByIds = newsRepository.findAll(spec, pageable);
        
        return newsByIds.map(this::mapToListDTO);
    }
    
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
        if (!"ADMIN".equals(member.getRole())) {
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
        
        return fileDataList.stream()
                .map(obj -> (Map<String, String>) obj)
                .filter(this::isAllowedFileType)
                .map(fileData -> NewsFile.builder()
                        .originalName(fileData.get("originalName"))
                        .filePath(fileData.get("filePath"))
                        .fileType(fileData.get("fileType").toLowerCase())
                        .news(news)
                        .build())
                .toList();
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
        fileDTO.setOriginalName(newsFile.getOriginalName());
        fileDTO.setFilePath(newsFile.getFilePath());
        fileDTO.setFileType(newsFile.getFileType());
        
        return fileDTO;
    }
}