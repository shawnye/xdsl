package unicom.web;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.time.DateFormatUtils;

import unicom.common.FileHelper;
import unicom.common.port.ExportFileException;
import unicom.xdsl.service.UserInfoService;

/**
 * Servlet implementation class ExportAccessRoomServlet
 */
public class ExportAccessRoomServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		boolean b = super.checkPrivilege(request, response, "您需要导出权限.", 3);
		if(b){
			return;
		}
		
		String ext = "csv";
		String downloadFileName = "指定接入间信息" + DateFormatUtils.format(new Date(), "yyyyMMdd_HHmmss") + "." + ext ;

		File destFile = new File(WebUtils.getDownloadDir(request) + "/" + downloadFileName );

		b = FileHelper.createFile(destFile);
		if(!b){
			request.setAttribute("popMsg", "接入间信息导出失败：无法生成导出文件，请联系管理员" );
			request.getRequestDispatcher("/WEB-INF/popup.jsp").forward(request, response);
			return;
		}

		UserInfoService userInfoService = (UserInfoService) wc.getBean("userInfoService");

		Boolean export_all = Boolean.valueOf(request.getParameter("export_all"));
		
		Boolean ftth_only = Boolean.valueOf(request.getParameter("ftth_only"));
		
		int sqlFileId = Integer.valueOf(request.getParameter("fileId"));//不能有误，否则出错
		try {
			if(ftth_only){
				userInfoService.exportAllFtthInfo(destFile,sqlFileId);
			}else if(export_all){
				userInfoService.exportAllAccessRoomInfo(destFile,sqlFileId);
			}else{
				String[] pidArray = request.getParameter("accessRooms").split("\n");
				
				userInfoService.exportSpecifiedAccessRoomInfo(destFile, pidArray, sqlFileId );

			}
			log.debug("帐号〔" + WebUtils.getSessionAccount(request) + "〕：成功导出接入间信息: " + destFile.getAbsolutePath());
		} catch (ExportFileException e) {
			userInfoService.setCanExportSpecifiedInfo(true);
			log.error( "导出失败", e);
			request.setAttribute("popMsg", "接入间信息导出失败：" + e.getMessage());
			request.getRequestDispatcher("/WEB-INF/popup.jsp").forward(request, response);
			return;
		}


		WebUtils.download(response, destFile, downloadFileName);

		return;
	}

}
