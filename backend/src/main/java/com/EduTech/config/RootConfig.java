package com.EduTech.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.EduTech.dto.demonstration.DemonstrationFormResDTO;
import com.EduTech.dto.event.EventInfoDTO;
import com.EduTech.entity.demonstration.Demonstration;
import com.EduTech.entity.event.EventInfo;

@Configuration
public class RootConfig {

	// DTO ↔ Entity 자동 매핑 도구
	@Bean
	public ModelMapper getMapper() {
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration()
			.setFieldMatchingEnabled(true)
			.setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE)
			.setMatchingStrategy(MatchingStrategies.LOOSE)
			.setSkipNullEnabled(true);	//event에 등록시간null 중복입력 방지를 위한 코드 추가
		
		// 필드 매핑 방지 (중요)
		modelMapper.typeMap(EventInfoDTO.class, EventInfo.class)
        	.addMappings(mapper -> {
        		mapper.skip(EventInfo::setEventNum);    // EventNum null로 덮어쓰기 방지
        		mapper.skip(EventInfo::setApplyAt);     // 등록일은 null로 덮어쓰지 않도록 방지
        		mapper.skip(EventInfo::setMainImagePath);     // null로 덮어쓰기 방지
                mapper.skip(EventInfo::setFilePath);          // null로 덮어쓰기 방지
                mapper.skip(EventInfo::setOriginalName);	  // null로 덮어쓰기 방지
        	});

		// Demonstration -> DemonstrationFormResDTO 매핑 간에 오류가 잇어 충돌나는 칼럼에 대해 skip 처리
		modelMapper.typeMap(Demonstration.class, DemonstrationFormResDTO.class)
	    .addMappings(mapper -> {
	        mapper.skip(DemonstrationFormResDTO::setImageUrlList);
	        mapper.skip(DemonstrationFormResDTO::setImageNameList);
	        mapper.skip(DemonstrationFormResDTO::setIsMain);
	    });
		return modelMapper;
	}
}