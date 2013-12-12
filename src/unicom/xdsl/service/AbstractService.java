package unicom.xdsl.service;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import unicom.common.Page;
import unicom.common.SearchConditions;


public abstract class AbstractService {
	protected Log log = LogFactory.getLog(this.getClass());
	/**
	 * 
	 * @param searchCondition
	 * @param start
	 * @param length
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public abstract Page<Map> listAsMap(SearchConditions searchCondition, int pageNo, int pageSize);
	
	public void updateAField(Object id, String fieldName, Object fieldValue) {
		throw new UnsupportedOperationException();
	}
}
