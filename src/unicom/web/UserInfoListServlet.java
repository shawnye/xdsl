package unicom.web;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import unicom.bo.AccountInfo;
import unicom.common.Page;
import unicom.common.SearchConditions;
import unicom.xdsl.service.AbstractService;

/**
 * Servlet implementation class ListUserInfoServlet
 */
public class UserInfoListServlet extends BaseListServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UserInfoListServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		Enumeration parameterNames = request.getParameterNames();
		
		SearchConditions searchCondition = new SearchConditions();
		
		for (; parameterNames.hasMoreElements();) {
			String name = (String) parameterNames.nextElement();
			if(name.startsWith("@")){
//				request.setAttribute(name, request.getParameter(name));//reset
				continue;//忽略@
			}
			String value = request.getParameter(name);//FIXME 只有string类型
			if(value != null && StringUtils.isNotBlank(value)){
				value = value.trim();

				searchCondition.addCondition(name, value);
				request.setAttribute(searchCondition.getEchoField(name), value);//回显
			}
			
		}
		
		
		
		Boolean defaultCond=Boolean.valueOf(request.getParameter("@default"));
		if(defaultCond){
//			设置默认条件
			this.setDefaultCondition(request, searchCondition);
		}
		
		if(searchCondition.getConditions().size() == 0){
			request.setAttribute("popMsg", "必须填写查询条件!");
			request.getRequestDispatcher("/WEB-INF/popup.jsp").forward(request, response);
			return;
		}
		
//		Integer pageNo = 1;
//		Integer pageSize = 10;
//		
//		try {
//			pageNo = new Integer(request.getParameter("@pageNo"));
//		} catch (NumberFormatException e) {
//		}
//		
//		try {
//			pageSize = new Integer(request.getParameter("@pageSize"));
//		} catch (NumberFormatException e) {
//		}
		
		AbstractService userInfoService = (AbstractService) wc.getBean("userInfoService");
		Page<Map> page = userInfoService.listAsMap(searchCondition , super.getPageNo(request) , super.getPageSize(request));
		
//		System.out.println(page.getTotalItems());
		
		request.setAttribute("page", page);
		
        AccountInfo accountInfo = super.getAccountInfo(request); 

		request.setAttribute("daiwei",accountInfo.getDaiwei());

		
		request.getRequestDispatcher("/WEB-INF/listUserInfo.jsp").forward(request, response);
	}

	@Override
	protected void setDefaultCondition(HttpServletRequest request, SearchConditions searchCondition) {
//		String key = "begin_date>=";
//		searchCondition.addCondition(key ,DateHelper.getLastMonthStart(5));//半年内
//		
//		request.setAttribute(searchCondition.getFieldStart(key), DateFormatUtils.format(searchCondition.getConditionValueAsDate(key), "yyyy-MM-dd"));
		String key = "u_id=";
		searchCondition.addCondition(key ,"-1");
		request.setAttribute(searchCondition.getEchoField(key), "-1");
	}

}
