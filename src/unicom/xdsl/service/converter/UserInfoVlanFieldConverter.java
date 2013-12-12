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
 * //根据VLAN自动配置横列：设置jx_id属性
 * @author yexy6
 *
 */
@Component
public class UserInfoVlanFieldConverter implements FieldConverter {
	protected Log log = LogFactory.getLog(this.getClass());

	private BaseDaoInterface baseDao;

	@Autowired
	public void setBaseDao(BaseDaoInterface baseDao) {
		this.baseDao = baseDao;
	}

	private String field;
//	private String outer_vlan_field;
//	private String inner_vlan_field;
	private ImportConfig importConfig;


	public UserInfoVlanFieldConverter() {
	}

	public UserInfoVlanFieldConverter(ImportConfig importConfig,String field) {
		this.importConfig = importConfig;
		this.field = field;

	}

	public String getField() {
		return field;
	}
	
//	public String getOuterVlanField() {
//		return outer_vlan_field;
//	}
//	
//	public String getInnerVlanField() {
//		return inner_vlan_field;
//	}

	public void setField(String field) {
		this.field = field;
	}

	public ImportConfig getImportConfig() {
		return importConfig;
	}

//	public void setOuterVlanField(String field) {
//		this.outer_vlan_field = field;
//	}
//	
//	public void setInnerVlanField(String field) {
//		this.inner_vlan_field = field;
//	}

	public void setImportConfig(ImportConfig importConfig) {
		this.importConfig = importConfig;
	}

	/**
	 * 优先使用J_ID查询，如果J_ID为空，使用vlan查询
	 */
	public Object convertFrom(Object[] availableValues)
			throws FieldConvertException {
		int index = importConfig.getFieldIndex("j_id");//优先
		if(index == -1){
			//配置错误！
			return null;
		}
		String j_id_str = (String) availableValues[index];//
		if(StringUtils.isNotBlank(j_id_str)){
			j_id_str = j_id_str.trim();
			Integer j_id = Integer.parseInt(j_id_str);
			
			String sqlFetch = "select j_id from jx_info j where j.j_id = ? and (used=0 or used_ont_ports < ont_ports)" ;

			Map jxInfo = null;
			try {
				jxInfo = this.baseDao.findUnique(sqlFetch, new Object[]{j_id});
			} catch (Exception e) {
				log.error(e);
 			}
			if(jxInfo == null){
				log.error("无法查询到未占用的端口(J_ID):" + j_id);
				return null;
			}
			
			return j_id;
		}
		
		//==============================================================
		index = importConfig.getFieldIndex("@outer_vlan");
		if(index == -1){
			//配置错误！
			return null;
		}
		 
		String outer = (String) availableValues[index];//

		if(StringUtils.isBlank(outer)){
			log.error("外层VLAN不能为空");
			return null;
		}
		
		index = importConfig.getFieldIndex("@inner_vlan");
		if(index == -1){
			//配置错误！
			return null;
		}
		
		String inner = (String) availableValues[index];//

		if(StringUtils.isBlank(outer)){
			log.error("内层VLAN不能为空");
			return null;
		}
		
		index = importConfig.getFieldIndex("@access_room_name");
		if(index == -1){
			//配置错误！
			return null;
		}
		
		String access_room_name = (String) availableValues[index];//

		if(StringUtils.isBlank(access_room_name)){
			log.error("接入间名称不能为空");
			return null;
		}
		

		//涉及到 从 OSS机房名称到 adsl机房名称的转换...
		String sqlFetch = "select j_id from jx_info j where j.outer_vlan=? and j.inner_vlan=? and j.jx like ? and j.used=0" ;

		Map jxInfo = null;
		try {
			jxInfo = this.baseDao.findUnique(sqlFetch, new Object[]{outer.trim(),inner.trim() , "%" + access_room_name.trim() + "%"});
		} catch (Exception e) {
			log.error(e);
		}

		if(jxInfo == null){
			log.error("找不到VLAN对应机房端口："+ access_room_name +"<" + outer+"," +inner + ">");
			return null;
		}



		return jxInfo.get("j_id");//jx_id
	}
}
