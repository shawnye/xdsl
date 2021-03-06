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
 * Servlet implementation class UserInfoPrepareEditServlet
 */
public class UserInfoPrepareEditServlet extends BaseServlet  {
	private static final long serialVersionUID = 1L;
       
   

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		boolean b = super.checkPrivilege(request, response, "用户信息编辑需要写权限.", 2);
		if(b){
			return;
		}
		String u_id = request.getParameter("u_id");
        UserInfoService userInfoService = (UserInfoService) wc.getBean("userInfoService"); 
		JxInfoService jxInfoService = (JxInfoService) wc.getBean("jxInfoService");

        AccountInfo accountInfo = super.getAccountInfo(request); 
        
        String type = null;
        String sn = null;
        String ont_id = null;
		if(StringUtils.isNotBlank(u_id)){//根据U_ID查询
	        Map userInfo = userInfoService.findByKey(u_id);
	        String jx = (String) userInfo.get("jx");
	        type = (String) userInfo.get("type");
	        sn = (String) userInfo.get("sn");
	        ont_id = (String) userInfo.get("ont_id");
	        
	        if(StringUtils.isNotBlank(jx) && StringUtils.isNotBlank(type)){
 				List sbhs = jxInfoService.findSbhLike( jx ,type );
				request.setAttribute("sbhs", sbhs);
				
 			}
	        
	        request.setAttribute("userInfo", userInfo);
		}else { //根据P_ID查询
			String p_id = request.getParameter("p_id");
			if(StringUtils.isNotBlank(p_id)){
				Map userInfo = userInfoService.findByPid(p_id);
				if(userInfo == null){
					gotoPopup(request, response, "用户资料不存在或者已经删除（拆机/退单）: " + p_id);
	    			 
	    			return;
				}
		        String jx = (String) userInfo.get("jx");
		        type = (String) userInfo.get("type");
		        sn = (String) userInfo.get("sn");
		        ont_id = (String) userInfo.get("ont_id");

		        String sbh = (String) userInfo.get("sbh");
		        
		        if(StringUtils.isNotBlank(jx) && StringUtils.isNotBlank(type)){
					List sbhs = jxInfoService.findSbhLike( jx ,type );
					request.setAttribute("sbhs", sbhs);
					
			        Integer slot = (Integer) userInfo.get("slot");
			        Integer sp_port = (Integer) userInfo.get("sp_port");
			        
			        List slots = jxInfoService.findSlotsLike( jx ,sbh );
			        request.setAttribute("slots", slots);
			        
			        List sbports = jxInfoService.findSbportsLike( jx ,sbh, "" + slot );
			        request.setAttribute("sb_ports", sbports);
			        
			        //FIXME 如何查找未占用+自身？--不必，在页面直接加
			        
	 			}
		        
		        request.setAttribute("userInfo", userInfo);
			}else{//根据J_ID查询
				String j_id = request.getParameter("j_id");
				if(StringUtils.isNotBlank(j_id)){
					Map jxInfo = jxInfoService.findByKey(j_id);
					if(jxInfo == null){
						gotoPopup(request, response, "端口资料不存在或者已经删除: " + j_id);
		    			 
		    			return;
					}
					
					Map userInfo = new HashMap<String, Object>();
					userInfo.putAll(jxInfo);
					userInfo.remove("u_id");//make sure it is new
					
					String jx = (String) userInfo.get("jx");
			        type = (String) userInfo.get("type");
			        sn = (String) userInfo.get("sn");
			        ont_id = (String) userInfo.get("ont_id");

			        String sbh = (String) userInfo.get("sbh");
			        
			        if(StringUtils.isNotBlank(jx) && StringUtils.isNotBlank(type)){
						List sbhs = jxInfoService.findSbhLike( jx ,type );
						request.setAttribute("sbhs", sbhs);
						
				        Integer slot = (Integer) userInfo.get("slot");
				        Integer sp_port = (Integer) userInfo.get("sp_port");
				        
				        List slots = jxInfoService.findSlotsLike( jx ,sbh );
				        request.setAttribute("slots", slots);
				        
				        List sbports = jxInfoService.findSbportsLike( jx ,sbh, "" + slot );
				        request.setAttribute("sb_ports", sbports);
				        
				        //FIXME 如何查找未占用+自身？--不必，在页面直接加
				        
		 			}
			        
			        if(StringUtils.isNotBlank(jx) ){
			        	userInfo.put("area", jxInfoService.extractArea(jx));
			        }
			        request.setAttribute("userInfo", userInfo);
				}
			}
 	        
		}
		
		request.setAttribute("daiwei",accountInfo.getDaiwei());

		if("FTTH".equalsIgnoreCase(type) ){
			StringBuilder alertMessage = new StringBuilder();
			if(StringUtils.isBlank(ont_id)){
				alertMessage.append("FTTH ONT端口号为空！\\n");
			}
			if(StringUtils.isBlank(sn)){
				alertMessage.append("FTTH SN为空！\\n");//for js alert!
			} 
			
			request.setAttribute("alertMessage", alertMessage.toString());

    	}
		
		request.getRequestDispatcher("/WEB-INF/editUserInfo.jsp").forward(request, response);

	}

}
