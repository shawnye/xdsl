package unicom.common;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class SearchConditions {
	private Log log = LogFactory.getLog(SearchConditions.class);

	private Map<String, SearchKey> keys = new HashMap<String, SearchKey>();
	private Map<SearchKey, Object> conditions = new HashMap<SearchKey, Object>();

	/**
	 * 不能重复key，问题是  c is null or c=''
	 * @param key
	 * @return
	 */
	public Object getConditionValue(String key) {
		SearchKey searchKey = this.keys.get(key);
		if(searchKey == null){
			return null;
		}
		return this.conditions.get(searchKey);
	}

	public Map<SearchKey, Object> getConditions() {
		return this.conditions;
	}

	public void addCondition(String key, Object value) {
		if(StringUtils.isNotBlank(key)){

			SearchKey searchKey = new SearchKey(key);
			if(String.class.isAssignableFrom(value.getClass()) ){
				searchKey.setThetaIfNull("like");
			}else{
				searchKey.setThetaIfNull("=");
			}

			this.conditions.put(searchKey , value);
			this.keys.put(key, searchKey);
		}
	}

	public void clear() {
		this.conditions.clear();
	}
	/**
	 *
	 * @param key
	 * @return
	 */
	public String getEchoField(String key){
		SearchKey searchKey = this.keys.get(key);
		if(searchKey == null){
			return null;
		}
		if(">=".equals(searchKey.getTheta()) || ">".equals(searchKey.getTheta())){
			return searchKey.getFieldStart();
		}else if("<=".equals(searchKey.getTheta()) || "<".equals(searchKey.getTheta())){
			return searchKey.getFieldEnd();
		}

		return searchKey.getField();
	}

	public String getField(String key){
		return this.keys.get(key).getField();
	}

	public String getFieldStart(String key){
		return this.keys.get(key).getFieldStart();
	}

	public String getFieldEnd(String key){
		return this.keys.get(key).getFieldEnd();
	}


	public Boolean getConditionValueAsBoolean(String key) {
		return Boolean.valueOf(this.getConditionValueAsString(key));
	}

	public Date getConditionValueAsDate(String key) {
		return this.getConditionValueAsDate(key, Constants.DEFAULT_DATE_PATTERNS);
	}

	public Date getConditionValueAsDate(String key, String[] patterns) {
		Object conditionValue = this.getConditionValue(key);
		if(conditionValue == null || Date.class.isAssignableFrom(conditionValue.getClass())){
			return (Date) conditionValue;
		}

		String string = this.getConditionValueAsString(key);
		if(string == null){
			return null;
		}
		try {
			return DateUtils.parseDate(string, patterns);
		} catch (ParseException e) {
			log.error("Fail to getConditionValueAsDate:" + string, e);
			return null;
		}
	}

	public Integer getConditionValueAsInteger(String key) {
		Object conditionValue = this.getConditionValue(key);
		if(conditionValue == null || Integer.class.isAssignableFrom(conditionValue.getClass())){
			return (Integer) conditionValue;
		}

		String s = this.getConditionValueAsString(key);
		if(s == null){
			return null;
		}
		try {
			return new Integer(s);
		} catch (NumberFormatException e) {
			log.error("Fail to getConditionValueAsInteger", e);
			return null;
		}
	}

	public String getConditionValueAsString(String key) {
		Object object = this.getConditionValue(key);
		if(object != null){
			return object.toString();
		}
		return null;
	}
}
