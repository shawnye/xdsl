<%@ tag language="java" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ tag import="unicom.common.SystemEnvironment" %>
<c:set var="ctx" value="${ pageContext.request.contextPath }"/>

<div style="clear:both;">
<hr/>
&copy;2009-2013&nbsp;&nbsp;中国联通江门分公司（网络运维部） &nbsp;&nbsp;|&nbsp;&nbsp;开发版本:<%=SystemEnvironment.getProperty("develop-version") %>&nbsp;&nbsp;|&nbsp;&nbsp;来访地址： ${ pageContext.request.remoteAddr }
</div>