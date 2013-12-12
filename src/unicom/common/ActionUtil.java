package unicom.common;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
 

public class ActionUtil {
	/**
	 * Logger for this class
	 */
	private static final Logger log = Logger.getLogger(ActionUtil.class);

	public static final String SESSION_STAFF = "sessionStaff";
	public static final String ONLINE_STAT = "onlineStat";
	public static final String WRITE_LOCKER_MANAGER = "writeLockerManager";

	public static final String DEFAULT_ENCODING = "gbk";//NOT UTF-8!
	public static final String DEFAULT_CONTENT_TYPE = "text/plain";
	public static final String DEFAULT_BINARY_CONTENT_TYPE = "application/octet-stream";

	private static final String LAST_LINK = "_session_last_link_";

	private static final String PORT_LOGGER = "_port_logger_";


	 
	 

	public static void writeToPage(HttpServletResponse response, String msg, String encoding, String contentType){
		if(msg == null){
			return ;
		}
		
//		log.debug("writing message to page: " + msg);
		
		if(encoding == null){
			encoding = DEFAULT_ENCODING;
		}
		if(contentType == null){
			contentType = DEFAULT_CONTENT_TYPE;
		}
		response.setCharacterEncoding(encoding);//必须这样写出，页面才不乱码。
		response.setContentType(contentType);

		//清空缓存
		response.setHeader("Pragma","no-cache");
		response.setHeader("Cache-Control","no-cache");
		response.setHeader("Expires","0");

		byte[] reply = msg.getBytes();

		ServletOutputStream out = null;
		try {
			out = response.getOutputStream();

			out.write(reply);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(null != out){
				try {
					out.close();
				} catch (IOException e) {
				}
			}
		}
	}

