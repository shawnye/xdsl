package unicom.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sf.ezmorph.bean.MorphDynaBean;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
 

public class JsonUtil {
	private static final Log log = LogFactory.getLog(JsonUtil.class);

	public static String getJsonString(Object obj){
		return getJsonString(obj,(String[])null);
	}

	public static String getJsonString(Object obj, String exclude){
		if(StringUtils.isBlank(exclude)){
			return getJsonString(obj);
		}else{
			return getJsonString(obj,new String[]{exclude});
		}
	}

	public static String getJsonString(Object obj, String[] excludes){
		return getJsonString(obj, excludes, false);
	}

	/**
	 *
	 * @param obj
	 * @param excludes
	 * @param silent
	 * not throw ex
	 * @return
	 */
	public static String getJsonString(Object obj, String[] excludes, boolean silent){
		if(obj == null){
			obj = new HashMap<String, Object>();
		}
		try {
			JsonConfig config = new JsonConfig();

			if(silent){
				config.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
			}

			if(excludes != null && excludes.length > 0){
				config.setExcludes(excludes);
			}
			if( obj.getClass().isArray() || Collection.class.isAssignableFrom(obj.getClass())){
				return JSONArray.fromObject(obj , config).toString();
			}else{
				return JSONObject.fromObject(obj , config).toString();
			}
		} catch (RuntimeException e) {
			log.error("无法获得对象的json值：" + obj, e);
			return "";
		}
	}

	public static JSONArray buildJson(List<Map<String,Object>> table){
		if(table == null || table.size() == 0){
			return null;
		}

		JSONArray array = new JSONArray();
		for (Map<String,Object> objects : table) {
			JSONObject jobj = new JSONObject();

			for (String key : objects.keySet()) {
				jobj.element(key, objects.get(key));
			}

			array.add(jobj);//注意顺序！
		}
		return array;
	}

	public static String testJsonTable(){
		String[] names = {"江门高沙机房","江门篁庄机房","江门阜康机房","新会明兴机房","新会金辉机房"};
		List<Map<String, Object>> table = new ArrayList<Map<String,Object>>();
		for (int i = 0; i < 21; i++) {
			Map<String, Object> mo = new LinkedHashMap<String, Object>();
			mo.put("seqNo", i+1);
			mo.put("maintainAgent", (i%2==0? "广西润建":"广州讯联"));
			mo.put("stationName", names[(int)Math.floor(Math.random()*names.length)]);
			mo.put("stationNo", 5000+i);
			mo.put("reserved1", 100+5*i);
			table.add( mo );
		}

		return buildJson(table ).toString();
	}

 
}
