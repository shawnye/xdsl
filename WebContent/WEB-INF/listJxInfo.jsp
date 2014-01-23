<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<c:set var="ctx" value="${ pageContext.request.contextPath }"/>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>机房宽带端口信息</title>
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
	#cond_table{
		font-size:smaller;
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
		
		//是否精确查询
		$("input[id$='exact']").each(function(){
			$(this).click(function(){
				var id = $(this).attr('id');
				var e = id.indexOf('_exact');
				if(e == -1 ){
					return;
				}
				
				var prefix = id.substring(0,e);
//				alert(prefix);//前缀测试
				
				var ck = $(this).attr('checked');
//				alert(ck);
				if(ck){
					$('#' + prefix).css('color','red');
					$('#' + prefix).attr('name',prefix + '=');
				}else{
					$('#' + prefix).css('color','black');
					$('#' + prefix).attr('name',prefix);
				}
			});
		});
		
		<%--  占地方
		$(".data_table thead tr td").each(function(i){ 
			var n = $(this).text();
			$(this).append('<a href="javascript:void(0);" id="a_cp_col_'+i+'" title="复制[' + n + ']列" col_name="'+n+'"><img src="${ctx}/images/copy.gif" border="0"></a>');	
		});
		
		//复制列
		digit_extract_reg=/(\d+)$/;
		$('a[id^=a_cp_col_]').each(function(){
			$(this).click(function(btn){
				var col_name = $(this).attr("col_name");
				var id = $(this).attr("id");
				var arr = digit_extract_reg.exec(id);
				var i = RegExp.$1 ;
//				alert('copy col:' + id  + ', last num=' + i);
 
				i = parseInt(i)+1;
				var list = col_name +  '\n';
				var t ;
				var rows = 0;
				 
				$(".data_table tbody tr td:nth-child("+ i +")").each(function(){
// 					t = $(this).text();//包含<a>子节点数据
 					t = $(this).contents()
				  		.filter(function() {
				    		return this.nodeType == 3;
				  		}).get(0);
				  	if(t){
				  		t = t.nodeValue;//文本节点值
				  	}else{
						t = '';
					}
 					 
					list += $.trim(t);
					list += '\n';
					rows++;
					
				});
				var w = window.open('copy_col',null,'height=500,width=300,status=no,toolbar=no,menubar=no,location=no,scrollbars=yes');
				w.document.write('<textarea rows="24" cols="30" wrap="off" onfocus="this.select();">' + list + '</textarea><div>复制行数(含空行)：'+ rows +'&nbsp;&nbsp;&nbsp;<input type="button" onclick="javascript:window.close();" value="关闭"/> </div>');
				w.focus();
				
			});
		});
		--%>
		
		
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

		$('#stop_service_help').click(function(){
			setValue(this.id,'username');
		});
		$('#wait_to_stop_service_help').click(function(){
			setValue(this.id,'username');
		});

		$(':button').each(function(){
			$(this).click(function(btn){
				if($(this).attr("id") != 'clearCond' && $(this).attr("id") != 'hideCond'){
					$(this).attr("disabled","disabled");
				}

			});
		});
		$('#type_help').click(function(){
			 
			setValue(this.id,'type');
		});
		$('#state_help').click(function(){
			setValue(this.id,'state');
		});
		$('#area_help').click(function(){
			setValue(this.id,'area');
		});
		$('#board_type_help').click(function(){
			setValue(this.id,'board_type');
		});
		$('#sbh_help').click(function(){
			setValue(this.id,'sbh');
		});

		$('#search').click(function(btn){
			$(this).attr("disabled","disabled");
			$('#userInfo').attr("target","");
			$('#userInfo').attr("action","${ctx }/JxInfo/List");
			$('#userInfo').submit();
		});
		$('#export').click(function(btn){
			$(this).val("导出中...");
			$(this).attr("disabled","disabled");

			$('#userInfo').attr("action","${ctx }/JxInfo/Export");
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
			$('input[type=checkbox]').each(function(){
				$(this).attr('checked',null);
			});
		});

		$('#hideCond').click(function(btn){
			//$('#cond_table').toggle('fast');
			$('#cond_table').show();
		});
		
		$('#cond_field').mouseover(function(){
			var c = $('#hideCond').attr('checked');
			if(c){
				$('#cond_table').show();	
			}
			
		});
		
		$('#cond_field').mouseout(function(){
			var c = $('#hideCond').attr('checked');
			if(c){
				$('#cond_table').hide();	
			}
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
		
		$('#j_id').val('');

		new LiveClock('liveClock', true, true);
	});

 
	
	function updateRemark(btn){
		var d = new Date();
		var m = d.getMonth()+1;
		var date = d.getDate();
		if(m<10){
			m =  '0' + m;
		}
		if(date<10){
			date = '0' + date;
		}
		btn.form.jremark.value = '导入批次：' + d.getFullYear() + m + date;
	}
	 
	function doAdd(){
		var d = new Date();
		var x = d.getHours() + '_' + d.getMinutes() + '_' + d.getSeconds();//避免使用相同窗口
		var w = window.open("${ctx }/UserInfoPrepareEdit",'UserInfoPrepareEdit' + x,'height=600,width=750,status=yes,toolbar=no,menubar=no,location=no,resizable=yes,scrollbars=yes',true);
		w.focus();
	}
	
	//delete_type: 0--退单，1--拆机
	function disorder(p_id,u_id,delete_type){
		
		var c= confirm('您确定要退单('+p_id+')吗？(退单后不可恢复)');
		if(!c){
			return false;
		}
		
		var remark = prompt('请说明退单原因(不能为空):','');
		if(!remark){
			alert('退单原因不能为空, 此次退单操作已忽略。');
			return false;
		}
 		  
		submitHiddenForm({'u_id':u_id, 'delete_type':delete_type, 'remark':remark});

		return false;
		
	}
	