	public static void download(HttpServletResponse response, File srcFile ,String downloadFileName) {
		download(response, DEFAULT_BINARY_CONTENT_TYPE, DEFAULT_ENCODING, srcFile, downloadFileName);
	}
	/**
	 * 下载专用
	 * @param response
	 * @param contentType
	 * @param encoding
	 * @param srcFile
	 * @param downloadFileName
	 * 保存的文件名
	 */
	public static void download(HttpServletResponse response,String contentType,String encoding, File srcFile ,String downloadFileName) {
		if(StringUtils.isBlank(contentType)){
			response.setContentType(DEFAULT_BINARY_CONTENT_TYPE);
		}else{
			response.setContentType(contentType);
		}
		if(StringUtils.isBlank(encoding)){
			response.setCharacterEncoding(DEFAULT_ENCODING);
		}else{
			response.setContentType(encoding);
		}
		//不保留缓存
//		response.setHeader("Pragma","no-cache");
//		response.setHeader("Cache-Control","no-cache");
//		response.setHeader("Expires","0");
		try {
			downloadFileName = new String(downloadFileName.getBytes(),"iso-8859-1");
		} catch (UnsupportedEncodingException e) {
			log.error(e);
		}
		//中文"预约"转换为iso-8859-1时导致乱码?对于opera 9.5,文件名不得少于5个字,否则乱码
		response.setHeader("Content-Disposition","attachment;filename=\"" + downloadFileName+"\"");

		InputStream in = null;
		ServletOutputStream out = null;

		if(srcFile == null || !srcFile.exists()){
			log.error("源文件不存在: " + srcFile.getAbsolutePath());
			return ;
		}
		try {
			in = new FileInputStream(srcFile);
			int bytesRead = 0;
			int offset = 0;//offset in inputstream
			int fileLen = (int) srcFile.length();
			byte[] BUFFER = new byte[1024];

			log.debug("src file length=" + fileLen);
		    out = response.getOutputStream();
		    //不要直接用BUFFER.length,否则可能会抛出IndexOutOfBoundsException
		    while((bytesRead = in.read(BUFFER, 0, Math.min(BUFFER.length ,fileLen-offset)))>0){
		    	offset += bytesRead;
				out.write(BUFFER,0,bytesRead);
		    }
		    out.flush();
		    log.debug("write bytes:" + offset);
		    if(offset < fileLen){
		    	throw new IOException("文件没有完整下载:" + srcFile.getAbsolutePath());
		    }
		} catch (IOException e) {
			log.error(e);
		}catch (Exception e) {
			log.error(e);
		}finally{
			if(null != out){
				try {
					out.close();
				} catch (IOException e) {
				}
			}
			if(null != in){
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}
	}

	/**
	 * 文件格式：jpg,jpeg, gif, png, 其它直接下载！
	 * @param response
	 * @param file
	 */
	public static void showImage(HttpServletResponse response, File file) {
		if(file == null || !file.exists()){
			log.error("源文件不存在: " + file.getAbsolutePath());
			return ;
		}

		String extension = FilenameUtils.getExtension(file.getName());

		if(extension != null && extension.matches("jpg|jpeg|gif|png")){
			response.setContentType("image/" + extension);
		}else{
			response.setContentType(DEFAULT_BINARY_CONTENT_TYPE);
		}

		InputStream in = null;
		ServletOutputStream out = null;

		try {
			in = new FileInputStream(file);
			int bytesRead = 0;
			int offset = 0;//offset in inputstream
			int fileLen = (int) file.length();
			byte[] BUFFER = new byte[1024];

			log.debug("src file length=" + fileLen);
		    out = response.getOutputStream();
		    //不要直接用BUFFER.length,否则可能会抛出IndexOutOfBoundsException
		    while((bytesRead = in.read(BUFFER, 0, Math.min(BUFFER.length ,fileLen-offset)))>0){
		    	offset += bytesRead;
				out.write(BUFFER,0,bytesRead);
		    }
		    out.flush();
		    log.debug("write bytes:" + offset);
		    if(offset < fileLen){
		    	throw new IOException("文件没有完整下载:" + file.getAbsolutePath());
		    }
		} catch (IOException e) {
			log.error(e);
		}catch (Exception e) {
			log.error(e);
		}finally{
			if(null != out){
				try {
					out.close();
				} catch (IOException e) {
				}
			}
			if(null != in){
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}
	}

	/**
	 *  context path+request+'?' + queryString
	 * @param req
	 */
	@SuppressWarnings("unchecked")
	public static void setLastLink(HttpServletRequest req) {
		StringBuffer link = new StringBuffer(req.getRequestURI());

		boolean q = false;

//		if(StringUtils.isNotBlank(req.getQueryString() )){
//			q = true;
//			link.append('?');
//		q = true;
//			link.append(req.getQueryString().trim());
//		}

		Enumeration parameterNames = req.getParameterNames();

		for (; parameterNames.hasMoreElements();) {
			String name = (String) parameterNames.nextElement();
			Object value = req.getParameter(name);
			if(value != null && value.toString().trim().length() > 0){
				if(q == false){
					link.append('?');
					q = true;
				}else{
					link.append('&');
				}

				link.append(name);
				link.append("=");
				link.append(value);
			}

		}

//		if(StringUtils.isNotBlank(req.getQueryString())){
//			link.append('?');
//			link.append(req.getQueryString());
//		}
		req.getSession().setAttribute( LAST_LINK, link.toString());
	}

	public static String getLastLink(HttpServletRequest req){
		return (String) req.getSession().getAttribute(LAST_LINK);
	}

	public static String getLastLink(HttpSession session) {
		return (String) session.getAttribute(LAST_LINK);
	}


	/*
	 *根据user-agent判断
	 */
	@SuppressWarnings("unchecked")
	public static Boolean isWapUser(HttpServletRequest request) {

		List<String> wapUserAgents = (List<String>) request.getSession().getServletContext().getAttribute("wap_user_agent");

		if(wapUserAgents == null){
			URL resource = ActionUtil.class.getClassLoader().getResource("config/wap_user_agent.txt");
			File configFile = new File(resource.getFile().replace("%20", " "));
			try {
				wapUserAgents = FileUtils.readLines(configFile);

				request.getSession().getServletContext().setAttribute("wap_user_agent", wapUserAgents);
			} catch (IOException e) {
				log.error("无法加载：config/wap_user_agent.txt"  , e);
			}
		}

		if(wapUserAgents == null){
			return false;
		}

		String userAgent = request.getHeader("user-agent");
		if(StringUtils.isBlank(userAgent)){
			return false;
		}

		userAgent = userAgent.toUpperCase();

		for (String wua : wapUserAgents) {
			if( StringUtils.isNotBlank(wua) && !wua.startsWith("#")){
				if(userAgent.contains(wua.toUpperCase())){
					log.info("[WAP]登录:" + userAgent);
					return true;
				}
			}
		}
		log.info("[WEB]登录:" + userAgent);
		return false;
	}




}
