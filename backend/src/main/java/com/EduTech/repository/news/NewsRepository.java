package com.EduTech.repository.news;

import org.springframework.data.jpa.repository.JpaRepository;

import com.EduTech.entity.news.News;

public interface NewsRepository extends JpaRepository<News, Long>{

}
