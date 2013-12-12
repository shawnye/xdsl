package unicom.xdsl.service.converter;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import unicom.common.port.FieldConvertException;
import unicom.common.port.FieldConverter;
import unicom.common.port.ImportConfig;
import unicom.xdsl.service.OntService;
/**
 * 自动更新ont表状态
 * @author yexy6
 *
 */
@Component
public class OntIdFieldConverter implements FieldConverter {
	protected Log log = LogFactory.getLog(this.getClass());
	
	private String field ;
	private ImportConfig importConfig;
//	private BaseDaoInterface baseDao;
//
//	@Autowired
//	public void setBaseDao(BaseDaoInterface baseDao) {
//		this.baseDao = baseDao;
//	}
	
	private OntService ontService;
	 
	@Autowired
	public void setOntService(OntService ontService) {
		this.ontService = ontService;
	}
 
	
	public void setField(String field) {
		this.field = field;
	}

	public String getField() {
		return field;
	}
	
	public ImportConfig getImportConfig() {
		return importConfig;
	}
 
	public void setImportConfig(ImportConfig importConfig) {
		this.importConfig = importConfig;
	}


	public Object convertFrom( Object[] availableValues) throws FieldConvertException {
		Object[] fieldsValues = availableValues;
		
		int index = importConfig.getFieldIndex(field);
		if(index == -1){
			//配置错误！
			return null;
		}
		
		int j_id_idx = importConfig.getFieldIndex("j_id");
		if(j_id_idx == -1){
			//配置错误！
			return null;
		}
 
 		String ontId = (String) fieldsValues[index];
 		if(StringUtils.isBlank(ontId)){
 			return null;
 		}
 		Integer jid = (Integer) fieldsValues[j_id_idx]; //j_id已经有或由vlan查出来了(排在后面)
		//ONT标注占用
 		int updates = this.ontService.updateStatus(jid,ontId,1);
  		
		return ontId;
	}

	public OntIdFieldConverter() {
		super();
	}

	

}
