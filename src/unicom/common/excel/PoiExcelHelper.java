package unicom.common.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


/**
 * 支持Excel 2003~2007 简化读写和导入导出
 *
 * Excel read and write
 * @author Shawn Ye
 * @version 1.1
 *
 */
public class PoiExcelHelper {
	private static final Log log = LogFactory.getLog(PoiExcelHelper.class);


//	public final static String[] ALPHA_BETA = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};

	/**
	 * of no veil
	 */
//	public static PoiExcelHelper newInstance() {
//		HSSFWorkbook workbook = new HSSFWorkbook();
//		HSSFSheet sheet = workbook.createSheet();
//		PoiExcelHelper instance= new PoiExcelHelper(workbook, sheet);
//		return instance;
//	}

	private SpreadsheetVersion sheetVersion = SpreadsheetVersion.EXCEL97;
	private Workbook workbook;
	private Sheet sheet;
	private FormulaEvaluator formulaEvaluator ;

	private transient CellStyle currentStyle;
	private int currentRowNum=-1;
	private int currentColNum=-1;


	public SpreadsheetVersion getSheetVersion() {
		return sheetVersion;
	}
	
	public void switchToSheet(Sheet newSheet) {
		if(newSheet == null || newSheet.equals(this.sheet)){
			return;
		}
		this.sheet = newSheet;
		this.currentStyle = null;
		this.currentColNum = -1;
		this.currentRowNum = -1;
	}

	
	public PoiExcelHelper(Sheet sheet){
		if(sheet == null){
			throw new IllegalArgumentException("workbook required");
		}
		this.workbook = sheet.getWorkbook();
		
		SpreadsheetVersion version = this.sheetVersion;//default
		if(XSSFWorkbook.class == workbook.getClass()){
			version = SpreadsheetVersion.EXCEL2007;
		}

		init(sheet.getWorkbook() , sheet, version);
	}
	
	/**
	 *
	 * @param workbook
	 * @param sheet
	 * @param sheetVersion
	 * 根据workbook自动判断
	 */
	public PoiExcelHelper(Workbook workbook, Sheet sheet){
		if(workbook == null){
			throw new IllegalArgumentException("workbook required");
		}

		SpreadsheetVersion version = this.sheetVersion;//default
		if(XSSFWorkbook.class == workbook.getClass()){
			version = SpreadsheetVersion.EXCEL2007;
		}

		init(workbook, sheet, version);
	}

	public PoiExcelHelper(Workbook workbook, Sheet sheet, String versionStr){
		SpreadsheetVersion version = this.sheetVersion;//default
		if(StringUtils.isNotBlank(versionStr)){
			version = SpreadsheetVersion.valueOf(versionStr.trim().toUpperCase());
		}
		init(workbook, sheet,  version);

	}

	/**
	 * do jobs in one sheet.
	 * @param workbook
	 * @param sheet
	 * @param sheetVersion
	 * Excel97 / Excel2007
	 */
	private void init(Workbook workbook, Sheet sheet, SpreadsheetVersion version){
		if(workbook == null){
			throw new IllegalArgumentException("workbook required");
		}
		if(sheet == null){
			throw new IllegalArgumentException("sheet required");
		}
		this.workbook = workbook;
		this.sheet = sheet;
		this.sheetVersion = version;

 		if(SpreadsheetVersion.EXCEL97 == sheetVersion){
			this.formulaEvaluator = new HSSFFormulaEvaluator((HSSFWorkbook) workbook);
		}else if(SpreadsheetVersion.EXCEL2007 == sheetVersion){
			this.formulaEvaluator = new XSSFFormulaEvaluator((XSSFWorkbook) workbook);
		}

	}


	public Workbook getWorkbook() {
		return workbook;
	}

	public Sheet getSheet() {
		return sheet;
	}
	
	

	public CellStyle getCurrentStyle() {
		return currentStyle;
	}


	public PoiExcelHelper setCurrentStyle(CellStyle currentStyle) {
		this.currentStyle = currentStyle;

		return this;
	}

	public PoiExcelHelper setCurrentPosition(int rowNum, int colNum){
		if(rowNum < 0){
			this.currentRowNum = -1;
		}else{
			this.currentRowNum = rowNum;
		}

		if(colNum < 0){
			this.currentColNum = -1;
		}else{
			this.currentColNum = colNum;
		}

		return this;
	}


	public int getCurrentRowNum() {
		return currentRowNum;
	}


	public int getCurrentColNum() {
		return currentColNum;
	}

	static Pattern pattern = Pattern.compile("([A-Z]{1,2})(\\d+)");
	/**
	 * such as B3, AH23
	 * @param alphabetaDigitStr
	 * @return
	 */
	public Cell getCellByAlphabetaDigitFormat(String alphabetaDigitStr){
		if(StringUtils.isBlank(alphabetaDigitStr)){
			return null;
		}

		Matcher matcher = pattern.matcher(alphabetaDigitStr.trim().toUpperCase());

		boolean b = matcher.lookingAt();
		if(!b){
			log.warn("未知EXCEL定位符：" + alphabetaDigitStr + ", 要求格式：字母＋数字");
		}

		String colstr = matcher.group(1);

		Integer cellRowNum = Integer.valueOf(matcher.group(2))-1;
		Integer cellColNum = toColNum(colstr)-1;

		return this.getCell(cellRowNum, cellColNum);

	}

	/**
	 * 字母转数字
	 * eg: C==>3, AH==>34
	 * @param colstr
	 * @return
	 */
	private Integer toColNum(String colstr) {
		int sum = 0;
		for (int i = 0; i < colstr.length(); i++) {
			sum += (colstr.length()- i - 1 )*26 + (colstr.charAt(i)-'A') ;
		}

		return sum+1;
	}

