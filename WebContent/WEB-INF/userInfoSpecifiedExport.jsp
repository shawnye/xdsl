<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<c:set var="ctx" value="${ pageContext.request.contextPath }"/>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>指定产品号码/用户帐号批量导出</title>
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
		delayStatus($("#importBtn"), true, 10000);
		return true;
	}
</script>
</head>
<body>
<tags:header></tags:header>
<form action="${ctx }/UserInfo/SpecifiedExport" method="post" accept-charset="utf-8" onsubmit="return isReady(this);">
	<div>
	请粘贴产品号码/用户帐号列表（可以<b>混合</b>，一行仅代表一个产品号码/用户帐号前缀）：<br/>
	<textarea rows="25" cols="50" name="pids" id="pids" title="一次仅支持30行左右"></textarea>
	</div>
	 
	<input type="submit" value="导出" id="importBtn"
		<c:if test="${ sessionScope.accountInfo.level == 4 }">disabled="disabled"</c:if>
	>
</form>
<div><b>导出说明：</b>
	产品号码/用户帐号格式示例：<b>  002011200043520 </b> 或 <b>'002011200043520</b> 或  <b>"	002011200043520  "</b><br/>或"JMDSL1003130535@16900.gd"等 。<br/>
	系统自动忽略空行，忽略每行产品号码前后空格。
</div>
<div >
	导出时间与机器性能和导出数量有关，请耐心等待。
</div>
<tags:footer></tags:footer>
</body>
</html>