package unicom.xdsl.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import unicom.bo.Role;
import unicom.common.Page;
import unicom.common.SearchConditions;
import unicom.dao.BaseDaoInterface;

@SuppressWarnings("rawtypes")
public class RoleService extends AbstractService{
	private BaseDaoInterface baseDao;
	
	@Autowired
	public void setBaseDao(BaseDaoInterface baseDao) {
		this.baseDao = baseDao;
	}

	public List<Role> findByCode(String code) {
		Object[] objects = new Object[]{code};
		String sqlFetch = "SELECT id, code, name, menu_code, include_only, exclude_only from Role where code=? order by menu_code ";
		List<Role> maps = null;
		try {
			maps = this.baseDao.findList(sqlFetch, objects, Role.class);
		} catch (Exception e) {
			log.warn(e.getMessage());
		}
		return maps;
	}

	@Override
	public Page<Map> listAsMap(SearchConditions searchCondition, int pageNo,
			int pageSize) {

		return null;
	}
	
	 
}
