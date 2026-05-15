package com.green.pds.service.impl;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.green.pds.dto.PdsDto;
import com.green.pds.mapper.PdsMapper;
import com.green.pds.service.PdsService;

@Service
public class PdsServiceImpl implements PdsService {
	
	// @Value : 설정값들을 Java 변수에 주입할 때 사용
	@Value("${part1.upload-path}")
	private String uploadPath;
	
	@Autowired
	private PdsMapper pdsMapper;
	
	@Override
	public List<PdsDto> getPdsList(HashMap<String, Object> map) {
		List<PdsDto> pdsList = pdsMapper.getPdsList(map);
		return pdsList;
	}
	
	@Override
	public void setWrite(HashMap<String, Object> map, MultipartFile[] uploadFiles) {
		// 파일저장 + DB 저장
		// 1. 파일 저장 : uploadFiles -> d:dev/springboot/data/
		map.put("uploadPath", uploadPath); // map 에 uploadPath 저장
		System.out.println("PdsFile 이전 map: " + map);
		
		// 별도 클래스 생성해서 처리 : PdsFile
		PdsFile.save(map, uploadFiles);
		System.out.println("PdsFile 이후 map: " + map); // fileList, fto
		
		// 2. DB 저장 : 자료실 글 쓰기 <- map 
		return;
	}

}
