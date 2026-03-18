package com.company.wording.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.company.wording.service.WordService;

@Controller
public class WordController {
	
	@Autowired
	private WordService wordService;
	
	@RequestMapping(value="/", method=RequestMethod.GET)
	public String index() {
		return "index";
	}

	@RequestMapping(value="/goods-wording-insert", method=RequestMethod.GET, produces="application/json;charset=utf-8")
	public String wordingInsert(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html; charset=utf-8");

		return "goodsWordingInsert";
	}

	@RequestMapping(value="/goods-wording-update", method=RequestMethod.GET, produces="application/json;charset=utf-8")
	public String wordingUpdate(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html; charset=utf-8");

		return "goodsWordingUpdate";
	}
	
	@RequestMapping(value="/list", method=RequestMethod.GET, produces="application/json;charset=utf-8")
	@ResponseBody
	public List<Map<String, Object>> list(HttpServletRequest request, HttpServletResponse response, @RequestParam("pstartno") int pstartno) throws Exception {
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html; charset=utf-8");
		
		List<Map<String, Object>> list = wordService.getMapGoodsListZero(pstartno);
		//String regx = "((크림)|([0-9]\\s*종))"; //보통명사 ex) 크림스킨, 2종, 3종
		String regx1 = "(크림)"; //보통명사 ex) 크림스킨, 2종, 3종
		String regx2 = "(^[0-9]*\\s*종)";
		Pattern p1 = Pattern.compile(regx1);
		Pattern p2 = Pattern.compile(regx2);
		
		for(Map<String, Object> map : list) {
			String goodsName = (""+map.get("GOODS_NAME")).trim().replaceAll("[^\uAC00-\uD7A3xfe0-9a-zA-Z\\.s]", ",");
			// ex) 브랜드?상품명?35ml 특수문자 인식이 안됨
			goodsName = goodsName.replace(",", " ").trim();
			long id = Long.parseLong(map.get("ID")+"");
			
			map.remove("GOODS_NAME");
			map.remove("ID");
			map.put("goodsName", goodsName);
			map.put("id", id);
			
			int cnt = wordService.getMapMasterGoods(id);
			
			if(cnt != 1) {
				map.put("masterCode", goodsName);
				map.put("imageUrl", id+".jpg");
				
				try {
					wordService.insertMapMasterGoods(map);
					map.remove("masterCode");
					map.remove("imageUrl");
				} catch (Exception e) {
					//e.printStackTrace();
				}
			}
			
			//String [] word = (!goodsName.contains(" "))? goodsName.split(",") : goodsName.split(" ");
			String [] word = goodsName.trim().split(" ");
			String word1 = word[0]; // 상품명의 첫 글자는 브랜드명이라는 가정
			String word2 = word[word.length-1].replaceAll("[^0-9||^.]", ""); // 개수, 용량은 마지막에 표시할거라는 가정
			
			String wording = "";
			
			if(word1.equals("이커머스")) {
				// ex) 이커머스 상품명 180ml - 그룹사명이 앞에 붙는 경우
				// ex) 이커머스 상품명 2종 세트, 이커머스 상품명 200ml
				// ex) 이커머스 상품명 125ml
				
				//if(!word2.equals("")&&(word.length-2 < 3)&&!word2.contains("세트")&&!goodsName.contains("상품명")) {
				//if(!word2.equals("")&&(word.length-2 < 3)&&!word2.contains("세트")) {
				if(!word2.equals("")&&(word.length-2 < 3)) {
					wording += word1+".*"+word[1]+".*";
				//} else if(!word2.equals("")&&word2.contains("세트")&&(word.length-1 < 4)||word2.equals("")&&(word.length-1) < 4||!word2.equals("")&&(word.length-2 < 4)) {
				} else if((!word2.equals("")||word2.equals(""))&&(word.length-1) < 5||(!word2.equals("")&&(word.length-2 < 5))) {
					wording += word[1]+".*";
				}
				
				for(int i = 2; i < word.length-1; i++ ) {
					Matcher m = p1.matcher(word[i]);
					Matcher m2 = p2.matcher(word[i]);
					
					if(((!word2.equals("")&&(word.length-2)==i))||word2.equals("")&&m2.find()) { 
						//wording += word[i].replaceAll("[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]", "");
						wording += word[i];
					} else if(m.find()) {
						//wording += word[i].replaceAll("[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]", "")+"\\s*";
						wording += word[i]+"\\s*";
					} else {
						//if(!word[i].replaceAll("[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]", "").equals("")) wording += word[i].replaceAll("[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]", "")+".*";
						if(!word[i].equals("")) wording += word[i]+".*";
					}
					
				}
				
			} else {
				// ex) 브랜드B 상품명 100ml, 브랜드B 상품명 세트, 브랜드C 상품명 15ml
				// ex) 브랜드D 상품명A 상품명B 상품명C 115ml
				//if(!word2.equals("")&&word2.contains("세트")&&word.length-1 < 3||word2.equals("")&&word.length-1 < 3||!word2.equals("")&&(word.length-1 < 4)) wording += word1+".*";
				//if(!word2.equals("")&&word.length-1 < 3||word2.equals("")&&word.length-1 < 3||!word2.equals("")&&(word.length-1 < 4)) wording += word1+".*";
				if(!word2.equals("")&&word.length-1 < 4||word2.equals("")&&word.length-1 < 4||(!word2.equals("")&&(word.length-1 < 4))) wording += word1+".*";

				for(int i = 1; i < word.length-1; i++ ) {
					Matcher m = p1.matcher(word[i]);
					Matcher m2 = p2.matcher(word[i]);
					//if(id == 3174) { System.out.println(id+" : "+word[i]+" | word2 : "+word2+" | word.length : "+word.length+" | i : "+i); }
					//if((!word2.equals("")||word2.equals("세트"))&&((word.length-2)==i)) {
					//if((!word2.equals("")&&(word[word.length-1].equals("세트"))||((!word2.equals("")&&(word.length-2)==i)))) {
					//if((word2.equals("")&&((word[word.length-1].equals("세트"))||(word.length-2)==i))) {
					if(((!word2.equals("")&&(word.length-2)==i))||word2.equals("")&&m2.find()) {
						//wording += word[i].replaceAll("[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]", "");
						
						wording += word[i];
					} else if(m.find()) {
						//wording += word[i].replaceAll("[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]", "")+"\\s*";
						wording += word[i]+"\\s*";
					} else {
						//if(!word[i].replaceAll("[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]", "").equals("")) { wording += word[i].replaceAll("[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]", "")+".*"; }
						// ex) 브랜드D 상품명A 상품명B 상품명C 115ml
						if(!word[i].equals("")) wording += word[i]+".*";
						//if(!word[i].equals("")&&!word2.equals("")) wording += word[i]+".*";
					}
				}
			}
			/*
			if(word2.equals("")) { // "" 이라면 용량이 아니라는 의미
				String set = word[word.length-2];
				Matcher m = p2.matcher(set);
				if(!m.find()) wording += "";
				//if(m.find()) wording += word[word.length-1];
				//else if(word.length == 2) wording += word[word.length-1]; //상품길이가 2 라면 세트를 붙임
				//else wording += ".*"+word[word.length-1];
			}
			*/
			map.put("word1", wording.trim());
			map.put("word2", word2.trim());
			
		}
		
		Map<String, Object> map = new HashMap<>();
		
		int listcount = wordService.cntMapGoodsListZero();
		int onepagelimit = 10000; 
		int pagetotal = (int)Math.ceil(listcount/(float)onepagelimit);
		int bottomlimit = 10;
		int currentpage = (int)(Math.ceil((pstartno+1)/(float)onepagelimit));
		int startpage = ((int)(Math.floor((currentpage-1)/(float)bottomlimit)))*bottomlimit+1;
		int endpage = startpage+bottomlimit-1;
		
		if(pagetotal < endpage) { endpage = pagetotal; }
		map.put("listcount", listcount);
		map.put("onepagelimit", onepagelimit);
		map.put("pagetotal", pagetotal);
		map.put("bottomlimit", bottomlimit);
		map.put("pstartno", pstartno);
		map.put("currentpage", currentpage);
		map.put("startpage", startpage);
		map.put("endpage", endpage);
		
		list.add(map);
		
		return list;
	}
	
