<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%@taglib prefix="tags" tagdir="/WEB-INF/tags"%>   
<c:set var="ctx" value="${ pageContext.request.contextPath }"/>
 
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>机房语音端口信息</title>
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

		$('#dhhm_help').click(function(){
			setValue(this.id,'dhhm');
		});
		
		$('#fj_help').click(function(){
			setValue(this.id,'fj');
		});
		
		$('#search').click(function(btn){
			$(this).attr("disabled","disabled");
			$('#userInfo').attr("target","");
			$('#userInfo').attr("action","${ctx }/Zytj/List");
			$('#userInfo').submit();
		});
		$('#export').click(function(btn){
			$(this).val("导出中...");
			$(this).attr("disabled","disabled");
			
			$('#userInfo').attr("action","${ctx }/Zytj/Export");
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
	<legend>语音端口查询条件 <span class="emphasis">(默认是江门地区的资料)</span> </legend>
	<div>
	<form action="${ctx }/Zytj/List" id="userInfo" method="post" accept-charset="utf-8">
		<input type="hidden" name="@pageNo" id="pageNo" value="${page.pageNumber }">
		<input type="hidden" name="@pageSize" id="pageSize" value="${page.pageSize }">
		<table id="cond_table">
			<tr>
				<td class="label">产品号码：</td>
				<td><input type="text" name="dhhm" value="${ dhhm }" width="40" id="dhhm">
				<input type="button" id="dhhm_help" value="空号" style="background: white">
				</td>
				<td class="label">机房名称：</td>
				<td><input type="text" name="jfmc" value="${ jfmc }" width="40" >
				<td class="label">是否反极：</td>
				<td><input type="text" name="fj" value="${ fj }" id="fj" width="40">
		<select id="fj_help">
			<option value="">[请选择]</option>
			<option value="是">是</option>
			<option value="否">否</option>
		</select>
				</td>
			</tr>
			
		</table>
		<div>
		<input type="submit" value="查询" id="search"/>&nbsp;
		<c:if test="${ sessionScope.accountInfo.level == 1 }">
			<input type="submit" value="导出" id="export" title="请不要大量导出(少于3000条)!"/>&nbsp;
		</c:if>
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
			<td>机房名称</td>
			<td>场地号</td>
			<td>模块号</td>
			
			<td>框号</td>
			<td>槽号</td>
			<td>端口号</td>
			
			<td>设备号</td>
			
			<td>是否反极</td>
			<td>产品号码</td>
			<td>MDF横列(不准)</td>
			
			
		</tr>
	</thead>
	<tbody>
		<c:forEach items="${page.pageItems}" var="info" varStatus="s">
		<tr>
			<td>${ s.count + (page.pageNumber-1)*page.pageSize }</td>
			<td>${ info.jfmc }</td>
			<td><fmt:formatNumber value="${ info.cdh }" pattern="######"/></td>
			<td><fmt:formatNumber value="${ info.mkh }" pattern="######"/></td>
			<td><fmt:formatNumber value="${ info.kh }" pattern="######"/></td>
			
			<td><fmt:formatNumber value="${ info.ch }" pattern="######"/></td>
			<td><fmt:formatNumber value="${ info.dkh }" pattern="######"/></td>
			<td><fmt:formatNumber value="${ info.sbh }" pattern="########"/></td>
			
			<td>${ info.fj }</td>
			<td>${ info.dhhm }</td>
			<td>${ info.hl }</td>
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

