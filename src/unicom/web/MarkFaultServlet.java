package unicom.web;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import unicom.bo.AccountInfo;
import unicom.xdsl.service.JxInfoService;

/**
 * Servlet implementation class MarkFaultServlet
 */
public class MarkFaultServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MarkFaultServlet() {
        super();
        // TODO Auto-generated constructor stub
    }
 
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		boolean b = super.checkPrivilege(request, response, "您需要写权限.", 2);
		if(b){
			return;
		}
		JxInfoService jxInfoService = (JxInfoService) wc.getBean("jxInfoService");
		String j_id = request.getParameter("j_id");
		Map j = jxInfoService.findByKey(j_id);
		if(j == null){
			gotoPopup(request, response, "端口不存在或者已经屏蔽: " + j_id);
			 
			return;
		}
		Boolean used = (Boolean) j.get("used");
		if(used == null ){
			gotoPopup(request, response, "此端口已经置坏: " + j_id); 
 			return;
		}
		
		if(used != null && used){
			gotoPopup(request, response, "此端口有用户信息，请使用更换端口置坏: " + j_id); 
 			return;
		}
		
		AccountInfo accountInfo = this.getAccountInfo(request);
		String remark = request.getParameter("remark");
		jxInfoService.markFault(j_id,  remark, accountInfo.getAccount());
		
		logService.log("置坏端口", "置坏端口：J_ID=" + j_id + ",原因：" + remark, accountInfo.getAccount());
		
		log.debug("["+accountInfo.getAccount()+"]成功置坏端口:" + j_id);
		
		
		gotoPopup(request, response, "成功置坏端口: " + j_id); 
  
	}

}
