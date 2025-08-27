package com.EduTech.repository.qna;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.EduTech.entity.qna.Answer;
import com.EduTech.entity.qna.Question;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

public class QnASpecs {
    public static Specification<Question> searchQnA(String searchType, String search, String sortBy, String sort,
            LocalDate startDate, LocalDate endDate, String answered) {

        return (root, query, cb) -> {

            // 중복 제거를 위해 distinct 설정
            query.distinct(true);

            // Question -> Answer 조인 (Question 엔티티의 answer 필드 사용)
            Join<Question, Answer> answerJoin = root.join("answer", JoinType.LEFT);
            List<Predicate> predicates = new ArrayList<>();

            // 날짜 필터링
            if (startDate != null && endDate != null) {
                predicates.add(cb.between(
                    root.get("createdAt"),
                    startDate.atStartOfDay(),
                    endDate.atTime(LocalTime.MAX)
                ));
            }

            // 답변 상태 필터링 (answered 매개변수 사용)
            if("wait".equals(answered)) {
                // 답변이 없는 질문만 (answerJoin이 null인 경우)
                predicates.add(cb.isNull(answerJoin.get("answerNum")));
            } else if("ok".equals(answered)) {
                // 답변이 있는 질문만 (answerJoin이 null이 아닌 경우)
                predicates.add(cb.isNotNull(answerJoin.get("answerNum")));
            }

            // 검색어 필터링 (searchType 매개변수 사용)
            if (StringUtils.hasText(search)) {
                if ("title".equalsIgnoreCase(searchType)) {
                    predicates.add(cb.like(cb.lower(root.get("title")), "%" + search.toLowerCase() + "%"));
                } else if ("memId".equalsIgnoreCase(searchType)) {
                    predicates.add(cb.like(cb.lower(root.get("member").get("memId")), "%" + search.toLowerCase() + "%"));
                }
            }

            // 정렬 처리
            Path<?> sortPath;
            switch (sortBy != null ? sortBy.toLowerCase() : "createdat") {
                case "view":
                    sortPath = root.get("view");
                    break;
                case "updatedAt":
                    sortPath = root.get("updatedAt");
                    break;
                default:
                    sortPath = root.get("createdAt");
                    break;
            }

            if ("desc".equalsIgnoreCase(sort)) {
                query.orderBy(cb.desc(sortPath));
            } else {
                query.orderBy(cb.asc(sortPath));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}