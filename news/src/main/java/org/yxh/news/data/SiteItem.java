/**
 * 
 */
package org.yxh.news.data;

/**
 * @author Max
 *
 */
public class SiteItem {

	private String site_name;
	private String site_address;

	private String site_encoding;

	public SiteItem(String site_name, String site_address, String site_encoding) {
		super();
		this.site_name = site_name;
		this.site_address = site_address;
		this.site_encoding = site_encoding;
	}

	public String getSite_encoding() {
		return site_encoding;
	}

	public void setSite_encoding(String site_encoding) {
		this.site_encoding = site_encoding;
	}

	public String getSite_name() {
		return site_name;
	}

	public void setSite_name(String site_name) {
		this.site_name = site_name;
	}

	public String getSite_address() {
		return site_address;
	}

	public void setSite_address(String site_address) {
		this.site_address = site_address;
	}

	/**
	 * 
	 */
	public SiteItem() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
