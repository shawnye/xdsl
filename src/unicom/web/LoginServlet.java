package unicom.web;

import java.io.IOException;
import java.util.EnumSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import unicom.bo.AccountInfo;
import unicom.bo.RoleType;
import unicom.xdsl.service.AuthenticationService;
import unicom.xdsl.service.MenuService;

/**
 * Servlet implementation class LoginServlet
 */
public class LoginServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoginServlet() {
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
		String account = (String) request.getParameter("account");
		String password = (String) request.getParameter("password");
		
	
		
		AuthenticationService authenticationService = (AuthenticationService) wc.getBean("authenticationService");
		AccountInfo accountInfo = null;
		try {
			accountInfo = authenticationService.login(account, password, request.getRemoteAddr());
			accountInfo.setSessionId(request.getSession().getId());
//			accountInfo.setLoginTime(new Date());
//			accountInfo.setIp(request.getRemoteAddr());
			String ua = request.getHeader("user-agent");
			//操作平台(版本)windows,linux, android ,iphone ,nokia,mac
			
			//浏览器类型MSIE ,Gecko, WebKit, Konqueror ,Opera, Chrome
			log.debug("成功登录用户["+account+"]的User-Agent:\n" + ua);
			
			LoginUserList.getInstance().addLoginUser(accountInfo);
		} catch (RuntimeException e) {
			log.error("登录失败:" + account + ", 来自 " + request.getRemoteAddr() + "(原因："+ e.getMessage() +")");
			request.setAttribute("login_error", e.getMessage());
			request.setAttribute("account", account);
			request.getRequestDispatcher("/WEB-INF/login.jsp").forward(request, response);
			return;
		}
		log.info("登录成功：" + account + ", 来自 " + request.getRemoteAddr());
		request.getSession().setAttribute("accountInfo", accountInfo);
		
		logService.log("登录", "IP=" + request.getRemoteAddr(), account);
		
		MenuService menuService = (MenuService) wc.getBean("menuService");

		if(StringUtils.isNotBlank(accountInfo.getRoles())){
			try {
				String menu = menuService.getRootMenuHtml(RoleType.valueOf(accountInfo.getRoles()),request.getContextPath());
				
				request.getSession().setAttribute("menu", menu);
			} catch (Exception e) {
				log.error("账号权限设置错误，无法显示菜单 ："+ e.getMessage() );
				request.setAttribute("login_error", "账号权限设置错误，无法显示菜单 ，请联系管理员.");
				request.setAttribute("account", account);
				request.getRequestDispatcher("/WEB-INF/login.jsp").forward(request, response);
				return;
			}
			
			EnumSet<RoleType> roleTypes = EnumSet.allOf(RoleType.class);
			request.getSession().setAttribute("roleTypes", roleTypes);

		}
		
		request.getRequestDispatcher("/WEB-INF/main.jsp").forward(request, response);

	}

}
