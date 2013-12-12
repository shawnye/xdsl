package unicom.common;

import java.util.Map;

public class MapHelper {
	private MapHelper(){}
	/**
	 * 
	 * @param map
	 */
	public static void trimAllStringValue(Map map){
		for (Object key : map.keySet()) {
			Object value = map.get(key);
			if(value != null && value.getClass() == String.class){
				map.put(key, value.toString().trim());
			}
		}
	}
	
	public static void removeAllNullValue(Map map){
		
	}

	public static MapValueGetter getMapValueGetter(Map map){
		return new MapValueGetter(map);
	}
}
