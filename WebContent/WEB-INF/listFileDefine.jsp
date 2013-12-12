<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%@taglib prefix="tags" tagdir="/WEB-INF/tags"%>   
<c:set var="ctx" value="${ pageContext.request.contextPath }"/>
 
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>文件信息</title>
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
		$('#nextPage').click(function(){
			$('#pageNo').val(parseInt($('#pageNo').val()) +1);
			$('#userInfo').submit();
		});
		$('#prevPage').click(function(){
			$('#pageNo').val(parseInt($('#pageNo').val())-1);
			$('#userInfo').submit();
		});
		$('#firstPage').click(function(){
			$('#pageNo').val(1);
			$('#userInfo').submit();
		});
		$('#lastPage').click(function(){
			$('#pageNo').val( '${ page.pageNumber + page.pagesAvailable }' );
			$('#userInfo').submit();
		});
		
		$('#pageSizeSelect').change(function(){
			if($('#userInfo').attr("disabled")){
				alert("正在查询...请等待页面刷新");
				return;
			}
			$('#pageSize').val(parseInt($('#pageSizeSelect').val()));
			$('#pageNo').val(1);

			$(':submit').attr("disabled","disabled");
			$('#userInfo').submit();
		});
		$(':button').each(function(){
			$(this).click(function(btn){
				if($(this).attr("id") != 'clearCond' && $(this).attr("id") != 'hideCond'){
					$(this).attr("disabled","disabled");
				}
				
			});
		});
		$('#state_help').click(function(){
			setValue(this.id,'state');
		});
		$('#area_help').click(function(){
			setValue(this.id,'area');
		});
		$('#board_type_help').click(function(){
			setValue(this.id,'board_type');
		});
		
		$('#search').click(function(btn){
			$(this).attr("disabled","disabled");
			$('#userInfo').attr("target","");
			$('#userInfo').attr("action","${ctx }/FileDefine/List");
			$('#userInfo').submit();
		});
		$('#export').click(function(btn){
			$(this).val("导出中...");
			$(this).attr("disabled","disabled");
			
			$('#userInfo').attr("action","${ctx }/JxInfo/Export");
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
			$('#cond_table').toggle('fast');
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
<tags:header></tags:header>
<fieldset>
	<legend>文件信息查询条件 </legend>
	<div>
	<form action="${ctx }/FileDeine/List" id="userInfo" method="post" accept-charset="utf-8">
		<input type="hidden" name="@pageNo" id="pageNo" value="${page.pageNumber }">
		<input type="hidden" name="@pageSize" id="pageSize" value="${page.pageSize }">
		<table id="cond_table">
			
			<tr>
				<td class="label">标题：</td>
				<td><input type="text" name="title" id="title" value="${ title }" width="40" >
		
				</td>
				<td class="label">描述：</td>
				<td><input type="text" name="file_desc" value="${ desc }"></td>
			</tr>
			
		</table>
		<div>
		<input type="submit" value="查询" id="search"/>&nbsp;
		<input type="button" value="清空" id="clearCond"/>&nbsp;
		<input type="button" value="显示/隐藏" id="hideCond"/>&nbsp;
		</div>
	</form>	
	</div>
</fieldset>

<table border="1" class="data_table">
	<thead>
		<tr>
			<td>序号</td>
			<td>标题</td>
			<td>大小</td>
			<td>描述</td>
			<td>更新时间</td>
		</tr>
	</thead>
	<tbody>
		<c:forEach items="${page.pageItems}" var="info" varStatus="s">
		<tr>
			<td>${ s.count + (page.pageNumber-1)*page.pageSize }</td>
			<td>${ info.title }&nbsp;&nbsp;
				<a href="${ctx }/File/Download?id=${info.id}" target="_blank">[下载]</a>
			</td>
			<td>${ info.file_size }</td>
			<td>${ info.file_desc }</td>
			<td><fmt:formatDate value="${ info.modify_time }" pattern="yyyy-MM-dd HH:mm"/></td>
			
		</tr>
		</c:forEach>
	</tbody>
</table>
<div class="paging">
<c:choose>
	<c:when test="${page.totalItems >0 }">
	<input type="button" value="首页" id="firstPage"/>&nbsp; 
	<input type="button" value="上一页" id="prevPage"/>&nbsp;
	第   ${page.pageNumber } 页&nbsp;
	<input type="button" value="下一页" id="nextPage"/> &nbsp; 
	<input type="button" value="末页" id="lastPage"/> &nbsp; 
	每页 
	<select id="pageSizeSelect">
		<c:forTokens items="10,15,20,30,50,100,200" delims="," var="size" >
		<c:choose>
			<c:when test="${ page.pageSize == size}"><option value="${page.pageSize}" selected="selected" style="color:red;">${page.pageSize}</option></c:when>
			<c:otherwise><option value="${size}">${size}</option></c:otherwise>
		</c:choose>
		</c:forTokens>
	</select>
	  项&nbsp;&nbsp;
	共  ${page.pageNumber + page.pagesAvailable } 页 &nbsp;&nbsp; 共  <span class="emphasis">${page.totalItems }</span> 项  </c:when>
	<c:otherwise>没有找到任何记录</c:otherwise>
</c:choose>
</div>
<tags:footer></tags:footer>
</body>
</html> 


