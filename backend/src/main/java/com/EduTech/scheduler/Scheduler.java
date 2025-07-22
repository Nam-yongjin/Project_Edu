package com.EduTech.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.EduTech.repository.member.MemberRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class Scheduler {
	
	private final MemberRepository memberRepository;

	// 회원탈퇴 일주일지난 회원정보 자동삭제
    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul") // 매일 0시에 실행
    public void cleanUpLeaveMembers() {
        memberRepository.deleteMembersAfterOneWeekLeave();
    }
}
