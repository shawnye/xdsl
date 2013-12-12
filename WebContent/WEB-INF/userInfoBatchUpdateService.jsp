<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="tags" tagdir="/WEB-INF/tags"%>   
<c:set var="ctx" value="${ pageContext.request.contextPath }"/>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>导入停复机文件</title>
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
	
</style>
<script type="text/javascript">
	$(function(){
		new LiveClock('liveClock', true, true);
	});
	function isReady(form){
		form.importBtn.disabled = true;

		return true;
	}
</script>
</head>
<body>
<tags:header></tags:header>
<form action="${ctx }/UserInfo/BatchUpdateService" method="post" accept-charset="utf-8" enctype="multipart/form-data" onsubmit="return isReady(this);">
	<b>停复机文件(csv格式)</b>：
	<input type="file" name="file" size="50">
	<input type="submit" value="上传更新" id="importBtn">
</form>

<div >
请确保使用<b>csv</b>文件，其
格式说明：第一行为标题，标题名随意，注意<b>顺序</b>不能错。示例如下：
<table border="1"
	style="border: 1 solid black; border-collapse: collapse">
	<tr>
		<td>停复机状态</td>
		<td>产品号码</td>
		<td>停复机日期（可选）</td>
	</tr>
	<tr>
		<td style="color: red;">停机</td>
		<td>002011200998457</td>
		<td>2009-12-2</td>
	</tr>
	<tr>
		<td style="color: red;">复机</td>
		<td>002011201388231</td>
		<td>&nbsp;</td>
	</tr>
	<tr>
		<td style="color: red;">待停机</td>
		<td>002011201201397</td>
		<td>&nbsp;</td>
	</tr>
</table>
	导入时间与机器性能和导入数量有关，请耐心等待。
</div>
<tags:footer></tags:footer>
</body>
</html>