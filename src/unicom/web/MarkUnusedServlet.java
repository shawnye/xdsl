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
 * Servlet implementation class MarkUnusedServlet
 */
public class MarkUnusedServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MarkUnusedServlet() {
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
		if(used != null ){
			gotoPopup(request, response, "此端口未置坏: " + j_id);
			return;
		}
		
		AccountInfo accountInfo = this.getAccountInfo(request);
		String remark = request.getParameter("remark");
		jxInfoService.markUnused(j_id,  remark, accountInfo.getAccount());
		log.debug("["+accountInfo.getAccount()+"]成功设置端口为未占用:" + j_id);
		
		logService.log("恢复端口", "恢复端口：J_ID=" + j_id + ",原因：" + remark, accountInfo.getAccount());

		
		gotoPopup(request, response, "成功设置端口为未占用:" + j_id); 

	}


	
}