	/**
	 * 
	 * @param cell
	 * @param evaluateFormula
	 * @return
	 */
    public Object getCellValue(Cell cell, boolean evaluateFormula) {
    	if(cell == null){
    		return null;
    	}
    	int cellType = cell.getCellType();
    	CellValue value = null;
    	
    	FormulaEvaluator e = null;
		if(this.getSheetVersion() == SpreadsheetVersion.EXCEL97){
			e= new HSSFFormulaEvaluator((HSSFWorkbook) sheet.getWorkbook());
		}else if(this.getSheetVersion() == SpreadsheetVersion.EXCEL2007){
			e= new XSSFFormulaEvaluator((XSSFWorkbook) sheet.getWorkbook());
		}
		
    	if(evaluateFormula && Cell.CELL_TYPE_FORMULA == cellType){
			value = e.evaluate(cell);
			cellType = value.getCellType();
		}else{
			value = null;
			cellType = cell.getCellType();
		}
    	
		Object cellValue = null;
        switch (cellType) {
        case Cell.CELL_TYPE_STRING:
        	if(value != null){
        		cellValue = ObjectUtils.toString(value.getStringValue(),"");
        	}else{
        		cellValue = ObjectUtils.toString(cell.getStringCellValue(),"");
        	}

            break;
        case Cell.CELL_TYPE_NUMERIC:
        	Double d = null;
        	if(value != null){
        		d = value.getNumberValue();
        	}else{
        		d = cell.getNumericCellValue();
        	}

//            if(config.isLongNumberAsString() && d > config.getNumberByThreshold() ){
//            	cellValue = df.format(d);
//            }else{
//            	 cellValue = d;
//            }
        	 cellValue = d;
            break;
        case Cell.CELL_TYPE_FORMULA:   //?
        	cellValue= cell.getCellFormula();

//            cell.setCellType(Cell.CELL_TYPE_NUMERIC);
//            cellValue = String.valueOf(cell.getNumericCellValue());
            break;
        case Cell.CELL_TYPE_BLANK:
            cellValue="";
            break;
        case Cell.CELL_TYPE_BOOLEAN:
        	if(value != null){
        		cellValue = value.getBooleanValue();
        	}else{
        		cellValue = cell.getBooleanCellValue();
        	}

            break;
        case Cell.CELL_TYPE_ERROR:

        	if(value != null){
        		cellValue = value.getErrorValue();
        	}else{
        		cellValue = cell.getErrorCellValue();
        	}


            break;
        default:
            break;
        }  
    	
        return cellValue;
	}
    
	public  List<Object[]>  readRange(int startRowNum, int endRowNum, int startColumnNum, int endColumnNum ) {
		if(startRowNum > endRowNum || startRowNum < 0){
			return null;
		}
		if(startColumnNum>endColumnNum || startColumnNum < 0){
			return null;
		}
		List<Object[]> objList = new ArrayList<Object[]>();
		Object[] objs = new Object[endColumnNum-startColumnNum+1];
		Cell cell = null;
		for (int i = startRowNum; i <= endRowNum; i++) {
			for (int j = startColumnNum; j <= endColumnNum; j++) {
				cell = getCell(i, j, false);
				objs[i]= getCellValue(cell, true);
			}
			objList.add(objs);
		}
		
		return objList;
	}
	
	/**
	 * 
	 * @param sheet
	 * @param startColumnNum
	 * 0-based
	 * @param endColumnNum
	 * included
	 * @return
	 */
    public  Object[]  readRow(int rowNum, int startColumnNum, int endColumnNum) {
		if(rowNum<0 || startColumnNum>endColumnNum || startColumnNum < 0){
			return null;
		}
		Cell cell = null;
		Object[] objs = new Object[endColumnNum-startColumnNum+1];
		for (int i = startColumnNum; i <= endColumnNum; i++) {
			cell = getCell(rowNum, i, false);
			objs[i]= getCellValue(cell, true);
		} 
		
		return objs;
	}
   

	/**
     * 0-based
     * @param sheet
     * @param startRowNum
     * @param endRowNum
     * included
     * @return
     */
    public Object[]  readColumn(int columnNum, int startRowNum, int endRowNum) {
		if(startRowNum > endRowNum || startRowNum < 0){
			return null;
		}
		Cell cell = null;
		Object[] objs = new Object[endRowNum-startRowNum+1];
		for (int i = startRowNum; i <= endRowNum; i++) {
			cell = getCell(i, columnNum, false);
			objs[i]= getCellValue(cell, true);
		} 
		
		return objs;
	}
	
	/**
	 * format: row,col
	 * @param rowColStr
	 * @return
	 */
	public Cell getCellByRowColFormat(String rowColStr){
		if(StringUtils.isBlank(rowColStr)){
			return null;
		}

		String[] rc = rowColStr.trim().split(",");

		if(rc.length < 2 ){
			return null;
		}

		Integer cellRowNum = Integer.valueOf(rc[0])-1;
		Integer cellColNum = Integer.valueOf(rc[1])-1;

		return this.getCell(cellRowNum, cellColNum);
	}

	/**
	 * 判断是否空CELL
	 * @param cellRowNum
	 * @param cellColNum
	 * @return
	 */
	public boolean isNullCell(int cellRowNum, int cellColNum){
		Row row = this.sheet.getRow(cellRowNum );
		if(row == null){
			return true;
		}

		Cell cell = row.getCell( cellColNum );
		if(cell == null ){
			return true;
		}

		return false;
	}

