package com.green.pds.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.green.pds.dto.FilesDto;
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
		//   Board table 에 저장
		pdsMapper.setWrite(map); // insertBoard
		
		
		// 3. Files 에 저장
		List<FilesDto> fileList = (List<FilesDto>) map.get("fileList");
		if(fileList.size() > 0)
			pdsMapper.setFileWriter(map);
		
		return;
	}
	
	// map(IDX) 에 해당하는 조회수 증가
	@Override
	public void setReadCountUpdate(HashMap<String, Object> map) {
		pdsMapper.setReadCountUpdate(map);
	}
	
	// 자료실 게시글(Pds) 을 조회한다 : map(idx)
	@Override
	public PdsDto getPds(HashMap<String, Object> map) {
		
		PdsDto pto = (PdsDto) pdsMapper.getPds(map);
		
		return pto;
	}
	
	// IDX 에 해당하는 FILES TABLE 의 정보
	@Override
	public List<FilesDto> getFileList(HashMap<String, Object> map) {
		
		List<FilesDto> fileList = pdsMapper.getFileList(map);
		
		return fileList;
	}

	// file_num 로 조회할 파일 정보를 조회
	@Override
	public FilesDto getFileInfo(Long file_num) {
		
		FilesDto fileInfo = pdsMapper.getFileInfo(file_num);
		
		return fileInfo;
	}

	@Override
	public void setDelete(HashMap<String, Object> map) {	
		
		//0. 해당 파일 정보 조회 
		List<FilesDto> fileList = pdsMapper.getFileList(map);
		
		//1. 실제 파일도 삭제 : springboot/data 에 있는 IDX 관련 파일 삭제
		PdsFile.delete(uploadPath, fileList);
		
		//2. IDX 에 해당하는 파일을 삭제 : Files Table 에 실제 삭제된 정보 삭제
		// 외래키가 설정된 관계에서 삭제는 자식레코드를 먼저 삭제해야한다
		pdsMapper.deleteUploadFile(map);
		
		//3. IDX 에 해당하는 자료 글 삭제 : Board 
		pdsMapper.setDelete(map);
	}

}















