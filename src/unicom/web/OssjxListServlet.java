package unicom.web;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import unicom.common.SearchConditions;
import unicom.xdsl.service.OssJxInfoService;

/**
 * Servlet implementation class OssjxListServlet
 */
public class OssjxListServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public OssjxListServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		OssJxInfoService ossJxInfoService = (OssJxInfoService) wc.getBean("ossJxInfoService");

		SearchConditions searchCondition = new SearchConditions();
		List<Map> ossjx = ossJxInfoService.listAllAsMap(searchCondition );
		
		request.setAttribute("ossjx", ossjx);
		
		request.getRequestDispatcher("/WEB-INF/listOssjx.jsp").forward(request, response);

	}

}
