package unicom.web;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import unicom.common.port.ImportConfig;
import unicom.common.port.ImportFileException;
import unicom.xdsl.service.JxInfoService;
import unicom.xdsl.service.converter.BatchNumFieldConverter;
import unicom.xdsl.service.converter.IntegerFieldConverter;
import unicom.xdsl.service.converter.JxInfoRemarkFieldConverter;
import unicom.xdsl.service.converter.TypeConverter;

/**
 * Servlet implementation class UserInfoImportServlet
 */
public class JxInfoImportServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public JxInfoImportServlet() {
        super();
        // TODO Auto-generated constructor stub
    }


	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		boolean b = super.checkPrivilege(request, response, "端口信息导入功能需要写权限.", 2);
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

		 Map<String , String> fields = new HashMap<String , String>();

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
			        processFormField(item, fields);//nothing to do...
			    } else {
					File file = new File(UPLOAD_TMP_DIR + item.getFieldName() + System.currentTimeMillis());
			        processUploadedFile(item, file);
			        String name = item.getName();

			        JxInfoService jxInfoService = (JxInfoService) wc.getBean("jxInfoService");
			        

			        ImportConfig importConfig = new ImportConfig();
			        importConfig.setTable("jx_info");
			        
			        importConfig.parseFieldOrdersString("jx=,sbh=,type=,slot=,sb_port=,mdf_port=,used=,board_type=,inner_vlan=,outer_vlan=,ip=,olt=,ont_ports=,jremark=,batch_num=,@j_id=", ",");//,mdf_port=9

			        importConfig.addFieldConverter(new TypeConverter(importConfig, "type","AD,PON-AD,PON-LAN,LAN,WLAN,FTTH".split("\\,")));

			        importConfig.addFieldConverter(new IntegerFieldConverter(importConfig, "slot"));
			        importConfig.addFieldConverter(new IntegerFieldConverter(importConfig, "sb_port"));
			        importConfig.addFieldConverter(new IntegerFieldConverter(importConfig, "used", 0));//默认是0
			        importConfig.addFieldConverter(new IntegerFieldConverter(importConfig, "ont_ports", 4));//默认是4
			        importConfig.addFieldConverter(new BatchNumFieldConverter(importConfig, "batch_num"));

			        importConfig.setUpdateType(Integer.valueOf(fields.get("updateType")));//这里不能用request.getParame
			        
			        JxInfoRemarkFieldConverter jxInfoRemarkFieldConverter = (JxInfoRemarkFieldConverter) wc.getBean("jxInfoRemarkFieldConverter");

			        jxInfoRemarkFieldConverter.setField("jremark");
			        jxInfoRemarkFieldConverter.setImportConfig(importConfig);
			        jxInfoRemarkFieldConverter.setTemplate("导入批次：{0}"); 
			        importConfig.addFieldConverter(jxInfoRemarkFieldConverter); 

					try {
						b =  jxInfoService.importFile(file, importConfig, msg);
						
						logService.log("端口资源导入", "原始文件名：" + name + "," + msg,  super.getAccountInfo(request).getAccount());
					} catch (ImportFileException e) {
						log.error(e);
						request.setAttribute("popMsg", "端口信息导入出现异常。详细情况如下：\n" + msg);
						request.getRequestDispatcher("/WEB-INF/popup.jsp").forward(request, response);
						return;
					}//csv
					if(b){
						msg.append("\t导入**成功**。\n");
//						msg.append("如果导入行数大于0，请在〔端口资料查询〕中 查询 （备注）为‘" + DateFormatUtils.format(importConfig.getStamp(), "yyyy-MM-dd HH:mm") + "%新装导入’的记录！");
					}else{
						msg.append("导入**失败**。");
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

	private void processFormField(FileItem item, Map<String , String> fields) {
		String name = item.getFieldName();
	    String value = item.getString();

	    fields.put(name, value);
//	    System.out.println(name + "=" + value);
	}

}
