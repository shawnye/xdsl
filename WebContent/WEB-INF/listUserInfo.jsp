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
<title>用户信息</title>
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

	.sub_menu, .sub_menu li{
		display: inline;
		margin: 0px;
		background-color:lightyellow;
		text-align: center;

	}

</style>
<script type="text/javascript">
function doConfirm(msg){
	var c = $('#confirmStautsChange').attr('checked');
	if(c){
		return window.confirm(msg);
	}
}
	//页面初始化
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
		$('#state_help').click(function(){
			setValue(this.id,'state');
		});
		$('#area_help').click(function(){
			setValue(this.id,'area');
		});
		$('#board_type_help').click(function(){
			setValue(this.id,'board_type');
		});

		$('#type_help').click(function(){
			setValue(this.id,'type');
		});
		
		$('#search').click(function(btn){
			$(this).attr("disabled","disabled");
			$('#userInfo').attr("target","");
			$('#userInfo').attr("action","${ctx }/UserInfo/List");
			$('#userInfo').submit();
		});
		$('#export').click(function(btn){
			$(this).val("导出中...");
			$(this).attr("disabled","disabled");

			$('#userInfo').attr("action","${ctx }/UserInfo/Export");
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
			$('textarea').each(function(){
				$(this).val('');
			});
		});

		$('#hideCond').click(function(btn){
			$('#cond_table').toggle('fast');
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

		$('#u_id').val('');
		
		new LiveClock('liveClock', true, true);
	});

	function toggleSize(textCtlId){
		textCtl = $('#' + textCtlId).get(0);

		if(textCtl.rows ==  1){
			textCtl.rows = 10;
		}else{
			textCtl.rows = 1;
		}
	}
	
	function doAdd(){
		var w = window.open("${ctx }/UserInfoPrepareEdit",'UserInfoPrepareEdit','height=600,width=750,status=yes,toolbar=no,menubar=no,location=no,resizable=yes,scrollbars=yes',true);
		w.focus();
	}
</script>
</head>
<body>

