package unicom.web;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import unicom.xdsl.service.UserInfoService;

/**
 * 批量导入
 * @author yexy6
 *
 */
public class UserInfoBatchUpdateServiceServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UserInfoBatchUpdateServiceServlet() {
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
		
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		if(!isMultipart){
			log.info( "没有找到上传文件");
			request.setAttribute("popMsg", "没有找到上传文件");
			request.getRequestDispatcher("/WEB-INF/popup.jsp").forward(request, response);
			return;
		}
		
		 int updates = 0;
		 StringBuffer msg = new StringBuffer();
		 long start = System.currentTimeMillis();
//		 int j = 0;
//		 List<String> ommited = new ArrayList<String>();
		// Parse the request
		try {
			List /* FileItem */ items = upload.parseRequest(request);
			
			// Process the uploaded items
			Iterator iter = items.iterator();
			while (iter.hasNext()) {
			    FileItem item = (FileItem) iter.next();

			    if (item.isFormField()) {
			        processFormField(item);//nothing to do...
			    } else {
					File file = new File(UPLOAD_TMP_DIR + item.getFieldName() + System.currentTimeMillis());
			        processUploadedFile(item, file);
			        
			        UserInfoService userInfoService = (UserInfoService) wc.getBean("userInfoService");
//			        msg.append("导入文件：" + file.getAbsolutePath());//内部文件名
//			        msg.append("\n");
					updates +=  userInfoService.updateServiceStatus(file, msg);//csv
					msg.append("\n");
			    }
			}
			
			msg.append("导入耗时(秒)：" + (System.currentTimeMillis()-start)/1000) ;
			
			log.info( "停复机更新成功,影响行数: " + updates + "。详细情况如下：\n" + msg);
			request.setAttribute("popMsg", "停复机更新成功,影响行数: " + updates + "。详细情况如下：\n" + msg);
			request.getRequestDispatcher("/WEB-INF/popup.jsp").forward(request, response);

			
		} catch (FileUploadException e) {
			log.error(e);
		}
	}

	private void processUploadedFile(FileItem item, File file) throws IOException {
		log.debug("上传文件类型：" + item.getContentType());
		log.debug("上传文件大小：" + item.getSize());
		
		try {
			item.write(file);
		} catch (Exception e) {
			log.error("保存上传文件失败：" + file, e);
		}
	}

	private void processFormField(FileItem item) {
//		String name = item.getFieldName();
//	    String value = item.getString();

		
	}

}
