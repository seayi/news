package org.yxh.news.dao;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.yxh.news.domain.Page;
import org.yxh.news.util.NewsUtil;

public class PageDAOImp implements PageDAO {

	private static final Logger logger = Logger.getLogger(PageDAOImp.class);
	private JdbcTemplate jdbc_template;

	public PageDAOImp(JdbcTemplate jdbc_template) {
		super();
		this.jdbc_template = jdbc_template;
	}

	@Override
	public int[] addAllPages(List<Page> pages) {
		SimpleJdbcInsert sji = new SimpleJdbcInsert(jdbc_template)
				.withTableName("pages");
		Map<String, Object>[] args_array = new HashMap[pages.size()];
		int count = 0;
		logger.debug("SIZE:" + pages.size());
		for (Page page : pages) {
			Map<String, Object> args = new HashMap<String, Object>();
			args.put("purl", page.getPurl());
			args.put("pmd5", page.getPmd5());
			args.put("ptitle", page.getPtitle());
			args.put("keyword", page.getKeyword());
			args.put("site_name", page.getSite_name());
			args.put("site_url", page.getSite_url());
			args.put("type", page.getType());
			args.put("crawl_time", page.getCrawl_time());
			args.put("publish_time", page.getPublish_time());
			args_array[count] = args;
			logger.debug(page.getPurl() + "||" + page.getPmd5());
			count++;
		}
		return sji.executeBatch(args_array);
	}

	@Override
	public int addOnePage(Page page) {
		SimpleJdbcInsert sji = new SimpleJdbcInsert(jdbc_template)
				.withTableName("pages");
		int count = jdbc_template.queryForInt(
				"select count(*) from pages where pmd5=?", page.getPmd5());
		if (count == 0) {
			Map<String, Object> args = new HashMap<String, Object>();
			args.put("purl", page.getPurl());
			args.put("pmd5", page.getPmd5());
			args.put("ptitle", page.getPtitle());
			args.put("keyword", page.getKeyword());
			args.put("site_name", page.getSite_name());
			args.put("site_url", page.getSite_url());
			args.put("type", page.getType());
			args.put("crawl_time", page.getCrawl_time());
			args.put("publish_time", page.getPublish_time());
			return sji.execute(args);
		} else
			return 0;
	}

	@Override
	public int addAllPagesWithCheck(List<Page> pages) {
		int count = 0;
		for (Page page : pages) {
			count += this.addOnePage(page);
		}
		return count;
	}

	@Override
	public List<Page> searchPagesByKW(String kw, String type) {
		if (type.equals("all")) {
			return this.jdbc_template.query(
					"select * from pages where ptitle like '%" + kw
							+ "%' order by crawl_time desc", new PagesMapper());
		} else {
			return this.jdbc_template.query(
					"select * from pages where ptitle like '%" + kw
							+ "%' and type='" + type
							+ "' order by crawl_time desc ", new PagesMapper());

		}
	}

	@Override
	public List<Page> getPagsByKeyWord(String kw, String type) {
		if (type.equals("all")) {
			return this.jdbc_template.query(
					"select * from pages where keyword='" + kw
							+ "' order by crawl_time desc ", new PagesMapper());
		} else {
			return this.jdbc_template.query(
					"select * from pages where keyword='" + kw + "' and type='"
							+ type + "' order by crawl_time desc ",
					new PagesMapper());

		}
	}

	private static final class PagesMapper implements RowMapper<Page> {

		@Override
		public Page mapRow(ResultSet rs, int rowNum) throws SQLException {
			Page page = new Page();
			page.setCrawl_time(new Date(rs.getTimestamp("crawl_time").getTime()));
			page.setKeyword(rs.getString("keyword"));
			page.setPmd5(rs.getString("pmd5"));
			page.setPtitle(rs.getString("ptitle"));
			page.setPublish_time(rs.getDate("publish_time"));
			page.setPurl(rs.getString("purl"));
			page.setSite_name(rs.getString("site_name"));
			page.setSite_url(rs.getString("site_url"));
			page.setType(rs.getString("type"));
			return page;
		}

	}

	@Override
	public List<Page> getAllPages(String type) {
		if (type.equals("all"))
			return this.jdbc_template.query(
					"select * from pages order by crawl_time desc limit 5000",
					new PagesMapper());
		else
			return this.jdbc_template.query(
					"select * from pages where type='"+type+"' order by crawl_time desc limit 5000",
					new PagesMapper());

	}

}
