package unicom.web;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import unicom.common.ActionUtil;
import unicom.common.JsonUtil;
import unicom.xdsl.service.JxInfoService;

/**
 * Servlet implementation class UserInfoEditServlet
 */
public class SlotJsonListServlet extends BaseServlet  {
	private static final long serialVersionUID = 1L;
       
  
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String jx = request.getParameter("jx");
		String sbh = request.getParameter("sbh");
//		String sbh = request.getParameter("sbh");
		
		String jsonString = "";
		if(StringUtils.isNotBlank(jx) && StringUtils.isNotBlank(sbh)){
			JxInfoService jxInfoService = (JxInfoService) wc.getBean("jxInfoService");
			List slots = jxInfoService.findSlotsLike( jx ,sbh );
			jsonString = JsonUtil.getJsonString(slots);
		}
		
		
		ActionUtil.writeToPage(response, jsonString, "gbk", ActionUtil.DEFAULT_CONTENT_TYPE);

		return;
	}

}
