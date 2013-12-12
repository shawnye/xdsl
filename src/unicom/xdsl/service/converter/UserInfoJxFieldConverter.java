package unicom.xdsl.service.converter;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import unicom.common.port.FieldConvertException;
import unicom.common.port.FieldConverter;
import unicom.common.port.ImportConfig;
import unicom.dao.BaseDaoInterface;
/**
 * //自动配置横列：设置jx_id属性
 * @author yexy6
 *
 */
@Component
public class UserInfoJxFieldConverter implements FieldConverter {
	protected Log log = LogFactory.getLog(this.getClass());

	private BaseDaoInterface baseDao;

	@Autowired
	public void setBaseDao(BaseDaoInterface baseDao) {
		this.baseDao = baseDao;
	}

	private String field;
	private ImportConfig importConfig;


	public UserInfoJxFieldConverter() {
	}

	public UserInfoJxFieldConverter(ImportConfig importConfig,String field) {
		this.importConfig = importConfig;
		this.field = field;

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

	public Object convertFrom(Object[] availableValues)
			throws FieldConvertException {
		int index = importConfig.getFieldIndex(field);
		if(index == -1){
			//配置错误！
			return null;
		}

		String jx = (String) availableValues[index];//jx

		if(StringUtils.isBlank(jx)){
			log.error("机房名称不能为空");
			return null;
		}

		//涉及到 从 OSS机房名称到 adsl机房名称的转换...
		String sqlFetch = "select j_id from jx_info j where j.mdf_port=(\r\n" +
				"SELECT MIN(j0.mdf_port) AS Expr1\r\n" +
				"FROM jx_info j0\r\n" +
				"WHERE (j0.jx = (select o.jx from oss_jx_info o where o.oss_jx=? )) AND (j0.used = 0) AND (j0.u_id IS NULL)\r\n" +
				")\r\n" +
				"and (j.jx =(select o.jx from oss_jx_info o where o.oss_jx=? )) AND (j.used = 0) AND (j.u_id IS NULL)\r\n" ;

		Map jxInfo = null;
		try {
			jxInfo = this.baseDao.findUnique(sqlFetch, new Object[]{jx,jx});
		} catch (Exception e) {
			log.error(e);
		}

		if(jxInfo == null){
			log.error("找不到机房名称或端口已满：" + jx);
			return null;
		}



		return jxInfo.get("j_id");//jx_id
	}
}
