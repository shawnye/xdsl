package unicom.common;

import java.io.File;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FileHelper {
	private static Log log = LogFactory.getLog(FileHelper.class);
	
	public static boolean createFile(File f){
		if(f == null){
			return false;
		}
		
		if(f.exists()){
			return true;
		}
		//create file
		log.debug("creating file:" + f.getAbsolutePath());
		if( !f.getParentFile().exists()){
			boolean b = f.getParentFile().mkdirs();
			if(!b){
				log.error("fail to create dir:" + f.getParentFile());
				return false;
			}
		}
		try {
			f.createNewFile();
		} catch (IOException e) {
			log.error("fail to create file:" ,e);
			return false;
		}
		
		return true;
	}

	public static boolean createDir(File f) {
		if(f == null){
			return false;
		}
		
		if(f.exists()){
			return true;
		}
		
		log.debug("creating dir:" + f.getAbsolutePath());
		return f.mkdirs();
	}
}
