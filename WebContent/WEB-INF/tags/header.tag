<%@ tag language="java" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ tag import="unicom.common.SystemEnvironment" %>
<c:set var="ctx" value="${ pageContext.request.contextPath }"/>
<style type="text/css">
.header{
		background-color: #C8D1D2;
		font-weight: bold;
		width: 100%;
		border-collapse: collapse;
}
</style>
<div >
<table border="1" class="header">
	<tr>
		<td><img src="${ctx }/images/logo-mini.jpg" alt="logo" />宽带/语音资源系统<span style="color:red;"><%=SystemEnvironment.getProperty("version") %></span>
		</td>
		<td><span id="liveClock"></span>&nbsp;</td>
		<td>帐号：<c:out value="${sessionScope.accountInfo.account }" default="未登录"></c:out>&nbsp;
			名称：<c:out value="${sessionScope.accountInfo.branch }" default="无"></c:out>&nbsp;
		<a href="${ctx }/ChangePassword" onclick="return changePassword(this);" target="_blank" style="color:green;">[修改密码]</a> 	
		</td>
		<td><a href="javascript:window.external.AddFavorite('http://${pageContext.request.localAddr }:${pageContext.request.localPort }<c:if test="${ not empty ctx }">/${ctx }</c:if>','xDSL系统');">收藏</a>

		&nbsp;<a href="${ctx }/Main">首页</a>
		&nbsp;<a href="${ctx }/Logout">退出</a></td>
	</tr>
	<tr >
	<td colspan="4" style="color:red;background:yellow;font-size: smaller;font-weight: normal;">注意：部分360浏览器不能正常导出，请<a href="http://www.zoum5.com/saysth/280.html" target="_blank">更换其默认的下载工具</a>，或者直接使用其他浏览器。</td>
	</tr>
</table>

 
</div>