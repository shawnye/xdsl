package unicom.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class PrepareExportAccessRoomServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;



	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Integer sqlFileId = null;
		
		try {
			sqlFileId = Integer.valueOf(request.getParameter("fileId"));//不能有误，否则出错
		} catch (NumberFormatException e) {
			e.printStackTrace();
			request.getRequestDispatcher("/WEB-INF/error500.jsp").forward(request, response);
			return ;
		}

		boolean b = super.checkPrivilege(request, response, "您需要导出权限.", 3);
		if(b){
			return;
		}
		
		request.getRequestDispatcher("/WEB-INF/accessRoomExport_"+sqlFileId+".jsp").forward(request, response);
	}

}
