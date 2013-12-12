package unicom.xdsl.service.converter;

import java.text.MessageFormat;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import unicom.common.port.FieldConvertException;
import unicom.common.port.ImportConfig;

@Component
public class JxInfoRemarkFieldConverter extends TemplateFieldConverter {
	protected Log log = LogFactory.getLog(this.getClass());
	
	 
	public JxInfoRemarkFieldConverter() {
		super();
	}
 
	/**
	 * 
	 * @param importConfig
	 * @param field
	 * @param template
	 * {0}--fieldValue, {1} current datetime,{2} 关联的jx_info
	 */
	public JxInfoRemarkFieldConverter(ImportConfig importConfig,
			String field, String template) {
		super(importConfig, field, template);
	}

 	public Object convertFrom(Object[] availableValues)
			throws FieldConvertException {
		if(StringUtils.isBlank(this.template)){
			return this.template;
		}
		 
		
		return MessageFormat.format(this.template,  this.importConfig.getDefatulBatchNum()  );
	}
}
