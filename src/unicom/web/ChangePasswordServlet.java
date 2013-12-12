package unicom.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import unicom.bo.AccountInfo;
import unicom.xdsl.service.AuthenticationService;

/**
 * Servlet implementation class ChangePortServlet
 */
public class ChangePasswordServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
  
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String np = request.getParameter("np");
		AccountInfo accountInfo = super.getAccountInfo(request);
		if(StringUtils.isBlank(np)){
			gotoPopup(request, response, "新密码为空，不能修改：" + accountInfo);
			return;
		}
		accountInfo.setPassword(np);
		AuthenticationService authenticationService = (AuthenticationService) wc.getBean("authenticationService");
		authenticationService.updateAField(accountInfo.getM_id(), "password", np);
		
		gotoPopup(request, response, "新密码已经修改成功：" + accountInfo);

		
 	}

}
