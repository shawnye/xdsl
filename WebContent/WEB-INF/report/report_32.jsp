<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%@taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<c:set var="ctx" value="${ pageContext.request.contextPath }"/>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>PSTN查询(关联横列)</title><%-- 修改标题 --%>
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


	 
		$('#exportExcel').click(function(btn){
			var jfmc = $('#jfmc').val();
			if(! jfmc){
				alert('导出时，机房不能为空！');
				return;
			}
			if(jfmc.length < 2){
				alert('导出时，机房名称不能少于2个字符！');
				return;
			}
			if(jfmc == '江门'){
				alert('导出时，机房名称不能为[江门]');
				return;
			}
			 
			
			var d = window.confirm('真的要导出吗？');
			if(!d){
				return ;
			}
			
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
		new LiveClock('liveClock', true, true);
	});//ready



</script>
</head>
<body>
<tags:header></tags:header><a name="top"></a>
<fieldset>
	<legend>PSTN查询(关联横列) <span id="countDisplay" style="color:red;"></span> </legend><%-- 修改标题 --%>
	<div style="background: #CCFFCC;">提示：<b>%</b>表示在指定位置模糊查询(默认前后位置)</div>

	<div >
	<form action="${ctx }/report?@id=${report.id}" id="reportForm" method="post" accept-charset="utf-8">
		<input type="hidden" name="@selectedIds" id="selectedIds">
		<input type="hidden" name="@columnsDisplayed" id="columnsDisplayed">
		<table id="cond_table">	<%-- BEGIN 修改条件 --%>
			<tr>
				<td class="label" >仅仅显示前</td>
				<td><input type="text" name="page_limit" value="${ page_limit }" size="15" style="color:red;background:yellow;" title="请填写大于0的整数,不超过2000行">行</td>
				<td class="label">电话号码：</td>
				<td><input type="text" name="dhhm" value="${ dhhm }" width="10" ></td>
				<td class="label" rowspan="2">机房：</td>
				<td rowspan="2">
				<textarea name="jfmc" rows="5" cols="20" id="jfmc" title="支持多行模糊查询，不能超过20行， 导出时 不能少于2个字符，不能为江门">${ jfmc }</textarea>
				</td>
			 	
			</tr>
			<tr>
			  <td class="label">设备号：</td>
			  <td><input type="text" name="sbh=" value="${ sbh }" width="30" title="精确匹配"></td>
			  <td class="label">是否反极：</td>
			  <td><input type="text" name="fj=" value="${ fj }" width="20" title="精确匹配">(是/否)</td>
			 
			</tr>	 
		 
		</table>
	</form>
	</div>
	<div> <%-- 修改条件 END --%>
		<input type="submit" value="查询" id="search"/>&nbsp;
 		<input type="button" value="清空" id="clearCond"/>&nbsp; 
 		<c:if test="${ sessionScope.accountInfo.level < 4 }">	
			<input type="button" value="导出Excel(请按照机房导出！)" id="exportExcel" title="请不要大量导出(一次少于5000条)!" />
 		</c:if>
 	</div>
	 
</fieldset>
<c:if test="${ page_limit == report.dataSize }"><div style="color:red;background: yellow;">***列表可能仅显示部分查询记录***</div></c:if>
<%-- 如果行数不多于3000行，下面一般不需要修改 --%>
<table border="1" class="data_table" id="data_table">
	<thead>
		<tr>
			<td><input type="checkbox" name="c0" id="c0">序号</td>
			<c:forEach items="${ report.displayedTitles }" var="t" varStatus="s">
				<td <c:if test="${s.index == report.keyIndex }">class="key_field"</c:if> >${ t }</td>
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
			<td <c:if test="${s0.index == report.keyIndex }">class="key_field"</c:if> >${ m.value }
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

