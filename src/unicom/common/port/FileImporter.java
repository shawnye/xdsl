package unicom.common.port;

import java.io.File;

public interface FileImporter {
	/**
	 * 
	 * @param srcFile
	 * @param importConfig
	 * @param msg
	 * @return
	 * @throws ExportFileException
	 */
	public boolean importFile(File srcFile, ImportConfig importConfig, StringBuffer msg)throws ImportFileException;

}
