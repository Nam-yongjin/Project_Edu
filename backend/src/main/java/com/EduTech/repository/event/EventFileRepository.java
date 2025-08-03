package com.EduTech.repository.event;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.EduTech.entity.event.EventFile;
import com.EduTech.entity.event.EventInfo;

public interface EventFileRepository extends JpaRepository<EventFile, Long> {

	List<EventFile> findByEvent(EventInfo event);   // 수정됨
    
	void deleteByEvent(EventInfo event);           // 수정됨

}
