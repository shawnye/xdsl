package unicom.common.excel;


/**
 * 导入导出配置使用
 * @author yexy6
 *
 */
public class SheetPortConfig {

	Object sheetKey;//pattern or index


	int skipRows = 1;//=导出标题行号
	int skipCols = 0;
	
	int maxRow;//未实现
	int maxCol;//未实现

	int cols=1;
	private ImportSheetRowProcesser rowProcesser;//导入
	private ExportSheetRowProcesser exportRowProcesser;//导出

	private FieldsConfig fieldsConfig;

	boolean evaluateFormula = true;
	boolean longNumberAsString = true;
	int numbersThreshold = 6;

//	int skipBlanksBeforeAppendRows = 1;
//	List<Object[]> appendedRows ;

	/**
	 * 导出专用，隔几行附加数据
	 * @return
	 */
//	public int getSkipBlanksBeforeAppendRows() {
//		return skipBlanksBeforeAppendRows;
//	}
//
//	public void setSkipBlanksBeforeAppendRows(int blanksBeforeAppendRows) {
//		this.skipBlanksBeforeAppendRows = blanksBeforeAppendRows;
//	}

	/**
	 * 导出专用，一般导出数据后附加下标，说明等等
	 * @return
	 */
//	public List<Object[]> getAppendedRows() {
//		return appendedRows;
//	}
//
//	public void setAppendedRows(List<Object[]> appendedRows) {
//		this.appendedRows = appendedRows;
//	}


	/**
	 * 导入时超过N位数字就作为文本而不是数字
	 * @return
	 */
	public boolean isLongNumberAsString() {
		return longNumberAsString;
	}

	public void setLongNumberAsString(boolean longNumberAsString) {
		this.longNumberAsString = longNumberAsString;
	}

	/**
	 * 是否展开公式
	 * @return
	 */
	public boolean isEvaluateFormula() {
		return evaluateFormula;
	}

	public void setEvaluateFormula(boolean evaluateFormula) {
		this.evaluateFormula = evaluateFormula;
	}

	/**
	 * 配合isLongNumberAsString()使用
	 * @return
	 */
	public int getNumbersThreshold() {
		return numbersThreshold;
	}

	public void setNumbersThreshold(int numbersThreshold) {
		this.numbersThreshold = numbersThreshold;
	}
	/**
	 * 例如11位数，则是 10000000000
	 * @return
	 */
	public long getNumberByThreshold(){
		long n = 1;
		for (int i = 0; i < this.numbersThreshold - 1; i++) {
			n = n * 10;
		}

		return n;
	}

	/**
	 *
	 * @return
	 */
	public int getSkipRows() {
		return skipRows;
	}

	public void setSkipRows(int skipRows) {
		if(skipRows < 0){
			skipRows = 0;
		}
		this.skipRows = skipRows;
	}

	public int getSkipCols() {
		return skipCols;
	}

	public void setSkipCols(int skipCols) {
		this.skipCols = skipCols;
	}

	public int getMaxRow() {
		return maxRow;
	}

	public void setMaxRow(int maxRow) {
		this.maxRow = maxRow;
	}

	public int getMaxCol() {
		return maxCol;
	}

	public void setMaxCol(int maxCol) {
		this.maxCol = maxCol;
	}

	/**
	 * 导入行数
	 * @return
	 */
	public int getCols() {
		return cols;
	}

	public void setCols(int cols) {
		this.cols = cols;
	}


	public ExportSheetRowProcesser getExportRowProcesser() {
		return exportRowProcesser;
	}

	public void setExportRowProcesser(ExportSheetRowProcesser exportRowProcesser) {
		this.exportRowProcesser = exportRowProcesser;
	}

	public ImportSheetRowProcesser getImportRowProcesser() {
		return rowProcesser;
	}

	public void setImportRowProcesser(ImportSheetRowProcesser excelRowProcesser) {
		this.rowProcesser = excelRowProcesser;
	}

	/**
	 * 更新列数
	 * @param fc
	 */
	public void setFieldsConfig(FieldsConfig fc) {
		this.fieldsConfig = fc;
		if(fc != null && fc.getFields() != null){
			this.cols = fc.fields.length;
		}
	}
	/**
	 *
	 * @return
	 */
	public FieldsConfig getFieldsConfig() {
		return fieldsConfig;
	}

	public static void main(String[] args) {
		System.out.println(new SheetPortConfig().getNumberByThreshold());
	}
}
