package org.yxh.news.domain;

public class Keyword {
	private String keyword;
	private String type;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	@Override
	public boolean equals(Object obj) {
		Keyword obj_k = (Keyword) obj;
		if (obj_k.keyword.equals(keyword) && obj_k.type.equals(type))
			return true;
		else
			return false;
	}
}
