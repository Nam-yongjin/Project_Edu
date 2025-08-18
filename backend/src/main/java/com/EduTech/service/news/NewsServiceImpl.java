package com.EduTech.service.news;

import java.time.LocalDateTime;
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

import com.EduTech.dto.news.NewsCreateRegisterDTO;
import com.EduTech.dto.news.NewsDetailDTO;
import com.EduTech.dto.news.NewsListDTO;
import com.EduTech.dto.news.NewsSearchDTO;
import com.EduTech.dto.news.NewsUpdateRegisterDTO;
import com.EduTech.entity.member.Member;
import com.EduTech.entity.member.MemberRole;
import com.EduTech.entity.news.News;
import com.EduTech.repository.member.MemberRepository;
import com.EduTech.repository.news.NewsRepository;
import com.EduTech.repository.news.NewsSpecifications;
import com.EduTech.security.jwt.JWTFilter;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class NewsServiceImpl implements NewsService {
    
    private final NewsRepository newsRepository;
    private final MemberRepository memberRepository;
    
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
                .imageUrl(registerDto.getImageUrl())
                .linkUrl(registerDto.getLinkUrl())
                .member(currentMember)
                .build();
        
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
        news.updateContent(registerDto.getTitle(), registerDto.getContent(), registerDto.getImageUrl(), registerDto.getLinkUrl());
        
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
        	newsRepository.delete(news); //하나씩 삭제 --> orphanRemoval 정상 작동
        }
       
    }
    //뉴스 상세조회
    @Override
    @Transactional(readOnly = true)
    public NewsDetailDTO getNewsDetail(Long newsNum) {
        News news = newsRepository.findById(newsNum)
                .orElseThrow(() -> new EntityNotFoundException("뉴스를 찾을 수 없습니다. ID: " + newsNum));
        
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
    
   
    
    // DTO 매핑 메서드들
    
    //상세용 DTO 변환
    private NewsDetailDTO mapToDetailDTO(News news) {
        
        NewsDetailDTO detailDTO = new NewsDetailDTO();
        detailDTO.setNewsNum(news.getNewsNum());
        detailDTO.setTitle(news.getTitle());
        detailDTO.setContent(news.getContent());
        detailDTO.setImageUrl(news.getImageUrl());
        detailDTO.setLinkUrl(news.getLinkUrl());
        detailDTO.setViewCount(news.getViewCount());
        detailDTO.setName(news.getMember().getName());
        detailDTO.setMem_id(news.getMember().getMemId());
        detailDTO.setCreatedAt(news.getCreatedAt());
        detailDTO.setUpdatedAt(news.getUpdatedAt());
        
        return detailDTO;
    }
    //목록용 DTO 변환
    private NewsListDTO mapToListDTO(News news) {
        NewsListDTO listDTO = new NewsListDTO();
        listDTO.setNewsNum(news.getNewsNum());
        listDTO.setTitle(news.getTitle());
        listDTO.setName(news.getMember().getName());
        listDTO.setViewCount(news.getViewCount());
        listDTO.setCreatedAt(news.getCreatedAt());
        
        return listDTO;
    }

}