function markFault(j_id){ 
	var remark = window.prompt('请填写置坏原因(必填，否则不做修改):','');
	if(!remark){
		return false;
	}
 		  
		submitHiddenMarkFaultForm({'j_id':j_id,  'remark':remark});

		return false;
		
	}
	
function markUnused(j_id){
	
	var remark = window.prompt('请填写恢复原因(必填，否则不做修改):','');
	if(!remark){
		return false;
	}
	submitHiddenMarkUnusedForm({'j_id':j_id,  'remark':remark});

	return false;
	
}
	
</script>
</head>
<body>
<tags:header></tags:header>
<div style="display:none;">
<tags:hiddenForm action="${ctx }/UserInfoDelete" fieldNames="u_id,delete_type,remark"></tags:hiddenForm> 
<tags:hiddenForm action="${ctx }/MarkFault" fieldNames="j_id,remark" formId="MarkFaultForm"></tags:hiddenForm> 
<tags:hiddenForm action="${ctx }/MarkUnused" fieldNames="j_id,remark" formId="MarkUnusedForm"></tags:hiddenForm> 

  

</div>
<fieldset id="cond_field">
	<legend>机房端口查询条件 <span class="emphasis">(请务必输入一个条件进行查询！)</span> </legend>
	<div>
	<form action="${ctx }/JxInfo/List" id="userInfo" method="post" accept-charset="utf-8">
		<input type="hidden" name="@pageNo" id="pageNo" value="${page.pageNumber }">
		<input type="hidden" name="@pageSize" id="pageSize" value="${page.pageSize }">
		<table id="cond_table">
			<tr>
				<td class="label" style="color:red;">客户名称：</td>
				<td><input type="text" name="username" value="${ username }" width="40" id="username">
				<%--<input type="button" id="wait_to_stop_service_help" value="待停机" style="background: white"/><input type="button" id="stop_service_help" value="停机" style="background: white"/> --%>
				</td>
				<td class="label" style="color:red;">产品号码：</td>
				<td>新<input type="text" name="p_id" value="${ p_id }" id="p_id" size="10" title="按新产品号码查询">&nbsp;
				旧<input type="text" name="old_p_id" value="${ old_p_id }"  size="10" title="按旧产品号码查询">
				<span  title="仅仅针对新产品号码"><input type="checkbox" id="p_id_exact">精确查询</span>
				</td>
				<td class="label" style="color:red;">帐号：</td>
				<td><input type="text" name="user_no" value="${ user_no }" width="40"></td>
			</tr>
			<tr>
				<td class="label">用户区域：</td>
				<td><input type="text" name="area=" id="area" value="${ area }" size="10" title="精确查询">
		<select id="area_help">
			<option value="">[请选择]</option>
			<option value="蓬江">蓬江</option>
			<option value="江海">江海</option>
			<option value="恩平">恩平</option>
			<option value="鹤山">鹤山</option>
			<option value="开平">开平</option>
			<option value="台山">台山</option>
			<option value="新会">新会</option>
		</select>
				</td>
				<td class="label" style="color:red;">机房：</td>
				<td><input type="text" name="jx" value="${ jx }" id="jx" size="10"><input type="checkbox" id="jx_exact" >精确查询
				<br/><a href="${ctx }/report?@id=4&page_limit=200" target="_blank">机房名称表 </a>
				</td>
				<td class="label">装机地址：</td>
				<td><input type="text" name="address" value="${ address }" width="40"></td>

			</tr>

			<tr>
				<td class="label">设备号：</td>
				<td><input type="text" name="sbh" value="${ sbh }" id="sbh" size="8">
				<select id="sbh_help">
			<option value="">[请选择]</option>
			<option value="7302FD">7302FD</option>
			<option value="9806H">9806H</option>
			
			<option value="F820">F820</option>
			<option value="F822">F822</option>
			
			<option value="C220">C220</option>
			<option value="C300">C300</option>
			<option value="jm_kejiju_2403">jm_kejiju_2403</option>
			
			<option value="MA5100">MA5100</option>
			
			<option value="MA5103">MA5103</option>
			<option value="MA5105">MA5105</option>
			
			<option value="MA5600">MA5600</option>
			<option value="MA5603">MA5603</option>
			<option value="MA5605">MA5605</option>
			
			<option value="S1008">S1008</option>
			<option value="S2052">S2052</option>
			<option value="S3328">S3328</option>
			
			<option value="UA5000ipm">UA5000ipm</option>
			<option value="UA5000ipmB">UA5000ipmB</option>  
		</select>
		<br/>
		 <input type="checkbox" id="sbh_exact" >精确查询
		</td>
				<td class="label">板类型：</td>
				<td><input type="text" name="board_type" id="board_type" value="${ board_type }" size="10">
		<select id="board_type_help">
			<option value="">[请选择]</option>
			<option value="ADLA">ADLA</option>
			<option value="ADCE">ADCE</option>
			<option value="normal">normal</option>
			<option value="ADLE">ADLE</option>
			<option value="NALT-B">NALT-B</option>
			<option value="ADMB">ADMB</option>
			<option value="ADLB">ADLB</option>
			<option value="ADRB">ADRB</option>
			<option value="ADGE">ADGE</option>
			<option value="ADEE">ADEE</option>
			 
		</select></td>
				<td class="label">槽号、端口号：</td>
				<td><input type="text" name="slot=" value="${ slot }" size="5" title="槽号，精确查询">
					<input type="text" name="sb_port=" value="${ sb_port }" size="5" title="设备端口号，精确查询">
				</td>
			</tr>

			<tr>
				<td class="label" style="color:red;">是否占用：</td>
				<td>
					<select name="used" >
						<option value="">[所有]</option>
						<option value="1" <c:if test="${ used == 1 }">selected="selected" style="color:red;"</c:if> >已占用</option>
						<option value="0" <c:if test="${ used == 0 }">selected="selected" style="color:red;"</c:if>>未占用</option>
						<option value="[IS NULL]" <c:if test="${ used=='[IS NULL]' }">selected="selected" style="color:red;"</c:if>>已坏</option>
						
					</select>
				</td>
				<td class="label" style="color:gray;">状态：</td>
				<td><input type="text" name="state"  id="state" value="${ state }" size="10">
		<select id="state_help">
			<option value="">[请选择]</option>
			<option value="新装竣工">新装竣工</option>
			<option value="预拆机">预拆机</option>
			<option value="未分配">未分配</option>
			<option value="更换端口">更换端口</option>
			<option value="移机竣工">移机竣工</option>
			<option value="已换端口">已换端口</option>
			<option value="正常">正常</option>
			<option value="预分配">预分配</option>
			<option value="已分配">已分配</option>
			<option value="已拆机">已拆机</option>
		</select>
				</td>
				<td class="label">MDF横列：</td>
				<td><input type="text" name="mdf_port" value="${ mdf_port }" width="40"></td>
			</tr>

			<tr>
				<td class="label">录入时间：</td>
				<td>
				从<input type="text" name="begin_date&gt;=" value="${ begin_date_start }" id="begin_date_start" size="10">
				到 <input type="text" name="begin_date&lt;=" value="${ begin_date_end }" id="begin_date_end" size="10">
				</td>
				<td class="label" style="color:gray;">竣工时间：</td>
				<td >
				从<input type="text" name="finish_date&gt;=" value="${ finish_date_start }" id="finish_date_start" size="10">
				到 <input type="text" name="finish_date&lt;=" value="${ finish_date_end }" id="finish_date_end" size="10">
				</td>
				<td class="label" style="color:red;">J_ID:</td>
				<td><input type="text" name="j_id=" value="${ j_id }" id="j_id"  size="5" title="请填写整数，精确匹配" style="color:red;background: yellow;font-weight: bold;">
					<input type="button" value="清空" onclick="this.form.j_id.value=''">
				</td>
			</tr>

			<tr>
				<td class="label">VLAN：</td>
				<td>
				<span title="即SLAN">外层</span><input type="text" name="outer_vlan=" value="${ outer_vlan }" id="outer_vlan" size="5" title="精确匹配">
				.<span title="即CLAN">内层</span><input type="text" name="inner_vlan=" value="${ inner_vlan }" id="inner_vlan" size="5" title="精确匹配">
				</td>
				<td class="label">IP地址：</td>
				<td><input type="text" name="ip=" value="${ ip }" width="40" title="精确匹配"></td>
				<td class="label" style="color:red">U_ID(单号):</td>
				<td><input type="text" name="u_id=" value="${ u_id }" id="u_id" size="5" title="请填写整数，精确匹配">
					<input type="button" value="清空" onclick="this.form.u_id.value=''">
				</td>
			</tr>
				
			<tr>
				<td class="label" style="color:red;">AD端口类型：</td>
				<td>
				<input type="text" name="type=" id="type" value="${ type }" size="10" title="精确查询">
		<select id="type_help">
			<option value="">[请选择]</option>
			<c:forTokens items="AD,PON-AD,PON-LAN,LAN,WLAN,FTTH" delims="," var="i">
						<option value="${ i }" <c:if test="${ userInfo.type == i}">selected</c:if> >${ i }</option>
			</c:forTokens> 
		</select>
		<br/><input type="checkbox" id="type_exact" >精确查询
 				</td>
 				<td class="label">OLT名称：</td>
				<td><input type="text" name="olt" value="${ olt }" width="40" ></td>
 				<td class="label">已屏蔽端口：</td>
				<td>不显示
				<%--
				<c:choose>
					<c:when test="${ not empty mask }">
					<input type="checkbox" name="mask=" id="mask" value="[IS NULL]" checked="checked">
					</c:when>
					<c:otherwise><input type="checkbox" name="mask=" id="mask" value="[IS NULL]" ></c:otherwise>
				</c:choose> --%>
 				</td> 
				 
			</tr>
			<tr>
				<td class="label">ONT端口：</td>
				<td><input type="text" name="ont_id=" id="ont_id" value="${ ont_id }" width="35" title="精确查询"/>
 				</td>
				<td class="label">FTTH-SN号：</td>
				<td><input type="text" name="sn" id="sn" value="${ sn }" width="35" /></td>
				<td class="label">&nbsp;</td>
				<td></td>
			</tr>
			<tr>
				<td class="label">端口备注：</td>
				<td><input type="text" name="jremark" id="jremark" value="${ jremark }" width="35" />
				<input type="button" value="导入批次" onclick="updateRemark(this)">
				</td>
				<td class="label">端口资料导入批次：</td>
				<td><input type="text" name="jx_batch_num" id="jx_batch_num" value="${ jx_batch_num }" width="35" /></td>
				<td class="label">用户资料导入批次：</td>
				<td><input type="text" name="ui_batch_num" id="ui_batch_num" value="${ ui_batch_num }" width="35" /></td>
			</tr>

		</table>
		<div>
		<input type="submit" value="查询" id="search"/>&nbsp;
		<c:if test="${ sessionScope.accountInfo.level < 3 && sessionScope.accountInfo.canEditUserInfo }">
			<input type="button" value="新增" id="add" onclick="doAdd();" style="color:green;"/>&nbsp;
		</c:if>
		<c:if test="${ sessionScope.accountInfo.level < 4 }">
			<input type="submit" value="导出" id="export" title="请不要大量导出(少于3000条)!"/>&nbsp;
		</c:if>
		<input type="button" value="清空" id="clearCond"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		<input type="checkbox" id="hideCond" /><span style="background: lightgreen;">自动显示/隐藏条件</span>&nbsp;
		</div>
	</form>
	</div>
