package unicom.common.excel;

import java.util.LinkedHashMap;
import java.util.Map;

public class ExcelPortConfig {
	private Map <Object, SheetPortConfig> sheetPortConfigs = new LinkedHashMap<Object, SheetPortConfig>();

	private boolean createSheetIfNotFound = true;//导入忽略，一般导出要求true

	private String spreadsheetVersion = "EXCEL97";

	/**
	 * 导入忽略，一般导出要求true
	 * 找到sheet则作为模板。
	 * @return
	 */
	public boolean isCreateSheetIfNotFound() {
		return createSheetIfNotFound;
	}

	public void setCreateSheetIfNotFound(boolean createSheetIfNotFound) {
		this.createSheetIfNotFound = createSheetIfNotFound;
	}

	/**
	 * 必须指定sheet处理，不指定不处理
	 * 注意不要重复！（特别是index与名称可能同指向一个sheet）
	 * @param sheetKey
	 * name pattern or index
	 * @param config
	 */
	public void addSheetPortConfig(Object sheetKey, SheetPortConfig config){
		if(sheetKey != null){
			this.sheetPortConfigs.put(sheetKey, config);
		}
	}

	public Map<Object, SheetPortConfig> getSheetPortConfigs() {
		return sheetPortConfigs;
	}

	public SheetPortConfig getSheetPortConfig(Object sheetKey){
		return this.sheetPortConfigs.get(sheetKey);
	}

	public void clearSheetPortConfigs(){
		this.sheetPortConfigs.clear();
	}

	public String getSpreadsheetVersion() {
		return spreadsheetVersion;
	}

	public void setSpreadsheetVersion(String spreadsheetVersion) {
		this.spreadsheetVersion = spreadsheetVersion;
	}


}
