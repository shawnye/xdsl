package unicom.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import unicom.common.DateHelper;

public class BaseBatchPreparedStatementSetter implements
		BatchPreparedStatementSetter {

	private List<Object[]> args ;
	private int batchSize = 0;
	
	public BaseBatchPreparedStatementSetter(List<Object[]> args) {
		this(args,args.size());
	}
	
	public BaseBatchPreparedStatementSetter(List<Object[]> args, int batchSize) {
		super();
		this.args = args;
		this.batchSize =batchSize;
	}

	public int getBatchSize() {
		return batchSize;
	}

	public void setValues(PreparedStatement ps, int i) throws SQLException {
		Object[] objects = args.get(i);
		for (int j = 0; j < objects.length; j++) {
			if(objects[j] != null && objects[j].getClass() == Date.class){
//				objects[j] = DateHelper.toSqlDate((Date) objects[j]);//无法记录几时修改！
				objects[j] = DateHelper.toSqlTimestamp((Date) objects[j]);//无法记录几时修改！

			}
			ps.setObject(j+1, objects[j]);
		}
	}

}
