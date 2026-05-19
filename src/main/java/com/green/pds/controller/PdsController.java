package com.green.pds.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import com.green.config.MvcConfig;
import com.green.interceptor.AuthInterceptor;
import com.green.menus.dto.MenuDTO;
import com.green.menus.mapper.MenuMapper;
import com.green.paging.dto.Pagination;
import com.green.paging.dto.SearchDto;
import com.green.pds.dto.FilesDto;
import com.green.pds.dto.PdsDto;
import com.green.pds.mapper.PdsMapper;
import com.green.pds.service.PdsService;

import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/Pds")
public class PdsController {

    private final AuthInterceptor authInterceptor;

    private final MvcConfig mvcConfig;
	
	@Value("${part1.upload-path}")
	private String uploadPath;
	
	@Autowired
	private MenuMapper menuMapper;
	@Autowired
	private PdsMapper pdsMapper;
	@Autowired
	private PdsService pdsService;

    PdsController(MvcConfig mvcConfig, AuthInterceptor authInterceptor) {
        this.mvcConfig = mvcConfig;
        this.authInterceptor = authInterceptor;
    }

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
	@RequestMapping("/WriteForm")
	public ModelAndView writeForm(			
			@RequestParam HashMap<String, Object> map) {
		
		List<MenuDTO> mList = menuMapper.getMenuList();
		
		ModelAndView mv = new ModelAndView();
		mv.setViewName("pds/write");
		mv.addObject("mList", mList);
		mv.addObject("map", map);
		return mv;
	}
	// menu_id=MENU01, nowpage=1, title=aaa, writer=aa, content=aaa,
	// upfile=(binary), upfile=(binary)
	@RequestMapping("/Write")
	public ModelAndView write(
			@RequestParam HashMap<String, Object> map,
			@RequestParam(value="upfile") MultipartFile [] uploadFiles 
			) {
		System.out.println("map:" + map + "uploadFiles: " + uploadFiles);
		
		// 넘어온 정보 저장
		pdsService.setWrite(map, uploadFiles);
		
		// 저장 후 돌아가기
		String menu_id = String.valueOf(map.get("menu_id"));
		int nowpage = Integer.parseInt(String.valueOf(map.get("nowpage")));
		
		ModelAndView mv = new ModelAndView();
		String loc = """
				redirect:/Pds/List?menu_id=%s&nowpage=%d
				""".formatted(menu_id, nowpage);
		mv.setViewName(loc);
		return mv;
	}
	// 내용 보기
	// Pds/View?idx=114&menu_id=MENU01&nowpage=1
	@RequestMapping("/View")
	public ModelAndView view(
			@RequestParam HashMap<String, Object> map) {
		
		// 메뉴 목록 조회
		List<MenuDTO> mList = menuMapper.getMenuList();
		
		// 조회수 증가
		pdsService.setReadCountUpdate(map); // map : IDX, incHit
		
		//넘겨줄 pdsDto 정보를 조회 IDX
		PdsDto pto = pdsService.getPds(map);
		//넘겨줄 filesDto 정보를 조회 IDX
		List<FilesDto> fileList = pdsService.getFileList(map);
		
		//-------------------------------------------------------------------
		ModelAndView mv = new ModelAndView();
		mv.setViewName("pds/view");
		mv.addObject("mList", mList);
		
		mv.addObject("pds", pto); // 게시물 정보 : pds
		mv.addObject("fileList", fileList); // 파일 정보 : file
		
		mv.addObject("map", map);
		return mv;
	}
	
	// /Pds/Delete?idx=823&menu_id=MENU01&nowpage=1
	// 삭제 
	@RequestMapping("/Delete")
	public ModelAndView delete(
			@RequestParam HashMap<String, Object> map) {
		
		System.out.println("Delete map: " + map);
		
		// DB 에서 자료 삭제 - Service - Serviceimpl - PdsFile
		pdsService.setDelete(map);
		
		// 삭제 이후 목록 조회 돌아갈 주소 이동
		ModelAndView mv = new ModelAndView();
		String loc = "redirect:/Pds/List"
				+ "?menu_id=" + map.get("menu_id")
				+ "&nowpage=" + map.get("nowpage");
		mv.setViewName(loc);
		return mv;
	}
	//Pds/UpdateForm?idx=209&menu_id=MENU01&nowpage=1
	@RequestMapping("/UpdateForm")
	public ModelAndView updateForm(
			@RequestParam HashMap<String, Object> map) {
		
		// 메뉴 목록
		List<MenuDTO> mList = menuMapper.getMenuList();
		
		// 수정할 Board 정보 IDX 로 검색
		PdsDto pds = pdsService.getPds(map);
		
		// 수정할 Files 정보 IDX 로 검색
		List<FilesDto> fileList = pdsMapper.getFileList(map); 
		
		ModelAndView mv = new ModelAndView();
		mv.setViewName("pds/update");
		mv.addObject("mList", mList);
		mv.addObject("pds", pds);
		mv.addObject("fileList", fileList);
		
		mv.addObject("map", map);
		return mv;
	}
	
	@RequestMapping("/Update")
	public ModelAndView update(
			@RequestParam HashMap<String, Object> map) {
		ModelAndView mv = new ModelAndView();
		String loc = "redirect:/Pds/List"
				+ "?menu_id=" + map.get("menu_id") 
				+ "&nowpage=" + map.get("nowpage");
		mv.setViewName(loc);
		return mv;
	}
	
	// 파일다운로드
	// 서버에서 바이너리데이터를 다운받는다.
	@GetMapping("/filedownload/{file_num}")
	@ResponseBody
	public void downloadFile(
			HttpServletResponse res,
			@PathVariable(value="file_num") long file_num		// ?file_num=1
			) throws UnsupportedEncodingException {
		// HttpServletResponse 객체를 사용하면 return 문 없이도 data를 서버 
		// -> 클라이언트로 보낼 수 있다.
		FilesDto fileInfo = pdsService.getFileInfo(file_num);
		
		// 파일경로 : 다운로드할 파일의 경로 생성
		Path saveFilePath = Paths.get(
					uploadPath
					+ File.separator
					+ fileInfo.getSfilename()
				);
		
		// http 헤더 설정 : 클라이언트 브라우저에게 주는 정보
		setFileHeader(res, fileInfo);
		
		// 파일 복사 -> 함수 (서버 -> 클라이언트) : 실제 다운로드
		fileCopy(res, saveFilePath); 
		
	}
	//실제 파일 다운로드 부분 : binary data 를 다운로드
	private void fileCopy(HttpServletResponse res, Path saveFilePath) {
		
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(saveFilePath.toFile());
			FileCopyUtils.copy(fis, res.getOutputStream()); //처리 속도 빠름
			res.getOutputStream().flush(); // 남아있는 버퍼 초기화
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
		try {
			fis.close();
		} catch (IOException e) {
			e.printStackTrace();
			}
		}
	}
	//다운로드 받을 파일의 header 정보 기본 설정 
	private void setFileHeader(HttpServletResponse res, FilesDto fileInfo) 
			throws UnsupportedEncodingException {
		res.setHeader("Content-Disposition",
				"attachmen; filename=\"" + 
				URLEncoder.encode( 
						(String) fileInfo.getFilename(), "UTF-8") + "\"; ");
		res.setHeader("Content-Transfer-Encoding", "binary");
		//res.setHeader("Content-Type", "application/download; utf-8");
		res.setHeader("Content-Type", "application/octet-stream; utf-8");
		res.setHeader("Pragma", "no-cache");
		res.setHeader("Expires", "-1");
	}
	
}



















