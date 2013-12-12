package unicom.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import unicom.xdsl.service.UserInfoService;

/**
 * Servlet implementation class UserInfoRestoreServiceServlet
 */
@Deprecated
public class UserInfoRestoreServiceServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UserInfoRestoreServiceServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		boolean b = super.checkPrivilege(request, response, "复机更新功能需要写权限.", 2);
		if(b){
			return;
		}
		UserInfoService userInfoService = (UserInfoService) wc.getBean("userInfoService");
		String p_id = request.getParameter("p_id");
		int updates = userInfoService.restoreServie(request.getParameter("p_id"));
		log.info( p_id + "复机数量:" + updates);
		request.setAttribute("popMsg", "复机数量：" + updates);
		request.getRequestDispatcher("/WEB-INF/popup.jsp").forward(request, response);
	}

}
