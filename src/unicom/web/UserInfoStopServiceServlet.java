package unicom.web;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.time.DateUtils;

import unicom.common.Constants;
import unicom.xdsl.service.UserInfoService;

/**
 * Servlet implementation class StopServiceServlet
 */
public class UserInfoStopServiceServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public UserInfoStopServiceServlet() {
        super();
        // TODO Auto-generated constructor stub
    }


	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		boolean b = super.checkPrivilege(request, response, "(待)停机更新功能需要写权限.", 2);
		if(b){
			return;
		}

		UserInfoService userInfoService = (UserInfoService) wc.getBean("userInfoService");

		Date stopDate = null;

		try {
			stopDate = DateUtils.parseDate(request.getParameter("stop_date"), Constants.DEFAULT_DATE_PATTERNS);
		} catch (Exception e) {
			log.error("fail to get stop_date: " + e.getMessage());
		}
		String p_id = request.getParameter("p_id");
		Boolean wait = Boolean.valueOf(request.getParameter("wait"));
		int updates = userInfoService.stopService(wait , p_id,  stopDate);
		log.info( p_id +  (wait?"待":"")+ "停机数量:" + updates);
		request.setAttribute("popMsg", p_id +  (wait?"待":"")+ "停机数量：" + updates);
		request.getRequestDispatcher("/WEB-INF/popup.jsp").forward(request, response);
	}

}
