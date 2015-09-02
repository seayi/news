package org.yxh.news.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.yxh.news.domain.Keyword;

public class KeyWordDAOImp implements KeywordDAO {
	private JdbcTemplate jdbc_template;
	
	public KeyWordDAOImp(JdbcTemplate jdbc_template) {
		super();
		this.jdbc_template = jdbc_template;
	}

	@Override
	public int addKeyWord(Keyword keyword) {
		SimpleJdbcInsert sji = new SimpleJdbcInsert(jdbc_template).withTableName("keywords");
		Map<String, Object> args = new HashMap<String,Object>();
		args.put("keyword", keyword.getKeyword());
		args.put("type", keyword.getType());
		return sji.execute(args);
	}

	@Override
	public int[] addAllKeyWords(HashSet<Keyword> keywords) {
		SimpleJdbcInsert sji = new SimpleJdbcInsert(jdbc_template).withTableName("keywords");
		Map<String, Object>[] args_array = new HashMap[keywords.size()];
		int count = 0;
		for (Keyword kw : keywords) {
			Map<String, Object> args = new HashMap<String,Object>();
			args.put("keyword", kw.getKeyword());
			args.put("type", kw.getType());
			args_array[count] = args;
			count++;
		}
		return sji.executeBatch(args_array);
	}

	@Override
	public List<Keyword> getAllKeyWords() {
		 return this.jdbc_template.query("select * from keywords", new KeyWordMapper());
	}
    private static final class KeyWordMapper implements RowMapper<Keyword>{

		@Override
		public Keyword mapRow(ResultSet arg0, int arg1) throws SQLException {
			String word = arg0.getString("keyword");
			String type = arg0.getString("type");
			Keyword kw = new Keyword();
			kw.setKeyword(word);
			kw.setType(type);
			return kw;
		}
    	
    }
}
