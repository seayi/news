package org.yxh.news.website;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.yxh.news.domain.Site;

public interface ICrawler {
	
	public String crawlerContent(Site site);

}
