package unicom.xdsl.service.converter;

import org.apache.commons.lang.StringUtils;

import unicom.common.port.FieldConvertException;
import unicom.common.port.FieldConverter;
import unicom.common.port.ImportConfig;

public class IntegerFieldConverter implements FieldConverter {

	public Object convertFrom(Object[] availableValues)
			throws FieldConvertException {
		int index = importConfig.getFieldIndex(field);
		if(index == -1){
			//配置错误！
			return null;
		}
		String intStr = (String) availableValues[index];
		if(StringUtils.isBlank(intStr)){
			return defaultValue;
		}
		
		if(intStr.equalsIgnoreCase("true")){
			return 1;
		}else if(intStr.equalsIgnoreCase("false")){
			return 0;
		}

		return Integer.parseInt(intStr.trim());
	}

	private String field;
	private ImportConfig importConfig;
	private Integer defaultValue = null;

	public IntegerFieldConverter(ImportConfig importConfig,String field,Integer defaultValue) {
		this.importConfig = importConfig;
		this.field = field;
		this.defaultValue = defaultValue;
	}
	
	public IntegerFieldConverter(ImportConfig importConfig,String field) {
		this(importConfig,field,null);
	}

	public String getField() {
		return field;
	}

	public ImportConfig getImportConfig() {
		return importConfig;
	}
	
}
