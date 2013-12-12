package unicom.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import unicom.bo.AccountInfo;
import unicom.xdsl.service.JxInfoService;

/**
 * Servlet implementation class UserInfoPrepareImportServlet
 */
public class JxInfoDeleteServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public JxInfoDeleteServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		boolean b = super.checkPrivilege(request, response, "端口删除功能需要写权限.", 2);
		if(b){
			return;
		}
		
		String j_id = request.getParameter("j_id");
		String[] j_id_arr = j_id.split("\\s*\\,\\s*");
		
		JxInfoService jxInfoService = (JxInfoService) wc.getBean("jxInfoService");
		
		Integer[] jids = new Integer[j_id_arr.length];
		List<String> jxInfos = new ArrayList<String>(); 
		for (int i = 0; i < jids.length; i++) {
			jids[i] = new Integer(j_id_arr[i]);
			Map map = jxInfoService.findByKey(j_id_arr[i]);
			if(map != null){
				Boolean used = (Boolean) map.get("used");
 
				if(used != null && used){
					gotoPopup(request, response, "此端口有用户信息，不能删除: " + j_id); 
		 			return;
				}
				
				jxInfos.add("J_ID=" + map.get("j_id") + ",机房=" + map.get("jx") + ",设备号=" + map.get("shb")
						+ ",槽号=" + map.get("slot") + ",端口号=" + map.get("sb_port")+ ",MDF=" + map.get("mdf_port") +  ",VLAN=" +map.get("outer_vlan")+ "." + map.get("inner_vlan"));
			}
		}
		
		jxInfoService.deleteByJids(jids , false);
		
		AccountInfo accountInfo = this.getAccountInfo(request);

		logService.log("删除端口", "已经删除端口:" + StringUtils.join(jxInfos,"; "), accountInfo.getAccount());
		
		super.gotoPopup(request, response, "已经删除指定（未占用）端口");
	}

}
