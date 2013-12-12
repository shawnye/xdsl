package unicom.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import unicom.bo.AccountInfo;

/**
 * Servlet Filter implementation class CommonFilter
 */
public class CommonFilter implements Filter {
	protected Log log = LogFactory.getLog(this.getClass());
    /**
     * Default constructor. 
     */
    public CommonFilter() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		//编码
//		request.setCharacterEncoding("utf-8");
		//use org.springframework.web.filter.CharacterEncodingFilter
		
		
		//权限
		HttpServletRequest req = (HttpServletRequest) request;
		String requestURI = req.getServletPath();
		

		if(! requestURI.contains(".") && ! requestURI.equals("/Login") && ! requestURI.equals("/Logout")){
			AccountInfo accountInfo = (AccountInfo) req.getSession().getAttribute("accountInfo");

			if(accountInfo == null){
				request.setAttribute("login_error", "请登录");
				request.getRequestDispatcher("/WEB-INF/login.jsp").forward(request, response);
				return ;
			}
			
			log.debug("帐号[" + accountInfo.getAccount() + "] 访问: " + requestURI);
			
		}
		
		
//		req.getSession().getAttribute("account");
//		req.getSession().setAttribute("account", "ShawnYe");

		// pass the request along the filter chain
		chain.doFilter(request, response);
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

}
