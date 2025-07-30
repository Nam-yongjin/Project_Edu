package com.EduTech.repository.notice;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.jpa.domain.Specification;

import com.EduTech.dto.notice.NoticeSearchDTO;
import com.EduTech.entity.notice.Notice;

public class NoticeSpecifications {
    
    public static Specification<Notice> createSpecification(NoticeSearchDTO searchDto) {
        return Specification
                .where(keywordFilter(searchDto.getSearchType(), searchDto.getKeyword()))
                .and(pinnedFilter(searchDto.isPinned()))
                .and(dateRangeFilter(searchDto.getStartDate(), searchDto.getEndDate()));
    }
    
    private static Specification<Notice> keywordFilter(String searchType, String keyword) {
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
    
    private static Specification<Notice> pinnedFilter(boolean isPinned) {
        return (root, query, cb) -> cb.equal(root.get("isPinned"), isPinned);
    }
    
    private static Specification<Notice> dateRangeFilter(LocalDate startDate, LocalDate endDate) {
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