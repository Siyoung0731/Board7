package com.green.pds.service;

import java.util.HashMap;
import java.util.List;

import com.green.pds.dto.PdsDto;

// 기능 정의
public interface PdsService {

	List<PdsDto> getPdsList(HashMap<String, Object> map);
}
