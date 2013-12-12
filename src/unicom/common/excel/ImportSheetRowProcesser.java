package unicom.common.excel;

public interface ImportSheetRowProcesser {
	/**
	 *
	 * @param config
	 * @param rowNum
	 * @param row
	 * row data,
	 * for import: data come from excel files
	 * for export: data come from db ,other files,etc
	 * @param msg
	 */
	public void process(SheetPortConfig config, int rowNum, Object[] row, StringBuffer msg);

}
