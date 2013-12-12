package unicom.web;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import unicom.bo.AccountInfo;
import unicom.xdsl.service.JxInfoService;
import unicom.xdsl.service.UserInfoService;

/**
 * Servlet implementation class ChangePortServlet
 */
public class ChangePortServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
  
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		boolean b = super.checkPrivilege(request, response, "端口更新功能需要写权限.", 2);
		if(b){
			return;
		}

		UserInfoService userInfoService = (UserInfoService) wc.getBean("userInfoService");
		
		String u_id = request.getParameter("u_id");
//		String old_j_id = request.getParameter("old_j_id");
		String new_j_id = request.getParameter("new_j_id");
		
		if(StringUtils.isBlank(new_j_id)){
			gotoPopup(request, response, "新J_ID为空！" );
			 
			return;
		}
		
		String new_ont_id = request.getParameter("new_ont_id");
		//must trim preceding - zero
		if(StringUtils.isNotBlank(new_ont_id)){
			new_ont_id = new_ont_id.trim().replaceFirst("^0+", "");
		}
		
		Map userInfo = userInfoService.findByKey(u_id);
		if(userInfo == null){
			gotoPopup(request, response, "用户资料不存在或对应多个端口，请联系管理员: " + u_id);
			 
			return;
		}
		
		AccountInfo accountInfo = super.getAccountInfo(request);
		log.debug("["+accountInfo+"]修改用户端口...u_id=" + userInfo.get("u_id"));
		
		Boolean makeFault = Boolean.valueOf(request.getParameter("makeFault"));
		String type = (String) userInfo.get("type");
		
		if(type != null && type.trim().equalsIgnoreCase("FTTH")){//原端口为FTTH
//			if(StringUtils.isBlank(new_ont_id)){
//				request.setAttribute("error", "FTTH端口必须填写ONT端口，J_ID=" + new_j_id);
//				request.getRequestDispatcher("/WEB-INF/changePort.jsp").forward(request, response);
//				 
//				return;
//			}
			
		}else{
			new_ont_id = null;//自动忽略
		}

		if(makeFault){//必须确保端口无用户数据
			if(type != null && type.trim().equalsIgnoreCase("FTTH")){
				Integer used_ont_ports = (Integer) userInfo.get("used_ont_ports");
				if(used_ont_ports != null && used_ont_ports > 1){
 					request.setAttribute("error", "对于原FTTH机房端口而言，ONT端口已占用数>1时，不能置坏: ONT端口已占用数=" + used_ont_ports);
					request.getRequestDispatcher("/WEB-INF/changePort.jsp").forward(request, response);
					 
					return;
 				}
			}
			
			userInfo.put("used", "已坏");
		}
		
		request.setAttribute("userInfo",userInfo);//含通路
		
		JxInfoService jxInfoService = (JxInfoService) wc.getBean("jxInfoService");
		
		Integer j_id = (Integer) userInfo.get("j_id");
		String ont_id = (String) userInfo.get("ont_id");
		Integer ont_ports = (Integer) userInfo.get("ont_ports");


		Map newPort = new HashMap();

		if(new_j_id.equals("" + j_id)){//同一个机房设备端口
			
			if(type != null && type.trim().equalsIgnoreCase("FTTH")){
				if(new_ont_id != null && new_ont_id.equals(ont_id)){//同一个ONT端口
 					request.setAttribute("error", "FTTH机房端口（J_ID)和ONT端口都没有改变，系统不做任何修改: J_ID=" + j_id + ",ONT端口=" + ont_id);
					request.getRequestDispatcher("/WEB-INF/changePort.jsp").forward(request, response);
						
					return;
				}else{
					//不同ONT端口
					if(new_ont_id != null && ont_ports !=null && ont_ports > 0){
						try {
							Integer noi = new Integer(new_ont_id.trim());
							if(noi < 0 || noi > ont_ports){
 	 							
								request.setAttribute("error", "ONT端口不在ONT端口总数范围之内[1-" + ont_ports + "]：" + ont_id );
	 							request.getRequestDispatcher("/WEB-INF/changePort.jsp").forward(request, response);
	 							
	 							
	 							return;
							}
							
						} catch (NumberFormatException e) {
 							log.error("新ONT端口非数字或为空：" + new_ont_id); 
 							request.setAttribute("error", "新ONT端口非数字或为空：" + new_ont_id );
 							request.getRequestDispatcher("/WEB-INF/changePort.jsp").forward(request, response);
 							
 							return;
						}
						
					}
				}
			}else{//非FTTH
 				request.setAttribute("error", "机房端口（J_ID)没有改变，系统不做任何修改: J_ID=" + j_id );
				request.getRequestDispatcher("/WEB-INF/changePort.jsp").forward(request, response);
				return;
				 
			}
			 
		}else{//机房端口不同,一定更新
			
		}
		
		newPort = jxInfoService.findByKey(new_j_id);//not masked

		if(newPort == null){
			request.setAttribute("error", "不存在此J_ID或者端口已经屏蔽: " + new_j_id);
			request.getRequestDispatcher("/WEB-INF/changePort.jsp").forward(request, response);
			return;
		}else{
			if(StringUtils.isNotBlank(new_j_id) && newPort.get("used") == null){
				request.setAttribute("error", "此J_ID已经置坏: " + new_j_id);
				request.getRequestDispatcher("/WEB-INF/changePort.jsp").forward(request, response);
				return;
			}
			
			
		}
 		
		String newType = (String) newPort.get("type");
		if("FTTH".equalsIgnoreCase(newType.trim()) && StringUtils.isBlank(new_ont_id)){
			request.setAttribute("error", "新端口[J_ID="+new_j_id+"]类型为FTTH，必须填写新ONT端口");
			request.getRequestDispatcher("/WEB-INF/changePort.jsp").forward(request, response);
			return;
		}
		
		String oldType = (String) userInfo.get("type");
		if("FTTH".equalsIgnoreCase(newType.trim()) && "FTTH".equalsIgnoreCase(oldType.trim())){
			String dest_sn = (String) newPort.get("sn");
			String src_sn = (String) userInfo.get("sn");
//没有判断空的时候如何处理
			if(StringUtils.isNotBlank(src_sn) && StringUtils.isNotBlank(dest_sn)){
				if(!dest_sn.trim().equalsIgnoreCase(src_sn.trim())){
					request.setAttribute("error", "FTTH新旧SN冲突，新端口[J_ID="+new_j_id+"]的原SN=" + dest_sn + ", 新SN=" + src_sn);//使用原端口覆盖新端口的SN
					request.getRequestDispatcher("/WEB-INF/changePort.jsp").forward(request, response);
					return;
				}
			}
		}
		 
 			String remark = request.getParameter("remark");
			
			try {
				userInfoService.changePort(u_id, new_j_id, new_ont_id, makeFault, remark, accountInfo.getAccount());
				
				if(StringUtils.isNotBlank(new_ont_id)){
					userInfo.put("ont_id", new_ont_id);
				}
				
				
				String r = "产品号码="+userInfo.get("p_id")+",\t用户名="+userInfo.get("username")+",\t新J_ID="+new_j_id+",\t原J_ID="+ j_id;
				if(type != null && type.trim().equalsIgnoreCase("FTTH")){
					r += ",\t新ONT端口="+new_ont_id+",\t原ONT端口="+ ont_id;
				}
				r += ";\t备注=" + remark;
				logService.log("更新端口", r , accountInfo.getAccount());
				log.info("更新端口：u_id=" + u_id);
			} catch (Exception e) {
				request.setAttribute("error", e.getMessage());
			}
		 
			newPort = jxInfoService.findByKey(new_j_id);//refresh
			request.setAttribute("newPort",newPort);
		
//		request.setAttribute("message", "更新成功: " + new_j_id);
		request.getRequestDispatcher("/WEB-INF/changePort.jsp").forward(request, response);

 	}

}
