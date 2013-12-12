package unicom.web;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.time.DateFormatUtils;

import unicom.common.FileHelper;
import unicom.common.port.ExportFileException;
import unicom.xdsl.service.UserInfoService;

/**
 * Servlet implementation class UserInfoSpecifiedExportServlet
 */
public class UserInfoSpecifiedExportServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see BaseServlet#BaseServlet()
     */
    public UserInfoSpecifiedExportServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String ext = "csv";
		String downloadFileName = "指定用户信息" + DateFormatUtils.format(new Date(), "yyyyMMdd_HHmmss") + "." + ext ;
		
		File destFile = new File(WebUtils.getDownloadDir(request) + "/userinfo/" + downloadFileName );
		
		boolean b = FileHelper.createFile(destFile);
		if(!b){
			request.setAttribute("popMsg", "导出失败：无法生成导出文件，请联系管理员" );
			request.getRequestDispatcher("/WEB-INF/popup.jsp").forward(request, response);
			return;
		}
		
		UserInfoService userInfoService = (UserInfoService) wc.getBean("userInfoService");
		
		
		String[] pidArray = request.getParameter("pids").split("\n");
		try {
			userInfoService.exportSpecifiedInfo(destFile, pidArray);
			log.debug("帐号〔" + WebUtils.getSessionAccount(request) + "〕：成功导出指定用户信息: " + destFile.getAbsolutePath());
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
