package com.EduTech.repository.notice;

import org.springframework.data.jpa.repository.JpaRepository;

import com.EduTech.entity.notice.Notice;

public interface NoticeRepository extends JpaRepository<Notice, Long>{

}
