package unicom.web;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;

import unicom.common.FileHelper;
import unicom.common.SearchConditions;
import unicom.common.port.ExportConfig;
import unicom.common.port.ExportFileException;
import unicom.xdsl.service.UserInfoService;

/**
 * Servlet implementation class UserInfoExportServlet
 */
public class UserInfoExportServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UserInfoExportServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		boolean b = super.checkPrivilege(request, response, "您需要导出权限.", 3);
		if(b){
			return;
		}
		
		String ext = "csv";
		String downloadFileName = "用户信息" + DateFormatUtils.format(new Date(), "yyyyMMdd_HHmmss") + "." + ext ;
		
		File destFile = new File(WebUtils.getDownloadDir(request) + "/userinfo/" + downloadFileName );
		
		b = FileHelper.createFile(destFile);
		if(!b){
			request.setAttribute("popMsg", "导出失败：无法生成导出文件，请联系管理员" );
			request.getRequestDispatcher("/WEB-INF/popup.jsp").forward(request, response);
			return;
		}
		
		UserInfoService userInfoService = (UserInfoService) wc.getBean("userInfoService");
		
		ExportConfig exportConfig = new ExportConfig();
		exportConfig.setFormat(ext);

		Enumeration parameterNames = request.getParameterNames();
		
		SearchConditions searchCondition = new SearchConditions();
		
		for (; parameterNames.hasMoreElements();) {
			String name = (String) parameterNames.nextElement();
			if(name.startsWith("@")){
//				request.setAttribute(name, request.getParameter(name));//reset
				continue;//忽略@
			}
			String value = request.getParameter(name);//FIXME 只有string类型
			if(value != null && StringUtils.isNotBlank(value)){
				value = value.trim();

				searchCondition.addCondition(name, value);
//				request.setAttribute(searchCondition.getEchoField(name), value);//回显
			}
			
		}
		
		exportConfig.setSearchCondition(searchCondition);
		
		try {
			userInfoService.exportFile(destFile, exportConfig );
			log.debug("帐号〔" + WebUtils.getSessionAccount(request) + "〕：成功导出: " + destFile.getAbsolutePath());
		} catch (ExportFileException e) {
			log.error( "导出失败", e);
			request.setAttribute("popMsg", "导出失败：" + e.getMessage());
			request.getRequestDispatcher("/WEB-INF/popup.jsp").forward(request, response);
			return;
		}
		
		WebUtils.download(response, destFile, downloadFileName);
		
		return;
	}

}
