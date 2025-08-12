package com.EduTech.repository.facility;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.EduTech.entity.facility.PublicHoliday;

public interface PublicHolidayRepository extends JpaRepository<PublicHoliday, Long> {
	
	boolean existsByDate(LocalDate date);
	
    List<PublicHoliday> findByDateBetween(LocalDate start, LocalDate end);
    
    boolean existsByDateBetween(LocalDate start, LocalDate end);
    
    @Transactional
    long deleteByDate(LocalDate date);
    
}
