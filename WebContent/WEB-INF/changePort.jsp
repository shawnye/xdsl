<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<c:set var="ctx" value="${ pageContext.request.contextPath }"/>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>更新端口</title>
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
	
	ul{
		list-style-type: none;
		text-align:left;
	}
	.edit{
		background: lightyellow;
		border:1px solid black;
	}
</style>
<script type="text/javascript">
	$(function(){
		new LiveClock('liveClock', true, true);
	});
	function isReady(form){
		if(!form.j_id.value){
			alert('请填写新的机房端口ID（J_ID）.');
			return false;
		}
		
		if(form.new_sn.value){
			var sn_reg = /^0750\d{6}$/;
			var sn_reg2 = /^[a-zA-Z]+\d+$/;
			if(! sn_reg.test(form.new_sn.value) &&! sn_reg2.test(form.new_sn.value) ){
				alert('FTTH SN必须是0750开头10位数字或英文开头+数值');
				form.new_sn.focus();
				return false;
			}
		}
		
		if(form.new_address.value){
			var address_reg = /.{6,}/;
			var address_reg2 = /(蓬江|江海|新会|鹤山|台山|恩平|开平)/;
			if(! address_reg.test(form.new_address.value) ){
				alert('装机地址不能少于6个中文字');
				form.new_address.focus();
				return false;
			}else if(! address_reg2.test(form.new_address.value) ){
				alert('装机地址必须包含7区字眼（蓬江、江海、新会、鹤山、台山、恩平、开平）');
				form.new_address.focus();
				return false;
			}
		}
		
		var c = window.confirm('您确定更新？');
		if(!c){
			return false;
		}
		
		form.importBtn.disabled = true;
		delayStatus($("#importBtn"), true, 20000);
		return true;
	}
</script>
</head>
<body>

<form action="${ctx }/ChangePort" method="post" accept-charset="utf-8" onsubmit="return isReady(this);">
	<input type="hidden" value="${ userInfo.u_id }" name="u_id">
	<ul>
		<c:if test="${ not empty error }"><li style="color:red;background-color:yellow;">更新出错：${error }</li></c:if>
		<li>
<table border="1" class="data_table">
	<thead>
		<tr>
			<td>U_ID</td>
			<td>客户名称</td>
			<td>产品号码</td>
			<td>帐号</td>
			<td>装机地址</td>
			<td>竣工时间</td>	
			<td style="color:red;">ONT端口</td> 
		</tr>
	</thead>
	<tbody>
 		<tr>
 			<td>${ userInfo.u_id }</td>
 			<td>${ userInfo.username }</td>
			<td>${ userInfo.p_id }</td>
			<td>${ userInfo.user_no }</td>
			<td>${ userInfo.address }</td>
			<td><fmt:formatDate value="${ userInfo.finish_date }" pattern="yyyy-MM-dd HH:mm"/></td>
			<td style="color:red;">${ userInfo.ont_id }</td>
		</tr>
	</tbody>
</table>			
			
		</li>
		<li>
<table border="1" class="data_table">
	<thead>
		<tr> 
			<td>J_ID</td>
			<%--<td>区域</td> --%>
			<td>机房</td>
 			<td>设备号</td>
			<td>AD类型</td>
			<td>板类型</td>
			<td>槽号</td>
			<td>端口号</td>
			<td>MDF横列</td>

			<td style="color:red;">外层VLAN</td>
			<td style="color:red;">内层VLAN</td>
		
			<td>是否占用</td>
		
		<c:if test="${ fn:trim(userInfo.type) == 'FTTH'}">
			<td style="color:red;">SN</td>	 
			<td>ONT端口总数</td>
			<td>ONT端口已占用数</td>
		</c:if>	
 
		 
		</tr>
	</thead>
	<tbody>
 		<tr>
 			<td>原J_ID:${ userInfo.j_id }</td>
			<%--<td>${ userInfo.area }</td>--%>
			<td>${ userInfo.jx }</td>
		 
			<td>${ userInfo.sbh }</td>
			<td>${ userInfo.type }</td>
			<td>${ userInfo.board_type }</td>
			<td>${ userInfo.slot }</td>
			<td>${ userInfo.sb_port }</td>
			<td>${ userInfo.mdf_port }</td>

			<td>${ userInfo.outer_vlan }</td>
			<td>${ userInfo.inner_vlan }</td>

			<td>${ userInfo.used } </td> 
			
