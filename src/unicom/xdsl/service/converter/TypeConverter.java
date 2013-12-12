package unicom.xdsl.service.converter;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import unicom.common.port.FieldConvertException;
import unicom.common.port.FieldConverter;
import unicom.common.port.ImportConfig;

public class TypeConverter implements FieldConverter {

	private String field;
	private ImportConfig importConfig;
	private String[] validValues;
	
	public TypeConverter(ImportConfig importConfig,String field,String[] validValues) {
		this.importConfig = importConfig;
		this.field = field;
		this.validValues = validValues;
 	}
	
	public String getField() {
 		return field;
	}

	public ImportConfig getImportConfig() {
 		return importConfig;
	}

	public Object convertFrom(Object[] availableValues)
			throws FieldConvertException {
		int index = importConfig.getFieldIndex(field);
		if(index == -1){
			//配置错误！
			return null;
		}
		String intStr = (String) availableValues[index];
		if(StringUtils.isBlank(intStr)){
			throw new FieldConvertException("类型(type)不能为空!");
		}	
		
		
		intStr = intStr.trim().toUpperCase();
		if(!ArrayUtils.contains(validValues, intStr)){
			throw new FieldConvertException("不支持类型(type)："  + intStr + ", 请查看导入页面说明。若确实需要增加新类型，请联系管理员.");
		}
		
		return intStr;
	}

}
