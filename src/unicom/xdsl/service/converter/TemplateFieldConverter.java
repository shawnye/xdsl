package unicom.xdsl.service.converter;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;

import unicom.common.port.FieldConvertException;
import unicom.common.port.FieldConverter;
import unicom.common.port.ImportConfig;

public class TemplateFieldConverter implements FieldConverter {
	protected String field;//field name
	protected ImportConfig importConfig;
	protected String template;
	

	//可以不设置
//	protected Date currentTime = null;
//	
//	public Date getCurrentTime() {
//		return currentTime;
//	}
//
//	public void setCurrentTime(Date currentTime) {
//		this.currentTime = currentTime;
//	}
	
//	protected MessageFormat form = null;

//	private final static String FIELD_VALUE_PLACE_HOLDER = "${FV}";
//	private final static String CURRENT_DATE_PLACE_HOLDER = "${DATE}";
//	private final static String CURRENT_DATE_TIME_PLACE_HOLDER = "${DATETIME}";

	
	/**
	 * 
	 * @param importConfig
	 * @param field
	 * @param template
	 * {0}--fieldValue, {1} current datetime
	 */
	public TemplateFieldConverter(ImportConfig importConfig,String field, String template/*, Object ... args */) {
		this.importConfig = importConfig;
		this.field = field;
		this.template = template;
//		form = new MessageFormat(template);
	}

	public TemplateFieldConverter() {
		super();
	}

	public String getField() {
		return field;
	}
	
	public ImportConfig getImportConfig() {
		return importConfig;
	}
	
	public void setField(String field) {
		this.field = field;
	}

	public void setImportConfig(ImportConfig importConfig) {
		this.importConfig = importConfig;
	}

	public void setTemplate(String template) {
		this.template = template;
//		form = new MessageFormat(template);
	}

	@SuppressWarnings("unchecked")
	public Object convertFrom(Object[] availableValues)
			throws FieldConvertException {
		if(StringUtils.isBlank(this.template)){
			return this.template;
		}
		
		int index = importConfig.getFieldIndex(field);
		if(index == -1){
			//配置错误！
			return null;
		}
		
		String str = (String) availableValues[index];//field value
		
		List arguments = new ArrayList();
		arguments.add(str);
		arguments.add(DateFormatUtils.format(this.importConfig.getStamp(), "yyyy-MM-dd HH:mm"));
		
//		if(this.currentTime == null){
//			arguments.add(DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm"));
//		}else{
//			arguments.add(DateFormatUtils.format(this.currentTime, "yyyy-MM-dd HH:mm"));
//		}
		
		return MessageFormat.format( this.template,  arguments.toArray() );
		
//		str = StringUtils.replace(this.template, FIELD_VALUE_PLACE_HOLDER, str);
//		str = StringUtils.replace(str, CURRENT_DATE_PLACE_HOLDER, DateFormatUtils.format(new Date(), "yyyy-MM-dd"));
//		str = StringUtils.replace(str, CURRENT_DATE_TIME_PLACE_HOLDER, );
		
//		return str;
	}
}
