<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<c:set var="ctx" value="${ pageContext.request.contextPath }"/>

<html>
<head>
<tags:head></tags:head>
<title>（非FTTH）割接资源表导入</title>

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
/*重复 for IE*/
.emphasis{
	color:red;
	font-weight:bold;
	background-color: yellow;
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
<div style="text-align: left;">
<form action="${ctx }/Cutover/Import" method="post" accept-charset="utf-8" enctype="multipart/form-data" onsubmit="return isReady(this);">
	<b><span class="emphasis">（非FTTH）</span>割接资源表导入(请导出<a href="${ctx }/template/cutover.xls">模板</a>更新数据后，另存为csv格式)</b>：<br/>
	更新后屏蔽机房:<input type="text" name="mask_jx" size="20">设备号:<input type="text" name="mask_sbh" size="20">端口(可选)<br/>
	割接备注：<input type="text" name="remark" size="100">(填写割接名称等信息)<br/>
	选择上传文件：<input type="file" name="file" size="50">
	<input type="submit" value="导入 " id="importBtn" <c:if test="${ sessionScope.accountInfo.level >2 }">disabled="disabled"</c:if>
	>
</form>
	<div class="emphasis">注意：不要重复导入! </div>
	<div class="emphasis">新端口可以为置坏端口和屏蔽端口，但是不能为占用（冲突）端口 </div>
	<div class="emphasis">更新后屏蔽机房、设备号可选，必须按照系统命名！ </div>
 
	导入时间与机器性能和导入数量有关，请耐心等待。
<div class="emphasis">导出后可以到<a href="${ctx }/report?@id=14&page_limit=30">割接资源表查询</a>根据‘导入批次’查找刚刚导入的信息。</div>
</div>
<tags:footer></tags:footer>
</body>
</html>