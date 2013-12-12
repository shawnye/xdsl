package unicom.web;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import unicom.xdsl.service.UserInfoService;

/**
 * Servlet implementation class ChangePortServlet
 */
public class PrepareChangePortServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
  
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		boolean b = super.checkPrivilege(request, response, "更新端口需要写权限.", 2);
		if(b){
			return;
		}

		UserInfoService userInfoService = (UserInfoService) wc.getBean("userInfoService");
		
		String u_id = request.getParameter("u_id");
  
		Map userInfo = userInfoService.findByKey(u_id);
		if(userInfo == null){
			gotoPopup(request, response, "用户资料不存在或对应多个端口，请联系管理员: " + u_id);
			 
			return;
		}
		
		request.setAttribute("userInfo", userInfo);
		 
		request.getRequestDispatcher("/WEB-INF/changePort.jsp").forward(request, response);

 	}

}
