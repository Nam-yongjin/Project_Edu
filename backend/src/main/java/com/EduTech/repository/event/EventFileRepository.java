package com.EduTech.repository.event;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.EduTech.entity.event.EventFile;

public interface EventFileRepository extends JpaRepository<EventFile, Long> {
    List<EventFile> findByEventInfo_EventNum(Long eventNum);
}
