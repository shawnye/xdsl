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
<title>已屏蔽端口查询</title><%-- 修改标题 --%>
<tags:head></tags:head>
<link href="${ctx }/components/calendar/calendar-system.css" rel="stylesheet" type="text/css" >
<script language="javascript" src='${ctx }/components/calendar/calendar.js'></script>
<script language="javascript" src='${ctx }/components/calendar/calendar-setup.js'></script>
<script language="javascript" src='${ctx }/components/calendar/lang/calendar-zh-utf8.js'></script>
 
<style type="text/css">

</style>
<script type="text/javascript">
function collectSelectedItems(delim){
	if(typeof delim == 'undefined'){
		delim = ',';
	}
	var values = '';
	$("#data_table input[name^='c'][name!='c0']").each(function(i, n){
			var checked = $(this).attr('checked');
			if(checked){
				if(values){
					values += delim;
				}
				values += $(this).attr('value').replace(/^\s+|\s+$/g,'');
			}
	});
	return values;
}

	$(function(){

		$(':button').each(function(){
			$(this).click(function(btn){
				if($(this).attr("id") != 'clearCond' && $(this).attr("id") != 'hideConfig' &&$(this).attr("id") != 'hideCond'){
					$(this).attr("disabled","disabled");
					delayStatus($(this), true, 1000);
				}

			});
		});
		$('#count').click(function(btn){
			$(this).attr("disabled","disabled");
			$('#countDisplay').text('(...)');

			var params = {};//object
			$('form :text, form textarea').each(function(i,n){
				if($(this).val()){
//					alert($(this).attr('name'));
					params[$(this).attr('name')] = $(this).val();
				}

			});
//			alert(params);
//			var params=$(':text').serialize();
			var data=$.param(params);

			$.post('/report/count?@id=${report.id}',data,function(text){
				//alert(text + ',' + $('#countDisplay'));
				$('#countDisplay').text('(共' + text + '行)');
			},'text');

			delayStatus($(this), true, 2000);

			return false;
		});
		$('#zcbqh').change(function(btn){
			var a = $(this);
			a.val($.trim(a.val()));
			var b = a.val().split('\n');
			$(this).attr("title", b.length + '行');
		});
		$('#zcbh').change(function(btn){
			var a = $(this);
			a.val($.trim(a.val()));
			var b = a.val().split('\n');
			$(this).attr("title", b.length + '行');
		});

		$('#search').click(function(btn){

			$(this).attr("disabled","disabled");
			$('#reportForm').attr("target","");
			$('#reportForm').attr("action","${ctx }/report?@id=${report.id}");
			$('#reportForm').submit();
		});

		//collect columnsDisplayed
		function getColumnConfig(){
			var s = '';
			$("input[name^='col'][name!='col0']").each(function(i, n){
				var checked = $(this).attr('checked');
				s += i + ':' + (checked?1:0) + ';' ;
			});
			return s;
		}

		var c = $.cookie('report${report.id}_columnsDisplayed');//初始化
		if(!c){
			$('#browseConfig').attr('disabled','disabled');
		}
		$('#browseConfig').click(function(btn){
			ck = $.cookie('report${report.id}_columnsDisplayed');//初始化
			if(ck){
				var cols = [];
				var pairs = ck.split(';');
				for(var i=0; i < pairs.length ; i++){
					var pair = pairs[i].split(':');
					$('#col'+(parseInt(pair[0])+1)).attr('checked', pair[1]=='1'?'checked':'');
				}
			}

		});
		$('#saveConfig').click(function(btn){
			$('#columnsDisplayed').val(getColumnConfig());

			//save to cookie too
			$.cookie('report${report.id}_columnsDisplayed',$('#columnsDisplayed').val() );//不用path先

			$(this).attr("disabled","disabled");
			$('#reportForm').attr("target","");
			$('#reportForm').attr("action","${ctx }/report?@id=${report.id}");
			$('#reportForm').submit();
		});


		$('#exportCsv').click(function(btn){
			$(this).val("导出中...");
			$(this).attr("disabled","disabled");
			$('#exportExcel').attr("disabled","disabled");

			$('#columnsDisplayed').val(getColumnConfig());
			$('#reportForm').attr("action","${ctx }/report/export?@id=${report.id}&@ext=csv");
			$('#reportForm').attr("target","_blank");
			$('#reportForm').submit();

			$('#columnsDisplayed').val('');
			delayStatus($(this), true, 5000);
			delayStatus($('#exportExcel'), true, 5000);

			$(this).val("导出Csv");

		});
		$('#exportExcel').click(function(btn){
			$(this).val("导出中...");
			$(this).attr("disabled","disabled");
			$('#exportCsv').attr("disabled","disabled");

			$('#columnsDisplayed').val(getColumnConfig());
			$('#reportForm').attr("action","${ctx }/report/export?@id=${report.id}&@ext=xls");
			$('#reportForm').attr("target","_blank");
			$('#reportForm').submit();

			$('#columnsDisplayed').val('');
			delayStatus($(this), true, 5000);
			delayStatus($('#exportCsv'), true, 5000);
			$(this).val("导出Excel");

		});




		$('#update').click(function(btn){
			$('#selectedIds').val(collectSelectedItems());

			if(! $('#selectedIds').val()){
				alert('请选择一项进行处理');
				return;
			}

			//window.open("${ctx }/report/modify/prepare?@id=${report.id}",'_blank');//no #selectedIds
			$('#reportForm').attr("action","${ctx }/report/modify/prepare?@id=${report.id}");
			$('#reportForm').attr("target","_blank");
			$('#reportForm').submit();

			$(this).attr("disabled","");
		});

		$('#clearCond').click(function(btn){
			$('input[type=text]').each(function(){
				$(this).val('');
			});
			$('textarea').each(function(){
				$(this).val('');
			});
			$('select').each(function(){
				$(this).val('');
			});
		});

		$('#hideCond').click(function(btn){
			$('#sql').toggle('fast');
		});
		$('#hideConfig').click(function(btn){
			$('#colconfig').toggle('fast');
		});

		$('.data_table tbody tr').each(function(i, n){
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

		<%-- 列配置用 --%>
		$('#col0').click(function(){
			var checked = $(this).attr('checked');
			$("input[name^='col'][name!='col0']").each(function(i, n){

				if(checked){
					$(this).attr('checked',true);
					$(this).parent().removeClass('column_deselected');
				}else{
					$(this).attr('checked',false);
					$(this).parent().addClass('column_deselected');

				}
			});
		});

		$("input[name^='col'][name!='col0']").each(function(i, n){

			$(this).click(function(){
				var checked = $(this).attr('checked');
				if(!checked){
					$(this).parent().addClass('column_deselected');
					$('#col0').attr('checked',false);
				}else{
 					$(this).parent().removeClass('column_deselected');
				}
			});
		});

		$('#c0').click(function(){
			var checked = $(this).attr('checked');
			$("input[name^='c']").each(function(i, n){

				if(checked){
					$(this).attr('checked',true);
				}else{
					$(this).attr('checked',false);
				}
			});
		});
		//有一个uncheck, 总的选择为空
		$("input[name^='c'][name!='c0']").each(function(i, n){
			var checked = $(this).attr('checked');
			$(this).click(function(){
				if(!checked){
					$('#c0').attr('checked',false);
				}
			});
		});
		
		$('#board_type_help').click(function(){
			setValue(this.id,'board_type');
		});
		$('#sbh_help').click(function(){
			setValue(this.id,'sbh');
		});
		
		$('#type_help').click(function(){
			setValue(this.id,'type');
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
		
		new LiveClock('liveClock', true, true);
	});//ready
 
</script>
</head>
<body>
<tags:header></tags:header><a name="top"></a>
<fieldset>
	<legend>已屏蔽端口查询（不应该有任何占用！）
 <span id="countDisplay" style="color:red;"></span> </legend><%-- 修改标题 --%>
 
	<div >
	<form action="${ctx }/report?@id=${report.id}" id="reportForm" method="post" accept-charset="utf-8">
		<input type="hidden" name="@selectedIds" id="selectedIds">
		<input type="hidden" name="@columnsDisplayed" id="columnsDisplayed">
		<table id="cond_table">	<%-- BEGIN 修改条件 --%>
			<tr>
<tr>
	<td class="label">仅仅显示前：</td>
	<td><input type="text" name="page_limit" id="page_limit" value="${ page_limit }" size="10"></td>
	<td class="label">机房：</td>
	<td><input type="text" name="jx" value="${ jx }" size="10" id="jx"><input type="checkbox" id="jx_exact" >精确查询</td> 
	<td class="label">设备号：</td>
	<td><input type="text" name="sbh" id="sbh"  value="${ sbh }"  size="10"  title="模糊查询">
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
			 
		</select><input type="checkbox" id="sbh_exact" >精确查询</td> 
</tr>
<tr> 
	<td class="label">槽号</td>
	<td><input type="text" name="slot=" id="slot" value="${ slot }" size="10" title="精确查询"></td>
	<td class="label">VLAN：</td>
				<td>
				外层<input type="text" name="outer_vlan=" value="${ outer_vlan }" id="outer_vlan" size="5" title="精确匹配">
				.内层<input type="text" name="inner_vlan=" value="${ inner_vlan }" id="inner_vlan" size="5" title="精确匹配">
	
	</td>
	<td class="label">板卡型号</td>
	<td><input type="text" name="board_type" id="board_type" value="${ board_type }" size="10" >
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
</tr>
<tr> 
	<td class="label">J_ID&nbsp;</td>
	<td><input type="text" name="j_id=" id="j_id" value="${ j_id }" size="10" title="精确查询">&nbsp;</td>
	<td class="label">&nbsp;</td>
	<td>&nbsp;</td>
	<td class="label">产品类型</td>
	<td><input type="text" name="type" id="type" value="${ type }" size="10" >
	<select id="type_help">
			<option value="">[请选择]</option>
			<c:forTokens items="AD,PON-AD,PON-LAN,(普通)LAN,WLAN,FTTH" delims="," var="i">
						<option value="${ fn:substring(i,0, fn:indexOf(i,'(')) }" <c:if test="${ userInfo.type == i}">selected</c:if> >${ i }</option>
			</c:forTokens> 
		</select><input type="checkbox" id="type_exact" >精确查询
		</td>
	
</tr>
  
		</table>
	</form>
	</div>
	<div> <%-- 修改条件 END --%>
		<input type="submit" value="查询" id="search"/>&nbsp;
 		<input type="button" value="清空" id="clearCond"/>&nbsp;  
 		<c:if test="${ sessionScope.accountInfo.level < 4 }">
 		|&nbsp;
		<input type="button" value="导出csv" id="exportCsv" title="请不要大量导出(一次少于5000条)!"/>&nbsp;
		<input type="button" value="导出Excel" id="exportExcel" title="请不要大量导出(一次少于5000条)!"/>&nbsp;
		</c:if>
 	</div>
	 <div id="sql" style="display:none;"><textarea rows="5" cols="80" style="border:1px solid gray;">${ report.currentSql }</textarea></div>
	<div id="colconfig" style="display:none;border:1px solid gray;background-color:lightyellow;">
	<ul style="text-align:left;style=clear:both;">
		<c:forEach items="${ report.titles }" var="t" varStatus="s">
			<li style="width:150px;float:left;" <c:if test="${s.index == report.keyIndex }">class="key_field"</c:if>><input type="checkbox" id="col${s.count}" name="col${s.count}"  <c:if test="${ report.columnsDisplayed[s.index] }">checked</c:if> >${ t }</li>
		</c:forEach>
	</ul>
	<div ><input type="checkbox" name="col0" id="col0">全选/全不选&nbsp;&nbsp;&nbsp;<input type="button" value="获得Cookie配置" id="browseConfig"/>&nbsp; <input type="button" value="保存配置" id="saveConfig"/></div>
	</div>
</fieldset>
<c:if test="${ page_limit == report.dataSize }"><div style="color:red;background: yellow;">***列表可能仅显示部分查询记录***</div></c:if>
<%-- 如果行数不多于3000行，下面一般不需要修改 --%>
<table border="1" class="data_table" id="data_table">
	<thead>
		<tr>
			<td ><input type="checkbox" name="c0" id="c0">序号</td>
			<c:forEach items="${ report.displayedTitles }" var="t" varStatus="s">
				<td  <c:if test="${s.index == report.keyIndex }">class="key_field"</c:if> >${ t }</td>
			</c:forEach>
		</tr>
	</thead>
	<tbody>
		<c:forEach items="${report.data }" var="d" varStatus="s">
		<tr>
			<td>
			<input type="checkbox" name="c${ s.count }" id="c${ s.count }" value="${ d[report.keyField]}">
			${ s.count }</td>
			<c:forEach items="${d}" var="m" varStatus="s0">

			<c:if test="${ report.columnsDisplayed[s0.index] }"><%-- 是否显示此列 --%>
			<td nowrap="nowrap" <c:if test="${s0.index == report.keyIndex }">class="key_field"</c:if> >${ m.value }
				<c:if test="${not empty report.links[m.key].path}">
				&nbsp;<a href="${report.links[m.key].path}&@selectedIds=${d[report.keyField] }" target="_blank">${report.links[m.key].title}</a>
				</c:if>
			</td>
			</c:if>

			</c:forEach>
		</tr>
		</c:forEach>
	</tbody>
</table>
<div><a href="#top">回到页顶</a></div>
<tags:footer></tags:footer>
</body>
</html>

