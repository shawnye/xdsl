package unicom.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import unicom.bo.AccountInfo;


public class WebUtils {
	/**
	 * Logger for this class
	 */
	private static final Logger log = Logger.getLogger(WebUtils.class);


	public static final String DEFAULT_ENCODING = "gbk";//NOT UTF-8!
	public static final String DEFAULT_CONTENT_TYPE = "text/plain";
	public static final String DEFAULT_BINARY_CONTENT_TYPE = "application/octet-stream";

	public static final String DOWNLOAD_DIR = "/TMP/download";
	public static final String UPLOAD_DIR = "/TMP/upload";
	
	public static final String SESSION_ACCOUNT = "accountInfo";

	public static String getDownloadDir(HttpServletRequest request){
		return request.getSession().getServletContext().getRealPath("/").replace("%20", " ") + DOWNLOAD_DIR;
	}
	
	public static String getUploadDir(HttpServletRequest request){
		return request.getSession().getServletContext().getRealPath("/").replace("%20", " ") + UPLOAD_DIR;
	}
	
	public static AccountInfo getSessionAccount(HttpServletRequest request){
		return (AccountInfo) request.getSession().getAttribute(SESSION_ACCOUNT);
	}
	
	public static void writeToPage(HttpServletResponse response, String msg, String encoding, String contentType){
		if(msg == null){
			return ;
		}
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
		
		if(srcFile == null && !srcFile.exists()){
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

	
	
	
}
