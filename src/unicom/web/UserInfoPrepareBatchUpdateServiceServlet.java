package unicom.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class UserInfoPrepareBatchStopServiceServlet
 */
@Deprecated
public class UserInfoPrepareBatchUpdateServiceServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UserInfoPrepareBatchUpdateServiceServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		boolean b = super.checkPrivilege(request, response, "停复机更新功能需要写权限.", 2);
		if(b){
			return;
		}
		request.getRequestDispatcher("/WEB-INF/userInfoBatchUpdateService.jsp").forward(request, response);
	}

}
