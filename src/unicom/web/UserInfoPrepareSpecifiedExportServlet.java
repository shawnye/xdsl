package unicom.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class UserInfoPrepareSpecifiedExportServlet
 */
public class UserInfoPrepareSpecifiedExportServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UserInfoPrepareSpecifiedExportServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		boolean b = super.checkPrivilege(request, response, "指定产品号码批量导出功能需要导出权限.", 3);
		if(b){
			return;
		}
		request.getRequestDispatcher("/WEB-INF/userInfoSpecifiedExport.jsp").forward(request, response);

	}

}
