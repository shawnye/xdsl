<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<c:set var="ctx" value="${ pageContext.request.contextPath }"/>

<html>
<head>
<tags:head></tags:head>
<title>宽带用户资料导入</title>

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
<form action="${ctx }/UserInfo/Import" method="post" accept-charset="utf-8" enctype="multipart/form-data" onsubmit="return isReady(this);">
	<b><span class="emphasis">[新装*]</span>宽带用户信息文件(csv格式)</b>：
	<input type="file" name="file" size="50">
	<input type="submit" value="导入 " id="importBtn" <c:if test="${ sessionScope.accountInfo.level >2 }">disabled="disabled"</c:if>
	>
</form>
	<div class="emphasis">注意：不要重复导入! </div>
	<div class="emphasis">导入时 按照 外层VLAN和内层VLAN查询端口，如果找不到，则不导入系统! </div>

<div >
请确保使用<span class="emphasis">csv</span>文件，其
格式说明：第一行为标题，标题名随意，注意<b>顺序</b>不能错。
<table border="1" cellpadding="0" cellspacing="0">
				<thead>

					<tr>
						<td>列序号</td>
						<td>Excel列编码</td>
						<td>列内容</td>
					</tr>
				</thead>
				<tbody>

					<%--
					<tr>

						<td>1</td>
						<td>A</td>
						<td>OSS接入间名称，参见：
						<a href="${ctx }/Ossjx/List" target="_blank">OSS与ADSL系统的机房名称对应表</a>
						</td>
					</tr>
					 --%>
<tr>
	<td>1</td>
	<td>A</td>
	<td>接入间名称</td>
</tr>

<tr>
	<td>2</td>
	<td>B</td>
	<td>客户名称</td>
</tr>

<tr>
	<td>3</td>
	<td>C</td>
	<td>受理时间</td>
</tr>

<tr>
	<td>4</td>
	<td>D</td>
	<td>联系电话(可选)</td>
</tr>

<tr>
	<td>5</td>
	<td>E</td>
	<td style="color:red;">装机号码(不能重复!)</td>
</tr>

<tr>
	<td>6</td>
	<td>F</td>
	<td>装机地址</td>
</tr>

<tr>
	<td>7</td>
	<td>G</td>
	<td>帐号(不能重复!)</td>
</tr>

<tr>
	<td>8</td>
	<td>H</td>
	<td>密码(建议填写！)</td>
</tr>

<tr>
	<td>9</td>
	<td>I</td>
	<td>外层VLAN</td>
</tr>

<tr>
	<td>10</td>
	<td>J</td>
	<td>内层VLAN</td>
</tr>
<tr>
	<td>11</td>
	<td>K</td>
	<td style="color:red;">J_ID (优先使用J_ID进行匹配！)</td>
</tr>
<tr>
	<td>12</td>
	<td>L</td>
	<td style="color:red;">ONT_ID（ONT端口号）</td>
</tr>
<tr>
	<td>13</td>
	<td>M</td>
	<td style="color:red;">FTTH SN</td>
</tr>

				</tbody>
			</table>
	导入时间与机器性能和导入数量有关，请耐心等待。
<div class="emphasis">导出后可以到<a href="${ctx }/UserInfo/List?@default=true">用户资料查询</a>查找“备注”为“[导入时间]新装导入”的用户信息即为刚刚导入的信息。</div>
例如2010-1-2 15点钟曾经导入过，查询条件可以设为：“[2010-1-2 15%]新装导入”

</div>
<tags:footer></tags:footer>
</body>
</html>