	public Cell getCell(int cellRowNum, int cellColNum){
		return this.getCell(cellRowNum, cellColNum, true);
	}
	/**
	 * 核心方法: 获得指定单元格
	 * @param cellRowNum
	 * @param cellColNum
	 * @return
	 */
	public Cell getCell(int cellRowNum, int cellColNum , boolean createIfNull){
//		System.err.println("access cell : " +cellRowNum +", " +cellColNum);

		Row row = this.sheet.getRow(cellRowNum );
		if(row == null && createIfNull){
			row = sheet.createRow(cellRowNum );
		}

		if(row == null){
			throw new IllegalArgumentException("不能获得行:" + (cellRowNum ));
		}

		Cell cell = row.getCell( cellColNum );
		if(cell == null && createIfNull){
			cell = row.createCell(  cellColNum );
		}

		return cell;
	}
	/**
	 * 与getBottomRightCell()不同，要求设置当前位置
	 * @see setCurrentPosition(int, int )
	 * @param deltaRow
	 * @param deltaCol
	 * @return
	 */
	public Cell moveToCell(int rowDelta, int colDelta){
		if(this.currentRowNum < 0 || this.currentColNum < 0){
			return null;
		}

		return this.getBottomRightCell(this.currentRowNum, this.currentColNum, rowDelta, colDelta);
	}
	/**
	 * 要求设置当前位置
	 * @see setCurrentPosition(int, int )
	 *
	 * @param delta
	 * @return
	 */
	public Cell left(int delta){
		if(this.currentRowNum < 0 || this.currentColNum < 0){
			return null;
		}
		return moveToCell(0, -delta);
	}

	/**
	 * 要求设置当前位置
	 * @see setCurrentPosition(int, int )
	 *
	 * @param delta
	 * @return
	 */
	public Cell right(int delta){
		if(this.currentRowNum < 0 || this.currentColNum < 0){
			return null;
		}
		return moveToCell(0, delta);
	}

	/**
	 * 要求设置当前位置
	 * @see setCurrentPosition(int, int )
	 *
	 * @param delta
	 * @return
	 */
	public Cell up(int delta){
		if(this.currentRowNum < 0 || this.currentColNum < 0){
			return null;
		}
		return moveToCell(-delta, 0);
	}

	/**
	 * 要求设置当前位置
	 * @see setCurrentPosition(int, int )
	 *
	 * @param delta
	 * @return
	 */
	public Cell down(int delta){
		if(this.currentRowNum < 0 || this.currentColNum < 0){
			return null;
		}
		return moveToCell(delta, 0);
	}

	/**
	 * 核心方法
	 * @param cell
	 * @param value
	 * @param style
	 * @param commentStr
	 */
	public PoiExcelHelper setCell(Cell cell, Object value, CellStyle style, String commentStr){
		if(cell == null){
			return this;
		}
		if(style !=null){
			cell.setCellStyle(style);
		}

		if(value != null){
			if(Number.class.isAssignableFrom(value.getClass())){
				cell.setCellValue(((Number)value).doubleValue());
			}else if(Boolean.class.isAssignableFrom(value.getClass())){
				cell.setCellValue((Boolean) value);
			}else if(Date.class.isAssignableFrom(value.getClass())){
				cell.setCellValue((Date)value);
			}else if(Calendar.class.isAssignableFrom(value.getClass())){
				cell.setCellValue((Calendar)value);
			}else{
				cell.setCellValue((value.toString()));
			}

		}
		//FIXME ClientAnchor如何使用?
//		if(StringUtils.isNotBlank(commentStr)){
//			Patriarch patr = sheet.createDrawingPatriarch();
//			Comment comment = patr.createComment(new ClientAnchor(0, 0, cell.getCellNum(), cell.getCellNum(), (short)(cell.getCellNum()), 2, (short) (cell.getCellNum()+4), 8));
//			comment.setString(new RichTextString(commentStr));
////			comment.setAuthor("Apache Software Foundation");
////			comment.setFillColor(204, 236, 255);
//
//			cell.setCellComment(comment);
//		}
		return this;
	}

	public PoiExcelHelper setCell(Cell cell, Object value, CellStyle style){
		return this.setCell(cell, value, style, null);
	}

	public void setCell(int rowNum , int colNum, Object value, CellStyle style, String commentStr){
		setCell(this.getCell(rowNum, colNum), value, style, commentStr);
	}

	public void setCellByRowColFormat(String rowColStr, Object value, CellStyle style){
		setCell(this.getCellByRowColFormat(rowColStr), value, style);
	}

	public void setCellByAlphabetaDigitFormat(String alphabetaDigitStr, Object value, CellStyle style){
		setCell(this.getCellByAlphabetaDigitFormat(alphabetaDigitStr), value, style);
	}

	/**
	 * 设置指定位置的Cell的值和风格
	 * @param sheet
	 * @param row
	 * @param col
	 * @param value
	 * @param style
	 */
	public void setCell(int rowNum , int colNum, Object value, CellStyle style){
		setCell(rowNum, colNum, value, style,null);
	}

	public void setCell(int rowNum , int colNum, Object value){
		setCell(rowNum, colNum, value, null);
	}
	/**
	 * 合并单元格
	 * @param sheet
	 * @param startRow
	 * 起始行号
	 * @param startCol
	 * 起始列号
	 * @param rowLen
	 * 行数
	 * @param colLen
	 * 列数
	 * @param style
	 * return 合并区左上角的单元格
	 */
	public Cell mergeCells(int startRow , int startCol, int rowLen, int colLen, CellStyle style){
		sheet.addMergedRegion(new CellRangeAddress(startRow ,(short)(startCol) ,startRow+rowLen ,(short)(startCol + colLen)));
		return this.getCell(startRow, startCol);
	}
	/**
	 * 在一行内合并多个单元格
	 * @param rowNum
	 * @param startCol
	 * @param colLen
	 * 0-自身
	 * @return
	 */
	public Cell mergeCellsInOneRow( int rowNum , int startCol, int colLen){
		return mergeCells(rowNum, startCol, 0, colLen, null);
	}
	/**
	 * 在一列内合并多个单元格
	 * @param startRow
	 * @param rowLen
	 * 0-自身
	 * @param colNum
	 * @return
	 */
	public Cell mergeCellsInOneCol( int startRow , int rowLen,int colNum ){
		return mergeCells(startRow, colNum, rowLen, 0, null);
	}

