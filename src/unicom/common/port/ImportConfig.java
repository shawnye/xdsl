package unicom.common.port;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 用于插入和更新时使用
 * @author yexy6
 *
 */
public class ImportConfig {
	private Log log = LogFactory.getLog(ImportConfig.class);

	private Date stamp;//导入批次时间印记
	private String table;//单表
	private String idField;//update 时使用
	private List<String> fields = new ArrayList<String>();
	private Map<String, Integer> fieldOrders = new HashMap<String, Integer>();
	//默认为null;
	private Map<String, Object> fieldDefaultValues = new HashMap<String, Object>();

	private Map<String, FieldConverter> fieldConverters = new HashMap<String, FieldConverter>();

	private Integer updateType = 0;//0 --create , 1- update , 2- create or update , 

	public ImportConfig() {
		this.stamp = new Date();
	}

	public Date getStamp() {
		return stamp;
	}

	@Deprecated
	public void setStamp(Date stamp) {
		this.stamp = stamp;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public void setIdField(String idField) {
		this.idField = idField;
	}

	public String getTable() {
		return table;
	}

	public String getIdField() {
		return idField;
	}

	public int getFieldIndex(String field) {
		return this.fields.indexOf(field);
	}


	/**
	 * 所有字段,并非都是导入
	 * @return
	 */
	public Collection<String> getFields() {
		return fields;//fieldOrders.keySet();
	}
	/**
	 * 导入时对应的数据库字段
	 * @return
	 */
	public Collection<String> getImportFields() {
		List<String> iflds = new ArrayList<String>();
		for (String field : fields) {
			if(field.startsWith("@")){
				continue;
			}
			iflds.add(field);
		}
		return iflds;
	}

	public Integer getOrderByField(String field){
		return this.fieldOrders.get(field);
	}

	public Integer getOrderByFieldIndex(Integer fieldIndex){
		if(fieldIndex==null || fieldIndex<0 || fieldIndex>this.fields.size()-1){
			return null;
		}
		return this.fieldOrders.get(this.fields.get(fieldIndex));
	}

	public Object getDefaultValueByField(String field){
		return this.fieldDefaultValues.get(field);
	}

	public Object getDefaultValueByFieldIndex(Integer fieldIndex){
		if(fieldIndex==null || fieldIndex<0 || fieldIndex>this.fields.size()-1){
			return null;
		}
		return this.fieldDefaultValues.get(this.fields.get(fieldIndex));
	}


	public FieldConverter getFieldConverterByField(String field){
		return this.fieldConverters.get(field);
	}

	public FieldConverter getFieldConverterByFieldIndex(Integer fieldIndex) {
		if(fieldIndex==null || fieldIndex<0 || fieldIndex>this.fields.size()-1){
			return null;
		}

		return this.fieldConverters.get(this.fields.get(fieldIndex));
	}

	public void setDefaultValue(String field,  Object defaultValue){
		if(!this.fieldOrders.containsKey(field)){
			return;
		}

		this.fieldDefaultValues.put(field, defaultValue);
	}

	public void addFieldConverter(FieldConverter fieldConverter){
		if(!this.fieldOrders.containsKey(fieldConverter.getField())){
			return;
		}

		this.fieldConverters.put(fieldConverter.getField(), fieldConverter);
	}
	/**
	 * field 不能重复!
	 * @param field
	 * @param order
	 * 可空，表示 '生成字段值'
	 */
	public void addFieldOrder(String field, Integer order){
		this.addFieldOrder(field, order, null);
	}

	public void addFieldOrder(String field, Integer order , Object defaultValue){
		if(StringUtils.isNotBlank(field)){
			if(this.fieldOrders.containsKey(field)){
				return;
			}
			this.fields.add(field);
			this.fieldOrders.put(field, order);
			this.fieldDefaultValues.put(field, defaultValue);
		}
	}

	public void clearFieldOrder(){
		this.fieldOrders.clear();
		this.fieldDefaultValues.clear();
		this.fieldConverters.clear();

		this.fields.clear();
	}

	//eg: username,p_id,address,user_no,password,area,tel,begin_date,state
	/**
	 * 如果刚好顺序一致...
	 */
	public void parseFieldsString(String fieldsString, String delim){
		if(StringUtils.isBlank(fieldsString)){
			return;
		}

		String[] fields = StringUtils.split(fieldsString, delim);
		for (int i = 0; i < fields.length; i++) {
			this.addFieldOrder(fields[i], i);
		}

	}

	/**
	 * 顺序从1开始，例如：
	 * username=11,p_id=32,address=33,user_no=74,password=75,area=,tel,begin_date=16,state=,@outer_vlan=
	 * 注意：取值也是按顺序来！
	 * @@表示忽略导入
	 * @param fieldOrdersString
	 * @param delim
	 */
	public void parseFieldOrdersString(String fieldOrdersString, String delim){
		if(StringUtils.isBlank(fieldOrdersString)){
			return;
		}

		String[] fields = StringUtils.split(fieldOrdersString, delim);//eg: username=11
		for (int i = 0; i < fields.length; i++) {
			String[] pairs = StringUtils.split(fields[i], "=");
			if(pairs.length ==1){
				this.addFieldOrder(fields[i].replace("=", ""), null);//remove =
			}else if(pairs.length > 1 ){
				if(StringUtils.isNotBlank(pairs[1])){
					Integer o = null;
					try {
						o = new Integer(pairs[1].trim());
						this.addFieldOrder(pairs[0], o);
					} catch (NumberFormatException e) {
						log.warn("忽略字段(非法顺序)：" + fields[i]);
					}

				}else{
					this.addFieldOrder(pairs[0], null);
				}
			}

		}

	}

	@Override
	public String toString() {
		return this.table + "(" + this.fields.size() + " 个字段)";
	}

	public static void main(String[] args) {
		ImportConfig config = new ImportConfig();
		config.setTable("user_info");
		config.parseFieldOrdersString("username=11,p_id=32,address=33,user_no=74,password=75,area=,tel,begin_date=16,state= ", ",");

		System.out.println(config);
	}


	public String getDefatulBatchNum(){
		return  "B" + DateFormatUtils.format(this.getStamp(), "yyyyMMddHHmm");
	}

	/**
	 * 
	 * @return
	 */
	public Integer getUpdateType() {
		return updateType;
	}

	public void setUpdateType(Integer updateType) {
		this.updateType = updateType;
	}



}
