package org.yxh.news.domain;

import java.sql.Date;

import org.yxh.news.util.NewsUtil;

public class Page {
	private String pmd5;
	private String ptitle;
	private String purl;
	private String keyword;
	private String site_name;
	private String site_url;
	private String type;
	private Date  crawl_time;
	private Date publish_time;
	
	public Date getCrawl_time() {
		return crawl_time;
	}
	public void setCrawl_time(Date crawl_time) {
		this.crawl_time = crawl_time;
	}
	public Date getPublish_time() {
		return publish_time;
	}
	public void setPublish_time(Date publish_time) {
		this.publish_time = publish_time;
	}
	public String getPmd5() {
		return pmd5;
	}
	public void setPmd5(String pmd5) {
		this.pmd5 = pmd5;
	}
	public String getPtitle() {
		return ptitle;
	}
	public void setPtitle(String ptitle) {
		this.ptitle = ptitle;
	}
	public String getPurl() {
		return purl;
	}
	public void setPurl(String purl) {
		
		this.purl = purl;
		
		this.pmd5 = NewsUtil.getMD5FromString(purl);
	}
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public String getSite_name() {
		return site_name;
	}
	public void setSite_name(String site_name) {
		this.site_name = site_name;
	}
	public String getSite_url() {
		return site_url;
	}
	public void setSite_url(String site_url) {
		this.site_url = site_url;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
}
