<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<c:set var="ctx" value="${ pageContext.request.contextPath }"/>

<html>
<head>
<tags:head></tags:head>
<title>端口资料导入</title>

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
<form action="${ctx }/JxInfo/Import" method="post" accept-charset="utf-8" enctype="multipart/form-data" onsubmit="return isReady(this);">
	<div style="border:1px solid gray;"><b>导入类型：</b>
	<select name="updateType">
		<option value="0" style="color:green;">纯新增(+)</option>
		<option value="1" style="color:red;">纯更新(M)</option>
	</select>&nbsp;
	<b>端口信息文件(csv格式)</b>：
	<input type="file" name="file" size="50">
	<input type="submit" value="导入 " id="importBtn"
	<c:if test="${ sessionScope.accountInfo.level > 2 }">disabled="disabled"</c:if>
	>
	</div>
</form>
	<div class="emphasis">注意：新增端口不要重复导入,特别是后退! </div>
	<div style="color:green;">导入时 按照 机房(jx)、外层VLAN(slan)、内层VLAN(clan)查询，如果找到，则提示端口重复错误! </div>

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
<tr>
	<td>1</td>
	<td>A</td>
	<td>机房(jx)</td>
</tr>

<tr>
	<td>2</td>
	<td>B</td>
	<td>设备号(sbh)</td>
</tr>

<tr>
	<td>3</td>
	<td>C</td>
	<td>宽带类型(type): AD,PON-AD,PON-LAN,LAN,WLAN,FTTH</td>
</tr>

<tr>
	<td>4</td>
	<td>D</td>
	<td>槽号(slot)</td>
</tr>

<tr>
	<td>5</td>
	<td>E</td>
	<td>端口号(sb_port)</td>
</tr>

<tr>
	<td>6</td>
	<td>F</td>
	<td>MDF横列(mdf_port)</td>
</tr>

<tr>
	<td>7</td>
	<td>G</td>
	<td>是否占用(used),0-未占用，1-占用</td>
</tr>

<tr>
	<td>8</td>
	<td>H</td>
	<td>板卡类型(board_type)</td>
</tr>

<tr>
	<td>9</td>
	<td>I</td>
	<td>内层vlan(clan)</td>
</tr>

<tr>
	<td>10</td>
	<td>J</td>
	<td>外层vlan(slan)</td>
</tr>

<tr>
	<td>11</td>
	<td>K</td>
	<td>IP(可选)</td>
</tr>

<tr>
	<td>12</td>
	<td>L</td>
	<td>OLT名称(可选)</td>
</tr>
<tr>
	<td>13</td>
	<td>M</td>
	<td>ONT端口总数（FTTH终端端口总数,宽带类型为FTTH时填写，默认是4,一旦设定暂时不能修改,可选）</td>
</tr>
<tr>
	<td>14</td>
	<td>N</td>
	<td style="color:red;" title="最后一列即可">机房端口号(J_ID)（更新时必填，请从<a href="${ctx }/report?@id=23&page_limit=30" target="_blank">机房端口导入更新专用查询</a> 导出修改后再导入！）</td>
</tr>
				</tbody>
			</table>
	导入时间与机器性能和导入数量有关，请耐心等待。
<div class="emphasis">导出后可以到<a href="${ctx }/JxInfo/List?@default=true" >端口资料查询</a>查找“备注”为“导入批次：时间”的端口信息即为刚刚导入的信息。</div>
例如2010-1-2 15点钟曾经导入过，查询条件可以设为：“导入批次：B2010010215”

</div>
<tags:footer></tags:footer>
</body>
</html>