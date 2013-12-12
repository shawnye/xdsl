package unicom.bo;

import java.util.Date;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

public class AccountInfo {
	private String m_id;
	private String account;
	private String password;
	private String branch;
	private int level;//1-所有操作，2-可写, 3-可导, 4-只读, 是包含关系
	//暂时只能一个
	private String roles;//管理员，监控，代维, 号线，故障，数据，集响，传输？，交换
	private Date loginTime;
	private String ip;
	
	private String sessionId;
	
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getBranch() {
		return branch;
	}
	public void setBranch(String branch) {
		this.branch = branch;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	
	@Override
	public String toString() {
		return this.account + "(" + this.branch + ")";
	}
	public String getRoles() {
		return roles;
	}
	public void setRoles(String roles) {
		this.roles = roles;
	}
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public Date getLoginTime() {
		return loginTime;
	}
	public void setLoginTime(Date loginTime) {
		this.loginTime = loginTime;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	 
	
	public String getM_id() {
		return m_id;
	}
	public void setM_id(String m_id) {
		this.m_id = m_id;
	}
	
	
	public Boolean isHaoxianDuankou(){
		if(this.branch == null){
			return false;
		}
		
		return this.branch.contains("端口");
	}
	public Boolean isJituanKehu(){
		if(this.branch == null){
			return false;
		}
		
		return this.branch.contains("集响") || this.branch.contains("集团响应中心") || this.branch.contains("集客");
	}
	
	public Boolean isJieruwang(){
		if(this.branch == null){
			return false;
		}
		
		return this.branch.contains("接入网");
	}
	
	public Boolean isShujuzu(){
		if(this.branch == null){
			return false;
		}
		
		return this.branch.contains("数据");
	}
	
	public Boolean getDaiwei(){
		if(this.roles != null){
			return this.roles.contains(RoleType.DAI_WEI.getName());
		}

		return false;
	}
	
	public Boolean getHaoxian(){
		if(this.roles != null){
			return this.roles.contains(RoleType.HAO_XIAN.getName());
		}
		
		return false;
		 
	}
	
	public Boolean getAdmin(){
		if(this.roles != null){
			return this.roles.contains(RoleType.ADMIN.getName());
		}
		
		return false;
		 
	}
	
	public String getRoleNames(){
		if(StringUtils.isNotBlank(this.roles)){
			return RoleType.valueOf(this.roles).getLabel();
		}
		
		return null;
	}
	
	public String getLevelName(){
		if(this.level < 0 || this.level > 4){
			return "非法级别";
		}
		
		switch (this.level) {
		case 1:
			return "管理员操作";
		case 2:
			return "可写，可导出，可读";
		case 3:
			return "可导出，可读";
		case 4:
			return "只读";

		default:
			return null;
		}
	}
	
//	public Boolean getCanRead(){//由Role来控制
//		
//	}
	
	public Boolean getCanWrite() {
		return this.level <= 2;
	}

	public Boolean getCanExport() {
		return this.level <= 3;
	}
	
	public Boolean getCanEditUserInfo(){
		String[] eui = {RoleType.ADMIN.getName(),RoleType.HAO_XIAN.getName(),RoleType.DAI_WEI.getName(),};
		return ArrayUtils.contains(eui, this.roles);//FIXME this.roles.contains one of element of eui
	}
	
	public Boolean getCanChangePort(){
		String[] eui = {RoleType.ADMIN.getName(),RoleType.HAO_XIAN.getName(),RoleType.HAO_XIAN_DUAN_KOU.getName(),};
		return ArrayUtils.contains(eui, this.roles);//FIXME this.roles.contains one of element of eui
	}
	
	public Boolean getCanEditPort(){
		String[] eui = {RoleType.ADMIN.getName(),RoleType.HAO_XIAN.getName(),};
		return ArrayUtils.contains(eui, this.roles);//FIXME this.roles.contains one of element of eui
	}
}
