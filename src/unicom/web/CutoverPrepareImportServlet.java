package unicom.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 割接资源表导入
 */
public class CutoverPrepareImportServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CutoverPrepareImportServlet() {
        super();
     }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		boolean b = super.checkPrivilege(request, response, "割接资源表导入功能需要写权限.", 2);
		if(b){
			return;
		}
		
		request.getRequestDispatcher("/WEB-INF/cutoverImport.jsp").forward(request, response);
	}

}
