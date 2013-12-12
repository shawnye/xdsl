package unicom.common.excel;

public interface ExportSheetRowProcesser {
	/**
	 *
	 * @param config
	 * @param rowNum
	 * @param msg
	 * return row []表示空行，null表示结束
	 * 可以是title
	 */
	public Object[] getRow(SheetPortConfig config, int rowNum, StringBuffer msg);

}
