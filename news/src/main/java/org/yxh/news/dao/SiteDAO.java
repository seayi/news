package org.yxh.news.dao;

import java.util.List;

import org.yxh.news.domain.Site;

public interface SiteDAO {
	
	public int addSite(Site site);

	public List<Site> getAllSites();
}
