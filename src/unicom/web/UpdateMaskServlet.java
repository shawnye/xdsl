package unicom.web;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
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
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import unicom.common.port.ImportConfig;
import unicom.common.port.ImportFileException;
import unicom.xdsl.service.CutoverService;
import unicom.xdsl.service.JxInfoService;
import unicom.xdsl.service.LogService;
import unicom.xdsl.service.converter.BatchNumFieldConverter;
import unicom.xdsl.service.converter.IntegerFieldConverter;
import unicom.xdsl.service.converter.JxInfoRemarkFieldConverter;
import unicom.xdsl.service.converter.TypeConverter;

/**
 */
public class UpdateMaskServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UpdateMaskServlet() {
        super();
        // TODO Auto-generated constructor stub
    }


	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		boolean b = super.checkPrivilege(request, response, "停/复端口功能需要写权限.", 2);
		if(b){
			return;
		} 
		Boolean mask = Boolean.valueOf(request.getParameter("mask"));
		String jx = request.getParameter("jx");
		String sbh = request.getParameter("sbh");
		String slotsStr = request.getParameter("slots");
		String sb_portsStr = request.getParameter("sb_ports");
		
		if(StringUtils.isBlank(jx) || StringUtils.isBlank(sbh)){
			request.setAttribute("popMsg",  "停/复端口失败：必须同时填写机房和设备号");
			request.getRequestDispatcher("/WEB-INF/popup.jsp").forward(request, response);
			return;
		}
		
		
		Integer[] slots = new Integer[0];
		Integer[] sb_ports = new Integer[0];
		
		if(StringUtils.isNotBlank(slotsStr)){
			String[] slotArr = slotsStr.split("\\s*[\\,，\\;；]\\s*");
			slots = new Integer[slotArr.length];
			for (int i = 0; i < slots.length; i++) {
				try {
					slots[i] = Integer.parseInt(slotArr[i]);
				} catch (NumberFormatException e) {
					log.error(e);
					request.setAttribute("popMsg",  "停/复端口失败：非法槽号：" + slotArr[i]);
					request.getRequestDispatcher("/WEB-INF/popup.jsp").forward(request, response);
					return;
				}
			}
		}
		if(StringUtils.isNotBlank(sb_portsStr)){
			String[] sb_portArr = sb_portsStr.split("\\s*[\\,，\\;；]\\s*");
			sb_ports = new Integer[sb_portArr.length];
			for (int i = 0; i < sb_ports.length; i++) {
				try {
					slots[i] = Integer.parseInt(sb_portArr[i]);
				} catch (NumberFormatException e) {
					log.error(e);
					request.setAttribute("popMsg",  "停/复端口失败：非法端口号：" + sb_portArr[i]);
					request.getRequestDispatcher("/WEB-INF/popup.jsp").forward(request, response);
					return;
				}
			}
		}
		

		
		
		
		
		
        JxInfoService jxInfoService = (JxInfoService) wc.getBean("jxInfoService");
        
        StringBuilder msg = new StringBuilder();
		 try{
			int masked = jxInfoService.updateMask(mask, jx.trim(), sbh.trim()	, slots, sb_ports);
			logService.log((mask?"屏蔽":"复开") + "机房端口", "更新机房【"+jx+"】设备【"+ sbh +"】 槽号【"+StringUtils.join(slots,",")+"】端口号【"+StringUtils.join(sb_ports,",")+"】 总端口数：" + masked ,  super.getAccountInfo(request).getAccount());

			msg.append((mask?"屏蔽":"复开") + "机房【"+jx+"】设备【"+ sbh +"】 槽号【"+StringUtils.join(slots,",")+"】端口号【"+StringUtils.join(sb_ports,",")+"】 总端口数：" + masked);
			

			log.info( msg);
			request.setAttribute("popMsg",  msg);
			request.getRequestDispatcher("/WEB-INF/popup.jsp").forward(request, response);


		} catch (Exception e) {
			log.error(e);
			request.setAttribute("popMsg",  "停/复端口失败：" + e.getMessage());
			request.getRequestDispatcher("/WEB-INF/popup.jsp").forward(request, response);
		}
	}
 

}
