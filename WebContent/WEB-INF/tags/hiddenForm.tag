<%@ tag language="java" pageEncoding="utf-8"%>
<%@attribute name="action" required="true" rtexprvalue="true"%>
<%@attribute name="fieldNames" required="true" rtexprvalue="true"%>
<%@attribute name="formId" required="false" rtexprvalue="true"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%--
支持中文，支持多个参数，避免使用get传递参数的乱码问题！
 --%>
<div style="display:none;">
<script type="text/javascript">
function submitHidden<c:out value="${formId}" default="Form"/>(obj, formId){
	if((typeof formId == 'undefined') || !formId){
		formId = '<c:out value="${formId}" default="batchUpdateForm"/>';
	} 
	
	formId = '#' + formId;//jquery instead of prototype
	
	var form2 = $(formId).get(0);//jquery
	
	/*
	for(p in obj){
	alert(p + '=' + obj[p]);
	}
	*/

	<c:forTokens items="${fieldNames}" delims="," var="n">
	form2.${ n }.value = obj.${n};
	</c:forTokens>

	form2.submit();
}
</script>
<form action="${action }" accept-charset="utf-8" method="post" target="_blank" id='<c:out value="${formId}" default="batchUpdateForm"/>'>
 	<c:forTokens items="${fieldNames}" delims="," var="n">
	<input type="hidden" name="${ n }" id="${n }" value=""/>
	</c:forTokens>
  </form>
</div>