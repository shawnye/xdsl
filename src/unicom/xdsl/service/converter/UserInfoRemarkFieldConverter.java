package unicom.xdsl.service.converter;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import unicom.common.port.FieldConvertException;
import unicom.common.port.ImportConfig;
import unicom.dao.BaseDaoInterface;

@SuppressWarnings("rawtypes")
@Component
public class UserInfoRemarkFieldConverter extends TemplateFieldConverter {
	protected Log log = LogFactory.getLog(this.getClass());
	
	private BaseDaoInterface baseDao;
	

	@Autowired
	public void setBaseDao(BaseDaoInterface baseDao) {
		this.baseDao = baseDao;
	}
	
	
	public UserInfoRemarkFieldConverter() {
		super();
	}



	/**
	 * 
	 * @param importConfig
	 * @param field
	 * @param template
	 * {0}--fieldValue, {1} current datetime,{2} 关联的jx_info
	 */
	public UserInfoRemarkFieldConverter(ImportConfig importConfig,
			String field, String template) {
		super(importConfig, field, template);
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
		
		Object jx_id =  availableValues[index];
		
		
		List arguments = new ArrayList();
		arguments.add(jx_id);
		arguments.add(DateFormatUtils.format(this.importConfig.getStamp(), "yyyy-MM-dd HH:mm"));
		
		index = importConfig.getFieldIndex("j_id");
		if(index != -1){
			String sqlFetch = "select * from jx_info j where j.j_id=?";
			
	
			Map jxInfo = null;
			try {
				jxInfo = this.baseDao.findUnique(sqlFetch, new Object[]{ availableValues[index]});
			} catch (Exception e) {
				log.error(e);
			}
			
			if(jxInfo != null){
				arguments.add("机房：" + jxInfo.get("jx") + "，设备号：" + jxInfo.get("sbh") + "，槽号：" + jxInfo.get("slot") + "，端口号：" + jxInfo.get("sb_port")+ "，MDF端口：" + jxInfo.get("mdf_port"));
			} 
		}
		
		if(arguments.size()==2){
			arguments.add("无");
		}
		
		return MessageFormat.format(this.template,  arguments.toArray() );
	}
}