</fieldset>

<table border="1" class="data_table">
	<thead>
		<tr>
			<td>No.</td>
			<td>J_ID</td>
			<td>区域</td>
			<td>机房*</td>
			
			<td>设备号</td>
			
			<td>槽号</td>
			<td>端口号</td>
			<td>MDF横列</td>

			
			<td style="color:red;" title="格式：外层VLAN（即SLAN）.内层VLAN（即CLAN）">VLAN</td> 
			 
			<td>占用?</td>

			<td title="单号">U_ID</td>
			<td title="仅对FTTH有效">ONT端口号</td> 
			<td>产品号码</td>
			<td>客户名称</td>
			
			<td>装机地址</td>
 
			<td>帐号(OSS)</td>
			
			<%--
			<td>帐号(BSS)</td>
			<td>帐号(省公司)</td>
			<td>是否一致</td>
			--%>
			<td>联系电话</td>
			<td>录入时间</td>
			<td>旧产品号码</td>
			<td>OLT名称</td>
			<td>IP地址</td>
			
			<td>端口类型</td>
			<td>板类型</td>
			<td>FTTH SN</td>
			<td>端口备注</td>

			<%--<td>Mac</td>

			<td>数据配置时间</td> 
			<td>竣工时间</td>--%>
			<td>状态</td>
			<td>维护单位</td>
			<td>用户资料备注</td>
			<td title="仅对FTTH有效">ONT端口总数</td>
			<td title="仅对FTTH有效">ONT端口已占用数</td>
			
			<td>端口导入批次</td>
			<td>用户资料导入批次</td>
		<%--
			<td>备份时间</td>
			<td>删除时间</td>
		--%>
		</tr>
	</thead>
	<tbody>
		<c:forEach items="${page.pageItems}" var="info" varStatus="s">
		<tr
		  <c:if test="${not empty info.mask}"> style="color:gray;" title="端口已经屏蔽"</c:if>
		>
			<td>${ s.count + (page.pageNumber-1)*page.pageSize }</td>
			<td>${ info.j_id }&nbsp; 
		<c:if test="${ sessionScope.accountInfo.canChangePort }">
			<c:if test="${empty info.u_id && not empty info.used  }"><a href="javascript:void(0)"   onclick="markFault('${info.j_id}');" class="haoxian_privilege">[置坏]</a> 
			</c:if>
			<c:if test="${empty info.u_id && empty info.used  }"><a href="javascript:void(0)"   onclick="markUnused('${info.j_id}');" class="haoxian_privilege">[置未占用]</a> 
			</c:if>
		</c:if>
			
		<c:if test="${ sessionScope.accountInfo.level < 3 && sessionScope.accountInfo.canEditPort }">
 			
			 
			<c:if test="${  empty info.u_id  }"> 
					<a href="${ctx }/JxInfo/delete?j_id=${ info.j_id }" target="_blank" onclick="return confirm('真的要删除此端口吗? 删除后不可恢复！');" class="haoxian_privilege"  >[删除]</a>&nbsp;
			</c:if>
		</c:if>
			</td>
			<td>${ info.area }</td>
			<td>${ info.jx }</td>
			
			<td>${ info.sbh }</td>
			
			<td>${ info.slot }</td>
			<td>${ info.sb_port }</td>
			<td>${ info.mdf_port }</td>

			<td title="格式：外层VLAN（即SLAN）.内层VLAN（即CLAN）" style=""> 
			<c:choose>
					<c:when test="${empty info.outer_vlan && empty info.inner_vlan }">无</c:when>
					<c:otherwise>${ info.outer_vlan }.${ info.inner_vlan }</c:otherwise>
				</c:choose>		
			</td>
			<td title="${ info.used_remark }"> <c:out value="${ info.used }" default="已坏"></c:out> </td>
			<td
			<c:if test="${fn:trim(info.state) == '预拆机' }">
			style="color:red;text-decoration: line-through;" title="预拆机状态"
			</c:if>
			>${ info.u_id }&nbsp;
		<c:if test="${ sessionScope.accountInfo.level < 3 && sessionScope.accountInfo.canEditUserInfo && not empty info.used}">
			<c:if test="${ empty info.u_id  || ( into.type=='FTTH' && into.used_ont_ports < info.ont_ports) }">
				<a href="${ctx }/UserInfoPrepareEdit?j_id=${ info.j_id }" target="_blank" style="font-size:smaller;">[新增]</a>
			</c:if>	
		</c:if>
		<c:if test="${ sessionScope.accountInfo.level < 3 && not empty info.u_id }">
			
 			<c:if test="${sessionScope.accountInfo.canEditUserInfo && fn:trim(info.state) != '预拆机' }">
			<a href="${ctx }/UserInfoChangeState?u_id=${ info.u_id }&state=PREDELETE" target="_blank" onclick="return confirm('您确定要预拆机(${ info.p_id })吗？');" style="font-size:smaller;">[预拆机]</a>
			</c:if>
			<c:if test="${(sessionScope.accountInfo.canEditUserInfo || sessionScope.accountInfo.admin) && fn:trim(info.state) == '预拆机' }">
			<a href="${ctx }/UserInfoChangeState?u_id=${ info.u_id }&state=NORMAL&recover=true" target="_blank" onclick="return confirm('您确定要恢复(${ info.p_id }）状态为正常吗？');" class="haoxian_privilege" >[恢复]</a>&nbsp;
			
			<a href="${ctx }/UserInfoDelete?u_id=${ info.u_id }&delete_type=0" target="_blank" onclick="return confirm('您确定要拆机(${ info.p_id })吗？(拆机后不可恢复)');" style="font-size:smaller;"><span style="color:red;">[拆机竣工]</span></a>
			&nbsp;</c:if>
			<%-- 端口与文员可以修改 --%>
			<c:if test="${ sessionScope.accountInfo.canChangePort || sessionScope.accountInfo.daiwei}">
			<a href="${ctx }/PrepareChangePort?u_id=${ info.u_id }" target="_blank" style="font-size:smaller;">[更新端口]</a>
			&nbsp;</c:if>
			
			<c:if test="${sessionScope.accountInfo.canEditUserInfo }">
				
				<c:if test="${ info.elapse_days < 30}">
				<a href="javascript:void(0);"  onclick="return disorder('${ info.p_id }','${ info.u_id }',1);" title="30日内可退单" style="font-size:smaller;">[退单]</a>
				&nbsp;</c:if>
 				
				<a href="${ctx }/UserInfoPrepareEdit?u_id=${ info.u_id }" target="_blank" style="font-size:smaller;" title="主要是更新用户信息">[更新]</a>
			</c:if>
			
		</c:if>
			</td>
			 <td>${ info.ont_id }</td>  
			
			<td>${ info.p_id }</td>
			<td>${ info.username }</td>
			<td>${ info.address }</td>
			<td>${ info.user_no }</td>
			<%--
			<td>${ info.bss_user_no }</td>
			<td>${ info.used_user_no }</td>
			<td>
			<c:choose>
				<c:when test="${ info.same_user_no == 1}">是</c:when>
				<c:when test="${ info.same_user_no == 0}">否</c:when>
				<c:otherwise>&nbsp;</c:otherwise>
			</c:choose>
			</td>
			--%>
			<td>${ info.tel }</td>
			<td><fmt:formatDate value="${ info.begin_date }" pattern="yyyy-MM-dd HH:mm"/></td>
			<td><c:if test="${ info.old_p_id != info.p_id}">${ info.old_p_id }&nbsp;<span style="color:red;">*</span> </c:if></td>
			
			<td>${ info.olt }</td>
			<td>${ info.ip }</td>
			<td>${ info.type }</td>
			<td>${ info.board_type }</td>
			
			<td>${ info.sn }</td>
			
			<td>${ info.jremark }</td>
			
			<%--<td>${ info.Mac }</td>

			<td>${ info.open_date }</td> 
			<td><fmt:formatDate value="${ info.finish_date }" pattern="yyyy-MM-dd HH:mm"/></td>--%>
			
			<td>${ info.state }</td>

			<td>${ info.branch }</td>
			<td>${ info.remark }</td>
			<td>${ info.ont_ports }</td>
			<td>${ info.used_ont_ports }</td>
			
			<td>${ info.jx_batch_num }</td>
			<td>${ info.ui_batch_num }</td>
		</tr>
		</c:forEach>
	</tbody>
