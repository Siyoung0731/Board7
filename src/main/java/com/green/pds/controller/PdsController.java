package com.green.pds.controller;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.green.menus.dto.MenuDTO;
import com.green.menus.mapper.MenuMapper;
import com.green.paging.dto.Pagination;
import com.green.paging.dto.SearchDto;
import com.green.pds.dto.PdsDto;
import com.green.pds.mapper.PdsMapper;
import com.green.pds.service.PdsService;

@Controller
@RequestMapping("/Pds")
public class PdsController {
	
	@Autowired
	private MenuMapper menuMapper;
	@Autowired
	private PdsMapper pdsMapper;
	@Autowired
	private PdsService pdsService;

	// /Pds/List?menu_id=MENU01&nowpage=1
	@RequestMapping("/List")
	public ModelAndView list(
			@RequestParam HashMap<String, Object> map) {
		// map 을 파라미터로 사용하려면 -> @RequestParam을 붙여야 한다.
		//System.out.println("map: " + map);
		
		// 메뉴 목록 조회
		List<MenuDTO> mList = menuMapper.getMenuList();
		
		// 자료실 목록 조회 (10개씩) - 페이징 처리 준비작업 시작
		// 해당 메뉴의 자료수
		int totCount = pdsMapper.count(map); // menu_id, searchType, keyword 전달 예정
		System.out.println(totCount);
		// String.valueOf(~) 이 방법을 많이 사용
		int nowpage = Integer.parseInt(String.valueOf(map.get("nowpage"))); 
		
		// 페이징을 위한 설정 
		SearchDto sto = new SearchDto();
		sto.setPageNo(nowpage);
		sto.setNumOfRows(10); // 한 페이지에 10줄의 자료
		sto.setPageSize(10); // 페이지 번호 목록
		
		// Pagination 설정
		Pagination pagination = new Pagination(totCount, sto);
		sto.setPagination(pagination);
		
		int offset = sto.getOffset();
		int numOfRows = sto.getNumOfRows();
		
		map.put("offset", offset);
		map.put("numOfRows", numOfRows);
		
		System.out.println("map2: " + map);
		
		// 자료 조회 - Service 할 일
		List<PdsDto> pdsList = pdsService.getPdsList(map);
			
		//--------------------------------------------------------------------
		ModelAndView mv = new ModelAndView();
		mv.setViewName("pds/list");
		
		mv.addObject("mList", mList);
		mv.addObject("pdsList", pdsList);
		mv.addObject("sto", sto);
		
		mv.addObject("map", map);
		return mv;
	}
	// 글 쓰기
	// /Pds/WriteForm?menu_id=&nowpage=
	@RequestMapping("WriteForm")
	public ModelAndView writeForm(			
			@RequestParam HashMap<String, Object> map) {
		
		List<MenuDTO> mList = menuMapper.getMenuList();
		
		ModelAndView mv = new ModelAndView();
		mv.setViewName("pds/write");
		mv.addObject("mList", mList);
		mv.addObject("map", map);
		return mv;
	}
	// 내용 보기
	// Pds/View?idx=114&menu_id=MENU01&nowpage=1
	@RequestMapping("View")
	public ModelAndView view(
			@RequestParam HashMap<String, Object> map) {
		
		//넘겨줄 pdsDto 정보를 조회 IDX
		
		//넘겨줄 filesDto 정보를 조회 IDX
		
		//-------------------------------------------------------------------
		ModelAndView mv = new ModelAndView();
		mv.setViewName("pds/view");
		//mv.addObject("mList", mList);
		
		mv.addObject("map", map);
		return mv;
	}
}



