	public Cell mergeCells( int startRow , int startCol, int rowLen, int colLen){
		return mergeCells(startRow, startCol, rowLen, colLen, null);
	}

	/**
	 * 公式等预生成。并替换单元格？
	 * @param cell
	 * @return
	 */
	public Cell getEvaluatedCell(Cell cell){
		if(cell == null){
			return null;
		}

		return formulaEvaluator.evaluateInCell(cell);
	}
	/**
	 * 获得右边的单元格
	 * @param cellRowNum
	 * @param cellColNum
	 * @param delta
	 * 第0个是自身,1为右边第一个,－1为左边第一个
	 * @return
	 */
	public Cell getRightCell(int cellRowNum, int cellColNum,  int delta){
		return getBottomRightCell(cellRowNum,  cellColNum, 0, delta);
	}
	/**
	 * 获得下边的单元格
	 * @param cellRowNum
	 * @param cellColNum
	 * @param delta
	 * 第0个是自身,1为下边第一个,－1为上边第一个
	 * @return
	 */
	public Cell getBottomCell(int cellRowNum, int cellColNum,  int delta){
		return getBottomRightCell(cellRowNum,  cellColNum, delta, 0);
	}
	/**
	 *  获得东南方向的单元格(右下)
	 *  Cell只有列号，没有行号！
	 * @param cellRowNum
	 * @param cellColNum
	 * @param rowDelta
	 * 0-行不变
	 * @param colDelta
	 * 0－列不变
	 * @return
	 */
	public Cell getBottomRightCell(int cellRowNum, int cellColNum, int rowDelta, int colDelta){
		return this.getCell(cellRowNum+rowDelta, cellColNum+colDelta);
	}

	/**
	 * 必选先设置当前风格
	 * @see setCurrentStyle(CellStyle)
	 * @param style
	 * @param color
	 * 必须来自Color，例如：Color.GREY_50_PERCENT.index
	 * @return
	 */
	public PoiExcelHelper addForegroundColorStyle(short color){
		if( this.currentStyle == null){
			return null;
		}

		this.currentStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);//必须设置，才能显示背景色！
		this.currentStyle.setFillForegroundColor(color);

		return this;

	}
	/**
	 * 必选先设置当前风格
	 * @see setCurrentStyle(CellStyle)
	 * @param style
	 * @param color
	 * 必须来自Color，例如：Color.WHITE.index
	 * @param bold
	 * @return
	 */
	public PoiExcelHelper addFontStyle( short color, boolean bold){
		if( this.currentStyle == null){
			return null;
		}


		Font font = workbook.createFont();
		if(color > 0){
			font.setColor(color);
		}

	    if(bold){
	    	font.setBoldweight(Font.BOLDWEIGHT_BOLD);
	    }

	    this.currentStyle.setFont(font);

		return this;
	}

	/**
	 * 必选先设置当前风格
	 * @see setCurrentStyle(CellStyle)
	 * @param style
	 * @param color
	 * CellStyle.ALIGN_CENTER
	 * @return
	 */
	public PoiExcelHelper addBorderStyle(short color){
		if( this.currentStyle == null){
			return null;
		}

//		if(align > 0){
//			this.currentStyle.setAlignment(align);
//		}

		if(color > 0){
			this.currentStyle.setBottomBorderColor(color);
			this.currentStyle.setTopBorderColor(color);
			this.currentStyle.setLeftBorderColor(color);
			this.currentStyle.setRightBorderColor(color);
		}

		this.currentStyle.setBorderBottom(CellStyle.BORDER_THIN);
		this.currentStyle.setBorderLeft(CellStyle.BORDER_THIN);
		this.currentStyle.setBorderTop(CellStyle.BORDER_THIN);
		this.currentStyle.setBorderRight(CellStyle.BORDER_THIN);

		return this;
	}

	/**
	 * 仅水平居中
	 * @param align
	 * @return
	 */
	public PoiExcelHelper addAlignmentStyle(short align) {
		return this.addAlignmentStyle(align, (short) -1);
	}
	/**
	 * 必选先设置当前风格
	 * @see setCurrentStyle(CellStyle)
	 * @param align
	 * @return
	 */
	public PoiExcelHelper addAlignmentStyle(short align,short valign) {
		if( this.currentStyle == null){
			return null;
		}

		if(align > 0){
			this.currentStyle.setAlignment(align);
		}
		if(valign > 0){
			this.currentStyle.setVerticalAlignment(valign);
		}

		return this;
	}
	/**
	 * 内置类型
0, "General"
1, "0"
2, "0.00"
3, "#,##0"
4, "#,##0.00"
5, "$#,##0_);($#,##0)"
6, "$#,##0_);[Red]($#,##0)"
7, "$#,##0.00);($#,##0.00)"
8, "$#,##0.00_);[Red]($#,##0.00)"
9, "0%"
0xa, "0.00%"
0xb, "0.00E+00"
0xc, "# ?/?"
0xd, "# ??/??"
0xe, "m/d/yy"
0xf, "d-mmm-yy"
0x10, "d-mmm"
0x11, "mmm-yy"
0x12, "h:mm AM/PM"
0x13, "h:mm:ss AM/PM"
0x14, "h:mm"
0x15, "h:mm:ss"
0x16, "m/d/yy h:mm"


// 0x17 - 0x24 reserved for international and undocumented 0x25, "#,##0_);(#,##0)"
0x26, "#,##0_);[Red](#,##0)"
0x27, "#,##0.00_);(#,##0.00)"
0x28, "#,##0.00_);[Red](#,##0.00)"
0x29, "_(*#,##0_);_(*(#,##0);_(* \"-\"_);_(@_)"
0x2a, "_($*#,##0_);_($*(#,##0);_($* \"-\"_);_(@_)"
0x2b, "_(*#,##0.00_);_(*(#,##0.00);_(*\"-\"??_);_(@_)"
0x2c, "_($*#,##0.00_);_($*(#,##0.00);_($*\"-\"??_);_(@_)"
0x2d, "mm:ss"
0x2e, "[h]:mm:ss"
0x2f, "mm:ss.0"
0x30, "##0.0E+0"
0x31, "@" - This is text format.
0x31 "text" - Alias for "@"


	 * 必选先设置当前风格
	 * @see setCurrentStyle(CellStyle)
	 * @param formatString
	 * build in style
	 * @return
	 */
	public PoiExcelHelper addDataFormatStyle(String formatString){
		if( this.currentStyle == null){
			return null;
		}


		if(StringUtils.isBlank(formatString)){
			return this;
		}

//		DataFormat format = workbook.createDataFormat();


		this.currentStyle.setDataFormat((short) BuiltinFormats.getBuiltinFormat(formatString)/*format.getFormat(formatString)*/);

		return this;
	}
