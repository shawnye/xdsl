package unicom.common;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
/**
 * 
 * @ref http://www.codefutures.com/tutorials/spring-pagination/
 *
 * @param <E>
 */
public class PaginationHelper<E> {
	protected Log log = LogFactory.getLog(this.getClass());
	
	
    public Page<E> fetchPage(
            final JdbcTemplate jt,
            final String sqlCountRows,
            final String sqlFetchRows,
            final Object args[],
            final int pageNo,
            final int pageSize,
            final ParameterizedRowMapper<E> rowMapper) {
    	
    	int pageNo0=pageNo;
    	
    	if(pageNo0 < 1){
    		pageNo0 = 1;
    	}
    	
    	log.debug("查询数量: \n" + sqlCountRows);
        // determine how many rows are available
        final int rowCount = jt.queryForInt(sqlCountRows, args);

        // calculate the number of pages
        int pageCount = rowCount / pageSize;
        if (rowCount > pageSize * pageCount) {
            pageCount++;
        }
        
        if(pageNo0 > pageCount){
        	pageNo0 = pageCount;
        }

        // create the page object
        final Page<E> page = new Page<E>();
        page.setPageNumber(pageNo0);
        page.setPagesAvailable(pageCount-pageNo0);
        page.setTotalItems(rowCount);
        page.setPageSize(pageSize);
        
        // fetch a single page of results
        final int startRow = (pageNo0 - 1) * pageSize;
        
        log.debug("查询数据: \n" + sqlFetchRows);
        jt.query(
                sqlFetchRows,
                args,
                new ResultSetExtractor() {
                    @SuppressWarnings("unchecked")
					public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
                        final List pageItems = page.getPageItems();
                        int currentRow = 0;
                        while (rs.next() && currentRow < startRow + pageSize) {
                            if (currentRow >= startRow) {
                                pageItems.add(rowMapper.mapRow(rs, currentRow));
                            }
                            currentRow++;
                        }
                        return page;
                    }
                });
        return page;
    }
}
