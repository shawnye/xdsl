<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<c:set var="ctx" value="${ pageContext.request.contextPath }"/>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>指定接入间用户信息导出（管理员）</title>
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

</style>
<script type="text/javascript">
	$(function(){
		new LiveClock('liveClock', true, true);
	});
	function isReady(form){
		if(form.export_all.value=='true'){
			var s = window.confirm('真的要导出所有接入间端口信息？这将影响整个AD系统运行，请不要在繁忙时段导出!');
			if(!s){
				return false;
			}
		}
		
		
		if(!form.ftth_only.value && form.export_all.value=='false' && !form.accessRooms.value){
			alert('请粘贴接入间列表');
			return false;
		}
		form.importBtn.disabled = true;
		delayStatus($("#importBtn"), true, 20000);
		delayStatus($("#importBtn2"), true, 20000);
		delayStatus($("#importBtn3"), true, 20000);
		return true;
	}
	 
</script>
</head>
<body>
<tags:header></tags:header>
<form action="${ctx }/ExportAccessRoom" method="post" accept-charset="utf-8" onsubmit="return isReady(this);">
	<input type="hidden" id="export_all" name="export_all" value="false">
	<input type="hidden" id="ftth_only" name="ftth_only" value="false">
	
	<input type="hidden" name="fileId" value="1">
	<div>
	请粘贴接入间列表（一行仅代表一个接入间，一次不要超过30个）：<br/>
	<textarea rows="20" cols="80" name="accessRooms" id="accessRooms"></textarea>
	<br/>
	<input type="submit" value="导出指定接入间" id="importBtn" onclick="form.export_all.value='false'"
	<c:if test="${ sessionScope.accountInfo.level > 3 }">disabled="disabled"</c:if>
	>&nbsp;&nbsp;&nbsp;&nbsp;
	<input type="submit" value="导出所有" id="importBtn2" onclick="form.export_all.value='true'" style="color:red;"
	<c:if test="${ sessionScope.accountInfo.level > 3 }">disabled="disabled"</c:if>
	>
	<input type="submit" value="导出所有FTTH" id="importBtn3" onclick="form.ftth_only.value='true'" style="color:blue;"
	<c:if test="${ sessionScope.accountInfo.level > 3 }">disabled="disabled"</c:if>
	>
	
	</div>
	
</form>
<div ><b>导出说明：</b>
 	接入间名称必须来自AD系统，
 	系统自动忽略空行，忽略每行接入间前后空格和单双引号，例如录入： ' 江门白石  ' ===》江门白石  。
</div>
<div>可以通过<a href="${ctx }/report?@id=4&page_limit=2000" target="_blank">【AD系统机房名称表 】</a>获取本系统机房名称。</div>
<div >
	导出时间与机器性能和导出数量有关，请耐心等待。
</div>
<tags:footer></tags:footer>
</body>
</html>