//	/**
//	 * 克隆主要风格：
//	 * @param style
//	 * @return
//	 * @deprecated 无法良好实现
//	 */
//	public CellStyle cloneCellStyle(CellStyle style){
//		if( this.currentStyle == null || style == null){
//			return null;
//		}
//
//		CellStyle clonedStyle = workbook.createCellStyle();
//
//		this.addAlignmentStyle(style.getAlignment());
//		this.addBorderStyle(style.getLeftBorderColor());//复制左边的颜色
//		this.addDataFormatStyle(style.get);
//		this.addFontStyle(style.getFontIndex(), bold)
//		this.addForegroundColorStyle(style.getFillForegroundColor());
//
//		return clonedStyle;
//	}
	public static Workbook readFromFile(String fileName){
		return readFromFile( fileName, false);//use absolute specified path
	}
	/**
	 *
	 * @param fileName
	 * @param underClasspath
	 * search only classpath if true
	 * @return
	 */
	public static Workbook readFromFile(String fileName, boolean underClasspath){
		if(underClasspath){//find under classpath

			URL resource = Thread.currentThread().getContextClassLoader().getResource(fileName);
//				PoiExcelUtils.class.getClassLoader().getResource(fileName);//直接class.getResource 要加'/'
			if(resource != null){
				fileName = resource.getFile().replace("%20", " ");
			}else{
				throw new RuntimeException("can not find file under classpath: " + fileName);
			}
		}
		try {
			return WorkbookFactory.create(new FileInputStream(fileName));
		} catch (FileNotFoundException e) {
			log.error(e);
			return null;
		} catch (IOException e) {
			log.error(e);
			return null;
		} catch (InvalidFormatException e) {
			log.error(e);
			return null;
		}
	}

	public static Workbook readFromFile(File file){
		try {
			return WorkbookFactory.create(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			log.error(e);
			return null;
		} catch (IOException e) {
			log.error(e);
			return null;
		} catch (InvalidFormatException e) {
			log.error(e);
			return null;
		}
	}

	public static void saveToStream(Workbook workbook, OutputStream out) throws IOException{
		if(workbook == null){
			throw new IllegalArgumentException("workbook required");
		}
		if(out == null){
			throw new IllegalArgumentException("Output Stream required");
		}
		workbook.write(out);
//		out.flush();
	}

	public static void saveToFile(Workbook workbook, File file){
		OutputStream out = null;
		try {
			out = new FileOutputStream (file);
		} catch (FileNotFoundException e) {
			log.error(e);
			return ;
		}

		try {
			workbook.write(out);
			out.flush();
		} catch (IOException e) {
			log.error(e);
		}finally{
			if(null != out){
				try {
					out.close();
				} catch (IOException e) {
				}
			}
		}
	}

	public static void saveToFile(Workbook workbook, String fileName){
		//write to file
		File file = new File(fileName);
		saveToFile(workbook, file);


	}

	public static String[] getSheetNames(Workbook workbook){
		if(workbook == null){
			throw new IllegalArgumentException("workbook required");
		}
		String[] names = new String[workbook.getNumberOfSheets()];
		for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
			names[i] = workbook.getSheetName(i);
		}
		return names;
	}
	/**
	 * get first matched sheet
	 * @param workbook
	 * @param sheetName
	 * @param exact
	 * exact or fuzzy
	 * @return
	 */
	public static Sheet getSheet(Workbook workbook, String sheetName, boolean exact){
		if(workbook == null){
			throw new IllegalArgumentException("workbook required");
		}
		if(sheetName == null){
			return null;
		}
		if(exact){
			return workbook.getSheet(sheetName);
		}else{
			//return first match!
			String[] names = getSheetNames( workbook);
			for (int i = 0; i < names.length; i++) {
				if(sheetName != null && names[i].matches("^\\s*" + sheetName + "\\s*$")){
					return workbook.getSheetAt(i);
				}
			}
			return null;
		}

	}

	public static Sheet getSheetAt(Workbook workbook,int index){
		if(workbook == null){
			throw new IllegalArgumentException("workbook required");
		}

		return workbook.getSheetAt(index);
	}

	//-------------------------------------------------------------------------
	/**
	 * @deprecated @see reserveSheets(Workbook workbook, String[] sheetNames)
	 */
	public static int reserveSheets(Workbook workbook, int[] sheetIndice){
		if(workbook == null){
			throw new IllegalArgumentException("workbook required");
		}
		if(sheetIndice == null || sheetIndice.length == 0){
			throw new IllegalArgumentException("sheetIndice required");
		}

		int c = 0;
		for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
			boolean contains = ArrayUtils.contains(sheetIndice, i);

			if(!contains){
				workbook.removeSheetAt(sheetIndice[i]);
				c++;
			}
		}
		return c ;
	}
	/**
	 * @deprecated @see reserveSheets(Workbook workbook, String[] sheetNames)
	 * @param workbook
	 * @param sheetIndice
	 * @return
	 */
	public static int removeSheets(Workbook workbook, int[] sheetIndice){
		if(workbook == null){
			throw new IllegalArgumentException("workbook required");
		}
		if(sheetIndice == null || sheetIndice.length == 0){
			return 0;
		}
		int c = 0;
//		Sheet sheet = null;
		for (int i = 0; i < sheetIndice.length; i++) {
//			sheet = workbook.getSheetAt(sheetIndice[i]);
			if(sheetIndice[i]>= 0 && sheetIndice[i] < workbook.getNumberOfSheets()){
				workbook.removeSheetAt(sheetIndice[i]);
				c++;
			}
		}
		return c ;
	}
	
	/**
	 * 返回-1表示空，否则返回第一个非空位置(0-based)，空格也算空
	 * @param row
	 * @param limit
	 * @return
	 */
	public static int emptyRow(Row row, int limit){
		if(row == null){
			return -1;
		}
		for (int i = 0; i < limit; i++) {
			Cell cell = row.getCell(i);
			if(cell == null) {
				continue;
			}
			
			int switchType = cell.getCellType();
			
			switch (switchType) {
	        case Cell.CELL_TYPE_STRING:
	        	String value = cell.getStringCellValue();
	        	
	        	if(StringUtils.isBlank(value)){
	        		continue;
	        	}
	        	
	        	return i;
	            
	        case Cell.CELL_TYPE_NUMERIC:
	        	return i;
	        case Cell.CELL_TYPE_FORMULA:  
	        	return i; 
	        case Cell.CELL_TYPE_BLANK:
	        	continue;
	        case Cell.CELL_TYPE_BOOLEAN:
	        	return i;
	        case Cell.CELL_TYPE_ERROR:
	        	return i;
	        default: 
	        	
	        }
			 
		}
		return -1;
	}
	/**
	 * reserve specified sheets
	 * @param workbook
	 * @param sheetNames
	 * @param exact
	 * exact or fuzzy(using regular expression)
	 * @return
	 */
	public static int reserveSheets(Workbook workbook, String[] sheetNames, boolean exact){
		if(workbook == null){
			throw new IllegalArgumentException("workbook required");
		}
		if(sheetNames == null || sheetNames.length == 0){
			throw new IllegalArgumentException("sheet names required");
		}
		int c = 0;
//		for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
//			String name = workbook.getSheetName(i);
//
//			boolean contains = ArrayUtils.contains(sheetNames, name);
//			if( !contains ){
//				workbook.removeSheetAt(i);
//				c++;
//			}
//		}
		int i=0;
		int orig = workbook.getNumberOfSheets();
		while(i < workbook.getNumberOfSheets()){
			String name = workbook.getSheetName(i);

			boolean contains = contains(sheetNames, name, exact);
			if( !contains ){
				workbook.removeSheetAt(i);
				c++;
				//length shortened
			}else{
				i++;
			}
		}
		if(orig == c ){
			log.error("No sheet is reserved, the file may not be opened : " + c + " sheets removed");
			System.err.println("No sheet is reserved, the file may not be opened: " + c + " sheets removed");
		}
		return c;
	}

	private static boolean contains(String[] array, String key,
			boolean exact) {
		if(key == null ){
			return false;
		}
		if(exact){
			return ArrayUtils.contains(array, key);
		}else{
			for (int i = 0; i < array.length; i++) {
				if(array[i] != null && key.matches("^\\s*" + array[i] + "\\s*$")){
					return true;
				}
			}
		}
		return false;
	}


	/**
	 * @deprecated @see reserveSheets(Workbook workbook, String[] sheetNames)
	 * @param workbook
	 * @param sheetNames
	 * @return sheets removed
	 */
	public static int removeSheets(Workbook workbook, String[] sheetNames){
		if(workbook == null){
			throw new IllegalArgumentException("workbook required");
		}
		if(sheetNames == null || sheetNames.length == 0){
			return 0;
		}
		int c = 0;
		for (int i = 0; i < sheetNames.length; i++) {
			int sheetIndex = workbook.getSheetIndex(sheetNames[i]);
			if(sheetIndex >=0 ){
				workbook.removeSheetAt(sheetIndex);
				c++;
			}
		}
		return c;
	}