<tags:header></tags:header>
<fieldset>
	<legend>用户资料查询条件<span class="emphasis">(请务必输入一个条件进行查询！)</span></legend>
	<div>
	<form action="${ctx }/UserInfo/List" id="userInfo" method="post" accept-charset="utf-8" >
		<input type="hidden" name="@pageNo" id="pageNo" value="${page.pageNumber }">
		<input type="hidden" name="@pageSize" id="pageSize" value="${page.pageSize }">
		<table id="cond_table">
			<tr>
				<td class="label">客户名称：</td>
				<td><input type="text" name="username" value="${ username }" width="40" id="username">
				<%-- 
				<input type="button" id="wait_to_stop_service_help" value="待停机" style="background: white"/><input type="button" id="stop_service_help" value="停机" style="background: white"/>
				--%>
				</td>
				<td class="label">产品号码：</td>
				<td>新<input type="text" name="p_id" value="${ p_id }" size="10" title="按新产品号码查询">&nbsp;
				旧<input type="text" name="old_p_id" value="${ old_p_id }"  size="10" title="按旧产品号码查询">
				</td>
			<%-- 
				<td><textarea rows="1" cols="22" name="p_id" id="p_id" title="单行时支持模糊查找，多行时，一行一个,支持50个号码同时精确查找,重复号码得到的结果会合并,自动删除空格和单引号(')">${ p_id }</textarea>
				<a href="javascript:void(0);" onclick="toggleSize('p_id')">单条/多条</a>
				</td>
			--%>	
				<td class="label">帐号：</td>
				<td><input type="text" name="user_no" value="${ user_no }" width="40"></td>
			</tr>
			<tr>
				<td class="label">区域：</td>
				<td><input type="text" name="area=" id="area" value="${ area}" width="20"  title="精确查询">
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
				<td class="label">机房：</td>
				<td><input type="text" name="jx" value="${ jx }" width="40">
				<br/><a href="${ctx }/report?@id=4&page_limit=200" target="_blank">机房名称表 </a>
				</td>
				<td class="label">装机地址：</td>
				<td><input type="text" name="address" value="${ address }" width="40"></td>

			</tr>

			<tr>
				<td class="label">设备号：</td>
				<td><input type="text" name="sbh" value="${ sbh }" width="40"></td>
				<td class="label">板类型：</td>
				<td><input type="text" name="board_type" id="board_type" value="${ board_type }" width="20">
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
		</select>
				</td>
				<td class="label">槽号：</td>
				<td><input type="text" name="slot=" value="${ slot }" width="40"></td>
			</tr>

			<tr>
				<td class="label">状态：</td>
				<td><input type="text" name="state" id="state" value="${ state }" width="20">
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
				<td class="label">维护单位：</td>
				<td><input type="text" name="branch" value="${ branch }" width="40"></td>
				<td class="label">备注：</td>
				<td><input type="text" name="remark" value="${ remark }" width="40"></td>
			</tr>

			<tr>
				<td class="label">录入时间：</td>
				<td>
				从<input type="text" name="begin_date&gt;=" value="${ begin_date_start }" id="begin_date_start" size="10"  style="color:red;">
				到 <input type="text" name="begin_date&lt;=" value="${ begin_date_end }" id="begin_date_end" size="10">
				</td>
				<td class="label" >竣工时间：</td>
				<td >
				从<input type="text" name="finish_date&gt;=" value="${ finish_date_start }" id="finish_date_start" size="10">
				到 <input type="text" name="finish_date&lt;=" value="${ finish_date_end }" id="finish_date_end" size="10">
				</td>
				<td class="label" style="color:red">J_ID:</td>
				<td><input type="text" name="j_id=" value="${ j_id }" size="10" title="精确匹配">(整数)</td>
			</tr>

			<tr>
				<td class="label">VLAN：</td>
				<td>
				外层<input type="text" name="outer_vlan=" value="${ outer_vlan }" id="outer_vlan" size="10" title="精确匹配">
				.内层<input type="text" name="inner_vlan=" value="${ inner_vlan }" id="inner_vlan" size="10" title="精确匹配">
	
				</td>
				<td class="label">IP地址</td>
				<td><input type="text" name="ip" value="${ ip }" width="40"></td>
				<td class="label" style="color:red">U_ID(单号):</td>
				<td><input type="text" name="u_id=" value="${ u_id }" id="u_id" size="10" title="精确匹配">(整数)</td>
			</tr>
			
			<tr>
				<td class="label">AD端口类型：</td>
				<td>
				<input type="text" name="type=" id="type" value="${ type }" size="10">
		<select id="type_help">
			<option value="">[请选择]</option>
			<c:forTokens items="AD,PON-AD,PON-LAN,LAN,WLAN,FTTH" delims="," var="i">
						<option value="${ i }" <c:if test="${ userInfo.type == i}">selected</c:if> >${ i }</option>
			</c:forTokens> 
		</select>
 				</td>
 				
				<td class="label" title="用户资料导入批次">导入批次：</td>
				<td><input type="text" name="ui_batch_num" id="ui_batch_num" value="${ ui_batch_num }" width="35" /></td>
				<td class="label"></td>
				<td></td>
				 
			</tr>
		</table>
		<div>
		<input type="submit" value="查询" id="search"/>&nbsp;
		<c:if test="${ sessionScope.accountInfo.level < 3 }">
			<input type="button" value="新增" id="add" onclick="doAdd();" style="color:green;"/>&nbsp;
		</c:if>
		<c:if test="${ sessionScope.accountInfo.level < 4 }">
			<input type="submit" value="导出" id="export" title="请不要大量导出(少于3000条)!"/>&nbsp;
		</c:if>
		<input type="button" value="清空" id="clearCond"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		<input type="button" value="显示/隐藏" id="hideCond"/>&nbsp;
		<%-- 
		<c:if test="${ sessionScope.accountInfo.level == 1 }">
		<input type="checkbox" id="confirmStautsChange" checked="checked">停复机是否确认？
		</c:if>
		--%>
		</div>
	</form>
	</div>
</fieldset>

