package unicom.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import unicom.common.Page;
import unicom.common.PaginationHelper;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class BaseDao extends JdbcDaoSupport implements BaseDaoInterface {
	private Log log = LogFactory.getLog(BaseDao.class);
 
	public Page<Map> listAsMap(final String sqlCountRows,
            final String sqlFetchRows, Object[] args, int pageNo, int pageSize) {
		JdbcTemplate template = this.getJdbcTemplate();
		//setFetchSize for processing large result sets
//		template.setFetchSize(start);//?
		//setMaxRows for processing subsets of large result sets
//		template.setMaxRows(length);

		PaginationHelper<Map> ph = new PaginationHelper<Map>();
//		final ParameterizedRowMapper<Map> rowMapper = new MapRowMapper() ;

		return ph.fetchPage(template, sqlCountRows, sqlFetchRows, args, pageNo, pageSize, new MapRowMapper()  );

	}

	
	public List<Map> listAllAsMap(String sqlFetchRows, Object[] args) {
		JdbcTemplate template = this.getJdbcTemplate();
		final ParameterizedRowMapper<Map> rowMapper = new MapRowMapper() ;

		log.debug("查询语句:\n" + sqlFetchRows);

		return (List<Map>) template.query(
                sqlFetchRows,
                args,
                new ResultSetExtractor() {

					public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
                        final List results = new ArrayList();
                        int currentRow = 0;
                        while (rs.next()) {
							results.add(rowMapper.mapRow(rs, currentRow));
                            currentRow++;
                        }
                        return results;
                    }
                });
	}

	 
	public Map findUnique(String sqlFetch, Object[] args) {
		JdbcTemplate template = this.getJdbcTemplate();
		return template.queryForMap(sqlFetch, args);
	}
	
	public Object findUnique(String sqlFetch, Object[] args,Class clazz) {
		JdbcTemplate template = this.getJdbcTemplate();
		return template.queryForObject(sqlFetch, args, new BeanPropertyRowMapper(clazz));
	}
	
	public List findList(String sqlFetch, Object[] args) {
		JdbcTemplate template = this.getJdbcTemplate();
		return template.queryForList(sqlFetch, args);//queryForList class 仅仅返回一列！
	}
	
	public List findList(String sqlFetch, Object[] args,Class clazz) {
		JdbcTemplate template = this.getJdbcTemplate();
		return template.query(sqlFetch, args, new BeanPropertyRowMapper(clazz));
//		return template.queryForList(sqlFetch, args,clazz);
	}


	public Long count(String sqlFetch, Object[] args) {
		JdbcTemplate template = this.getJdbcTemplate();


		log.debug("count语句：\n" + sqlFetch);

		return template.queryForLong(sqlFetch, args);
	}

	/**
	 * 更新一行多列
	 */
	public int update(String sqlUpdate, Object[] args) {
		JdbcTemplate template = this.getJdbcTemplate();

		int i = template.update(sqlUpdate, args);


		log.debug("更新语句：\n" + sqlUpdate);
		log.debug("更新影响数据行数：" + i);

		return i;
	}
	/**
	 * 更新多行多列
	 * @param sqlUpdate
	 * @param args
	 *
	 * @return
	 */
//	public int updates(String sqlUpdate, List<Object[]> args) {
//		JdbcTemplate template = this.getJdbcTemplate();
//
//		int i = 0;
//		for (Object[] objects : args) {
//			i += template.update(sqlUpdate, objects);
//		}
//
//		log.debug("更新语句：\n" + sqlUpdate);
//		log.debug("更新影响数据行数：" + i);
//
//		return i;
//	}

	/**
	 * 更新多行多列
	 */
	public int[] batchUpdateWithSameSql(String sqlUpdate, List<Object[]> args) {
		if(args == null){
			args = new ArrayList<Object[]>(0);
		}
//		return this.batchUpdateWithSameSql(sqlUpdate, args, args.size());
		JdbcTemplate template = this.getJdbcTemplate();

		BatchPreparedStatementSetter pss = new BaseBatchPreparedStatementSetter(args) ;

		log.debug("批量更新行数： " + args.size() + "，[相同]更新语句：\n" + sqlUpdate);
		return template.batchUpdate(sqlUpdate, pss );
	}
	/**
	 * 更新多行单列
	 */
	public int batchUpdateASql(String sqlUpdate, Object[] arg) {
		List<Object[]> args=new ArrayList<Object[]>(0);
		if(arg != null){
			args.add(arg);
		}
//		return this.batchUpdateWithSameSql(sqlUpdate, args, args.size());
		JdbcTemplate template = this.getJdbcTemplate();

		BatchPreparedStatementSetter pss = new BaseBatchPreparedStatementSetter(args) ;

		log.debug("批量更新行数： " + args.size() + "，更新语句：\n" + sqlUpdate);
		return template.batchUpdate(sqlUpdate, pss )[0];
	}


	public int[] batchUpdate(String[] sqlUpdates) {
		JdbcTemplate template = this.getJdbcTemplate();

		log.debug("批量更新语句 ：");
		for (int i = 0; i < sqlUpdates.length; i++) {
			log.debug("[" + (i+1) + "]" + sqlUpdates[i]);
		}
 
		return template.batchUpdate(sqlUpdates);
	}



	public void call(CallableStatementCreator csc, List<SqlParameter> declaredParameters){
		JdbcTemplate template = this.getJdbcTemplate();
		 
		if(declaredParameters == null){
			declaredParameters = new ArrayList<SqlParameter>(0);
		}
		template.call(csc , declaredParameters);

	}
	
	public void callWithoutParameter(final String callSqlFunction){
		CallableStatementCreator csc = new CallableStatementCreator() {
			
			public CallableStatement createCallableStatement(Connection con)
					throws SQLException {
				CallableStatement c = con.prepareCall("{call " + callSqlFunction + "}");
//	            demo3.setInt("arg_id",arg_id);
//	            demo3.setString("start", start);
//	            demo3.setString("end", end);
//	            demo3.registerOutParameter("bilv", Types.DOUBLE);
	            return c;
			}
		};
		this.call(csc , null );
	}

}
