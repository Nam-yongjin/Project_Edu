package com.EduTech.entity.qna;

import com.EduTech.entity.BaseEntity;
import com.EduTech.entity.notice.NoticeFile;

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
@Table(name = "answer")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Answer extends BaseEntity{

	@Id
	@Column(nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long answerNum;
}
