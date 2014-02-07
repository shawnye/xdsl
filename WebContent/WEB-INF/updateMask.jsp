<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<c:set var="ctx" value="${ pageContext.request.contextPath }"/>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>停/复端口</title>
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
	.edit{
		background: lightyellow;
		border:1px solid black;
	}
</style>
<script type="text/javascript">
	$(function(){
		$('#maskBtn').click(function(){
			$('#mask').val("true");
			$('#maskForm').submit();
		});
		$('#unmaskBtn').click(function(){
			$('#mask').val("false");
			$('#maskForm').submit();
		});
		
		new LiveClock('liveClock', true, true);
	});
	function isReady(form){
		 
		var c = window.confirm('您确定更新？');
		if(!c){
			return false;
		}
		
		form.importBtn.disabled = true;
		delayStatus($("#importBtn"), true, 20000);
		return true;
	}
</script>
</head>
<body>

<form action="${ctx }/UpdateMask" id="maskForm" method="post" accept-charset="utf-8" onsubmit="return isReady(this);">
	<input type="hidden" name="mask" id="mask">
	<table border="1">
		<caption>根据条件停/复端口(请谨慎操作)</caption> 
		<tr>
			<td style="color:red;">机房:</td>
			<td><input type="text" name="jx" id="jx">（必选，来自）</td>
		</tr>
		<tr>
			<td style="color:red;">设备号:</td>
			<td><input type="text" name="sbh">（必选）</td>
		</tr>
		<tr>
			<td>槽号列表:</td>
			<td><input type="text" name="slots">（可选，以中英文逗号或分号分隔）</td>
		</tr>
		<tr>
			<td>端口号列表:</td>
			<td><input type="text" name="sb_ports">（可选，以中英文逗号或分号分隔）</td>
		</tr>
	</table> 
	 
	<input type="button" value="屏蔽" id="maskBtn"
	<c:if test="${ sessionScope.accountInfo.level > 2 }">disabled="disabled"</c:if>
	>&nbsp;&nbsp;
	<input type="button" value="复开" id="unmaskBtn"
	<c:if test="${ sessionScope.accountInfo.level > 2 }">disabled="disabled"</c:if>
	>&nbsp;&nbsp;
	<input type="button" value="取消" onclick="window.close();">	
</form>
<p style="border:1px dotted red;text-align:left;"><b>更新说明：</b>
 	  屏蔽端口时，如果发现有用户占用，将提示错误！
</p>
 
</body>
</html>