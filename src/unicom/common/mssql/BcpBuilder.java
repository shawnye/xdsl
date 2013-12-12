package unicom.common.mssql;

import org.apache.commons.lang.StringUtils;

public class BcpBuilder {
	private String server;
	private String userName;
	private String password;

	private boolean quoted = false;
//	private String[] exportTitles;
	private String sql;

	private String filePath;

	private String terminal=",";

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getTerminal() {
		return terminal;
	}

	public void setTerminal(String terminal) {
		this.terminal = terminal;
	}

	public boolean isQuoted() {
		return quoted;
	}

	public void setQuoted(boolean quoted) {
		this.quoted = quoted;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public void setAuth(String server,String userName,String password){
		this.server = server;
		this.userName = userName;
		this.password = password;
	}
	/**
	 * 必须在注释中第一个出现优先：--<TITLE>xxxx,xxxx,xxx</TITLE>
	 * @return
	 */
	public String[] getTitlesFromSql(){
		if(StringUtils.isBlank(sql)){
			return null;
		}
		
		String[] lines = this.sql.split("(\r\n|\n|\r)");
		for (int i = 0; i < lines.length; i++) {
			if(lines[i].trim().startsWith("--")){
				int start = lines[i].indexOf("<TITLE>");
				int end = lines[i].indexOf("</TITLE>");
				if(start == -1 || end == -1){
					continue;
				}
				
				String titles = lines[i].substring(start+7,end).trim();
				
				return titles.split(",");
			}
		}
		return null;
	}

	public String queryout(){
		StringBuffer bcp = new StringBuffer("bcp \"");
		//删除注释行（--开头）
		bcp.append(this.sql.replaceAll("^\\-\\-[^\r\n]*", "").replaceAll("(\r\n|\n|\r)", " ").replace("%", "%%"));//转义
		bcp.append("\"");
		bcp.append(" queryout \""+ this.filePath +"\"");
		bcp.append("  -c  -t "+this.terminal+" -r \\n ");
		bcp.append(this.quoted?" -q":"");
		bcp.append(" -S" + this.server);
		bcp.append(" -U" + this.userName);
		bcp.append(" -P" + this.password);

		return bcp.toString();
	}

	public static void main(String[] args) {
		BcpBuilder bb = new BcpBuilder();

		bb.setAuth("GDCYEXY6", "yexy6", "cncjm'123");
		bb.setFilePath("%EXP_DIR%\\接入间信息.csv");
		bb.setSql("--<TITLE>j_id,机房,设备号,槽号,端口号,板卡类型,外层VLAN,内层VLAN,横列端口,u_id,产品号码,客户名称,账号,地址</TITLE>\r\n" +
				"SELECT   j.j_id, jx AS 机房, sbh AS 设备号, slot AS 槽号, \r\n" +
				"      sb_port AS 端口号, board_type AS 板卡类型, outer_vlan AS 外层VLAN, \r\n" +
				"      inner_vlan AS 内层VLAN, mdf_port AS 横列端口,COALESCE (u.u_id,-1) u_id, coalesce(u.p_id,'') 产品号码,coalesce( u.username,'') 客户名称,coalesce(user_no,'') 账号,coalesce( address,'') 地址\r\n" +
				"FROM dbo.jx_info j left join user_info u on j.u_id=u.u_id\r\n" +
				"WHERE 1=1 and jx in(\r\n" +
				"'江门丹井里',\r\n" +
				"'江门白石',\r\n" +
				"'鹤山中东西',\r\n" +
				"'江门步岭',\r\n" +
				"'江门潮莲卢边',\r\n" +
				"'鹤山中东西DJ0501EPON',\r\n" +
				"'江门白石DJ0401EPON',\r\n" +
				"'江门白石DJ0601EPON',\r\n" +
				"'江门白石DJ0701EPON',\r\n" +
				"'江门白石DJ1201EPON',\r\n" +
				"'江门丹井DJ0201EPON',\r\n" +
				"'江门丹井DJ0701EPON',\r\n" +
				"'江门丹井DJ0702EPON',\r\n" +
				"'江门丹井DJ1201EPON',\r\n" +
				"'鹤山中东西DJ0301EPON',\r\n" +
				"'江门白石DJ1301EPON',\r\n" +
				"'江门白石营业厅',\r\n" +
				"'江门邦德',\r\n" +
				"'江门滨江大道',\r\n" +
				"'江门长乔' \r\n" +
				")\r\n" +
				"ORDER BY jx, sbh, slot, sb_port\r\n" +
				"");
		System.out.println(bb.queryout());
		
		System.out.println(StringUtils.join(bb.getTitlesFromSql(),"\t"));

	}
}
