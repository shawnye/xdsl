<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<c:set var="ctx" value="${ pageContext.request.contextPath }"/>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>在线账户列表</title>
<tags:head></tags:head>
<style type="text/css">
	table td{
		padding:2px;
		text-align: left;
	}
	.data_table{
		border-collapse: collapse;
	}
	.data_table td{
		border : 1px solid gray;
		white-space: nowrap;
	}
	.data_table thead td{
		border : 1px solid gray;
		background-color: #C8D1D2;
		color: #000;
		font-weight: bold;
		text-align: center;
	}

	.paging{

	}
	.label{
		background-color: #C8D1D2;
		font-weight: bold;
		text-align: right;
	}

	.header{
		background-color: #C8D1D2;
		font-weight: bold;
		width: 100%;
		border-collapse: collapse;
	}
	a {
		text-decoration: none;
	}
	div{
		text-align:left;
	}
	
	ul{
		list-style-type: none;
		text-align:left;
	}

</style>
<script type="text/javascript">
 
</script>
</head>
<body>

<table border="1" class="data_table">
	<caption>在线账户列表</caption>
	<thead>
		<tr>
			<td>序号</td>
			<td>帐号</td>
			<td>名称(单位)</td>
			<td>IP</td>
			<td title="排序">登录时间</td> 
			<td>角色</td> 
			<td>操作级别</td> 
		</tr>
	</thead>
	<tbody>
	<c:forEach items="${loginUsers }" varStatus="s" var="u">
 		<tr>
 			<td>${ s.count }</td>
 			<td>${ u.account }</td>
			<td>${ u.branch }</td>
			<td>${ u.ip }</td>
			<td><fmt:formatDate value="${ u.loginTime }" pattern="yyyy-MM-dd HH:mm"/></td>
			<td>${ u.roleNames }</td>
			<td>${ u.levelName }</td>
		</tr>
	</c:forEach>	
	</tbody>
</table>		
	<a href="javascript:void(0);" onclick="window.location.reload();">刷新</a>
	<a href="javascript:void(0);" onclick="window.close();">关闭窗口</a>
</body>
</html>