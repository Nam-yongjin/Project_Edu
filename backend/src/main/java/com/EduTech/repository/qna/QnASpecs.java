package com.EduTech.repository.qna;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import com.EduTech.entity.qna.Question;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

public class QnASpecs {
	public static Specification<Question> searchQnA(String type, String search,
	         String sortBy, String sort) {

	      return (root, query, cb) -> {

	          // 중복 제거를 위해 distinct 설정 (fetch join 없으면 join만 있어도 중복 가능)
	          query.distinct(true);

	          List<Predicate> predicates = new ArrayList<>();


	          if (StringUtils.hasText(search)) {
	              if ("title".equalsIgnoreCase(type)) {
	                  predicates.add(cb.like(cb.lower(root.get("title")), "%" + search.toLowerCase() + "%"));
	              } 
	              if ("memId".equalsIgnoreCase(type)) {
	                  predicates.add(cb.like(cb.lower(root.get("memId")), "%" + search.toLowerCase() + "%"));
	              } 
	          }

	          Path<?> sortPath;
	          switch (sortBy.toLowerCase()) {
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


