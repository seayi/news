<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" isELIgnored="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %> 
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>舆情监控</title>
</head>
<body>
<form action="search" method="get">
 <label for="kw">KeyWord:</label><input type="text" name="kw" value="${kw}"/>
 <select name="type">
  <option value="all" selected="selected">所有新闻</option>
  <option value="gov">政府新闻</option>
  <option value="company">企业新闻</option>
 </select>
 <input type="submit" value="Go!"/>
</form><a href="search?type=gov">政府新闻</a>|<a href="search?type=company">企业新闻</a>|<a href="search?type=all">所有新闻</a>
<hr width="100%"/>
<c:forEach var="page" items="${pages}">
  关键词:<a href="search?word=${page.keyword}">${page.keyword}</a>||<a href="${page.purl}" target="_blank"> <span>${page.ptitle}</span></a>&nbsp;&nbsp;&nbsp;(<fmt:formatDate value="${page.crawl_time}" pattern="yyyy-MM-dd HH:mm:ss"/> ,${page.site_name})<br/>
</c:forEach>
</body>
</html>