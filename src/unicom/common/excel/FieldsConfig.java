package unicom.common.excel;

import org.apache.commons.lang.StringUtils;

/**
 * 局限性：仅适用固定列标题
 * @author jmunicom
 *
 */
public class FieldsConfig {
	String[] fields = null;

	public String[] getFields() {
		return fields;
	}

	void setFields(String[] fields) {
		this.fields = fields;
	}

	public static FieldsConfig parseFields(String fields) {
		return parseFields(fields, ",");
	}
	/**
	 *
	 * @param fieldsStr
	 * @param delim
	 * 不能为空格！
	 * @return
	 */
	public static FieldsConfig parseFields(String fieldsStr,String delim) {
		FieldsConfig fc = new FieldsConfig();

		if(StringUtils.isNotBlank(fieldsStr)){
			String[] fs = StringUtils.split(fieldsStr.replaceAll("\\s+", ""), delim);//无空格
			fc.setFields(fs);
		}

		return fc;
	}


}
