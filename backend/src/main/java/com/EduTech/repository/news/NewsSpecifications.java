package com.EduTech.repository.news;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.jpa.domain.Specification;


import com.EduTech.dto.news.NewsSearchDTO;

import com.EduTech.entity.news.News;


//사용자 검색용
//NewsEntity에 대한 정적 Specification 생성
public class NewsSpecifications {
    
    public static Specification<News> createSpecification(NewsSearchDTO searchDto) {
        return Specification
                .where(keywordFilter(searchDto.getSearchType(), searchDto.getKeyword()))
                .and(dateRangeFilter(searchDto.getStartDate(), searchDto.getEndDate()));
    }
    
    private static Specification<News> keywordFilter(String searchType, String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.trim().isEmpty()) {
                return null;
            }
            
            String likePattern = "%" + keyword.trim() + "%";
            
            return switch (searchType.toUpperCase()) {
                case "TITLE" -> cb.like(root.get("title"), likePattern);
                case "CONTENT" -> cb.like(root.get("content"), likePattern);
                case "WRITER" -> cb.like(root.get("member").get("name"), likePattern);
                case "ALL" -> cb.or(
                    cb.like(root.get("title"), likePattern),
                    cb.like(root.get("content"), likePattern),
                    cb.like(root.get("member").get("name"), likePattern)
                );
                default -> null; //정해진 패턴 아닐 때 null반환
            };
        };
    }
    
    private static Specification<News> dateRangeFilter(LocalDate startDate, LocalDate endDate) {
        return (root, query, cb) -> {
            if (startDate == null || endDate == null) {
                return null;
            }
            
            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
            
            return cb.between(root.get("createdAt"), startDateTime, endDateTime);
        };
    }
}