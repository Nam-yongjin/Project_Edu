package com.EduTech.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FileUtil {

	@Value("${file.upload.path}")
	private String uploadFileDir;
	
	@Value("${image.upload.path}")
	private String uploadImageDir;
}
