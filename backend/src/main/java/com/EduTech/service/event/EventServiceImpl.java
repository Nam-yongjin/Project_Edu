package com.EduTech.service.event;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.EduTech.repository.event.EventBannerRepository;
import com.EduTech.repository.event.EventInfoRepository;
import com.EduTech.repository.event.EventUseRepository;
import com.EduTech.util.FileUtil;

public class EventServiceImpl implements EventService {
	
	//==========		필드		=================
	
	private static final Logger log = LoggerFactory.getLogger(EventServiceImplold.class);
	private final EventBannerRepository bannerRepository;
	private final EventInfoRepository infoRepository;
	private final EventUseRepository useRepository;
	private final FileUtil fileUtil;
	private final ModelMapper modelMapper;
	
}
