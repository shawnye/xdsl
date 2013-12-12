package unicom.xdsl.service.converter;

import unicom.common.port.FieldConvertException;
import unicom.common.port.FieldConverter;
import unicom.common.port.ImportConfig;

public class AreaFieldConverter implements FieldConverter {
	private String field;
	private ImportConfig importConfig;
	
	public AreaFieldConverter(ImportConfig importConfig,String field) {
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
		
		int index = importConfig.getFieldIndex("address");
		if(index == -1){
			//配置错误！
			return null;
		}
		
//		if(fieldsValues.length <  index +1){//顺序是第三个
//			return null;
//		}
		//取地址前两个字符
		String address = (String) fieldsValues[index];
		if(address == null){
			return null;
		}
		
		address = address.replaceFirst("^江门市 ?", "");
		if(address == null || address.length() < 2){
			return null;
		}
		return address.substring(0, 2);
	}

	

}
