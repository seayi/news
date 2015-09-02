package org.yxh.news.dao;

import java.util.HashSet;
import java.util.List;

import org.yxh.news.domain.Keyword;

public interface KeywordDAO {
	
	public int addKeyWord(Keyword keyword);
	public int[] addAllKeyWords(HashSet<Keyword> keywords);
	public List<Keyword> getAllKeyWords();

}
