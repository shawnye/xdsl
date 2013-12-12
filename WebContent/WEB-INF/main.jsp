<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%@taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<c:set var="ctx" value="${ pageContext.request.contextPath }"/>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>宽带/语音资源系统--主页</title>
<tags:head></tags:head>

<style type="text/css">
.header{
		background-color: #C8D1D2;
		font-weight: bold;
		width: 100%;
		border-collapse: collapse;
}
	.menu_area{
		text-align: left;
	}

.summary{
	font-size: smaller;
	color: gray;
	margin-left: 2em;
}
</style>
<script type="text/javascript">

	$(function(){
	
		new LiveClock('liveClock', true, true);
		
		/**/
		$('#ROOT_MENU a').each(function(e){
			
			$(this).click(function(){
				if($(this).attr("disabled") ){
					alert('请勿连续点击链接，请耐心等待...');
					return false;
				}
				
				$(this).attr("disabled","disabled");
				$(this).attr("title","2秒后才能再次点击，请勿多次连续点击！");
				delayStatus($(this), true, 2000);
			});
			
		});
		
		
	});
	 
</script>
</head>
<body>
<div >
<tags:header></tags:header>

<div class="menu_area" >
	${menu }
</div>
 
<tags:footer></tags:footer>
</div>
</body>
</html>

