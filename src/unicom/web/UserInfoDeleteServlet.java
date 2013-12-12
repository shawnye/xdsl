package unicom.web;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import unicom.xdsl.service.UserInfoService;

/**
 * 拆机
 */
public class UserInfoDeleteServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UserInfoDeleteServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		boolean b = super.checkPrivilege(request, response, "拆机需要写权限.", 2);
		if(b){
			return;
		}
		
		UserInfoService userInfoService = (UserInfoService) wc.getBean("userInfoService");
  	        
	        String u_id = request.getParameter("u_id");
	        String deleteType = request.getParameter("delete_type");
	        String remark = request.getParameter("remark");
	        
	        Map userInfo = null;
	        if(StringUtils.isNotBlank(u_id)){
	        	userInfo =userInfoService.findByKey(u_id);
	    		if(userInfo == null){
	    			gotoPopup(request, response, "用户资料不存在或对应多个端口，请联系管理员: " + u_id);
	    			 
	    			return;
	    		}
	        } 
	        String event = "删除用户信息(未分类)";
	        if("0".equals(deleteType)){//拆机
	        	event = "拆机竣工";
	        }else if("1".equals(deleteType)){//退单
	        	event = "退单";
	        }
	        
	        try {
				userInfoService.deleteUserInfo(u_id, deleteType);
				
				if("0".equals(deleteType)){//拆机
					logService.log(event,"产品号码="+userInfo.get("p_id")+",\t用户名="+userInfo.get("username")+",\tJ_ID="+userInfo.get("j_id") +",\tSN="+userInfo.get("sn")+",\tONT端口="+userInfo.get("ont_id") , super.getAccountInfo(request).getAccount());

				}else if("1".equals(deleteType)){//退单
					logService.log(event,"产品号码="+userInfo.get("p_id")+",\t用户名="+userInfo.get("username")+",\t地址="+userInfo.get("address")+",\t帐号="+userInfo.get("user_no")+",\tJ_ID="+userInfo.get("j_id") +",\tSN="+userInfo.get("sn")+",\tONT端口="+userInfo.get("ont_id")+",\t录入时间="+userInfo.get("begin_date")
							+",\n退单原因="+ remark, 
							super.getAccountInfo(request).getAccount());
 
 				}
				

			} catch (Exception e) {
				
				request.setAttribute("popMsg", "X "+event+"失败("+e.getMessage()+ "):产品号码="+userInfo.get("p_id")+",\t用户名="+userInfo.get("username")+",\tJ_ID="+userInfo.get("j_id")+",\tSN="+userInfo.get("sn"));
				log.error(request.getAttribute("popMsg"));
				request.getRequestDispatcher("/WEB-INF/popup.jsp").forward(request, response);
				return ;
			}
	        
	        request.setAttribute("popMsg", event + "成功:产品号码="+userInfo.get("p_id")+",\t用户名="+userInfo.get("username")+",\tJ_ID="+userInfo.get("j_id")+",\tSN="+userInfo.get("sn") );
	        log.info(request.getAttribute("popMsg"));
			request.getRequestDispatcher("/WEB-INF/popup.jsp").forward(request, response);
	        
	}

}
