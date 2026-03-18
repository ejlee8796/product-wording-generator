package com.company.wording.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.company.wording.mapper.WordMapper;
import com.company.wording.service.WordService;

@Service(value="wordService")
public class WordServiceImpl implements WordService {

	@Resource
	private WordMapper wordMapper;
	
	@Override
	public List<Map<String, Object>> getMapGoodsListZero(int pstartno) throws Exception {
		return wordMapper.getMapGoodsListZero(pstartno);
	}
	
	@Override
	public void insertMapMasterGoodsMappingWord(Map<String, Object> map) throws Exception {
		wordMapper.insertMapMasterGoodsMappingWord(map);
	}

	@Override
	public int cntMapGoodsList() throws Exception {
		return wordMapper.cntMapGoodsList();
	}

	@Override
	public void insertMapMasterGoods(Map<String, Object> map) throws Exception {
		wordMapper.insertMapMasterGoods(map);
	}

	@Override
	public int getMapMasterGoods(long id) throws Exception {
		return wordMapper.getMapMasterGoods(id);
	}

	@Override
	public int getMapMasterGoodsMappingWord(long masterGoodsId) throws Exception {
		return wordMapper.getMapMasterGoodsMappingWord(masterGoodsId);
	}

	@Override
	public void updateMapGoods(long id) throws Exception {
		wordMapper.updateMapGoods(id);
	}

	@Override
	public void updateMapMasterGoodsWord(Map<String, Object> map) throws Exception {
		wordMapper.updateMapMasterGoodsWord(map);
	}

	@Override
	public List<Map<String, Object>> getMapMasterGoodsWord(int pstartno) throws Exception {
		return wordMapper.getMapMasterGoodsWord(pstartno);
	}

	@Override
	public int cntMapGoodsListZero() throws Exception {
		return wordMapper.cntMapGoodsListZero();
	}

}
