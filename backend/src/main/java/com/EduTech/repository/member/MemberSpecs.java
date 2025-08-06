package com.EduTech.repository.member;

import org.springframework.data.jpa.domain.Specification;

import com.EduTech.entity.member.Member;
import com.EduTech.entity.member.MemberRole;
import com.EduTech.entity.member.MemberState;

public class MemberSpecs {
	public static Specification<Member> memIdContains(String memId) {
		// root :조회 대상 엔티티
		// query: 쿼리 구조 전체 설정
		// builder: 쿼리 조건을 만드는 도구
		return (root, query, builder) -> memId == null ? null
				: builder.like(builder.lower(root.get("memId")), "%" + memId.toLowerCase() + "%");
	}

	public static Specification<Member> nameContains(String name) {
		return (root, query, builder) -> name == null ? null
				: builder.like(builder.lower(root.get("name")), "%" + name.toLowerCase() + "%");
	}

	public static Specification<Member> emailContains(String email) {
		return (root, query, builder) -> email == null ? null
				: builder.like(builder.lower(root.get("email")), "%" + email.toLowerCase() + "%");
	}

	public static Specification<Member> phoneContains(String phone) {
		return (root, query, builder) -> phone == null ? null
				: builder.like(builder.lower(root.get("phone")), "%" + phone.toLowerCase() + "%");
	}

	public static Specification<Member> hasRole(MemberRole role) {
		return (root, query, builder) -> role == null ? null : builder.equal(root.get("role"), role);
	}

	public static Specification<Member> hasState(MemberState state) {
		return (root, query, builder) -> state == null ? null : builder.equal(root.get("state"), state);
	}
}
