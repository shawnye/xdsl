package unicom.xdsl.service;

import java.util.Date;
import java.util.Map;

import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.stereotype.Service;

import unicom.bo.AccountInfo;
import unicom.bo.RoleType;
import unicom.common.DateHelper;
import unicom.dao.BaseDaoInterface;

@Service(value="authenticationService")
public class AuthenticationService {
	protected Log log = LogFactory.getLog(this.getClass());
	
	private BaseDaoInterface baseDao;
 
	@Autowired
	public void setBaseDao(BaseDaoInterface baseDao) {
		this.baseDao = baseDao;
	}
	
	private int accountFrozeThreshold = 21;//21日不登录就冻结，管理员除外
	 
	public int getAccountFrozeThreshold() {
		return accountFrozeThreshold;
	}
	public void setAccountFrozeThreshold(int accountFrozeThreshold) {
		this.accountFrozeThreshold = accountFrozeThreshold;
	}
	
	@SuppressWarnings("rawtypes")
	public AccountInfo login(String account, String password, String ip) {
		String sql = "select * from Manager m where m.users=?";
		Map acctObj = null;
		try {
			acctObj = this.baseDao.findUnique(sql, new Object[]{account});
		} catch(EmptyResultDataAccessException e){
			throw new RuntimeException("帐号不存在",e);
		} catch(IncorrectResultSizeDataAccessException e){
			throw new RuntimeException("帐号重复" + e.getActualSize() + "次",e); 
		}catch (CannotGetJdbcConnectionException e) {
			log.error("连接数据库失败: " + e.getMessage());
			throw new RuntimeException("连接数据库失败，请联系管理员" );
		} catch (Exception e) {
			throw new RuntimeException("查找帐号失败",e);
		}
		if(acctObj == null ){
			throw new RuntimeException("帐号不存在");
		}
		 
		Integer state = (Integer) acctObj.get("state");
		if(state != null && state.equals(new Integer(0))){
			throw new RuntimeException("帐号已经禁用。");
		}
		
		String roles = (String) acctObj.get("roles");
		if(! (roles != null && roles.contains(RoleType.ADMIN.getName())) ){//非管理员
			Date logon_time = (Date) acctObj.get("logon_time");
			if(logon_time != null && new Date().getTime() - logon_time.getTime() > accountFrozeThreshold * DateUtils.MILLIS_IN_DAY ){
				//除冻结状态:清除最近登录时间即可
				throw new RuntimeException("帐号超过 "+accountFrozeThreshold+" 日未登录，已经冻结。如需解除冻结状态，请联系管理员");
			}
		}
		
		
		String pswd = (String) acctObj.get("password");
		
		if(pswd == null || ! pswd.trim().equals(password)){
			throw new RuntimeException("帐号密码不对");
		}
		
		String sqlUpdate = "update Manager set ip=?,logon_time=? where users=?";
		this.baseDao.update(sqlUpdate, new Object[]{ip,new Date(),account});
		
		AccountInfo accountInfo = new AccountInfo();
		accountInfo.setM_id(acctObj.get("m_id").toString());
		accountInfo.setAccount(account);
		accountInfo.setPassword(password);
		accountInfo.setBranch((String) acctObj.get("branch"));
		accountInfo.setLevel((Integer) acctObj.get("M_level"));
		accountInfo.setRoles(roles);
		
		accountInfo.setIp(ip);
		accountInfo.setLoginTime(new Date());
 		
		return accountInfo;
	}
	/**
	 * 释放资源
	 * @param account
	 */
	public void logout(String account) {
		
	}
	
	public void updateAField(Object id, String fieldName, Object fieldValue) {
		Object[] args = new Object[]{fieldValue,id};
 		String updateSql = "update Manager set "+fieldName+" =? " + 
				" where m_id=?";
 		this.baseDao.update(updateSql, args);
	}
}
