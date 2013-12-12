package unicom.web;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import unicom.common.Page;
import unicom.common.SearchConditions;
import unicom.xdsl.service.ZytjService;

/**
 * Servlet implementation class ZytjListServlet
 */
public class ZytjListServlet extends BaseListServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ZytjListServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
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
		
		
		
		ZytjService zytjService = (ZytjService) wc.getBean("zytjService");
		Page<Map> page = zytjService.listAsMap(searchCondition , super.getPageNo(request) , super.getPageSize(request));

		request.setAttribute("page", page);
		
		request.getRequestDispatcher("/WEB-INF/listZytj.jsp").forward(request, response);
		
	
	}

	@Override
	protected void setDefaultCondition(HttpServletRequest request, SearchConditions searchCondition) {
		String key = "jfmc";
		searchCondition.addCondition(key ,"江门");
		
		request.setAttribute(searchCondition.getEchoField(key), searchCondition.getConditionValue(key));

	}
}
