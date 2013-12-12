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
 * Servlet implementation class ListJxInfoServlet
 */
public class JxInfoListServlet extends BaseListServlet {
	private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public JxInfoListServlet() {
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

		AbstractService jxInfoService = (AbstractService) wc.getBean("jxInfoService");
		Page<Map> page = jxInfoService.listAsMap(searchCondition , super.getPageNo(request) , super.getPageSize(request));

		request.setAttribute("jxCount", page.getAdditionInfo("jxCount"));
//		System.out.println(page.getTotalItems());

		request.setAttribute("page", page);
		
		AccountInfo accountInfo = super.getAccountInfo(request); 
		request.setAttribute("daiwei",accountInfo.getDaiwei());

		request.getRequestDispatcher("/WEB-INF/listJxInfo.jsp").forward(request, response);
	}

	@Override
	protected void setDefaultCondition(HttpServletRequest request, SearchConditions searchCondition) {
//		String key = "jx";
//		searchCondition.addCondition(key ,"江门高沙");
//		request.setAttribute(searchCondition.getEchoField(key), "江门高沙");

		String key = "j_id=";
		searchCondition.addCondition(key ,"-1");
		request.setAttribute(searchCondition.getEchoField(key), "-1");
		 
		//问题是很多情况下是''
		key = "mask=";
		searchCondition.addCondition(key ,"[is null]");
		request.setAttribute(searchCondition.getEchoField(key), "[is null]");
	}
}
