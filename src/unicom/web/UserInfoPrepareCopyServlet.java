package unicom.web;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import unicom.bo.AccountInfo;
import unicom.xdsl.service.JxInfoService;
import unicom.xdsl.service.UserInfoService;

/**
 * 复制用户信息，不含产品信息和通路
 * Servlet implementation class UserInfoPrepareEditServlet
 */
public class UserInfoPrepareCopyServlet extends BaseServlet  {
	private static final long serialVersionUID = 1L;
       
   

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		boolean b = super.checkPrivilege(request, response, "用户信息复制编码需要写权限.", 2);
		if(b){
			return;
		}
		String u_id = request.getParameter("u_id");
        UserInfoService userInfoService = (UserInfoService) wc.getBean("userInfoService"); 
//        AccountInfo accountInfo = super.getAccountInfo(request); 
        
		if(StringUtils.isNotBlank(u_id)){
	        Map userInfo = userInfoService.findByKey(u_id);
	        Map userInfoCopied = new HashMap();
	        
	        String[] copiedFields = "username,tel,address,area,jx,branch,remark".split("\\,");
	        for (int i = 0; i < copiedFields.length; i++) {
	        	userInfoCopied.put(copiedFields[i], userInfo.get(copiedFields[i]));
			}
	        
	        
	        request.setAttribute("userInfo", userInfoCopied);
		}else {
			gotoPopup(request, response, "用户资料不存在: U_ID=" + u_id); 
			return; 
		}
		
//		request.setAttribute("daiwei",accountInfo.getDaiwei());

		
		request.getRequestDispatcher("/WEB-INF/editUserInfo.jsp").forward(request, response);

	}

}
