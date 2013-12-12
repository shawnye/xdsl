package unicom.tool;

import java.text.MessageFormat;

public class ReportHtmlCreater {
	static String condTemplate = "\t<td class=\"label\">{0}：</td>\n" +
				"\t<td><input type=\"text\" name=\"{1}\" id=\"{1}\" value=\"$'{' {1} '}'\" width=\"{2}\"></td>\n";
	
	static public void createCondition(String labels,String names, int colCount){
		String[] labelArr = labels.split("(\\s+|[,，|;；])");
		String[] nameArr = names.split("(\\s+|[,，|;；])");
		
		int i = 0;
		StringBuffer b = new StringBuffer();
		for (; i < nameArr.length; i++) {
			if(i%colCount == 0){
				b.append("<tr>\n");
			}
			b.append(MessageFormat.format(condTemplate, labelArr[i],nameArr[i],"30"));
			if(i%colCount == colCount-1){
				b.append("</tr>\n");
			}
		}
		
		while(i%colCount !=0){
			if(i%colCount == colCount-1){
				b.append("</tr>\n");
				break;
			}
			b.append("\t<td class=\"label\">&nbsp;</td>\n"); 
			b.append("\t<td>&nbsp;</td>\n"); 
			i++;
		}
		
		System.out.println(b);
	}
	
	public static void main(String[] args) {
//		ReportHtmlCreater.createCondition(
//				"仅仅显示前 产品号码  客户名称  状态  用户性质 业务类型 用户装机地址 用户ip地址 网关 联系人 联系电话 接入机房 接入设备名称 接入设备ip地址  网关设备 开通时间  用户带宽 ",
//				"page_limit p_num u_name state u_type s_type u_address u_ip  u_gateway  contact  tel  jifang  d_name  d_ip  g_dev  begin_date  bandwidth ",
//				4);
		
		ReportHtmlCreater.createCondition(
				"仅仅显示前 机房  设备名称  设备ip地址  设备端口   使用情况  设备类型  板类型 端口类型 产品号码  客户名称 ",
				"page_limit jifang  d_name  d_ip  port  used  d_type  b_type  port_type p_num u_name",
				4);
	}
}
