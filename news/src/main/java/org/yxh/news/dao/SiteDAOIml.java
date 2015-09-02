package org.yxh.news.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.yxh.news.domain.Site;
import org.yxh.news.util.NewsUtil;

public class SiteDAOIml implements SiteDAO {

	private JdbcTemplate jdbc_template;

	public SiteDAOIml(JdbcTemplate jdbc_template) {
		super();
		this.jdbc_template = jdbc_template;
	}

	@Override
	public int addSite(Site site) {
		int count = jdbc_template.queryForInt(
				"select count(*) from sites where url_md5=?", site.getSmd5());
		if (count == 0) {
			SimpleJdbcInsert insert = new SimpleJdbcInsert(this.jdbc_template)
					.withTableName("sites");
			Map<String, Object> args = new HashMap<String, Object>();
			args.put("name", site.getName());
			args.put("url", site.getUrl());
			args.put("encoding", site.getEncoding());
			args.put("type", site.getType());
			args.put("url_md5", site.getSmd5());
			args.put("status", site.getStatus());
			return insert.execute(args);
		}else{
			return jdbc_template.update("update sites set name=?,url=?,encoding=?,type=?,status=? where url_md5=?", site.getName(),site.getUrl(),site.getEncoding(),site.getType(),site.getStatus(),site.getSmd5());
		}
	}

	@Override
	public List<Site> getAllSites() {
		return this.jdbc_template.query("select * from sites where status='ok'",
				new SiteRowMapper());
	}

	private static final class SiteRowMapper implements RowMapper<Site> {

		@Override
		public Site mapRow(ResultSet rs, int index) throws SQLException {
			Site site = new Site();
			String name = rs.getString("name");
			String url = rs.getString("url");
			String encoding = rs.getString("encoding");
			String type = rs.getString("type");
			String status = rs.getString("status");
			site.setName(name);
			site.setUrl(url);
			site.setEncoding(encoding);
			site.setType(type);
			site.setStatus(status);
			return site;
		}

	}

}
