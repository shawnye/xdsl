<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%@taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<c:set var="ctx" value="${ pageContext.request.contextPath }"/>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>端口数统计</title><%-- 修改标题 --%>
<tags:head></tags:head>
<link href="${ctx }/components/calendar/calendar-system.css" rel="stylesheet" type="text/css" >
<script language="javascript" src='${ctx }/components/calendar/calendar.js'></script>
<script language="javascript" src='${ctx }/components/calendar/calendar-setup.js'></script>
<script language="javascript" src='${ctx }/components/calendar/lang/calendar-zh-utf8.js'></script>


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

		$(':button').each(function(){
			$(this).click(function(btn){
				if($(this).attr("id") != 'clearCond' && $(this).attr("id") != 'hideCond'){
					$(this).attr("disabled","disabled");
				}

			});
		});


		$('#search').click(function(btn){
			$(this).attr("disabled","disabled");
			$('#userInfo').attr("target","");
			$('#userInfo').attr("action","${ctx }/report?@id=${report.id}");
			$('#userInfo').submit();
		});
		$('#export').click(function(btn){
			$(this).val("导出中...");
			$(this).attr("disabled","disabled");

			$('#userInfo').attr("action","${ctx }/report/export?@id=${report.id}");
			$('#userInfo').attr("target","_blank");
			$('#userInfo').submit();

			delayStatus($(this), true, 3000);
			$(this).val("导出");
			//$('#userInfo').attr("action","${ctx }/UserInfo/List");
		});

		$('#clearCond').click(function(btn){
			$('input[type=text]').each(function(){
				$(this).val('');
			});
		});

		$('#hideCond').click(function(btn){
			$('#sql').toggle('fast');
		});

		$('.data_table tbody tr').each(function(){
			$(this).click(function(){
				if( ! $(this).data('selected')){
					$(this).css('background-color','#FFFF99');
					$(this).data('selected','true');
				}else{
					$(this).css('background-color','');
					$(this).data('selected','');
				}
			});
		});

		new LiveClock('liveClock', true, true);
	});

</script>
</head>
<body>
<tags:header></tags:header><a name="top"></a>
<fieldset>
	<legend>端口数统计条件 </legend><%-- 修改标题 --%>
	<div>
	<form action="${ctx }/report?@id=${report.id}" id="userInfo" method="post" accept-charset="utf-8">
		<table id="cond_table">	<%-- BEGIN 修改条件 --%>
			<tr>
				<td class="label" >仅仅显示前</td>
				<td><input type="text" name="page_limit" value="${ page_limit }" size="15" style="color:red;background:yellow;" title="请填写大于0的整数">行</td>

				<td class="label">机房：</td>
				<td><input type="text" name="jx" value="${ jx }" width="40" ></td>
				<td class="label">设备号：</td>
				<td><input type="text" name="sbh" value="${ sbh }" width="40"></td>
			</tr>
		</table>
		<div> <%-- 修改条件 END --%>
		<input type="submit" value="查询" id="search"/>&nbsp;
		<c:if test="${ sessionScope.accountInfo.level == 1 }">
			<input type="submit" value="导出" id="export" title="请不要大量导出(一次少于5000条)!"/>&nbsp;
		</c:if>
		<input type="button" value="清空" id="clearCond"/>&nbsp;
		<input type="button" value="显示/隐藏SQL语句" id="hideCond"/>&nbsp;
		</div>
	</form>
	</div>
	<div id="sql" style="display:none;"><textarea rows="5" cols="80" style="border:1px solid gray;">${ report.currentSql }</textarea>
	</div>
</fieldset>
<c:if test="${ page_limit == report.dataSize }"><div style="color:red;background: yellow;">***列表可能仅显示部分查询记录，可以导出所有查询记录，但大量导出会导致系统反应缓慢***</div></c:if>
<%-- 如果行数不多于3000行，下面一般不需要修改 --%>
<table border="1" class="data_table">
	<thead>
		<tr>
			<td>序号</td>
			<c:forEach items="${ report.titles }" var="i" >
			<td>${ i }</td>
			</c:forEach>
		</tr>
	</thead>
	<tbody>
		<c:forEach items="${report.data }" var="d" varStatus="s">
		<tr>
			<td>${ s.count + (page.pageNumber-1)*page.pageSize }</td>
			<c:forEach items="${d}" var="m" >
			<td>${ m.value }</td>
			</c:forEach>
		</tr>
		</c:forEach>
	</tbody>
</table>
<div><a href="#top">回到页顶</a></div>
<tags:footer></tags:footer>
</body>
</html>
