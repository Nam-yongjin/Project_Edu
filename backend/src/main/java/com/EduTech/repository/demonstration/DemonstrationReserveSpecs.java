package com.EduTech.repository.demonstration;

import java.util.ArrayList;
import java.util.List;

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

	public static Specification<DemonstrationReserve> withSearchAndSort(String memId, String type, String search,
			String sortBy, String sort) {

    return (root, query, cb) -> {

        if (DemonstrationReserve.class.equals(query.getResultType())) {
            root.fetch("demonstration", JoinType.LEFT)
                .fetch("demonstrationRegistration", JoinType.LEFT)
                .fetch("member", JoinType.LEFT)
                .fetch("company", JoinType.LEFT);
            query.distinct(true); // 중복 제거용
        }

        Join<DemonstrationReserve, Demonstration> demJoin = root.join("demonstration", JoinType.LEFT);
        Join<Demonstration, DemonstrationRegistration> regJoin = demJoin.join("demonstrationRegistration",
                JoinType.LEFT);
        Join<DemonstrationRegistration, Member> memJoin = regJoin.join("member", JoinType.LEFT);
        Join<Member, Company> compJoin = memJoin.join("company", JoinType.LEFT);

        List<Predicate> predicates = new ArrayList<>();

        if (StringUtils.hasText(memId)) {
            predicates.add(cb.equal(root.get("member").get("memId"), memId));
        }

        if (StringUtils.hasText(search)) {
            if ("demName".equalsIgnoreCase(type)) {
                predicates.add(cb.like(cb.lower(demJoin.get("demName")), "%" + search.toLowerCase() + "%"));
            } else if ("companyName".equalsIgnoreCase(type)) {
                predicates.add(cb.like(cb.lower(compJoin.get("companyName")), "%" + search.toLowerCase() + "%"));
            }
        }

        Path<?> sortPath;
        if ("companyName".equalsIgnoreCase(sortBy)) {
            sortPath = compJoin.get("companyName");
        } else if ("demName".equalsIgnoreCase(sortBy)) {
            sortPath = demJoin.get("demName");
        } else if ("startDate".equalsIgnoreCase(sortBy)) {
            sortPath = root.get("startDate");
        } else if ("endDate".equalsIgnoreCase(sortBy)) {
            sortPath = root.get("endDate");
        } else if ("applyAt".equalsIgnoreCase(sortBy)) {
            sortPath = root.get("applyAt");
        } else {
            sortPath = root.get("applyAt"); 
        }

        // 무조건 정렬 지정
        if ("desc".equalsIgnoreCase(sort)) {
            query.orderBy(cb.desc(sortPath));
        } else {
            query.orderBy(cb.asc(sortPath));
        }

        return cb.and(predicates.toArray(new Predicate[0]));
    };
}

}
