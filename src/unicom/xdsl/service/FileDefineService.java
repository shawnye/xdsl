package unicom.xdsl.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import unicom.common.Page;
import unicom.common.SearchConditions;
import unicom.common.SearchKey;
import unicom.dao.BaseDaoInterface;

/**
 * 文件信息，用于下载或定义[数量不定]
 * 
 * @author yexy6
 * 
 */
@Service(value = "fileDefineService")
public class FileDefineService extends AbstractService {
	private BaseDaoInterface baseDao;

	@Autowired
	public void setBaseDao(BaseDaoInterface baseDao) {
		this.baseDao = baseDao;
	}

	public Page<Map> listAsMap(SearchConditions searchCondition, int pageNo,
			int pageSize) {

		Map<SearchKey, Object> conditions = searchCondition.getConditions();
		int size = conditions.size();
		Object[] args = new Object[size];
		String sqlFetch = "select * from file_define where 1=1 ";
		String sqlCount = "select count(*) from file_define where 1=1 ";
		int i = 0;
		for (SearchKey searchKey : conditions.keySet()) {
			args[i++] = conditions.get(searchKey);

			String theta = searchKey.getTheta();
			String field = searchKey.getField();
			sqlFetch += " and " + field + " " + theta + " ? ";
			sqlCount += " and " + field + " " + theta + " ? ";

			if (theta.equals("like")) {
				args[i - 1] = "%" + args[i - 1].toString() + "%";
			}
		}

		return this.baseDao.listAsMap(sqlCount, sqlFetch, args, pageNo,
				pageSize);
	}
	
	public List<Map> listAllAsMap(SearchConditions searchCondition) {
		Map<SearchKey, Object> conditions = searchCondition.getConditions();
		int size = conditions.size();
		Object[] args = new Object[size];
		String sqlFetch = "select * from file_define where 1=1 ";
		int i=0;
		for (SearchKey searchKey : conditions.keySet()) {
			args[i++] = conditions.get(searchKey);

			String theta = searchKey.getTheta();
			String field = searchKey.getField();
			sqlFetch += " and " + field + " " + theta + " ? " ;
			
			if(theta.equals("like")){
				args[i-1] = "%" + args[i-1].toString() + "%";
			}
		}
		
		return this.baseDao.listAllAsMap( sqlFetch , args );
	}

	

	
}
