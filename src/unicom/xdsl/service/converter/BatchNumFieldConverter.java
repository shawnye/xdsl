package unicom.xdsl.service.converter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import unicom.common.port.FieldConvertException;
import unicom.common.port.FieldConverter;
import unicom.common.port.ImportConfig;
/**
 * 导入批次
 * @author yexy6
 *
 */
public class BatchNumFieldConverter implements FieldConverter {
	protected Log log = LogFactory.getLog(this.getClass());
	private String field;
	private ImportConfig importConfig;
	 
	 
	public BatchNumFieldConverter(ImportConfig importConfig,String field ) {
		this.importConfig = importConfig;
		this.field = field; 
	}
 
 
 	public Object convertFrom(Object[] availableValues)
			throws FieldConvertException {
 		return this.importConfig.getDefatulBatchNum();
	}


	public String getField() {
		return field;
	}


	public ImportConfig getImportConfig() {
 		return importConfig;
	}
 	
 	
}
