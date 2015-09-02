package org.yxh.news.dao.app;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.htmlparser.Parser;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.tags.MetaTag;
import org.htmlparser.util.NodeList;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.yxh.news.dao.SiteDAO;
import org.yxh.news.domain.Site;
import org.yxh.news.util.CharsetDetector;

public class SitesImport {
	private static final Logger logger = Logger.getLogger(SitesImport.class);

	public static void main(String[] args) throws Exception {

		ApplicationContext context = new FileSystemXmlApplicationContext(
				"src/main/webapp/WEB-INF/news-servlet.xml");
		SiteDAO site_dao = context.getBean("siteDAO", SiteDAO.class);
		InputStream wf_stream = new FileInputStream(
				"/Users/yixiaohua/Documents/workspace-pawall/news/src/main/resources/网站列表20141229.xlsx");
		Workbook site_book = WorkbookFactory.create(wf_stream);
		for (int m = 0; m < 2; m++) {
			Sheet web_sheet = site_book.getSheetAt(m);
			int webRows = web_sheet.getLastRowNum();
			for (int i = 1; i < webRows; i++) {
				Row row = web_sheet.getRow(i);
				Cell cell = row.getCell(1);
				if (cell.getStringCellValue().trim().length() > 0) {

					final Site site = new Site();
					site.setUrl(cell.getStringCellValue().trim());
					cell = row.getCell(0);
					site.setName(cell.getStringCellValue().trim());
					CloseableHttpClient client = HttpClients.createDefault();
					HttpGet get = new HttpGet(site.getUrl());
					RequestConfig rc = RequestConfig.custom()
							.setSocketTimeout(2000).setConnectTimeout(2000)
							.build();
					get.setConfig(rc);
					try {
						String body = client.execute(get,
								new ResponseHandler<String>() {

									@Override
									public String handleResponse(
											HttpResponse response)
											throws ClientProtocolException,
											IOException {
										int status = response.getStatusLine()
												.getStatusCode();
										if (status >= 200 && status < 300) {
											HttpEntity entity = response
													.getEntity();
											String cs[] = CharsetDetector
													.getInstance()
													.detectAllCharset(
															entity.getContent());
											return cs[0];
										} else {
											logger.error("Unexpected response status: "
													+ status);
											return null;
										}
									}
								});
						logger.info(body + "||" + site);
						if (body != null){
							site.setEncoding(body);
							site.setStatus("ok");
						}else{
							site.setEncoding("UTF-8");
							site.setStatus("no");
						}
						
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
						site.setEncoding("UTF-8");
						site.setStatus("no");
					}
					if (m == 0)
						site.setType("company");
					else
						site.setType("gov");
					site_dao.addSite(site);
				}
			}
			// Workbook web_wb = Workbook.getWorkbook(wf_stream);
			// Sheet web_sheet = web_wb.getSheet(0);
			// int webRows = web_sheet.getRows();
			// for (int i = 1; i < webRows; i++) {
			// Cell cell = web_sheet.getCell(1, i);
			// if (cell.getContents().trim().length() > 0) {
			// try{
			// final Site site = new Site();
			// site.setUrl(cell.getContents().trim());
			// cell = web_sheet.getCell(0, i);
			// site.setName(cell.getContents().trim());
			// CloseableHttpClient client = HttpClients.createDefault();
			// HttpGet get = new HttpGet(site.getUrl());
			// RequestConfig rc =
			// RequestConfig.custom().setSocketTimeout(2000).setConnectTimeout(2000).build();
			// get.setConfig(rc);
			// String body = client.execute(get,new ResponseHandler<String>() {
			//
			// @Override
			// public String handleResponse(HttpResponse response)
			// throws ClientProtocolException, IOException {
			// int status = response.getStatusLine()
			// .getStatusCode();
			// if (status >= 200 && status < 300) {
			// HttpEntity entity = response.getEntity();
			// String cs[] =
			// CharsetDetector.getInstance().detectAllCharset(entity.getContent());
			// return cs[0];
			// } else {
			// logger.error("Unexpected response status: "
			// + status);
			// return null;
			// }
			// }
			// });
			// logger.info(body+"||"+site);
			// site.setEncoding(body);
			// site.setType("company");
			// site_dao.addSite(site);
			// }catch(Exception e){
			// logger.error(e.getMessage(), e);
			// }
			// }
			// }

		}

	}
}
