package org.yxh.news.dao.app;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.yxh.news.dao.KeywordDAO;
import org.yxh.news.dao.PageDAO;
import org.yxh.news.dao.SiteDAO;
import org.yxh.news.domain.Keyword;
import org.yxh.news.domain.Page;
import org.yxh.news.domain.Site;
import org.yxh.news.website.ICrawler;
import org.yxh.news.website.IParser;

public class WebSiteParser {
	
	private static final Logger logger = Logger.getLogger(WebSiteParser.class);
	
	private KeywordDAO keywordDao;
	
	private PageDAO pageDao;
	
	private SiteDAO siteDao;
	
	private IParser pp;
	
	private ICrawler crawler;
	
	private List<Keyword> keywords;
	
	private List<Site> allSites;
	
	public WebSiteParser(KeywordDAO keywordDao,PageDAO pageDao,SiteDAO siteDao,IParser parser,ICrawler crawler) {
		super();
		this.keywordDao = keywordDao;
		this.pageDao = pageDao;
		this.siteDao = siteDao;
		this.pp = parser;
		this.crawler = crawler;
	}
	
	
	public void storeRelatedPages(){
		this.keywords = this.keywordDao.getAllKeyWords();
		this.allSites = this.siteDao.getAllSites();
		for (Site site : allSites) {
			logger.info(site);
			String body = crawler.crawlerContent(site);
			List<Page> pages = pp.parsePages(body, site, keywords);
			this.pageDao.addAllPagesWithCheck(pages);
		}
	}
	
	
	
	
	
	public static void main(String[] args) {
		
		ApplicationContext context = new FileSystemXmlApplicationContext("src/main/webapp/WEB-INF/news-servlet.xml");
		WebSiteParser  wsp = context.getBean("websiteParser", WebSiteParser.class);
		wsp.storeRelatedPages();
	}

}
