package org.yxh.news.website;

import java.util.List;

import org.yxh.news.domain.Keyword;
import org.yxh.news.domain.Page;
import org.yxh.news.domain.Site;

public interface IParser {
   public List<Page>  parsePages(String body,Site site,List<Keyword> keyword); 
}
