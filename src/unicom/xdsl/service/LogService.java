package unicom.xdsl.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import unicom.dao.BaseDaoInterface;

@Service(value="logService")
public class LogService {
	private BaseDaoInterface baseDao;
	
	@Autowired
	public void setBaseDao(BaseDaoInterface baseDao) {
		this.baseDao = baseDao;
	}
	
	public void log(String event, String remark, String account) {
		String updateSql = "insert into log(event, remark,users,now_time) values(?,?,?,?)" ;
		this.baseDao.update(updateSql, new Object[]{event,remark,account,new Date()});
	}

}
