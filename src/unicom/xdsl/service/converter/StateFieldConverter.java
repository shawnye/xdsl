package unicom.xdsl.service.converter;

import unicom.common.port.FieldConvertException;
import unicom.common.port.FieldConverter;
import unicom.common.port.ImportConfig;

public class StateFieldConverter implements FieldConverter {
	private String field;
	private ImportConfig importConfig;
	
	public StateFieldConverter(ImportConfig importConfig,String field) {
		this.importConfig = importConfig;
		this.field = field;
	}

	public String getField() {
		return field;
	}
	
	public ImportConfig getImportConfig() {
		return importConfig;
	}

	public Object convertFrom( Object[] availableValues) throws FieldConvertException {
		Object[] fieldsValues = availableValues;
		
		int index = importConfig.getFieldIndex("j_id");
		if(index == -1){
			//配置错误！
			return null;
		}
		
//		if(fieldsValues.length <  index +1){//顺序是第三个
//			return null;
//		}

		Object j_id = fieldsValues[index];
		if(j_id == null ){
			return "未分配";
		}else{
			return "预分配";
		}
	}

	

}
