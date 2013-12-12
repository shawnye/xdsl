package unicom.web;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import unicom.bo.UserInfoState;
import unicom.xdsl.service.UserInfoService;

/**
 * 修改用户状态：预拆机,正常，其他状态。。。
 */
public class UserInfoChangeStateServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UserInfoChangeStateServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		boolean b = super.checkPrivilege(request, response, "更新用户状态需要写权限.", 2);
		if(b){
			return;
		}
		
		UserInfoService userInfoService = (UserInfoService) wc.getBean("userInfoService");
  	        
	        String u_id = request.getParameter("u_id");
	        
	        String state = request.getParameter("state");
	        
	        Boolean recover = Boolean.valueOf(request.getParameter("recover"));
	        
	        if(StringUtils.isBlank(state)){
	        	gotoPopup(request, response, "用户状态为空，请联系管理员");
	   			 
    			return;
	        }
	        UserInfoState userInfoState = null;
	        try {
				userInfoState = UserInfoState.valueOf(state);
			} catch (Exception e1) {
				log.error(e1);
				gotoPopup(request, response, "非法用户状态，请联系管理员: " + state);
   			 
    			return;
				
			}
	        
	        
	        Map userInfo = null;
	        if(StringUtils.isNotBlank(u_id)){
	        	userInfo =userInfoService.findByKey(u_id);
	    		if(userInfo == null){
	    			gotoPopup(request, response, "用户资料不存在或对应多个端口，请联系管理员: " + u_id);
	    			 
	    			return;
	    		}
	        } 
	        
	        
			userInfoService.updateAField(u_id, "state", userInfoState.getLabel());
			if(UserInfoState.PREDELETE.equals(userInfoState)){//预拆机更新时间
				userInfoService.updateAField(u_id, "del_date", new Date());

	        }
			if(recover){
				userInfoService.updateAField(u_id, "del_date", null);

			}
			
			logService.log((recover?"恢复":"") + userInfoState.getLabel(),"产品号码="+userInfo.get("p_id")+",\t用户名="+userInfo.get("username")+",\t状态="+ userInfoState.getLabel() +",\t原状态="+userInfo.get("state") +",\tJ_ID="+userInfo.get("j_id") , super.getAccountInfo(request).getAccount());

	        
	        request.setAttribute("popMsg", "用户状态修改成功：产品号码="+userInfo.get("p_id")+",\t用户名="+userInfo.get("username")+",\t状态="+ userInfoState.getLabel() +",\t原状态="+userInfo.get("state") +",\tJ_ID="+userInfo.get("j_id") );
	        log.info(request.getAttribute("popMsg"));
			request.getRequestDispatcher("/WEB-INF/popup.jsp").forward(request, response);
	        
	}

}
