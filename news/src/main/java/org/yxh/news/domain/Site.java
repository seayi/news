package org.yxh.news.domain;

import org.yxh.news.util.NewsUtil;

public class Site {

	private String name;
	private String url;
	private String encoding;
	private String type;
	private String smd5;
	private String status;
	
	

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getSmd5() {
		return smd5;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.smd5 = NewsUtil.getMD5FromString(url);
		this.url = url;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Name:"+name+";URL:"+url;
	}

}
