package com.EduTech.repository.demonstration;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.EduTech.entity.demonstration.Demonstration;
import com.EduTech.entity.demonstration.DemonstrationRegistration;
import com.EduTech.entity.demonstration.DemonstrationReserve;
import com.EduTech.entity.member.Company;
import com.EduTech.entity.member.Member;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
public class DemonstrationReserveSpecs {

    public static Specification<DemonstrationReserve> withSearchAndSort(
        String memId,
        String type,
        String search,
        String sortBy,
        String sortDir
    ) {
        return (root, query, cb) -> {
            // 정렬 (count 쿼리일 경우 정렬 생략)
            if (!Long.class.equals(query.getResultType()) && !long.class.equals(query.getResultType())) {
                Path<?> sortPath;

                
                if ("demName".equalsIgnoreCase(sortBy)) {
                    Join<DemonstrationReserve, Demonstration> demJoin = root.join("demonstration", JoinType.LEFT);
                    sortPath = demJoin.get("demName");
                } else if ("companyName".equalsIgnoreCase(sortBy)) {
                    Join<DemonstrationReserve, DemonstrationRegistration> regJoin = root.join("demonstrationRegistration", JoinType.LEFT);
                    Join<DemonstrationRegistration, Member> memJoin = regJoin.join("member", JoinType.LEFT);
                    Join<Member, Company> compJoin = memJoin.join("company", JoinType.LEFT);
                    sortPath = compJoin.get("companyName");
                } else {
                    sortPath = root.get(sortBy); // 예: applyAt, startDate, endDate
                }

                if ("asc".equalsIgnoreCase(sortDir)) {
                    query.orderBy(cb.asc(sortPath));
                } else {
                    query.orderBy(cb.desc(sortPath));
                }
            }

            // 기본 조건: 회원 ID
            Predicate predicate = cb.equal(root.get("member").get("memId"), memId);

            // 검색 조건
            if (StringUtils.hasText(search)) {
                if ("demName".equalsIgnoreCase(type)) {
                    Join<DemonstrationReserve, Demonstration> demJoin = root.join("demonstration", JoinType.LEFT);
                    predicate = cb.and(predicate, cb.like(cb.lower(demJoin.get("demName")), "%" + search.toLowerCase() + "%"));
                } else if ("companyName".equalsIgnoreCase(type)) {
                    Join<DemonstrationReserve, DemonstrationRegistration> regJoin = root.join("demonstrationRegistration", JoinType.LEFT);
                    Join<DemonstrationRegistration, Member> memJoin = regJoin.join("member", JoinType.LEFT);
                    Join<Member, Company> compJoin = memJoin.join("company", JoinType.LEFT);
                    predicate = cb.and(predicate, cb.like(cb.lower(compJoin.get("companyName")), "%" + search.toLowerCase() + "%"));
                }
            }

            return predicate;
        };
    }
}
