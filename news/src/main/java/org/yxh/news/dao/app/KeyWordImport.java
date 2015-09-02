package org.yxh.news.dao.app;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashSet;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.yxh.news.dao.KeywordDAO;
import org.yxh.news.dao.SiteDAO;
import org.yxh.news.domain.Keyword;

public class KeyWordImport {

	public static void main(String[] args) throws Exception {
		ApplicationContext context = new FileSystemXmlApplicationContext("src/main/webapp/WEB-INF/news-servlet.xml");
		KeywordDAO kDao = (KeywordDAO) context.getBean("kwDAO");
		HashSet<Keyword> keywords = new HashSet<Keyword>();
		InputStream is = new FileInputStream(args[0]);
		Workbook readwb = WorkbookFactory.create(is);
		Sheet sheet = readwb.getSheetAt(0);
		int rsRows = sheet.getLastRowNum();
		for (int i = 1; i < rsRows; i++) {
			String cell = sheet.getRow(i).getCell(0).getStringCellValue();
			if (cell.trim().length() > 0){
				Keyword kw = new Keyword();
				kw.setType("company");
				kw.setKeyword(cell.trim());
				keywords.add(kw);
			}
		}
		
		System.out.println(kDao.addAllKeyWords(keywords));
	}

}
