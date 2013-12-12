<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="tags" tagdir="/WEB-INF/tags"%>   
<c:set var="ctx" value="${ pageContext.request.contextPath }"/>

<html>
<head>
<tags:head></tags:head>
<title>OSS与ADSL系统的机房名称对应表</title>
</head>
<body>
<div>
<div>来自 adsl系统 表[oss_jx_info]，可在此表中 增加或修改名称映射。</div>
<table border="1" style="border-collapse: collapse;">
	<thead style="background: #FFCC99;font-weight: bold;" >

					<tr>
						<td>序号</td>
						<td>OSS机房名称</td>
						<td>ADSL系统机房名称</td>
					</tr>
				</thead>
				<tbody>
				<c:forEach items="${ossjx}" var="m" varStatus="s">
					<tr
					<c:if test="${ s.count%2 == 0 }">style="background:#FFFF99" </c:if>
					>
						<td>${ s.count }</td>
						<td>${ m.oss_jx }</td>
						<td>${ m.jx }</td>
					</tr>
					</c:forEach>
				</tbody>
</table>

</div>
</body>
</html>