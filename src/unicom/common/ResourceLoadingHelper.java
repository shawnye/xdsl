package unicom.common;

import java.io.File;
import java.net.URL;

public class ResourceLoadingHelper {
	/**
	 * load resource from current thread context
	 * @param classpath
	 * @return
	 */
	public static File loadFileFromClasspath(String classpath){
		URL resource = Thread.currentThread().getContextClassLoader().getResource(classpath);
		if(resource == null){
			resource = ResourceLoadingHelper.class.getClassLoader().getResource(classpath);
			if(resource == null){
				return null;
			}

		}
		String fileName = resource.getFile().replace("%20", " ");//替换空白

		return new File(fileName);
	}
}
