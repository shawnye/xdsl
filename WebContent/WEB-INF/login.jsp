<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="unicom.common.SystemEnvironment"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${ pageContext.request.contextPath }"/>
<%@taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html>
<head>
<title>宽带/语音资源系统登录</title>
<tags:head></tags:head>
<script type="text/javascript">
	function isReady(form){
		if(!form.account.value){
			alert('请填写帐号');
			form.account.focus();
			return false;
		}

		if(!form.password.value){
			alert('请填写密码');
			form.password.focus();
			return false;
		}

		return true;
	}
</script>
<style type="text/css">
	.lefttd{
		text-align: right;
		font-weight: bold;
	}
</style>
</head>
<body>
<p>&nbsp;</p>
<p>&nbsp;</p>
<p>&nbsp;</p>
<div style="margin:0 auto;width:500px;">
<noscript><h3>您的浏览器不支持或禁用了Javascipt脚本 ，无法正常访问本站点，请在浏览器设置中启用Javascipt脚本或更换浏览器！</h3></noscript>
<form  action="${ctx }/Login" method="post" accept-charset="utf-8" onsubmit="return isReady(this);">
	<table border="1" style="border: 1px:solid gray; ">
		<caption  style="background:#FFCC33;font-weight: bold;font-size:14pt;"><img src="${ctx }/images/logo-mini.jpg" alt="logo" />宽带/语音资源系统<span style="color:red;"><%=SystemEnvironment.getProperty("version") %></span> </caption>
		<tr>
			<td colspan="2" style="text-align: center;"><span class="emphasis" style="color:red;">${ login_error }</span></td>
		</tr>
		<tr>
			<td class="lefttd">帐号：</td>
			<td><input type="text" name="account" class="text_input" style="width:100px;"  value="${ account }"/></td>

		</tr>
		<tr>
			<td class="lefttd">密码：</td>
			<td><input type="password" name="password" value="" class="text_input" style="width:100px;"  title="(密码不得包含空格)"/></td>

		</tr>
		<tr>
			<td>&nbsp;</td>
			<td><input type="submit" value="登录" />&nbsp;&nbsp;<input type="button" value="关闭" onclick="javascript:window.close();"/></td>

		</tr>
		
		<tr>
			<td colspan="2" style="color:red;background-color: yellow;">&nbsp;
			系统报障请联系监控室：3961846、3071846、13005802000
			</td>
		</tr>
		
		<tr style="font-size: smaller;">
			<td colspan="2" style="background-color: lightgreen;">
			<ul style="margin:10px;">
			<li>已经测试通过的浏览器列表：IE6～<span style="color:red;">IE11</span>, Chrome(Google), Firefox(火狐),Opera(欧朋),Safri(苹果)。<b>推荐使用<span style="color:red;">非</span>IE浏览器（IE10以上除外）.</b></li>
			<li>使用360、搜狗等双核浏览器时建议选中<b>高速(极速)内核</b>进行访问。</li>
			<li>请不要设置拦截本网站的弹出窗口。</li>
			<li style="color:brown;">注意：如果发现无法正常使用，请强制刷新几次浏览器，清除本地缓存后重试。</li>
			</ul>
			
			</td>
		</tr>
		
		
		<tr>
			<td colspan="2" style="font-size: smaller;">&nbsp;
			开发版本： <%=SystemEnvironment.getProperty("develop-version") %>&nbsp;&nbsp;|&nbsp;&nbsp;来访地址： ${ pageContext.request.remoteAddr }
			</td>
		</tr>
	</table>
</form>
</div>
</body>
</html>