	@RequestMapping(value="/list2", method=RequestMethod.GET, produces="application/json;charset=utf-8")
	@ResponseBody
	public List<Map<String, Object>> list2(HttpServletRequest request, HttpServletResponse response, @RequestParam("pstartno") int pstartno) throws Exception {
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html; charset=utf-8");
		
		List<Map<String, Object>> list = wordService.getMapMasterGoodsWord(pstartno);

		Map<String, Object> map = new HashMap<>();
		
		int listcount = wordService.cntMapGoodsList();
		int onepagelimit = 10000; 
		int pagetotal = (int)Math.ceil(listcount/(float)onepagelimit);
		int bottomlimit = 10;
		int currentpage = (int)(Math.ceil((pstartno+1)/(float)onepagelimit));
		int startpage = ((int)(Math.floor((currentpage-1)/(float)bottomlimit)))*bottomlimit+1;
		int endpage = startpage+bottomlimit-1;
		
		if(pagetotal < endpage) { endpage = pagetotal; }
		map.put("listcount", listcount);
		map.put("onepagelimit", onepagelimit);
		map.put("pagetotal", pagetotal);
		map.put("bottomlimit", bottomlimit);
		map.put("pstartno", pstartno);
		map.put("currentpage", currentpage);
		map.put("startpage", startpage);
		map.put("endpage", endpage);
		
		list.add(map);
		
		return list;
	}
	
