package unicom.web;

import javax.servlet.http.HttpServletRequest;

import unicom.common.SearchConditions;

@SuppressWarnings("serial")
public abstract class BaseListServlet extends BaseServlet {

	public BaseListServlet() {
		super();
	}
	
	protected void setDefaultCondition(HttpServletRequest request, SearchConditions searchCondition){
		
	}

	
	protected Integer getPageNo(HttpServletRequest request){
		Integer pageNo = 1;
		
		try {
			pageNo = new Integer(request.getParameter("@pageNo"));
		} catch (NumberFormatException e) {
		}
		
		return pageNo;
	}
	
	
	protected Integer getPageSize(HttpServletRequest request){
		Integer pageSize = 10;
		
		try {
			pageSize = new Integer(request.getParameter("@pageSize"));
		} catch (NumberFormatException e) {
		}
		
		return pageSize;
	}
	
}