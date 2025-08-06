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

	public static Specification<DemonstrationReserve> withSearchAndSort(
	        String memId, String type, String search, String sortBy, String sort) {

	    return (root, query, cb) -> {
	        List<Predicate> predicates = new ArrayList<>();

	        // 회원 ID로 필터
	        if (StringUtils.hasText(memId)) {
	            predicates.add(cb.equal(root.get("member").get("memId"), memId));
	        }

	        // 조인
	        Join<DemonstrationReserve, Demonstration> demJoin = root.join("demonstration", JoinType.LEFT);
	        Join<Demonstration, DemonstrationRegistration> regJoin = demJoin.join("demonstrationRegistration", JoinType.LEFT);
	        Join<DemonstrationRegistration, Member> memJoin = regJoin.join("member", JoinType.LEFT);
	        Join<Member, Company> compJoin = memJoin.join("company", JoinType.LEFT);

	        // 검색 조건
	        if (StringUtils.hasText(search)) {
	            if ("demName".equalsIgnoreCase(type)) {
	                predicates.add(cb.like(cb.lower(demJoin.get("demName")), "%" + search.toLowerCase() + "%"));
	            } else if ("companyName".equalsIgnoreCase(type)) {
	                predicates.add(cb.like(cb.lower(compJoin.get("companyName")), "%" + search.toLowerCase() + "%"));
	            }
	        }

	        // 정렬
	        Path<?> sortPath;
	        if ("companyName".equalsIgnoreCase(sortBy)) {
	            sortPath = compJoin.get("companyName");
	        } else if ("demName".equalsIgnoreCase(sortBy)) {
	            sortPath = demJoin.get("demName");
	        } else {
	            sortPath = root.get("applyAt"); // 기본 정렬
	        }

	        if (query.getOrderList().isEmpty()) {
	            query.orderBy("desc".equalsIgnoreCase(sort) ? cb.desc(sortPath) : cb.asc(sortPath));
	        }

	        return cb.and(predicates.toArray(new Predicate[0]));
	    };
	}

   
}
