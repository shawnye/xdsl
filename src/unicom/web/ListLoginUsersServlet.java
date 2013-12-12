package unicom.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class ListLoginUsersServlet
 */
public class ListLoginUsersServlet extends BaseListServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ListLoginUsersServlet() {
        super();

    }
 
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setAttribute("loginUsers", LoginUserList.getInstance().getAccountInfos());
		request.getRequestDispatcher("/WEB-INF/listLoginUsers.jsp").forward(request, response);

	}

}
