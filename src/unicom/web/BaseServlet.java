package unicom.web;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import unicom.bo.AccountInfo;
import unicom.common.FileHelper;
import unicom.xdsl.service.LogService;

@SuppressWarnings("serial")
public abstract class BaseServlet extends HttpServlet {
	protected Log log = LogFactory.getLog(this.getClass());
	
	protected WebApplicationContext wc;
	protected LogService logService;

	protected File UPLOAD_TMP_DIR;
	protected ServletFileUpload upload;
 

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		ServletContext sc = config.getServletContext();//request.getSession().getServletContext();
		wc = WebApplicationContextUtils.getRequiredWebApplicationContext(sc);
		logService = (LogService) wc.getBean("logService");
		
		// Create a factory for disk-based file items
		DiskFileItemFactory factory = new DiskFileItemFactory();
		factory.setSizeThreshold(10240 );//The default value is 10240 bytes. 
		
		UPLOAD_TMP_DIR = new File(config.getServletContext().getRealPath("/") +  "/TMP/upload/");
		FileHelper.createDir(UPLOAD_TMP_DIR);
		
		factory.setRepository(UPLOAD_TMP_DIR);

		// Create a new file upload handler
		upload = new ServletFileUpload(factory);
	}

	public BaseServlet() {
		super();
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doPost(request, response);
	}

	protected AccountInfo getAccountInfo(HttpServletRequest request){
		return (AccountInfo) request.getSession().getAttribute("accountInfo");
	}
	
	/**
	 * 简单的核查操作权限
	 * 1（管理员） , 2(写）, 3(导出) ,4(只读) 
	 * @param request
	 * @param response
	 * @param errorMsg
	 * @param min_level TODO
	 * @throws ServletException
	 * @throws IOException
	 * return forwarded
	 */
	protected boolean checkPrivilege(HttpServletRequest request, HttpServletResponse response, String errorMsg, Integer min_level) throws ServletException, IOException {
		AccountInfo accountInfo = this.getAccountInfo(request);
		if(accountInfo == null || accountInfo.getLevel() > min_level){
			request.setAttribute("login_error", errorMsg);
			request.getRequestDispatcher("/WEB-INF/login.jsp").forward(request, response);
			return true;
		}
		
		return false;
	}
	
	protected void gotoPopup(HttpServletRequest request,
			HttpServletResponse response, String message) throws ServletException,
			IOException {
		request.setAttribute("popMsg", message);
		request.getRequestDispatcher("/WEB-INF/popup.jsp").forward(request, response);
 	}

}