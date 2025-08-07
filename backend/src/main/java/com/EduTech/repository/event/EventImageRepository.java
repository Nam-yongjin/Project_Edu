package com.EduTech.repository.event;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.EduTech.entity.event.EventImage;

public interface EventImageRepository extends JpaRepository<EventImage, Long> {
    
	List<EventImage> findByEventInfo_EventNum(Long eventNum); 
    
}