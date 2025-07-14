package com.EduTech.repository.event;

import org.springframework.data.jpa.repository.JpaRepository;

import com.EduTech.entity.event.Event;

public interface EventRepository extends JpaRepository<Event, Long>{

}