	@RequestMapping(value="/wording", method=RequestMethod.GET)
	@ResponseBody
	public String wording(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html; charset=utf-8");
		
		boolean brandYn = Boolean.valueOf(request.getParameter("brandYn")); 
		boolean goodsYn = Boolean.valueOf(request.getParameter("goodsYn"));
		
		String goodsName = (request.getParameter("goodsName")+"").trim();
		//long id = Long.parseLong(request.getParameter("id")+"");

		String regx1 = "(크림)"; //보통명사 ex) 크림스킨, 2종, 3종
		String regx2 = "(^[0-9]*\\s*종)";
		Pattern p1 = Pattern.compile(regx1);
		Pattern p2 = Pattern.compile(regx2);
		
		String [] word = (!goodsName.contains(" "))? goodsName.split(",") : goodsName.split(" ");  
		String word1 = word[0]; // 상품명의 첫 글자는 브랜드명이라는 가정
		String word2 = word[word.length-1].replaceAll("[^0-9||^.]", ""); // 개수, 용량은 마지막에 표시할거라는 가정
		
		String wording = "";
		
		if(brandYn) {
			if(goodsYn&&word1.contains("이커머스")) {
				wording += word[1]+".*";
			} else if(goodsYn) {
				wording += word1+".*";
			} else {
				wording += word1; 
			}
		}
		
		if(goodsYn) {
			if(word1.contains("이커머스")) {
				// ex) 이커머스 상품명 180ml - 그룹사명이 앞에 붙는 경우
				for(int i = 2; i < word.length-1; i++ ) {
					Matcher m = p1.matcher(word[i]);
					Matcher m2 = p2.matcher(word[i]);
					
					if((!word2.equals("")&&((word.length-2)==i))||m2.find()) { 
						//wording += word[i].replaceAll("[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]", "");
						wording += word[i];
					} else if(m.find()) {
						//wording += word[i].replaceAll("[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]", "")+"\\s*";
						wording += word[i]+"\\s*";
					} else {
						//wording += word[i].replaceAll("[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]", "")+".*";
						if(!word[i].equals("")) wording += word[i]+".*";
					}
				}
			} else {
				for(int i = 1; i < word.length-1; i++ ) {
					Matcher m = p1.matcher(word[i]);
					Matcher m2 = p2.matcher(word[i]);
					if((!word2.equals("")&&(word.length-3)==i)||m2.find()) { 
						//wording += word[i].replaceAll("[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]", "");
						wording += word[i];
					} else if(m.find()) {
						//wording += word[i].replaceAll("[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]", "")+"\\s*";
						wording += word[i]+"\\s*";
					} else {
						//wording += word[i].replaceAll("[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]", "")+".*";
						if(!word[i].equals("")) wording += word[i]+".*";
					}
				}
			}
			/*
			//if(word2.equals("")) { wording += ".*"+word[word.length-1]; }
			if(word2.equals("")) { // 용량이 아니라는 의미
				String set = word[word.length-2];
				Matcher m = p2.matcher(set);
				//if(m.find()) wording += "\\s*"+word[word.length-1];
				if(!m.find()) wording += "";
			}
			*/
		}
		
		return wording;
	}
	
