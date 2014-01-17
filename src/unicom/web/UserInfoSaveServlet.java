package unicom.web;

import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import unicom.bo.AccountInfo;
import unicom.xdsl.service.JxInfoService;
import unicom.xdsl.service.OntService;
import unicom.xdsl.service.UserInfoService;

/**
 * Servlet implementation class UserInfoEditServlet
 */
public class UserInfoSaveServlet extends BaseServlet  {
	private static final long serialVersionUID = 1L;
	private static final String String = null;
       
  
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		boolean b = super.checkPrivilege(request, response, "用户信息编辑需要写权限.", 2);
		if(b){
			return;
		}
		
        UserInfoService userInfoService = (UserInfoService) wc.getBean("userInfoService");
        JxInfoService jxInfoService = (JxInfoService) wc.getBean("jxInfoService");
        OntService ontService = (OntService) wc.getBean("ontService");
		AccountInfo accountInfo = super.getAccountInfo(request); 

        String u_id = request.getParameter("u_id");
        
        Map userInfo0 = null;
        if(StringUtils.isNotBlank(u_id)){
        	userInfo0 =userInfoService.findByKey(u_id);
    		if(userInfo0 == null){
    			gotoPopup(request, response, "用户资料不存在或对应多个端口，请联系管理员: " + u_id);
    			 
    			return;
    		}
        } 
        
        Map userInfo = new HashMap(); 
        Enumeration parameterNames = request.getParameterNames();
        for (; parameterNames.hasMoreElements();) {
			String name = (String) parameterNames.nextElement();
			if (name.startsWith("@") || name.startsWith("page_")) {
				// request.setAttribute(name,
				// request.getParameter(name));//reset
				continue;// 忽略@
			}
			String value = request.getParameter(name);//null也要设置
			userInfo.put(name, value!=null ? value.trim() : null); 

		}
        
        Integer j_id = null;
        String old_j_id = (String) userInfo.remove("old_j_id");//do compare

        String jx = (String) userInfo.remove("jx");
        String mdfPort = (String) userInfo.remove("mdf_port");
        
        String sbh = (String) userInfo.remove("sbh");
        String slot = (String) userInfo.remove("slot");
        String sb_port = (String) userInfo.remove("sb_port");
        
        String type = (String) userInfo.remove("type");

        //代维的不更新端口
        if(StringUtils.isNotBlank(u_id) && accountInfo.getDaiwei()){
        	j_id = new Integer(old_j_id);//原来的j_id
        }else{
        	//find new jid: 
        	if(StringUtils.isNotBlank(mdfPort)){
        		j_id = jxInfoService.findJid(jx, mdfPort, StringUtils.isBlank(u_id)? false: null);//u_id=null find unused port 
                if(j_id == null){//也可能是可能重复导致！
                	if(StringUtils.isBlank(u_id)){
          
                    	request.setAttribute("msg",  "保存失败,通路已经占用或者不存在：机房=" + jx + ",MDF端口=" + mdfPort);
                	}else{
                    	request.setAttribute("msg",  "保存失败,通路不存在：机房=" + jx + ",MDF端口=" + mdfPort);
                	}
                	log.error(request.getAttribute("msg"));
                	
         			gotoPopup(request, response, (java.lang.String) request.getAttribute("msg"));

//                	request.getRequestDispatcher("/UserInfoPrepareEdit?u_id=" + u_id).forward(request, response);
                	return;
                }
        	}else{
        		if(StringUtils.isBlank(slot) || StringUtils.isBlank(sb_port)){
                	request.setAttribute("msg",  "保存失败,Ftt：机房=" + jx + ",a.MDF端口和b.(槽号，端口号)同时为空");
        			gotoPopup(request, response, (java.lang.String) request.getAttribute("msg"));

//        			request.getRequestDispatcher("/UserInfoPrepareEdit?u_id=" + u_id).forward(request, response);
                	return;
        		}
        		
        		j_id = jxInfoService.findJid3(jx, sbh,slot,sb_port, StringUtils.isBlank(u_id)? false: null);//u_id=null find unused port 
                if(j_id == null){
                	if(StringUtils.isBlank(u_id)){
                    	request.setAttribute("msg",  "保存失败,通路已经占用或者不存在：机房=" + jx + ",设备号=" + sbh+ ",槽号=" + slot+ ",设备号=" + sb_port);
                	}else{
                    	request.setAttribute("msg",  "保存失败,通路不存在：机房=" + jx + ",设备号=" + sbh+ ",槽号=" + slot+ ",设备号=" + sb_port);
                	}
                	log.error(request.getAttribute("msg"));
                	
         			gotoPopup(request, response, (java.lang.String) request.getAttribute("msg"));

//                	request.getRequestDispatcher("/UserInfoPrepareEdit?u_id=" + u_id).forward(request, response);
                	return;
                }
        		
        	}
            
            
            userInfo.put("j_id", j_id);
        }
         
        
        String sn = (String) userInfo.remove("sn");//update jx_info
    	Map jxInfo = jxInfoService.findByKey(""+j_id);