//-----------------------------------------------------------
	/**
	 * FIXME 无法设置style !
	 *空行空列不做任何处理，以免误伤
	 * @param templateFile, 可以混杂模板sheet和非模板sheet
	 * 模板文件一般是带标题的
	 * @param destFile
	 * @param config
	 * @param exportMsg
	 */
	public static void  exportData(File templateFile, File destFile, ExcelPortConfig config, StringBuffer exportMsg){
		Workbook workbook = null;

		if(templateFile != null){
			workbook = readFromFile(templateFile);
		}

		if(destFile == null || ! destFile.exists()){
			exportMsg.append("目标文件不存在，无法导出："+ destFile);
			return;
		}

		SpreadsheetVersion ver = null;
		if(workbook == null){
			exportMsg.append("创建新Excel表导出。\n");

			if(StringUtils.isBlank(config.getSpreadsheetVersion())){
				exportMsg.append("创建新Excel表失败：未知Excel版本（EXCEL97? EXCEL2007?）。\n");
				return;
			}

			ver = SpreadsheetVersion.valueOf(config.getSpreadsheetVersion().trim().toUpperCase());

			if(SpreadsheetVersion.EXCEL97 == ver){
				workbook = new HSSFWorkbook();
			}else if(SpreadsheetVersion.EXCEL2007 == ver){
				workbook = new XSSFWorkbook();
			}else{
				exportMsg.append("创建新Excel表失败：未知Excel版本（EXCEL97? EXCEL2007?）。\n");
				return;
			}

		}else{
			exportMsg.append("使用模板文件导出：" + templateFile + "\n");
		}

		Sheet sheet = null;
		boolean templateSheet = false;

		Map<Object, SheetPortConfig> sheetPortConfigs = config.getSheetPortConfigs();
		for (Object key : sheetPortConfigs.keySet()) {
			if(Integer.class.isAssignableFrom(key.getClass())){//as index
				Integer k = (Integer) key;
				if(k >= workbook.getNumberOfSheets() || k < 0){
					log.error("非法Sheet索引：" + k);
				}else{
					sheet = workbook.getSheetAt(k);
				}
			}else{//as pattern
				sheet = PoiExcelHelper.getSheet(workbook, key.toString() , false);
			}
			templateSheet = true;

			if(sheet == null && config.isCreateSheetIfNotFound()){
				if(Integer.class.isAssignableFrom(key.getClass())){//as index
					sheet = workbook.createSheet();
				}else{
					sheet = workbook.createSheet(key.toString());//as name NOT pattern
				}

				templateSheet = false;
			}

			if(sheet == null){
				log.error("[ERROR]无法找到指定sheet索引或名称(同时又不准生成): " + key);
				continue;
			}

			exportData(sheet, sheetPortConfigs.get(key), templateSheet, exportMsg);
		}

		if(SpreadsheetVersion.EXCEL97 == ver){
			HSSFFormulaEvaluator.evaluateAllFormulaCells((HSSFWorkbook) sheet.getWorkbook());//所有公式求值
		}else if(SpreadsheetVersion.EXCEL2007 == ver){
			XSSFFormulaEvaluator.evaluateAllFormulaCells((XSSFWorkbook) sheet.getWorkbook());
		}

		saveToFile(workbook, destFile);
	}

	/**
	 * FIXME 无法设置style !
	 * @param sheet
	 * 导出目标sheet
	 * @param config
	 * @param templateSheet
	 * 是否模板
	 * @param exportMsg
	 */
	private static void exportData(Sheet sheet,  SheetPortConfig config, boolean templateSheet, StringBuffer exportMsg){
		if(sheet == null){
			return;
		}
		log.debug("正在处理Sheet:" + sheet);

		PoiExcelHelper helper = new PoiExcelHelper(sheet.getWorkbook(), sheet);

		ExportSheetRowProcesser ep = config.getExportRowProcesser();

		Cell cell = null;


		CellStyle titleStyle = sheet.getWorkbook().createCellStyle();
		helper.setCurrentStyle(titleStyle)

//		.addForegroundColorStyle(HSSFColor.WHITE.index)
//		.addFontStyle(Color.BLACK.index, true)
		.addAlignmentStyle(CellStyle.ALIGN_CENTER);
//		.addBorderStyle(Color.BLACK.index);

		int rowNum = config.getSkipRows();
		Object[] row = null;
		String[] fields = config.getFieldsConfig().getFields();
		row = ep.getRow(config, rowNum, exportMsg);
		//空行空列不做任何处理，以免误伤
		while(row != null){//[]表示空行！
			if(row.length > 0){

				for (int i = 0; i < fields.length ; i++) {
					if(row.length > i && row[i] != null){//row[i]有值时设置，包括 ""，
						cell = helper.getCell(rowNum, config.getSkipCols() + i);

						if(!templateSheet && rowNum == config.getSkipRows()){//as title line
							helper.setCell(cell, row[i], titleStyle);
						}else{
							helper.setCell(cell, row[i], null);
						}

					}
//					else{//不必要了
//						boolean nullCell = helper.isNullCell(rowNum, config.getSkipCols() + i);
//						if(!nullCell){//判断是否公式,自动展开：
//							cell = helper.getCell(rowNum, config.getSkipCols() + i, false);
//							if(Cell.CELL_TYPE_FORMULA == cell.getCellType()){
//								e.evaluateFormulaCell(cell);
//							}
//						}
//					}
				}


			}

			rowNum++;
			row = ep.getRow(config, rowNum  , exportMsg);
		}



	}
	/**
	 * 导入excel文件中指定sheet的数据，
	 * 需要实现 Excel行处理 SheetRowProcesser接口
	 * @param srcFile
	 * @param config
	 * @param importMsg
	 */

	public static void  importData(File srcFile, ExcelPortConfig config, StringBuffer importMsg){
		Workbook workbook = PoiExcelHelper.readFromFile(srcFile);
		if(workbook == null){
			importMsg.append("无法读取Excel表。");
			return ;
		}

		Sheet sheet = null;

		Map<Object, SheetPortConfig> sheetImportConfigs = config.getSheetPortConfigs();
		for (Object key : sheetImportConfigs.keySet()) {
			if(Integer.class.isAssignableFrom(key.getClass())){//as index
				Integer k = (Integer) key;
				if(k >= workbook.getNumberOfSheets() || k < 0){
					log.error("非法Sheet索引：" + k);
				}else{
					sheet = workbook.getSheetAt(k);
				}
			}else{//as pattern
				sheet = PoiExcelHelper.getSheet(workbook, key.toString() , false);
			}

			if(sheet == null){
				log.error("[ERROR]无法找到指定sheet索引或名称: " + key);
				continue;
			}

			importData(sheet, sheetImportConfigs.get(key), importMsg);
		}

	}
	/**
	 * 直接导入sheet
	 * @param sheet
	 * @param key
	 * @param config
	 * @param importMsg
	 */
	public static void importData(Sheet sheet,  SheetPortConfig config, StringBuffer importMsg){
		if(sheet == null){
			return;
		}
		log.debug("正在处理Sheet:" + sheet);

		PoiExcelHelper helper = new PoiExcelHelper(sheet.getWorkbook(), sheet);


		int i = config.getSkipRows();//行号,0-based
		int j = config.getSkipCols();//列号,0-based

		NumberFormat df = new DecimalFormat("#0");
		ImportSheetRowProcesser p = config.getImportRowProcesser();

		FormulaEvaluator e = null;
		if(helper.getSheetVersion() == SpreadsheetVersion.EXCEL97){
			e= new HSSFFormulaEvaluator((HSSFWorkbook) sheet.getWorkbook());
		}else if(helper.getSheetVersion() == SpreadsheetVersion.EXCEL2007){
			e= new XSSFFormulaEvaluator((XSSFWorkbook) sheet.getWorkbook());
		}

		CellValue value = null;
		int switchType;

		Object[] colValues = null;
		boolean isEmptyRow = false;
		while(!isEmptyRow){

			colValues = new Object[config.getCols()];
			for (int k = 0; k < config.getCols(); k++) {
				Cell cell = helper.getCell(i,j + k);


				if(config.isEvaluateFormula() && Cell.CELL_TYPE_FORMULA == cell.getCellType()){
					value = e.evaluate(cell);
					switchType = value.getCellType();
				}else{
					value = null;
					switchType = cell.getCellType();
				}

				Object cellValue = null;
		        switch (switchType) {
		        case Cell.CELL_TYPE_STRING:
		        	if(value != null){
		        		cellValue = ObjectUtils.toString(value.getStringValue(),"");
		        	}else{
		        		cellValue = ObjectUtils.toString(cell.getStringCellValue(),"");
		        	}

		            break;
		        case Cell.CELL_TYPE_NUMERIC:
		        	Double d = null;
		        	if(value != null){
		        		d = value.getNumberValue();
		        	}else{
		        		d = cell.getNumericCellValue();
		        	}

		            if(config.isLongNumberAsString() && d > config.getNumberByThreshold() ){
		            	cellValue = df.format(d);
		            }else{
		            	 cellValue = d;
		            }

		            break;
		        case Cell.CELL_TYPE_FORMULA:   //?
		        	cellValue= cell.getCellFormula();

//		            cell.setCellType(Cell.CELL_TYPE_NUMERIC);
//		            cellValue = String.valueOf(cell.getNumericCellValue());
		            break;
		        case Cell.CELL_TYPE_BLANK:
		            cellValue="";
		            break;
		        case Cell.CELL_TYPE_BOOLEAN:
		        	if(value != null){
		        		cellValue = value.getBooleanValue();
		        	}else{
		        		cellValue = cell.getBooleanCellValue();
		        	}

		            break;
		        case Cell.CELL_TYPE_ERROR:

		        	if(value != null){
		        		cellValue = value.getErrorValue();
		        	}else{
		        		cellValue = cell.getErrorCellValue();
		        	}


		            break;
		        default:
		            break;
		        }
				colValues[k] = cellValue;
			}
 
			for (int k = 0; k < colValues.length; k++) {
				boolean b = false;//empty?
				if(StringUtils.isNotBlank(colValues[k].toString())){
					b = true;
					break;
				}
				isEmptyRow = !b;
			}

			if(!isEmptyRow){
				if(importMsg == null){
					importMsg = new StringBuffer();
				}
				p.process(config, i, colValues, importMsg);
			}

			i++;
		}

	}



	public static void main(String[] args) {
		Workbook workbook = new HSSFWorkbook();
		Sheet sheet = workbook.createSheet();
		PoiExcelHelper excelUtils= new PoiExcelHelper(workbook, sheet);

		CellStyle style = workbook.createCellStyle();

		excelUtils.setCurrentStyle(style);

		excelUtils.addBorderStyle(HSSFColor.WHITE.index)
				.addAlignmentStyle(CellStyle.ALIGN_CENTER)
				.addDataFormatStyle("##0.00")
				.addFontStyle(HSSFColor.CORNFLOWER_BLUE.index, true)
				.addForegroundColorStyle(HSSFColor.CORNFLOWER_BLUE.index);

		style = excelUtils.getCurrentStyle();

		Cell cell = excelUtils.getCell(0, 2);
		excelUtils.getRightCell(0, 2, 4);

		CellStyle titleStyle = workbook.createCellStyle();
		excelUtils.setCurrentStyle(titleStyle)
					.addForegroundColorStyle(HSSFColor.DARK_YELLOW.index)
					.addFontStyle(HSSFColor.WHITE.index, true)
					.addAlignmentStyle(CellStyle.ALIGN_CENTER)
					.addBorderStyle(HSSFColor.BLACK.index);

		Cell firstCell = excelUtils.mergeCellsInOneRow(0, 0, 1);
		excelUtils.setCell(firstCell,"受理时段:",titleStyle);

		excelUtils.setCellByAlphabetaDigitFormat("c4", "测试C4", titleStyle);
		excelUtils.setCellByAlphabetaDigitFormat("AH3", "测试AH3", titleStyle);
		excelUtils.setCellByRowColFormat("5,10", "测试5，10", titleStyle);
		PoiExcelHelper.saveToFile(workbook, "c:/test2.xls");


		Workbook workbook2 = new XSSFWorkbook();
		Sheet sheet2 = workbook2.createSheet("test");
		PoiExcelHelper h = new PoiExcelHelper(workbook2, sheet2);
		for (int i = 0; i < 500; i++) {
			for (int j = 0; j < 5; j++) {
				h.setCell(i,j , "test[" + i + "][" + j + "]", null);
			}
		}

		PoiExcelHelper.saveToFile(workbook2, "c:/poitest2.xlsx");

		System.out.println("c:/poitest2.xlsx created");


		//导入测试看test包
	}

	



}
