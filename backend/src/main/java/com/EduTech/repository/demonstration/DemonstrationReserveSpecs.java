package com.EduTech.repository.demonstration;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.EduTech.entity.demonstration.Demonstration;
import com.EduTech.entity.demonstration.DemonstrationRegistration;
import com.EduTech.entity.demonstration.DemonstrationRequest;
import com.EduTech.entity.demonstration.DemonstrationReserve;
import com.EduTech.entity.demonstration.DemonstrationState;
import com.EduTech.entity.member.Company;
import com.EduTech.entity.member.Member;
import com.EduTech.entity.member.Teacher;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

public class DemonstrationReserveSpecs {
// 풀품 대여 현황 페이지에서 정보를 가져오기 위한 조건
   public static Specification<DemonstrationReserve> withSearchAndSort(String memId, String type, String search,
         String sortBy, String sort,String statusFilter) {

      return (root, query, cb) -> {

          // 중복 제거를 위해 distinct 설정 (fetch join 없으면 join만 있어도 중복 가능)
          query.distinct(true);

          Join<DemonstrationReserve, Demonstration> demJoin = root.join("demonstration", JoinType.LEFT);
          Join<Demonstration, DemonstrationRegistration> regJoin = demJoin.join("demonstrationRegistration", JoinType.LEFT);
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

          if (!"total".equalsIgnoreCase(statusFilter)) {
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
          switch (sortBy.toLowerCase()) {
              case "companyname":
                  sortPath = compJoin.get("companyName");
                  break;
              case "demname":
                  sortPath = demJoin.get("demName");
                  break;
              case "startdate":
                  sortPath = root.get("startDate");
                  break;
              case "enddate":
                  sortPath = root.get("endDate");
                  break;
              case "applyat":
              default:
                  sortPath = root.get("applyAt");
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
   

   public static Specification<DemonstrationReserve> withResSearchAndSort(
	        String memId,
	        String type,
	        String search,
	        String sortBy,
	        String sort,
	        String statusFilter,
	        Long demNum) {

	    return (root, query, cb) -> {
	        query.distinct(true);

	        Join<DemonstrationReserve, Demonstration> demJoin = root.join("demonstration", JoinType.LEFT);
	        Join<DemonstrationReserve, Member> memberJoin = root.join("member", JoinType.LEFT);
	        Join<Member, Teacher> teacherJoin = memberJoin.join("teacher", JoinType.LEFT);

	        // demonstration -> demonstrationRegistrations 조인 (복수 연관관계 이름 정확히 확인 필요)
	        Join<Demonstration, DemonstrationRegistration> regJoin = demJoin.join("demonstrationRegistration", JoinType.LEFT);
	        Join<DemonstrationRegistration, Member> regMemberJoin = regJoin.join("member", JoinType.LEFT);

	        List<Predicate> predicates = new ArrayList<>();

	        // reg.member.memId 조건 (DemonstrationRegistration 기준)
	        if (StringUtils.hasText(memId)) {
	            predicates.add(cb.equal(regMemberJoin.get("memId"), memId));
	        }

	        if (demNum != null) {
	            predicates.add(cb.equal(demJoin.get("demNum"), demNum));
	        }

	        if (StringUtils.hasText(search) && StringUtils.hasText(type)) {
	            if ("demName".equalsIgnoreCase(type)) {
	                predicates.add(cb.like(cb.lower(demJoin.get("demName")), "%" + search.toLowerCase() + "%"));
	            } else if ("demMfr".equalsIgnoreCase(type)) {
	                predicates.add(cb.like(cb.lower(demJoin.get("demMfr")), "%" + search.toLowerCase() + "%"));
	            } else if ("schoolName".equalsIgnoreCase(type)) {
	                predicates.add(cb.like(cb.lower(teacherJoin.get("schoolName")), "%" + search.toLowerCase() + "%"));
	            } else if ("schoolMemId".equalsIgnoreCase(type)) {
	                predicates.add(cb.equal(teacherJoin.get("memId"), search));
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
	            }
	        }

	        Path<?> sortPath;
	        if ("endDate".equalsIgnoreCase(sortBy)) {
	            sortPath = root.get("endDate");
	        } else if ("startDate".equalsIgnoreCase(sortBy)) {
	            sortPath = root.get("startDate");
	        } else {
	            sortPath = root.get("applyAt");
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
