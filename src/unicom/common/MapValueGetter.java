package unicom.common;

import java.text.ParseException;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class MapValueGetter {
	private Log log = LogFactory.getLog(this.getClass());

	private Map map = null;

	public MapValueGetter(Map map) {
		super();
		this.map = map;
	}

	public Object get(String key) {
		return this.map.get(key);
	}

	public Boolean getAsBoolean(String key) {
		Object val = this.get(key);
		if(val!= null && Boolean.class.isAssignableFrom(val.getClass())){
			return (Boolean) val;
		}
		return Boolean.valueOf(this.getAsString(key));
	}

	public Date getAsDate(String key) {
		return this.getAsDate(key, Constants.DEFAULT_DATE_PATTERNS);
	}

	public Date getAsDate(String key, String[] patterns) {
		Object val = this.get(key);
		if(val == null || Date.class.isAssignableFrom(val.getClass())){
			return (Date) val;
		}

		String string = this.getAsString(key);
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

	public Integer getAsInteger(String key) {
		Object val = this.get(key);
		if(val == null || Integer.class.isAssignableFrom(val.getClass())){
			return (Integer) val;
		}

		String s = this.getAsString(key);
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

	public String getAsString(String key) {
		Object object = this.get(key);
		if(object != null){
			return object.toString();
		}
		return null;
	}
	/**
	 *
	 * @param key
	 * @return
	 */
	public String getAsString2(String key) {
		Object object = this.get(key);
		if(object != null){
			if(Date.class.isAssignableFrom(object.getClass())){
				return DateFormatUtils.format((Date)object,  Constants.DEFAULT_DATE_PATTERN);
			}
			return object.toString().trim();
		}
		return null;
	}

	public String getAsTrimedString(String key) {
		Object object = this.get(key);
		if(object != null){
			return object.toString().trim();
		}
		return null;
	}
	/**
	 * 去掉小数点和后面的数字,如.0
	 * @param key
	 * @return
	 */
	public String getAsIntegerString(String key) {
		Object object = this.get(key);
		if(object != null){
			return object.toString().trim().replaceFirst("\\.\\d+", "");
		}
		return null;
	}
}
