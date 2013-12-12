package unicom.dao;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

@SuppressWarnings("unchecked")
public class MapRowMapper implements ParameterizedRowMapper<Map>{

	/**
	 * 根据选择顺序返回！
	 */
	public Map mapRow(ResultSet rs, int rowNum) throws SQLException {
		ResultSetMetaData metaData = rs.getMetaData();
		Map<String , Object> map = new LinkedHashMap<String, Object>();
		int columnCount = metaData.getColumnCount();
		for (int i = 1; i < columnCount+1; i++) {
			String columnLabel = metaData.getColumnLabel(i);
			if(StringUtils.isBlank(columnLabel)){
//				metaData.getColumnType(i);
				map.put(metaData.getColumnName(i), rs.getObject(i));
			}else{
				map.put(columnLabel, rs.getObject(i));
			}
		}

		return map;
	}

}
