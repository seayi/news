package org.yxh.news.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

import org.apache.http.ParseException;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.ParserException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.ServletConfigAware;
import org.springframework.web.context.WebApplicationContext;
import org.yxh.news.WebSiteHtmlParser;
import org.yxh.news.data.SiteItem;

@Controller
@RequestMapping("/")
public class WebSiteController implements ServletConfigAware {
	private WebSiteHtmlParser parser;
	private List<SiteItem> site_items;
	private HashSet<String> keywords;
	private ServletConfig sc;

	public WebSiteController() {
		this.site_items = new ArrayList<SiteItem>();
		this.keywords = new HashSet<String>();
		this.parser = new WebSiteHtmlParser(this.site_items,
				this.keywords);
	}

	@RequestMapping(value = "/index2", method = RequestMethod.GET)
	public String index(Model model) {
		try {
				try {
					this.site_items.clear();
					this.keywords.clear();
					String web_site_file = (String) sc.getServletContext()
							.getInitParameter("web-site-file");
					String key_word_file = (String) sc.getServletContext()
							.getInitParameter("key-word-file");
					InputStream is = new FileInputStream(key_word_file);
					Workbook readwb = Workbook.getWorkbook(is);
					Sheet sheet = readwb.getSheet(0);
					int rsRows = sheet.getRows();
					for (int i = 1; i < rsRows; i++) {
						Cell cell = sheet.getCell(0, i);
						if (cell.getContents().trim().length() > 0)
							this.keywords.add(cell.getContents().trim());
					}
					InputStream wf_stream = new FileInputStream(web_site_file);
					Workbook web_wb = Workbook.getWorkbook(wf_stream);
					Sheet web_sheet = web_wb.getSheet(0);
					int webRows = web_sheet.getRows();
					for (int i = 1; i < webRows; i++) {
						Cell cell = web_sheet.getCell(1, i);
						if (cell.getContents().trim().length() > 0) {
							SiteItem si = new SiteItem();
							si.setSite_address(cell.getContents().trim());
							cell = web_sheet.getCell(0, i);
							si.setSite_name(cell.getContents().trim());
							cell = web_sheet.getCell(2, i);
							si.setSite_encoding(cell.getContents().trim());
							this.site_items.add(si);
						}
					}
					
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
			HashMap<String, HashMap<String, List<String>>> tags_map = this.parser
					.parseAllWebSite();
			model.addAttribute("tagsMap", tags_map);

		} catch (ParserException e) {
			System.out.println(e.getMessage());
		} catch (ParseException e) {
			System.out.println(e.getMessage());
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		return "index";
	}

	public void setServletConfig(ServletConfig sc) {
		this.sc = sc;
	}

}
