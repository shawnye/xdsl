package unicom.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class UserInfoPrepareBatchStopServiceServlet
 */
public class UserStatePrepareBatchUpdateServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UserStatePrepareBatchUpdateServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		boolean b = super.checkPrivilege(request, response, "状态更新功能需要写权限.", 2);
		if(b){
			return;
		}
		
		request.setAttribute("fieldName", request.getParameter("fieldName"));
		request.setAttribute("fieldDefaultValue", request.getParameter("fieldDefaultValue"));
		
		request.getRequestDispatcher("/WEB-INF/userStateBatchUpdate.jsp").forward(request, response);
	}

}
