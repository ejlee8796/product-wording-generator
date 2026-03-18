package com.company.wording.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface WordMapper {

	//public List<Map<String, Object>> getMapGoodsList() throws Exception;
	public List<Map<String, Object>> getMapGoodsListZero(int pstartno) throws Exception;
	public void insertMapMasterGoodsMappingWord(Map<String, Object> map) throws Exception;
	public int cntMapGoodsListZero() throws Exception;
	public int cntMapGoodsList() throws Exception;
	public void insertMapMasterGoods(Map<String, Object> map) throws Exception;
	public int getMapMasterGoods(long id) throws Exception;
	public int getMapMasterGoodsMappingWord(long masterGoodsId) throws Exception;
	public void updateMapGoods(long id) throws Exception;
	//public List<Map<String, Object>> getMapGoodsList(int pstartno) throws Exception;
	public List<Map<String, Object>> getMapMasterGoodsWord(int pstartno) throws Exception;
	public void updateMapMasterGoodsWord(Map<String, Object> map) throws Exception;
	
}
