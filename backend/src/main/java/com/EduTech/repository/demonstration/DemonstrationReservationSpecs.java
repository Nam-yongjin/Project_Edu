package com.EduTech.repository.demonstration;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.EduTech.entity.demonstration.Demonstration;
import com.EduTech.entity.demonstration.DemonstrationRegistration;
import com.EduTech.entity.demonstration.DemonstrationState;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

public class DemonstrationReservationSpecs {

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

}
