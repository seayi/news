package org.yxh.news.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletConfig;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.ServletConfigAware;
import org.yxh.news.dao.PageDAO;
import org.yxh.news.dao.SiteDAO;
import org.yxh.news.dao.app.WebSiteParser;
import org.yxh.news.domain.Page;
import org.yxh.news.domain.Site;
import org.yxh.news.util.NewsUtil;

@Controller
@RequestMapping("/")
public class NewsController implements ServletConfigAware {
	private static final Logger logger = Logger.getLogger(NewsController.class);
	private ServletConfig sc;
	private SiteDAO site_dao;
	private PageDAO page_dao;
	private WebSiteParser parser;

	@Autowired
	public NewsController(SiteDAO site_dao, PageDAO page_dao,
			WebSiteParser parser) {
		this.site_dao = site_dao;
		this.page_dao = page_dao;
		this.parser = parser;
	}

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String index() {
		return "index";
	}

	@RequestMapping(value = "/site", method = RequestMethod.GET)
	public String site(Model model) {
		List<Site> sites = site_dao.getAllSites();
		model.addAttribute("sites", sites);
		return "site";
	}

	@RequestMapping(value = "/search", method = RequestMethod.GET)
	public String search(Model model,
			@RequestParam(value = "kw", required = false) String kw,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "word", required = false) String word) {
		List<Page> pages = new ArrayList<Page>();
		if (NewsUtil.isNotEmpty(kw) && NewsUtil.isNotEmpty(type)) {
			pages = this.page_dao.searchPagesByKW(kw, type);
			model.addAttribute("kw", kw);
			if (type.equals("company"))
				model.addAttribute("type", "企业新闻");
			else if (type.equals("gov"))
				model.addAttribute("type", "政府新闻");
			else
				model.addAttribute("type", "所有新闻");
		} else if (NewsUtil.isNotEmpty(word)) {
			if (NewsUtil.isNotEmpty(type))
				pages = this.page_dao.getPagsByKeyWord(word, type);
			else
				pages = this.page_dao.getPagsByKeyWord(word, "all");
		} else if (NewsUtil.isNotEmpty(type)) {
			pages = this.page_dao.getAllPages(type);
		} else {
			pages = this.page_dao.getAllPages("all");
		}
		model.addAttribute("pages", pages);

		return "pages";
	}

	public SiteDAO getSite_dao() {
		return site_dao;
	}

	public void setSite_dao(SiteDAO site_dao) {
		this.site_dao = site_dao;
	}

	@Override
	public void setServletConfig(ServletConfig sc) {
		this.sc = sc;
	}

}
