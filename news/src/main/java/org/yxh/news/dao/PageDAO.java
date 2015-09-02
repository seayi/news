package org.yxh.news.dao;

import java.util.List;

import org.yxh.news.domain.Page;

public interface PageDAO {
	
	public int[] addAllPages(List<Page> pages);
	
	public int addAllPagesWithCheck(List<Page> pages);
	
	public int addOnePage(Page page);
	
	public List<Page> searchPagesByKW(String kw,String type);
	
	public List<Page> getPagsByKeyWord(String kw,String type);
	
	public List<Page> getAllPages(String type);

}
