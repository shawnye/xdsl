package unicom.dao;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.SqlParameter;

import unicom.common.Page;

public interface BaseDaoInterface {
	@SuppressWarnings("unchecked")
	public List<Map> listAllAsMap(final String sqlFetchRows, Object[] args);
	/**
	 *
	 * @param sqlCountRows
	 * @param sqlFetchRows
	 * @param args
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Page<Map> listAsMap(final String sqlCountRows,
            final String sqlFetchRows, Object[] args, int pageNo, int pageSize);


	@SuppressWarnings("unchecked")
	public Map findUnique(String sqlFetch, Object[] objects);

	public Object findUnique(String sqlFetch, Object[] args,Class clazz);
	
	public int update(String sqlUpdate, Object[] args);

	public int[] batchUpdate(String[] sqlUpdates);

//	public int[] batchUpdateWithSameSql(String sqlUpdate, List<Object[]> args, int batchSize);

	
	public int[] batchUpdateWithSameSql(String sqlUpdate, List<Object[]> args);
	public int batchUpdateASql(String sqlUpdate, Object[] arg);
	public Long count(String sqlFetch, Object[] args);
	
	public List findList(String sqlFetch, Object[] args) ;
	
	public List findList(String sqlFetch, Object[] args,Class clazz) ;
	
	public void call(CallableStatementCreator csc, List<SqlParameter> declaredParameters);
	
	public void callWithoutParameter(final String callSqlFunction);
}
