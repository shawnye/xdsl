package unicom.xdsl.service.converter;

import unicom.common.Constants;
import unicom.common.DateHelper;
import unicom.common.port.FieldConvertException;
import unicom.common.port.FieldConverter;
import unicom.common.port.ImportConfig;

public class DateFieldConverter implements FieldConverter {

	public Object convertFrom(Object[] availableValues)
			throws FieldConvertException {
		int index = importConfig.getFieldIndex(field);
		if(index == -1){
			//配置错误！
			return null;
		}
		String dateStr = (String) availableValues[index];

		return DateHelper.toDate(dateStr, Constants.DEFAULT_DATE_PATTERNS);
	}

	private String field;
	private ImportConfig importConfig;

	public DateFieldConverter(ImportConfig importConfig,String field) {
		this.importConfig = importConfig;
		this.field = field;
	}

	public String getField() {
		return field;
	}

	public ImportConfig getImportConfig() {
		return importConfig;
	}
}