</table>
<div class="paging" title="注：对应FTTH，一个机房端口（J_ID）对应多个ONT端口（用户信息）">
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
	共  ${page.pageNumber + page.pagesAvailable } 页 &nbsp;&nbsp; 共  ${page.totalItems } 项
	
	<c:if test="${not empty jxCount }">(机房端口(J_ID)数：<span class="emphasis">${jxCount }</span>)</c:if>
	 </c:when>
	<c:otherwise>没有找到任何记录</c:otherwise>
</c:choose>
</div>
<tags:footer></tags:footer>
</body>
</html>

<script type="text/javascript">

function isDisabled(date) {
	return false;
}

  Calendar.setup(
    {
      inputField  : "begin_date_start",         // ID of the input field
      ifFormat    : "%Y-%m-%d",    // the date format
      button      : "begin_date_start",       // ID of the button
      disableFunc : isDisabled
    }
  );

  Calendar.setup(
		    {
		      inputField  : "begin_date_end",         // ID of the input field
		      ifFormat    : "%Y-%m-%d",    // the date format
		      button      : "begin_date_end",       // ID of the button
		      disableFunc : isDisabled
		    }
 );

  Calendar.setup(
		    {
		      inputField  : "finish_date_start",         // ID of the input field
		      ifFormat    : "%Y-%m-%d",    // the date format
		      button      : "finish_date_start",       // ID of the button
		      disableFunc : isDisabled
		    }
		  );

	Calendar.setup(
				    {
				      inputField  : "finish_date_end",         // ID of the input field
				      ifFormat    : "%Y-%m-%d",    // the date format
				      button      : "finish_date_end",       // ID of the button
				      disableFunc : isDisabled
				    }
	);
</script>
