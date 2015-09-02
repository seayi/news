package org.yxh.news.website;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.htmlparser.Parser;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.yxh.news.domain.Keyword;
import org.yxh.news.domain.Page;
import org.yxh.news.domain.Site;
import org.yxh.news.util.NewsUtil;

public class PageParser implements IParser {

	private static final Logger logger = Logger.getLogger(PageParser.class);

	@Override
	public List<Page> parsePages(String body, Site site, List<Keyword> keyword) {
		List<Page> pages = new ArrayList<Page>();
		try {
			if (body != null) {
				Parser parser = new Parser();
				parser.setInputHTML(body);
				TagNameFilter link_tag = new TagNameFilter("a");
				NodeList link_nodes = parser.extractAllNodesThatMatch(link_tag);

				for (int i = 0; i < link_nodes.size(); i++) {
					LinkTag link = (LinkTag) link_nodes.elementAt(i);
					if (link.getLinkText().length() > 15) {
						for (Keyword kw : keyword) {
							if (link.getLinkText().contains(kw.getKeyword())&&(kw.getType().equals("all")||kw.getType().equals(site.getType()))) {
								Page page = new Page();
								page.setPtitle(link.getLinkText());
								String alink = link.extractLink();
								if (!alink.startsWith("http:")) {
									alink = site.getUrl() + "/" + alink;
								}
								page.setPurl(alink);
								page.setPmd5(NewsUtil.getMD5FromString(alink));
								page.setKeyword(kw.getKeyword());
								page.setSite_name(site.getName());
								page.setSite_url(site.getUrl());
								page.setCrawl_time(new Date(System
										.currentTimeMillis()));
								page.setPublish_time(new Date(System
										.currentTimeMillis()));
								page.setType(site.getType());
								pages.add(page);
							}
						}
					}
				}

			}
			return pages;
		} catch (ParserException e) {
			logger.error(e.getMessage(), e);
			return pages;
		}

	}

}
