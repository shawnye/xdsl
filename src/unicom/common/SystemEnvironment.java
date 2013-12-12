package unicom.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SystemEnvironment {
	protected static Log log = LogFactory.getLog(SystemEnvironment.class);

	private static final String SYSTEM_PROPERTIES_FILE = "config/system.properties";
	private SystemEnvironment(){}
	private static Properties properties = new Properties();
	static{
		//FIXME use ResourceLoadingHelper
		InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(SYSTEM_PROPERTIES_FILE);
		try {
			properties.load(inputStream);
		} catch (IOException e) {
			log.error("无法载入系统属性: " + SYSTEM_PROPERTIES_FILE ,e);
		}
	}

	public static String getProperty(String key){
		return properties.getProperty(key);
	}
	
	public static String getProperty(String key, String defaultValue){
		return ObjectUtils.toString(properties.getProperty(key),defaultValue);
	}
}
