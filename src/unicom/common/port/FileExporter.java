package unicom.common.port;

import java.io.File;

public interface FileExporter {
	/**
	 * 
	 * @param destFile
	 * @param exportConfig
	 * @throws ExportFileException
	 */
	public void exportFile(File destFile, ExportConfig exportConfig)throws ExportFileException;
}
