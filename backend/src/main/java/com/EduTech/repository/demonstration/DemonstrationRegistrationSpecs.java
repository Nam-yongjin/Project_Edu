package com.EduTech.repository.demonstration;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.EduTech.entity.demonstration.Demonstration;
import com.EduTech.entity.demonstration.DemonstrationRegistration;
import com.EduTech.entity.demonstration.DemonstrationState;
import com.EduTech.entity.member.Company;
import com.EduTech.entity.member.Member;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

public class DemonstrationRegistrationSpecs {
	// 기업 실증 물품 등록 페이지에서 정보들을 가져오는 spec
	public static Specification<DemonstrationRegistration> withSearchAndSort(
	        String memId, String type, String search,
	        String sortBy, String sort, String statusFilter) {

	    return (root, query, cb) -> {

	        query.distinct(true);

	        Join<DemonstrationRegistration, Demonstration> demJoin = root.join("demonstration", JoinType.LEFT);
	        List<Predicate> predicates = new ArrayList<>();

	        if (StringUtils.hasText(memId)) {
	            predicates.add(cb.equal(root.get("member").get("memId"), memId));
	        }

	        if (StringUtils.hasText(search)) {
	            if ("demName".equalsIgnoreCase(type)) {
	                predicates.add(cb.like(cb.lower(demJoin.get("demName")), "%" + search.toLowerCase() + "%"));
	            } else if ("demMfr".equalsIgnoreCase(type)) {
	                predicates.add(cb.like(cb.lower(demJoin.get("demMfr")), "%" + search.toLowerCase() + "%"));
	            }
	        }

	        if (StringUtils.hasText(statusFilter) && !"total".equalsIgnoreCase(statusFilter)) {
	            switch (statusFilter.toLowerCase()) {
	                case "reject":
	                    predicates.add(cb.equal(root.get("state"), DemonstrationState.REJECT));
	                    break;
	                case "accept":
	                    predicates.add(cb.equal(root.get("state"), DemonstrationState.ACCEPT));
	                    break;
	                case "wait":
	                    predicates.add(cb.equal(root.get("state"), DemonstrationState.WAIT));
	                    break;
	                case "cancel":
	                    predicates.add(cb.equal(root.get("state"), DemonstrationState.CANCEL));
	                    break;
	                case "expired":
	                      predicates.add(cb.equal(root.get("state"), DemonstrationState.EXPIRED));
	                      break;
	            }
	        }

	        Path<?> sortPath;
	        if ("expDate".equalsIgnoreCase(sortBy)) {
	            sortPath = root.get("expDate");
	        } else {
	            // 기본값 regDate
	            sortPath = root.get("regDate");
	        }

	        if ("desc".equalsIgnoreCase(sort)) {
	            query.orderBy(cb.desc(sortPath));
	        } else {
	            query.orderBy(cb.asc(sortPath));
	        }

	        return cb.and(predicates.toArray(new Predicate[0]));
	    };
	}

	
	public static Specification<DemonstrationRegistration> withSearchAdmin(
	        String type, String search, String statusFilter) {
	    
	    return (root, query, cb) -> {
	        Join<DemonstrationRegistration, Demonstration> demJoin = root.join("demonstration", JoinType.LEFT);
	        Join<DemonstrationRegistration, Member> memberJoin = root.join("member", JoinType.LEFT);
	        Join<Member, Company> teacherJoin = memberJoin.join("company", JoinType.LEFT);
	        List<Predicate> predicates = new ArrayList<>();

	        if (StringUtils.hasText(search) && StringUtils.hasText(type)) {
	            if ("demName".equalsIgnoreCase(type)) {
	                predicates.add(cb.like(cb.lower(demJoin.get("demName")), "%" + search.toLowerCase() + "%"));
	            } else if ("companyName".equalsIgnoreCase(type)) {
	                predicates.add(cb.like(cb.lower(teacherJoin.get("companyName")), "%" + search.toLowerCase() + "%"));
	            } else if ("memId".equalsIgnoreCase(type)) {
	                predicates.add(cb.like(cb.lower(memberJoin.get("memId")), "%" + search.toLowerCase() + "%"));
	            }
	        }

	        if (StringUtils.hasText(statusFilter) && !"total".equalsIgnoreCase(statusFilter)) {
	            switch (statusFilter.toLowerCase()) {
	                case "wait":
	                    predicates.add(cb.equal(root.get("state"), DemonstrationState.WAIT));
	                    break;
	                case "accept":
	                    predicates.add(cb.equal(root.get("state"), DemonstrationState.ACCEPT));
	                    break;
	                case "reject":
	                    predicates.add(cb.equal(root.get("state"), DemonstrationState.REJECT));
	                    break;
	                case "cancel":
	                    predicates.add(cb.equal(root.get("state"), DemonstrationState.CANCEL));
	                    break;
	                case "expired":
	                    predicates.add(cb.equal(root.get("state"), DemonstrationState.EXPIRED));
	                    break;
	            }
	        }

	        // 정렬 제거 - Pageable에서 처리
	        return cb.and(predicates.toArray(new Predicate[0]));
	    };
	}

}
