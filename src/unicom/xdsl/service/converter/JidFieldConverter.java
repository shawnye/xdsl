package unicom.xdsl.service.converter;

import org.apache.commons.lang.StringUtils;

import unicom.common.port.FieldConvertException;
import unicom.common.port.FieldConverter;
import unicom.common.port.ImportConfig;
import unicom.xdsl.service.JxInfoService;

public class JidFieldConverter implements FieldConverter {
	private String field;
	private ImportConfig importConfig;
	private JxInfoService jxInfoService;

	public JidFieldConverter(ImportConfig importConfig,String field, JxInfoService jxInfoService) {
		this.importConfig = importConfig;
		this.field = field;
		this.jxInfoService = jxInfoService;
	}

	public String getField() {
		return field;
	}

	public ImportConfig getImportConfig() {
		return importConfig;
	}

	public Object convertFrom( Object[] availableValues) throws FieldConvertException {
		Object[] fieldsValues = availableValues;

		int index1 =  importConfig.getFieldIndex("j_id");
		if(index1 == -1){
			return null;
		}
		int index2 = importConfig.getFieldIndex("mdf_port");
		if(index2 == -1){
			return null;
		}


		String jx = (String) fieldsValues[index1];
		if(StringUtils.isBlank(jx)){
			return null;
		}
		String mdf_port = (String) fieldsValues[index2];
		if(StringUtils.isBlank(mdf_port)){
			return null;
		}

		//find j_id by jx, mdf_port ,update used
		return jxInfoService.findJid(jx.trim(), mdf_port.trim(), null);
	}



}
