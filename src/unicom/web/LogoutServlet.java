package unicom.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import unicom.bo.AccountInfo;
import unicom.xdsl.service.AuthenticationService;

/**
 * Servlet implementation class LogoutServlet
 */
public class LogoutServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LogoutServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		LoginUserList.getInstance().killoutLoginUser(request.getSession().getId());
		AccountInfo accountInfo = (AccountInfo) request.getSession().getAttribute("accountInfo");
		if(accountInfo == null){
			log.info("用户已经退出" ); 
			request.getSession().invalidate();
			request.getRequestDispatcher("/").forward(request, response);
			return;
		}
		AuthenticationService authenticationService = (AuthenticationService) wc.getBean("authenticationService");
		authenticationService.logout(accountInfo.getAccount());
		
		log.info("用户退出：" + accountInfo.getAccount());
		request.getSession().invalidate();
		
		request.getRequestDispatcher("/").forward(request, response);
	}

}
