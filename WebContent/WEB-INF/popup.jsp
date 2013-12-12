<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${ pageContext.request.contextPath }"/>
<%@taglib prefix="tags" tagdir="/WEB-INF/tags"%> 
<html>
<head>
<tags:head></tags:head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>执行结果</title>

</head>
<body>
<p>&nbsp;</p>
<p>&nbsp;</p>
<div>
	
	<span>
	<textarea rows="10" cols="70">${ popMsg }</textarea>
	</span>
</div>
<div><a href="${ctx }/Main" >[首页]</a>&nbsp;&nbsp;<a href="javascript:history.back();" title="有时未必可用，若无效请点击浏览器上的后退按钮试试">[后退]</a>&nbsp;&nbsp;<a href="javascript:void(0);" onclick="window.close();">[关闭]</a></div>
</body>
</html>