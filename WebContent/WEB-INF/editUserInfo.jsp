<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<c:set var="ctx" value="${ pageContext.request.contextPath }"/>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<title>编辑用户信息</title>
<tags:head></tags:head>
<link rel="STYLESHEET" href="${ctx }/styles/jquery.autocomplete.css" type="text/css"> 
<script type="text/javascript" src="${ctx }/scripts/jquery/jquery.autocomplete.js"></script>

<style type="text/css">
	table td{
		padding:2px;
		text-align: left;
	}
	.data_table{
		border-collapse: collapse;
		width: 80%;
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
	.data_table caption{
		font-weight: bold;
		font-size: larger;
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

</style>
<script type="text/javascript">
$(document).ready(function() {
	// 使用 $ 作为 jQuery 别名的代码
	  $("#jx").autocomplete(
				'${ctx}/Jx/Json/List',
				{extraParams: {//动态参数
						"jx": function(){
 							return $("#jx").val();
						},
						"area": function(){
 							return $("#area").val();
						},
						"branch": function(){
 							return $("#branch").val();
						}
					},
					multiple: false,
					multipleSeparator:"\n",//没有办法获得当前word @see selectCurrent方法
					
					minChars: 1,//问题是第二个就耗资源
					matchSubset:false,//不缓存
					scroll: false,
					scrollHeight: 1000,
					dataType:'json',
					parse: function(data){//parse received data...json is not neccesary
				        var rows = [];
						if(!data){
							return rows;
						}
				        for(var i=0; i<data.length; i++){
				           rows[rows.length] = {
				               data: data[i].jx,
				               value: data[i].jx ,//显示
				               result: this.formatResult &&  this.formatResult(data, data[i].jx )   //最终显示
				           };
				         }
				        return rows;
					},
					//value @see behead
					formatItem: function(data, i, n, value) {
						if(!data){
							return false;
						}

						return "<div style='text-align:left;'>" + value + '</div>';
					}
					,
					//data=row,
					formatResult: function(data, value) {
						return value;
						 
					}
		});//end of autocomplete
		
		
	  $("#mdf_port").autocomplete(
				'${ctx}/Mdfport/Json/List',
				{extraParams: {//动态参数
						"jx": function(){
							return $("#jx").val();
						},
						"sbh": function(){
							return $("#sbh").val();
						},
						"mdf_port": function(){
							return $("#mdf_port").val();
						}
					},
					multiple: false,
					multipleSeparator:"\n",//没有办法获得当前word @see selectCurrent方法
					
					minChars: 1,//问题是第二个就耗资源
					matchSubset:false,//不缓存
					scroll: false,
					scrollHeight: 1000,
					dataType:'json',
					parse: function(data){//parse received data...json is not neccesary
				        var rows = [];
						if(!data){
							return rows;
						}
				        for(var i=0; i<data.length; i++){
				           rows[rows.length] = {
				               data: data[i].mdf_port,
				               value: data[i].mdf_port ,//显示
				               result: this.formatResult &&  this.formatResult(data, data[i].mdf_port )   //最终显示
				           };
				         }
				        return rows;
					},
					//value @see behead
					formatItem: function(data, i, n, value) {
						if(!data){
							return false;
						}

						return "<div style='text-align:left;'>" + value + '</div>';
					}
					,
					//data=row,
					formatResult: function(data, value) {
						return value;
						 
					}
		});
});
</script>     

<script type="text/javascript">
	$(function(){
		new LiveClock('liveClock', true, true);
	});
	
	function fieldRequired(fieldObject, desc){
		fieldObject.value = $.trim(fieldObject.value);
		if(!fieldObject.value){
			alert('请填写' + desc);
			fieldObject.focus();
			return false;
		}
		
		return true;
	}
	
	function isReady(form){
		
		var fieldObjects = [form.username,
		                    form.p_id,
		                    form.address,
		                    form.tel,
		                    //form.user_no,
		                    form.password,
		                    form.area, 
		                    form.jx, 
		                    form.type, 
		                    form.branch, 
		                    form.sbh 
		                    //form.mdf_port
		                    ];
 		var descs = ['用户名','产品号码','地址','联系电话','密码','区域','接入间','产品类型','代维公司','设备号'];//,'MDF端口'
		
		for(var i=0;i<fieldObjects.length; i++){
			var b = fieldRequired(fieldObjects[i], descs[i]);
			if(!b){
				return false;
			}
		}
		
		var p_id_reg = /^0750\d{8}$/;
		var p_id_reg2 = /[a-zA-Z0-9]+/;
//		alert(form.p_id.value.indexOf('0750'));
		if(form.p_id.value.indexOf('0750') == 0){

			if(! p_id_reg.test(form.p_id.value) ){
				alert('产品号码以0750开头时，只可以全数字，12位，您录入字符数：' + form.p_id.value.length);
				form.p_id.focus();
				return false;
			} 
		}else if(! p_id_reg2.test(form.p_id.value) ){
			alert('产品号码不以0750开头时，只能是数字和英文');
			form.p_id.focus();
			return false;
		} else if( form.p_id.value.charAt(0) == '1' ){
			alert('产品号码不能以1开头时，请勿录入手机号码');
			form.p_id.focus();
			return false;
		}
		
		var user_no_reg = /\@/;
		if(! user_no_reg.test(form.user_no.value) ){
			alert('帐号必须包含@');
			form.user_no.focus();
			return false;
		}
		
		var password_required=/\@16900\.gd$/;
		var password_reg = /[a-zA-Z0-9]+/;
		if( password_required.test(form.password.value) && ! password_reg.test(form.password.value) ){
			alert('密码只能是数字和英文');
			form.password.focus();
			return false;
		}
		
 		var address_reg = /.{6,}/;
		var address_reg2 = /(蓬江|江海|新会|鹤山|台山|恩平|开平)/;
		if(! address_reg.test(form.address.value) ){
			alert('装机地址不能少于6个中文字');
			form.address.focus();
			return false;
		}else if(! address_reg2.test(form.address.value) ){
			alert('装机地址必须包含7区字眼（蓬江、江海、新会、鹤山、台山、恩平、开平）');
			form.address.focus();
			return false;
		}
		
		var acctype = null;
		var acct = form.user_no.value.toUpperCase();
		if(acct.indexOf('FTTH') >= 0){
			acctype = 'FTTH';
		}else if(acct.indexOf('LAN') >= 0){
			acctype = 'LAN';
		}else if(acct.indexOf('DSL') >= 0){
			acctype = 'AD';
		}
 		
		var type = form.type.value;
		
		if(acctype && type.indexOf(type) == -1){
			alert('帐号类型（'+acctype+'）与产品类型（'+type+'）不匹配');
			return false;
		}
		
		
		if(type == 'FTTH'){
			var b = fieldRequired(form.mdf_port, 'MDF端口');
			if(!b){
				return false;
			}
			
			var b = fieldRequired(form.ont_id, 'ONT端口');
			if(!b){
				return false;
			}
			
			//remove preceding zero.
			form.ont_id.value = form.ont_id.value.replace(/^0+/,'');
			
			b = fieldRequired(form.sn, 'SN');
			if(!b){
				return false;
			}
			
			var sn_reg = /^0750\d{6}$/;
			var sn_reg2 = /^[a-zA-Z]+\d+$/;
			if(! sn_reg.test(form.sn.value) &&! sn_reg2.test(form.sn.value) ){
				alert('FTTH SN必须是0750开头10位数字或英文开头+数值');
				form.sn.focus();
				return false;
			}
			
		}else{
			form.ont_id.value = '';
			form.sn.value = '';
		}
		
		
//		 alert('submiting...');
		form.saveBtn.disabled = true;
		delayStatus($("#saveBtn"), true, 3000);
		return true;
	}
	
	function doAdd(){
		var w = window.open("${ctx }/UserInfoPrepareEdit",'UserInfoPrepareEdit','height=600,width=750,status=yes,toolbar=no,menubar=no,location=no,resizable=yes,scrollbars=yes',true);
		w.focus();
	}
	
	function doCopy(){
		var w = window.open("${ctx }/UserInfoPrepareCopy?u_id=${ userInfo.u_id }",'UserInfoPrepareEdit','height=600,width=750,status=yes,toolbar=no,menubar=no,location=no,resizable=yes,scrollbars=yes',true);
		w.focus();
	}
	
	function areaChanged(area){
		if($('#jx').val()){
			return ;
		}
		 $('#jx').val(area.value);
	}
	
	function typeChanged(type){
		var jx = $('#jx').val();
		if(!jx){
			return ;
		}
		//ajx update sbh
 

		$.post("${ctx}/Sbh/Json/List",{ 'jx':jx, 'type': type.value  }, 
		function(data){//[{sbh:''},{sbh:''}],$('#sbh')[0]=DOM object
		renderSelect($('#sbh')[0] ,data, 'sbh', 'sbh', false, false); 
		  
		},"json");
		   
		
		$('#mdf_port').val('');//重新选择mdf端口
		$('#slot').val('');
		$('#sb_port').val('');
	}
	
	function sbhChanged(){
		var jx = $('#jx').val();
		if(!jx){
			return ;
		}
		
		var sbh = $('#sbh').val();
		if(!sbh){
			return ;
		}
		  
		
		$.post("${ctx}/Slot/Json/List",{ 'jx':jx, 'sbh': sbh  }, 
		function(data){//[{sbh:''},{sbh:''}],$('#sbh')[0]=DOM object
			var select = $('#slot')[0];
			removeAll(select);
			select.options.add(new Option('请选择',''));
			
			if(sbh == '${userInfo.sbh }'){
				//显示slot原值
				
				if('${userInfo.slot }'){
					select.options.add(new Option('${userInfo.slot } [当前值]','${userInfo.slot }'));
				}
			}
			renderSelect2(select ,data, 
					function(slotAndType, i){ return slotAndType.slot + ' (' + slotAndType.board_type + ')'
				    }, 
					'slot', true //append
				  ); 
			
		},"json");
		   
		
		$('#mdf_port').val('');//重新选择mdf端口
		$('#slot').val('');
		$('#sb_port').val('');
	}
	
	function slotChanged(){
		var jx = $('#jx').val();
		if(!jx){
			return ;
		}
		
		var sbh = $('#sbh').val();
		if(!sbh){
			return ;
		}
		var slot = $('#slot').val();
		if(!slot){
			return ;
		}
 

		$.post("${ctx}/Sbport/Json/List",{ 'jx':jx, 'sbh': sbh ,'slot':slot }, 
		function(data){//[{sbh:''},{sbh:''}],$('#sbh')[0]=DOM object
			var select = $('#sb_port')[0];
			removeAll(select);
			select.options.add(new Option('请选择',''));
			if(slot == '${userInfo.slot }'){
				//显示sbport原值  
				if('${userInfo.sb_port }'){
//					alert('${userInfo.sb_port }' + '==>' + select.id);//??FIXME 下一步无法执行！
					select.options.add(new Option('${userInfo.sb_port } [当前值]','${sb_port.sb_port }'));
				}
			}
		
			renderSelect(select,data, 'sb_port', 'sb_port', false, false
			); 
		  
		},"json");
		   
		
		$('#mdf_port').val('');//重新选择mdf端口
	}
	
	function jxChanged(jx){
		var type = $('#type').val();
		if(!type){
			return ;
		}
		//ajx update sbh
		$.post("${ctx}/Sbh/Json/List", { 'jx':jx.value, 'type': type  }, 
		function(data){//[{sbh:''},{sbh:''}]
			renderSelect($('#sbh')[0] ,data, 'sbh', 'sbh', false, false); 
			  
		},"json");
		
		$('#mdf_port').val('');//重新选择mdf端口
		$('#slot').val('');
		$('#sb_port').val('');
		 

	}
</script>

<style type="text/css">
	.errors{
		color:red;
	}
</style>
</head>
<body style="text-align: center;">

<form action="${ctx }/UserInfoSave" method="post" accept-charset="utf-8" onsubmit="return isReady(this);">
	<input type="hidden" value="${ userInfo.u_id }" name="u_id">
 	
	<c:if test="${ not empty msg }">
	<div style="text-align:center;color:red;background-color:yellow;">${msg }</div> 
	</c:if>

	<table border="1" class="data_table"> 
	<caption>用户基本信息</caption>
	<tbody>
		<tr>
			<td>用户名：</td>
			<td><input type="text" name="username" id="username" value="${ userInfo.username }" size="40"></td>
			<td><span style="color:red;">*</span>必填</td> 
		</tr>
 		<tr>
			<td>产品号码：</td>
			<td><input type="text" name="p_id" id="p_id" value="${ userInfo.p_id }" size="40" title="0750开头，只可以全数字，12位，其他只能是数字和英文"></td>
			<td><span style="color:red;">*</span>必填，必须唯一</td> 
		</tr>
		
		<tr>
			<td>联系电话：</td>
			<td><input type="text" name="tel" id="tel" value="${ userInfo.tel }" size="40"></td>
			<td><span style="color:red;">*</span>必填</td> 
		</tr>
		<tr>
			<td>装机地址：</td>
			<td><input type="text" name="address" id="address" value="${ userInfo.address }" size="40" title="不能少于6个中文字"></td>
			<td><span style="color:red;">*</span>必填</td> 
		</tr>
		<tr>
			<td>帐号：</td>
			<td><input type="text" name="user_no" id="user_no" value="${ userInfo.user_no }" size="40" title="需要包含@"></td>
			<td><span style="color:green;">*</span>宽带必填，必须唯一</td> 
		</tr>
		<tr>
			<td>密码：</td>
			<td><input type="text" name="password" id="password" value="${ userInfo.password }" size="40" title="只能数字和英文"></td>
			<td><span style="color:red;">*</span>必填,没有密码请填写‘无’</td> 
		</tr>
		<tr>
			<td>区域：</td>
			<td>
			<c:choose>
				<c:when test="${fn:contains(sessionScope.accountInfo.branch ,'讯联') }">
					<c:set var="limitAreas" value="蓬江,鹤山,台山"></c:set>
				</c:when>
				<c:when test="${fn:contains(sessionScope.accountInfo.branch ,'润建') }">
					<c:set var="limitAreas" value="江海,新会,台山,开平,恩平"></c:set>
				</c:when>
				<c:otherwise>
					<c:set var="limitAreas" value="蓬江,江海,新会,鹤山,台山,开平,恩平"></c:set>
				</c:otherwise>
				
			</c:choose>
			
			<select name="area" id="area" onchange="areaChanged(this);" title="原值:[<c:out value="${userInfo.area }" default="空"></c:out>]" <c:if test="${ (not empty userInfo.u_id) && daiwei }">disabled="disabled" readonly="readonly"</c:if> >
				<option value="">请选择</option>
					<c:forTokens items="${limitAreas }" delims="," var="i">
						<option value="${ i }" <c:if test="${ userInfo.area == i}">selected</c:if> >${ i }</option>
					</c:forTokens> 
				</select>
 			</td>
			<td><span style="color:red;">*</span>必填</td> 
		</tr>
		<tr>
			<td>接入间：</td>
			<td><input type="text" name="jx" id="jx" value="${ userInfo.jx }" size="30" onchange="return jxChanged(this);" <c:if test="${ (not empty userInfo.u_id) && daiwei }">disabled="disabled" readonly="readonly"</c:if> 
			></td>
			<td><span style="color:red;">*</span>必填,请先选区域，按提示填写</td> 
		</tr>
		<tr>
			<td>产品类型：</td>
			<td>
			<select name="type" id="type" onchange="return typeChanged(this);" <c:if test="${ (not empty userInfo.u_id) && daiwei }">disabled="disabled" readonly="readonly"</c:if>
			 title="原值:[<c:out value="${userInfo.type }" default="空"></c:out>]" >
				<option value="">请选择</option>
					<c:forTokens items="AD,PON-AD,PON-LAN,LAN,WLAN,FTTH" delims="," var="i">
						<option value="${ i }" <c:if test="${ userInfo.type == i}">selected</c:if> >${ i }</option>
					</c:forTokens> 
				</select>
				 
			</td>
			<td><span style="color:red;">*</span>必填，应与帐号类型匹配</td> 
		</tr>
		<tr>
			<td>代维公司：</td>
			<td>
 			<select name="branch" id="branch" title="原值:[<c:out value="${userInfo.branch }" default="空"></c:out>]">
				<option value="">请选择</option>
					<c:forTokens items="自维,讯联,润建,其他" delims="," var="i">
						<option value="${ i }" <c:if test="${ userInfo.branch == i || (empty userInfo.branch && fn:contains(sessionScope.accountInfo.branch ,i) ) }">selected</c:if> >${ i }</option>
					</c:forTokens> 
				</select>
 			</td>
			<td><span style="color:red;">*</span>必填</td> 
		</tr>
		<tr>
			<td>备注：</td>
			<td><textarea rows="4" cols="50" name="remark" id="remark">${ userInfo.remark }</textarea></td>
 			<td>&nbsp;</td> 
		</tr>
	</tbody>
</table>			

<table border="1" class="data_table"> 
	<caption>用户配线资料（手动分配）</caption>
	<tbody>
		<tr>
			<td>设备类型：</td>
			<td><select name="sbh" id="sbh" onchange="sbhChanged();" <c:if test="${ (not empty userInfo.u_id) && daiwei }">disabled="disabled" readonly="readonly"</c:if>
			title="原值:[<c:out value="${userInfo.sbh }" default="空"></c:out>]" >
				<option value="">请选择</option>
				<c:forEach items="${ sbhs }" var="i">
					<option value="${ i.sbh }" <c:if test="${ userInfo.sbh == i.sbh}">selected</c:if> >${ i.sbh }</option>
				</c:forEach>	 
				</select> 
				 
			</td>
			<td><span style="color:red;">*</span>必填,请先选择接入间和产品类型</td> 
		</tr>
		<tr>
			<td colspan="3" style="color:red;">由于FTTH槽号、端口号不唯一，必须填写横列，其他类型的可填写横列（优先）或‘槽号、端口号’，必须二选一</td>
		</tr>
 		<tr>
			<td>横列：</td>
			<td><input type="text" name="mdf_port" id="mdf_port" value="${ userInfo.mdf_port }" size="40"  title="对于FTTH，格式是OLT编码 -分光器编码-分光端口，例如02-38-005（代表该机房第二台OLT-该OLT的38号分光-该分光的第5个端口)" <c:if test="${ (not empty userInfo.u_id) && daiwei }">disabled="disabled" readonly="readonly"</c:if>
			></td>
			<td><span style="color:green;">*</span>FTTH必填，有提示，建议先输入0</td> 
		</tr>
		<tr>
			<td>槽号、端口号：</td>
			<td>槽号：
			<select name="slot" id="slot" <c:if test="${ (not empty userInfo.u_id) && daiwei }">disabled="disabled" readonly="readonly"</c:if>
			title="原值:[<c:out value="${userInfo.slot }" default="空"></c:out>]" onchange="slotChanged();">
				
				<option value="">请选择</option>
				<c:if test="${not empty userInfo.slot }">
				<option value="${userInfo.slot }" style="color:red;" selected="selected">${userInfo.slot }[当前值]</option>
				</c:if>
				<c:forEach items="${ slots }" var="i">
					<option value="${ i.slot }" <c:if test="${ userInfo.slot == i.slot}">selected</c:if> >${ i.slot }</option>
				</c:forEach>	 
			</select>
			端口号：
			<select name="sb_port" id="sb_port" <c:if test="${ (not empty userInfo.u_id) && daiwei }">disabled="disabled" readonly="readonly"</c:if>
			title="原值:[<c:out value="${userInfo.sb_port }" default="空"></c:out>]" >
				<option value="">请选择</option>
				<c:if test="${not empty userInfo.sb_port }">
				<option value="${userInfo.sb_port }" style="color:red;"  selected="selected">${userInfo.sb_port }[当前值]</option>
				</c:if>
				<c:forEach items="${ sb_ports }" var="i">
					<option value="${ i.sb_port }" <c:if test="${ userInfo.sb_port == i.sb_port}">selected</c:if> >${ i.sb_port }</option>
				</c:forEach>	 
			</select>	
			 
			</td>
			<td>
			 请参考<a href="${ctx }/report?@id=11&page_limit=30" target="_blank">空闲AD端口查询</a>
			</td> 
		</tr>
		<tr>
			<td>ONT端口号：</td>
			<td><input type="hidden" name="old_ont_id" id="old_ont_id" value="${ userInfo.ont_id }" >
			<input type="text" name="ont_id" id="ont_id" value="${ userInfo.ont_id }" size="40" title="一般端口值应为1,2,3,4" <c:if test="${ (not empty userInfo.u_id) && daiwei }">disabled="disabled" readonly="readonly"</c:if>></td>
			<td><span style="color:green;">*</span>当产品类型为FTTH必填，请填写正整数</td> 
		</tr>
		<tr>
			<td>FTTH SN：</td>
			<td><input type="text" name="sn" id="sn" value="${ userInfo.sn }" size="40" ></td>
			<td><span style="color:green;">*</span>当产品类型为FTTH必填，0750开头10位数字</td> 
		</tr>
		<tr>
			<td>槽号：</td>
			<td>${ userInfo.slot }</td>
			<td>&nbsp;</td> 
		</tr>
		<tr>
			<td>设备端口号：</td>
			<td>${ userInfo.sb_port }</td>
			<td>&nbsp;</td> 
		</tr>
		<tr>
			<td>设备端口ID(J_ID)：</td>
			<td><input type="hidden" name="old_j_id" id="old_j_id" value="${ userInfo.j_id }" >
			${ userInfo.j_id }</td>
			<td>&nbsp;</td> 
		</tr>
		<tr>
			<td>VLAN：</td>
			<td>${ userInfo.outer_vlan }.${ userInfo.inner_vlan }</td>
			<td>&nbsp;</td> 
		</tr>
		
		<tr>
			<td>ONT端口总数：</td>
			<td>${ userInfo.ont_ports }</td>
			<td>&nbsp;</td> 
		</tr>
		<tr>
			<td>ONT端口已占用数：</td>
			<td>${ userInfo.used_ont_ports }</td>
			<td>&nbsp;</td> 
		</tr>
		
		<tr>
			<td>录入时间：</td>
			<td><fmt:formatDate value="${ userInfo.begin_date }" pattern="yyyy-MM-dd HH:mm"/></td>
			<td>&nbsp;</td> 
		</tr> 
	</tbody> 
</table>	
	 
	<div style="text-align: center;">
	<input type="submit" value="保存" id="saveBtn">&nbsp;&nbsp;
	
	<c:if test="${not empty userInfo.u_id }">
	<input type="button" value="新增" onclick="doAdd();">&nbsp;&nbsp;
	<input type="button" value="复制" onclick="doCopy();" title="复制并编辑用户信息，不含产品信息和端口信息">&nbsp;&nbsp;
	</c:if>
	
	<input type="button" value="关闭" onclick="window.close();">	
	</div>
</form>
 
 
</body>
</html>