<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="tags" tagdir="/WEB-INF/tags"%>   
<c:set var="ctx" value="${ pageContext.request.contextPath }"/>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>导入用户状态文件</title>
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
<form action="${ctx }/UserInfo/UserStateBatchUpdate" method="post" accept-charset="utf-8" enctype="multipart/form-data" onsubmit="return isReady(this);">
	<input type="hidden" name="fieldName" value="${ fieldName }">
	<input type="hidden" name="fieldDefaultValue" value="${ fieldDefaultValue }"><%--限定默认值 --%>
	<b>状态文件(csv格式)</b>：
	<input type="file" name="pidsFile" size="50">
	<input type="submit" value="上传更新" id="importBtn"
	<c:if test="${ sessionScope.accountInfo.level > 2 }">disabled="disabled"</c:if>
	>
</form>

<div >
请确保使用<b>csv</b>文件，其
格式说明：第一行为标题，标题名随意，注意<b>顺序</b>不能错。示例如下：
<table border="1"
	style="border: 1 solid black; border-collapse: collapse">
	<tr>
		
		<td>产品号码</td>
 		<td>状态</td>
	</tr>
	<tr>
		
		<td>002011200998457</td>
		<td style="color: red;">预拆机</td>
	</tr>
	<tr>
		
		<td title="产品号可以带有单引号（'）"><span style="color:red;">'</span> 002011201388231</td>
	    <td style="color: red;">正常</td>
	</tr>
	 
</table>
	导入时间与机器性能和导入数量有关，请耐心等待。
</div>
<tags:footer></tags:footer>
</body>
</html>