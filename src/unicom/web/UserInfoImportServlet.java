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
import org.apache.commons.lang.time.DateFormatUtils;

import unicom.common.port.ImportConfig;
import unicom.common.port.ImportFileException;
import unicom.xdsl.service.UserInfoService;
import unicom.xdsl.service.converter.AreaFieldConverter;
import unicom.xdsl.service.converter.BatchNumFieldConverter;
import unicom.xdsl.service.converter.DateFieldConverter;
import unicom.xdsl.service.converter.OntIdFieldConverter;
import unicom.xdsl.service.converter.StateFieldConverter;
import unicom.xdsl.service.converter.UserInfoRemarkFieldConverter;
import unicom.xdsl.service.converter.UserInfoVlanFieldConverter;

/**
 * Servlet implementation class UserInfoImportServlet
 */
public class UserInfoImportServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public UserInfoImportServlet() {
        super();
        // TODO Auto-generated constructor stub
    }


	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		boolean b = super.checkPrivilege(request, response, "用户信息导入功能需要写权限.", 2);
		if(b){
			return;
		}

		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		if(!isMultipart){
			log.info( "没有找到上传文件：页面没有设置 enctype=\"multipart/form-data\" ，或页面失效 ？ ");
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
			        String originalFileName = item.getName();

			        UserInfoService userInfoService = (UserInfoService) wc.getBean("userInfoService");
			        

			        ImportConfig importConfig = new ImportConfig();
			        importConfig.setTable("user_info");
			        //j_id为占位符，实际是更新jx ，
			        //客户名称,受理时间,联系电话(可选),装机号码,装机地址,帐号,密码(可选),外层VLAN,内层VLAN
			        importConfig.parseFieldOrdersString("@access_room_name=,username=1,begin_date=,tel=,p_id=,address=,user_no=,password=,@outer_vlan=,@inner_vlan=,j_id=,ont_id=,@sn=,area=,state=,remark=,batch_num=", ",");//,mdf_port=9

//			        importConfig.setDefaultValue("state", "已分配");

			        importConfig.addFieldConverter(new StateFieldConverter(importConfig, "state"));
			        importConfig.addFieldConverter(new AreaFieldConverter(importConfig, "area"));
			        importConfig.addFieldConverter(new DateFieldConverter(importConfig, "begin_date"));
			        importConfig.addFieldConverter(new BatchNumFieldConverter(importConfig, "batch_num"));

			        //要使用baseDao,所以要使用spring框架
			        OntIdFieldConverter ontIdFieldConverter = (OntIdFieldConverter) wc.getBean("ontIdFieldConverter");
			        ontIdFieldConverter.setField("ont_id");
			        ontIdFieldConverter.setImportConfig(importConfig);
			        importConfig.addFieldConverter(ontIdFieldConverter);
			        
			        UserInfoRemarkFieldConverter userInfoRemarkFieldConverter = (UserInfoRemarkFieldConverter) wc.getBean("userInfoRemarkFieldConverter");

			        userInfoRemarkFieldConverter.setField("remark");
			        userInfoRemarkFieldConverter.setImportConfig(importConfig);
			        userInfoRemarkFieldConverter.setTemplate("[{1}]新装导入，自动匹配端口:[{2}]");

			        importConfig.addFieldConverter(userInfoRemarkFieldConverter);

//			        importConfig.setFieldConverter(new TemplateFieldConverter(importConfig, "remark","[{1}]新装导入，自动匹配端口:[{2}]") );

			        UserInfoVlanFieldConverter userInfoVlanFieldConverter =  (UserInfoVlanFieldConverter) wc.getBean("userInfoVlanFieldConverter");
//			        //new UserInfoJxFieldConverter(importConfig, "jx_id");
			        userInfoVlanFieldConverter.setField("j_id");
			        userInfoVlanFieldConverter.setImportConfig(importConfig);
//
			        importConfig.addFieldConverter(userInfoVlanFieldConverter);

					try {
						b =  userInfoService.importFile(file, importConfig, msg);
						
						logService.log("新增用户导入","原始文件名：" + originalFileName + ", " + msg , super.getAccountInfo(request).getAccount());
						
					} catch (ImportFileException e) {
						log.error(e);
						request.setAttribute("popMsg", "用户信息导入出现异常。详细情况如下：\n" + msg);
						request.getRequestDispatcher("/WEB-INF/popup.jsp").forward(request, response);
						return;
					}//csv
					if(b){
						msg.append("\t导入成功。\n");
						msg.append("如果导入行数大于0，请在〔用户资料查询〕中 查询 （备注）为‘" + DateFormatUtils.format(importConfig.getStamp(), "yyyy-MM-dd HH:mm") + "%新装导入’的记录！");
					}else{
						msg.append("导入失败。");
					}
					msg.append("\n");
			    }
			}

			msg.append("导入耗时(秒)：" + (System.currentTimeMillis()-start)/1000) ;

			log.info( "用户信息导入完成 ，影响行数: " + updates + "。详细情况如下：\n" + msg);
			request.setAttribute("popMsg", "用户信息导入完成 ，影响行数: " + updates + "。详细情况如下：\n" + msg);
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