    	//FIXME 同一个设备FTTH SN无法修改！无法区分是移机还是错误填写。
        if(StringUtils.isNotBlank(sn)){
        	if(jxInfo != null){
        		String sn0 = (String) jxInfo.get("sn");//原SN
        		if(StringUtils.isNotBlank(sn0) && !sn0.trim().equals(sn.trim())){
//        			request.setAttribute("userInfo", userInfo);
        			if(accountInfo.getAdmin()){
        				log.warn( "可能同一个设备FTTH SN冲突: 设备端口号(J_ID)=" + j_id + ",原SN=" + sn0 + ",您新填写的SN=" + sn);
        			}else{
        				gotoPopup(request, response, "同一个设备FTTH SN冲突: 设备端口号(J_ID)=" + j_id + ",原SN=" + sn0 + ",您新填写的SN=" + sn);
              			 
            			return;
        			}
        			
        		}
        	}
        }

       
        
        
    	String ont_id = (String) userInfo.get("ont_id");
    	String old_ont_id = (String) userInfo.remove("old_ont_id");
    	
    	Integer ont_id_i = null;
    	//判断ont_id是否合法
    	if(StringUtils.isNotBlank(ont_id)){
    		try {
				ont_id_i = new Integer(ont_id.trim());
			} catch (NumberFormatException e) {
				gotoPopup(request, response, "ONT端口不是数字:" + ont_id);
      			 
    			return;
			}
    		 
    	}
    	
    	if(ont_id_i != null){
     		
    		Integer ont_ports = (Integer) jxInfo.get("ont_ports");
        	if(ont_ports != null && ont_ports > 0){
        		if(ont_id_i < 1 || ont_id_i > ont_ports){
        			gotoPopup(request, response, "ONT端口不再合法范围内【1-"+ont_ports+"】:" + ont_id);
         			 
        			return;
        		}
        	}
    	}
    	
        if(StringUtils.isBlank(u_id)){
        	//判断ont_id是否合法 
        	
        	userInfo.put("state", "预分配"); 
        	userInfo.put("begin_date", new Date());  

        	boolean occupied = jxInfoService.portOccupied("" + j_id, ont_id);
        	if(occupied){
//        		request.setAttribute("userInfo", userInfo);
        		
            	request.setAttribute("msg",  "保存失败,ONT端口已经占用：J_ID="+ j_id +",机房=" + jx + ",MDF端口=" + mdfPort + ",ONT端口号=" + ont_id);
            	log.error(request.getAttribute("msg")); 
            	
            	gotoPopup(request, response, (String)request.getAttribute("msg"));
   			 
    			return;
    			//错误提示
//            	request.getRequestDispatcher("/UserInfoPrepareEdit?u_id=" + u_id).forward(request, response);
//            	return;
        	}


            
        }else{//修改局端设备端口、ONT设备端口时也要判断.(FTTH)
//        	if("FTTH".equalsIgnoreCase(type)){
//        		
//        	}
        	if("".equals(old_ont_id)){
        		old_ont_id = null;
        	}
        	if(!old_j_id.equals("" + j_id) || (old_ont_id != null && !old_ont_id.equals(ont_id))   ){
        		boolean occupied = jxInfoService.portOccupied("" + j_id, ont_id);
            	if(occupied){
//            		request.setAttribute("userInfo", userInfo);
            		
                	request.setAttribute("msg",  "保存失败,不能更改端口,机房端口(J_ID)或ONT端口已经占用：J_ID="+ j_id +",机房=" + jx + ",MDF端口=" + mdfPort + ",ONT端口号=" + ont_id);
                	log.error(request.getAttribute("msg")); 
                	
                	gotoPopup(request, response, (String)request.getAttribute("msg"));
//                	request.getRequestDispatcher("/UserInfoPrepareEdit?u_id=" + u_id).forward(request, response);
                	return;
            	}
        		
        	} 
        	
        }
        
        
		log.debug("["+accountInfo+"]保存用户信息...u_id=" + u_id);
		
		try {
			if(StringUtils.isNotBlank(sn)){
				jxInfoService.updateAField(j_id, "sn", sn);
			} 

			u_id = userInfoService.save(userInfo);
			
			
			request.setAttribute("msg",  "保存成功！" + (accountInfo.getDaiwei()? "您不能在此修改端口信息（灰色部分）":"") );
			
 			if(StringUtils.isNotBlank(u_id)){
				logService.log("修改用户信息","产品号码="+userInfo.get("p_id")+",\t用户名="+userInfo.get("username")+",\tJ_ID="+j_id +",\t原J_ID="+ old_j_id , super.getAccountInfo(request).getAccount());

			}else{
				logService.log("新增用户信息","产品号码="+userInfo.get("p_id")+",\t用户名="+userInfo.get("username")+",\tJ_ID="+j_id +",\tONT_ID="+ont_id +",\tSN="+sn , super.getAccountInfo(request).getAccount());

			}

		} catch (Exception e) {
			log.error( "保存失败",e);
			
			request.setAttribute("msg",  "保存失败：" + e.getMessage());
			
			gotoPopup(request, response, (String)request.getAttribute("msg"));
			return;


		}
		 
        
		log.info("保存成功，产品号码="+userInfo.get("p_id")+",\t用户名="+userInfo.get("username")+",\tJ_ID="+userInfo.get("j_id"));
		
		request.setAttribute("daiwei",accountInfo.getDaiwei());
		 
		request.getRequestDispatcher("/UserInfoPrepareEdit?p_id=" + userInfo.get("p_id")).forward(request, response);

		
		
	}

}
