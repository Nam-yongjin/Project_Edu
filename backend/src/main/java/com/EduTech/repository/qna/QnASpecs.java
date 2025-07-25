package com.EduTech.repository.qna;

import org.springframework.data.jpa.domain.Specification;

import com.EduTech.entity.qna.Question;

public class QnASpecs {
	public static Specification<Question> memIdContains(String memId) {
		return (root, query, builder) -> memId == null ? null
				: builder.like(builder.lower(root.get("member").get("memId")), "%" + memId.toLowerCase() + "%");
	}

	public static Specification<Question> titleContains(String title) {
		return (root, query, builder) -> title == null ? null
				: builder.like(builder.lower(root.get("title")), "%" + title.toLowerCase() + "%");
	}
}
