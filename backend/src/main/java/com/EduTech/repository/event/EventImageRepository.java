package com.EduTech.repository.event;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.EduTech.entity.event.EventImage;
import com.EduTech.entity.event.EventInfo;

public interface EventImageRepository extends JpaRepository<EventImage, Long> {
	
	List<EventImage> findByEvent(EventInfo event);
    
    void deleteByEvent(EventInfo event);
    
}