<table border="1" class="data_table">
	<thead>
		<tr>
			<td>序号</td>
			<td>U_ID</td>
			<td>客户名称</td>
			<td>产品号码</td>
			<td>旧产品号码</td>
			<td>装机地址</td>
			<td>帐号</td>
			<td>ONT端口号</td>
			<%--<td>密码</td> --%>
			<td>区域</td>
			<td>联系电话</td>
			<td>录入时间</td>
			<td>J_ID</td>
			<td>机房IP</td>
			<td>机房</td>
			<td>设备号</td>
			<td>AD类型</td>
			<td>板类型</td>
			<td>槽号</td>
			<td>端口号</td>
			<td>MDF横列</td>
			<td>FTTH SN</td>
			
			<td style="color:red;">外层VLAN</td>
			<td style="color:red;">内层VLAN</td>
			<td>OLT名称</td>	 
			<td>Mac</td>

			<td>数据配置时间</td>
			<td>竣工时间</td>
			<td>状态</td>
			<td>维护单位</td>
			<td>备注</td>
		<%--
			<td>备份时间</td>
			<td>删除时间</td>
		--%>
		</tr>
	</thead>
	<tbody >
		<c:forEach items="${page.pageItems}" var="userInfo" varStatus="s">
		<tr>
			<td>${ s.count + (page.pageNumber-1)*page.pageSize }</td>
			<td>${ userInfo.u_id }

			</td>
			<td>${ userInfo.username }&nbsp;
			<%-- 
			<c:if test="${ sessionScope.accountInfo.level == 1 }">
				<c:choose>
					<c:when test="${fn:contains(userInfo.username,'停机') }">
					<a href="${ctx }/UserInfo/RestoreService?p_id=${ userInfo.p_id }" target="_blank" onclick="return doConfirm('号码[${ userInfo.p_id }]真的要复机吗？')">[复机]</a>&nbsp;
					</c:when>
					<c:when test="${fn:contains(userInfo.username,'待停机') }">
					<a href="${ctx }/UserInfo/StopService?p_id=${ userInfo.p_id }" target="_blank"  onclick="return doConfirm('号码[${ userInfo.p_id }]真的要停机吗？')">[停机]</a>&nbsp;
					<a href="${ctx }/UserInfo/RestoreService?p_id=${ userInfo.p_id }" target="_blank"  onclick="return doConfirm('号码[${ userInfo.p_id }]真的要复机吗？')">[复机]</a>&nbsp;
					</c:when>
					<c:otherwise>
						<a href="${ctx }/UserInfo/StopService?p_id=${ userInfo.p_id }&wait=true" target="_blank" onclick="return doConfirm('号码[${ userInfo.p_id }]真的要待停机吗？')">[待停机]</a>&nbsp;
						<a href="${ctx }/UserInfo/StopService?p_id=${ userInfo.p_id }" target="_blank" onclick="return doConfirm('号码[${ userInfo.p_id }]真的要停机吗？')">[停机]</a>&nbsp;
					</c:otherwise>
				</c:choose>
			</c:if>
			--%>
			</td>
			<td>${ userInfo.p_id }
		<c:if test="${sessionScope.accountInfo.level < 3 && not empty userInfo.u_id }">
			<c:if test="${fn:trim(userInfo.state) != '预拆机' }">
			<a href="${ctx }/UserInfoChangeState?u_id=${ userInfo.u_id }&state=PREDELETE" target="_blank" onclick="return confirm('您确定要预拆机(${ userInfo.p_id })吗？');">[预拆机]</a>
			</c:if>
			<c:if test="${fn:trim(userInfo.state) == '预拆机' }">
			<a href="${ctx }/UserInfoDelete?u_id=${ userInfo.u_id }" target="_blank" onclick="return confirm('您确定要拆机(${ userInfo.p_id })吗？(拆机后不可恢复)');">[拆机竣工]</a>
			</c:if>
			&nbsp;
			<a href="${ctx }/UserInfoPrepareEdit?u_id=${ userInfo.u_id }" target="_blank">[更新信息]</a>
			&nbsp;
			<a href="${ctx }/PrepareChangePort?u_id=${ userInfo.u_id }" target="_blank">[更换端口]</a>
		</c:if> 
			</td>
			<td><c:if test="${ userInfo.old_p_id != userInfo.p_id}">${ userInfo.old_p_id }&nbsp;<span style="color:red;">*</span></c:if></td>
			<td>${ userInfo.address }</td>
			<td>${ userInfo.user_no }</td>
			<td>${ userInfo.ont_id }</td>
			<td>${ userInfo.area }</td>

			<td>${ userInfo.tel }</td>
			<td><fmt:formatDate value="${ userInfo.begin_date }" pattern="yyyy-MM-dd HH:mm"/></td>

			<td>${ userInfo.j_id }&nbsp;</td>
			<td>${ userInfo.ip }</td>
			<td>${ userInfo.jx }</td>
			<td>${ userInfo.sbh }</td>
			<td>${ userInfo.type }</td>
			<td>${ userInfo.board_type }</td>
			<td>${ userInfo.slot }</td>
			<td>${ userInfo.sb_port }</td>
			<td>${ userInfo.mdf_port }</td>
			<td>${ userInfo.sn }</td>
			<td>${ userInfo.outer_vlan }</td>
			<td>${ userInfo.inner_vlan }</td>
		     <td>${ userInfo.olt }</td>
			<td>${ userInfo.Mac }</td>

			<td>${ userInfo.open_date }</td>
			<td><fmt:formatDate value="${ userInfo.finish_date }" pattern="yyyy-MM-dd HH:mm"/></td>

			<td>${ userInfo.state }</td>

			<td>${ userInfo.branch }</td>
			<td>${ userInfo.remark }</td>
		</tr>
		</c:forEach>
	</tbody>
</table>
<div class="paging">
<c:choose>
	<c:when test="${page.totalItems >0 }">
	<input type="button" value="首页" id="firstPage"/>&nbsp;
	<input type="button" value="上一页" id="prevPage"/>&nbsp;
	第   ${page.pageNumber } 页&nbsp;
	<input type="button" value="下一页" id="nextPage"/> &nbsp;
	<input type="button" value="末页" id="lastPage"/> &nbsp;
	每页
	<select id="pageSizeSelect">
		<c:forTokens items="10,15,20,30,50,100,200" delims="," var="size">
		<c:choose>
			<c:when test="${ page.pageSize == size}"><option value="${page.pageSize}" selected="selected" style="color:red;">${page.pageSize}</option></c:when>
			<c:otherwise><option value="${size}">${size}</option></c:otherwise>
		</c:choose>
		</c:forTokens>


	</select>
	  项&nbsp;&nbsp;
	共  ${page.pageNumber + page.pagesAvailable } 页 &nbsp;&nbsp; 共  <span class="emphasis">${page.totalItems }</span> 项  </c:when>
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