<c:if test="${ fn:trim(userInfo.type) == 'FTTH' || fn:trim(newPort.type) == 'FTTH'}">
			<td>${ userInfo.sn }</td>
			<td>${ userInfo.ont_ports }</td>
			<td>${ userInfo.used_ont_ports }</td>
</c:if>
		</tr>
		<tr>
 			<td title="必填">新J_ID:<input type="text" name="new_j_id" id="j_id" value="${ newPort.j_id }" size="10" class="edit"><span style="color:red;">*</span><br/>
 			新ONT端口<input type="text" name="new_ont_id" id="ont_id" value="${ newPort.ont_id }" size="10" class="edit" title="新端口为FTTH必填，一般端口值应为1,2,3,4"><br/>
 			新FTTH SN<input type="text" name="new_sn" id="sn" value="${ newPort.sn}" size="10" class="edit" title="新端口为FTTH建议填写,不变不用填"><br/>
 			
 			</td>
			<%--<td>${ newPort.area }</td> --%>
			<td>${ newPort.jx }</td>
			 
			<td>${ newPort.sbh }</td>
			<td>${ newPort.type }</td>
			<td>${ newPort.board_type }</td>
			<td>${ newPort.slot }</td>
			<td>${ newPort.sb_port }</td>
			<td>${ newPort.mdf_port }</td>

			<td>${ newPort.outer_vlan }</td>
			<td>${ newPort.inner_vlan }</td>
			<td>${ newPort.used } <c:if test="${ not empty newPort.j_id && empty newPort.used }">已坏</c:if> </td> 
<c:if test="${ fn:trim(userInfo.type) == 'FTTH' || fn:trim(newPort.type) == 'FTTH'}">
			<td>${ newPort.sn }</td>
			<td>${ newPort.ont_ports }</td>
			<td>${ newPort.used_ont_ports }</td>
</c:if>
 		</tr>
 	</tbody>
</table>
		</li>
		<li style="color:red;">
			<input type="checkbox" value="true" name="makeFault" id="makeFault">原机房端口置坏? (因原机房端口坏而更换端口的可以勾选,对原FTTH端口，请确保只有一个用户)
		</li>
		<li>
			移机新地址：<input type="text" name="new_address" value="${ newPort.address }" id="new_address" class="edit" size="50">(移机时填写, 不变不用填写)
		</li>
		<li>
			<span style="vertical-align: top;">更新备注:</span>
			<textarea rows="3" cols="30" name="remark" class="edit"></textarea>
		</li> 
	</ul>
	<input type="submit" value="更新" id="importBtn"
	<c:if test="${ sessionScope.accountInfo.level > 2 }">disabled="disabled"</c:if>
	>&nbsp;&nbsp;&nbsp;&nbsp;
	<input type="button" value="关闭" onclick="window.close();">	
</form>
<p style="border:1px dotted red;text-align:left;"><b>更新说明：</b>
 	请参考<a href="${ctx }/report?@id=11&page_limit=30" target="_blank">空闲AD端口查询</a>或<a href="${ctx }/JxInfo/List?@default=true" target="_blank">机房AD端口查询*</a>中获取可用的机房端口ID(J_ID)来更新原端口ID。<br/>
 	<span>对于原FTTH机房端口而言，ONT端口已占用数&gt;1时，不能置坏，因为置坏端口不能关联任何用户信息！</span><br/>
 	<span style="color:red;">注意：请确保机房端口ID(J_ID)(1)存在、(2)未被占用、(3)未置坏、(4)未被屏蔽(5)并且与原机房端口不同 或ONT端口不同</span><br/>
 	
 	
</p>
 
</body>
</html>