package com.EduTech.repository.event;

import org.springframework.data.jpa.repository.JpaRepository;

import com.EduTech.entity.event.EventReserve;

public interface EventUseRepository extends JpaRepository<EventReserve, Long>{

}
