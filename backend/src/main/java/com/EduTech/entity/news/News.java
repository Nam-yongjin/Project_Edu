package com.EduTech.entity.news;

import com.EduTech.entity.BaseEntity;
import com.EduTech.entity.facility.FacilityTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "news")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class News extends BaseEntity{

	@Id
	@Column(nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long newsNum;
}