	@RequestMapping(value="/insertWording", method=RequestMethod.GET, produces="application/json;charset=utf-8")
	@ResponseBody
	public String insertWording(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html; charset=utf-8");
		
		Map<String, Object> map = new HashMap<>();
		
		long id = Long.parseLong(request.getParameter("id")+"");
		String word1 = request.getParameter("word1")+""; // 개수, 용량은 마지막에 표시할거라는 가정
		String word2 = request.getParameter("word2")+"";
		
		map.put("masterGoodsId", id);
		map.put("word1", word1.trim());
		map.put("word2", word2.trim());
		
		wordService.insertMapMasterGoodsMappingWord(map);
		
		return "정제어 추가 완료";
	}
	
	@RequestMapping(value="/allInsertWording", method=RequestMethod.POST, produces="application/json;charset=utf-8")
	public void allInsertWording(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html; charset=utf-8");
		
		int startNo = Integer.parseInt(request.getParameter("startNo")+"");
		int endNo = Integer.parseInt(request.getParameter("endNo")+"");
		
		for(int i = startNo; i <= endNo; i++) {
			Map<String, Object> map = new HashMap<>();

			String word1 = request.getParameter("wording"+i);
			String word2 = request.getParameter("word"+i);
			
			int cnt = wordService.getMapMasterGoodsMappingWord(i);
			if(cnt != 1) {
				map.put("masterGoodsId", i);
				map.put("word1", word1.trim());
				map.put("word2", word2.trim());
				
				wordService.insertMapMasterGoodsMappingWord(map);
				wordService.updateMapGoods(i);
			}
		}
		
		response.sendRedirect("../wording/goods-wording-insert");
	}
	
	@RequestMapping(value="/updateWording", method=RequestMethod.GET, produces="application/json;charset=utf-8")
	@ResponseBody
	public String updateWording(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html; charset=utf-8");
		
		Map<String, Object> map = new HashMap<>();
		
		long id = Long.parseLong(request.getParameter("id")+"");
		String word1 = request.getParameter("word1")+""; // 개수, 용량은 마지막에 표시할거라는 가정
		String word2 = request.getParameter("word2")+"";
		
		map.put("id", id);
		map.put("word1", word1.trim());
		map.put("word2", word2.trim());
		
		wordService.updateMapMasterGoodsWord(map);
		
		return "정제어 수정 완료";
	}
	
	@RequestMapping(value="/allUpdateWording", method=RequestMethod.POST, produces="application/json;charset=utf-8")
	public void allUpdateWording(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html; charset=utf-8");
		
		int startNo = Integer.parseInt(request.getParameter("startNo")+"");
		int endNo = Integer.parseInt(request.getParameter("endNo")+"");

		for(int i = startNo; i <= endNo; i++) {
			Map<String, Object> map = new HashMap<>();

			String word1 = request.getParameter("wording"+i);
			String word2 = request.getParameter("word"+i);
			
			int cnt = wordService.getMapMasterGoodsMappingWord(i);
			if(cnt == 1) {
				map.put("id", i);
				map.put("word1", word1.trim());
				map.put("word2", word2.trim());
				
				wordService.updateMapMasterGoodsWord(map);
			}
		}
		response.sendRedirect("../wording/goods-wording-update");
	}
	
}
