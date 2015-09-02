/**
 * 
 */
package org.yxh.news;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.util.EntityUtils;
import org.htmlparser.Parser;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.yxh.news.data.SiteItem;
import org.yxh.news.util.WordSeg;

/**
 * @author Max
 * 
 */
public class WebSiteHtmlParser {
	private WordSeg word_seg;
	private List<SiteItem> site_items;
	private HashSet<String> keywords;

	/**
	 * 
	 */
	public WebSiteHtmlParser(List<SiteItem> site_items, HashSet<String> keywords) {
		this.site_items = site_items;
		this.keywords = keywords;
		this.word_seg = new WordSeg(keywords);
	}

	public List<SiteItem> getSite_items() {
		return site_items;
	}

	public void setSite_items(List<SiteItem> site_items) {
		this.site_items = site_items;
	}

	public HashSet<String> getKeywords() {
		return keywords;
	}

	public void setKeywords(HashSet<String> keywords) {
		this.keywords = keywords;
	}

	public HashMap<String, HashMap<String, List<String>>> parseAllWebSite()
			throws ParserException, ParseException, IOException {
		HashMap<String, HashMap<String, List<String>>> tags_map = new HashMap<String, HashMap<String, List<String>>>();
		TagNameFilter filter = new TagNameFilter("a");
		for (SiteItem site_item : site_items) {
			String content = this.crawlContent(site_item);
			if (content.trim().length() > 0) {
				Parser parser = new Parser(content);
				NodeList link_nodes = parser.extractAllNodesThatMatch(filter);
				for (int i = 0; i < link_nodes.size(); i++) {
					LinkTag link = (LinkTag) link_nodes.elementAt(i);
					if (link.getLinkText().length() > 8) {
						for (String word : keywords) {
							if (link.getLinkText().contains(word)) {
								if (tags_map.containsKey(word)) {
									HashMap<String, List<String>> site_tag_map = tags_map
											.get(word);
									if (site_tag_map.containsKey(site_item
											.getSite_address()))
										site_tag_map.get(
												site_item.getSite_address())
												.add(link.toHtml());
									else {
										List<String> tag_list = new ArrayList<String>();
										tag_list.add(link.toHtml());
										site_tag_map.put(
												site_item.getSite_address(),
												tag_list);
									}
								} else {
									HashMap<String, List<String>> site_tag_map = new HashMap<String, List<String>>();
									List<String> tag_list = new ArrayList<String>();
									tag_list.add(link.toHtml());
									site_tag_map.put(
											site_item.getSite_address(),
											tag_list);
									tags_map.put(word, site_tag_map);
								}
								break;
							}
						}
					}
				}
			}
		}

		return tags_map;

	}

	private String crawlContent(final SiteItem site_item) {
		HttpHost proxy = new HttpHost("10.37.84.124", 8080);
		DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(
				proxy);
		CloseableHttpClient httpclient = HttpClients.custom()
				.setRoutePlanner(routePlanner).build();
		HttpGet httpget = new HttpGet(site_item.getSite_address());
		try {
			String responseBody = httpclient.execute(httpget,
					new ResponseHandler<String>() {
						public String handleResponse(final HttpResponse response)
								throws IOException {
							int status = response.getStatusLine()
									.getStatusCode();
							if (status >= 200 && status < 300) {
								HttpEntity entity = response.getEntity();
								String content = entity != null ? EntityUtils
										.toString(entity,
												site_item.getSite_encoding())
										: "";
								return content;
							} else {
								System.out.print("Unexpected response status: "
										+ status);
								return "";
							}
						}

					});
			return responseBody;
		} catch (ClientProtocolException e) {
			System.out.println(e.getMessage());
			return null;
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return null;
		}
	}

	/**
	 * @param args
	 * @throws ParserException
	 * @throws IOException
	 * @throws ParseException
	 */
	public static void main(String[] args) throws ParserException,
			ParseException, IOException {

		SiteItem item = new SiteItem();
		item.setSite_address("http://finance.qq.com");
		item.setSite_encoding("GBK");
		List<SiteItem> site_items = new ArrayList<SiteItem>();
		site_items.add(item);
		HashSet<String> keywords = new HashSet<String>();
		keywords.add("");
		WebSiteHtmlParser html_parser = new WebSiteHtmlParser(site_items,
				keywords);
		System.out.println(html_parser.parseAllWebSite());
	}
}
