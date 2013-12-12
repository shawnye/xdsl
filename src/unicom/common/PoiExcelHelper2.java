package unicom.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.hssf.util.Region;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

/**
 * Excel read and write
 * @author Shawn Ye
 * @version 1.1
 *
 */
public class PoiExcelHelper2 {
	private static final Log log = LogFactory.getLog(PoiExcelHelper2.class);
	
	private HSSFWorkbook workbook;
	private HSSFSheet sheet;
	
	private transient HSSFCellStyle currentStyle;
	private int currentRowNum=-1;
	private int currentColNum=-1;
	
	/**
	 * do jobs in one sheet.
	 * @param workbook
	 * @param sheet
	 */
	public PoiExcelHelper2(HSSFWorkbook workbook, HSSFSheet sheet){
		if(workbook == null){
			throw new IllegalArgumentException("workbook required");
		}
		if(sheet == null){
			throw new IllegalArgumentException("sheet required");
		}
		this.workbook = workbook;
		this.sheet = sheet;
	}
	
	
	public HSSFWorkbook getWorkbook() {
		return workbook;
	}

	public HSSFSheet getSheet() {
		return sheet;
	}


	public HSSFCellStyle getCurrentStyle() {
		return currentStyle;
	}


	public PoiExcelHelper2 setCurrentStyle(HSSFCellStyle currentStyle) {
		this.currentStyle = currentStyle;
		
		return this;
	}

	public PoiExcelHelper2 setCurrentPosition(int rowNum, int colNum){
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

	
	/**
	 * 核心方法: 获得指定单元格
	 * @param cellRowNum
	 * @param cellColNum
	 * @return
	 */
	public HSSFCell getCell(int cellRowNum, int cellColNum){
		HSSFRow row = this.sheet.getRow(cellRowNum );
		if(row == null){
			row = sheet.createRow(cellRowNum );
		}
		
		if(row == null){
			throw new IllegalArgumentException("不能获得行:" + (cellRowNum ));
		}
	
		HSSFCell cell = row.getCell( cellColNum );
		if(cell == null){
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
	public HSSFCell moveToCell(int rowDelta, int colDelta){
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
	public HSSFCell left(int delta){
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
	public HSSFCell right(int delta){
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
	public HSSFCell up(int delta){
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
	public HSSFCell down(int delta){
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
	public PoiExcelHelper2 setCell(HSSFCell cell, Object value, HSSFCellStyle style, String commentStr){
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
				cell.setCellValue(new HSSFRichTextString(value.toString()));
			}
			
		}
		//FIXME HSSFClientAnchor如何使用?
//		if(StringUtils.isNotBlank(commentStr)){
//			HSSFPatriarch patr = sheet.createDrawingPatriarch();
//			HSSFComment comment = patr.createComment(new HSSFClientAnchor(0, 0, cell.getCellNum(), cell.getCellNum(), (short)(cell.getCellNum()), 2, (short) (cell.getCellNum()+4), 8));
//			comment.setString(new HSSFRichTextString(commentStr));
////			comment.setAuthor("Apache Software Foundation");
////			comment.setFillColor(204, 236, 255);
//
//			cell.setCellComment(comment);
//		}
		return this;
	}
	
	public PoiExcelHelper2 setCell(HSSFCell cell, Object value, HSSFCellStyle style){
		return this.setCell(cell, value, style, null);
	}
	
	public void setCell(int rowNum , int colNum, Object value, HSSFCellStyle style, String commentStr){
		setCell(this.getCell(rowNum, colNum), value, style, commentStr);
	}
	/**
	 * 设置指定位置的Cell的值和风格
	 * @param sheet
	 * @param row
	 * @param col
	 * @param value
	 * @param style
	 */
	public void setCell(int rowNum , int colNum, Object value, HSSFCellStyle style){
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
	public HSSFCell mergeCells(int startRow , int startCol, int rowLen, int colLen, HSSFCellStyle style){
		sheet.addMergedRegion(new Region(startRow ,(short)(startCol) ,startRow+rowLen ,(short)(startCol + colLen)));
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
	public HSSFCell mergeCellsInOneRow( int rowNum , int startCol, int colLen){
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
	public HSSFCell mergeCellsInOneCol( int startRow , int rowLen,int colNum ){
		return mergeCells(startRow, colNum, rowLen, 0, null);
	}
	
	public HSSFCell mergeCells( int startRow , int startCol, int rowLen, int colLen){
		return mergeCells(startRow, startCol, rowLen, colLen, null);
	}
	/**
	 * 获得右边的单元格
	 * @param cellRowNum
	 * @param cellColNum
	 * @param delta
	 * 第0个是自身,1为右边第一个,－1为左边第一个
	 * @return
	 */
	public HSSFCell getRightCell(int cellRowNum, int cellColNum,  int delta){
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
	public HSSFCell getBottomCell(int cellRowNum, int cellColNum,  int delta){
		return getBottomRightCell(cellRowNum,  cellColNum, delta, 0);
	}
	/**
	 *  获得东南方向的单元格(右下)
	 *  HSSFCell只有列号，没有行号！
	 * @param cellRowNum
	 * @param cellColNum
	 * @param rowDelta
	 * 0-行不变
	 * @param colDelta
	 * 0－列不变
	 * @return
	 */
	public HSSFCell getBottomRightCell(int cellRowNum, int cellColNum, int rowDelta, int colDelta){
		return this.getCell(cellRowNum+rowDelta, cellColNum+colDelta);
	}

	/**
	 * 必选先设置当前风格
	 * @see setCurrentStyle(HSSFCellStyle)
	 * @param style
	 * @param color
	 * 必须来自HSSFColor，例如：HSSFColor.GREY_50_PERCENT.index
	 * @return
	 */
	public PoiExcelHelper2 addForegroundColorStyle(short color){
		if( this.currentStyle == null){
			return null;
		}

		this.currentStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);//必须设置，才能显示背景色！
		this.currentStyle.setFillForegroundColor(color);
		
		return this;

	}
	/**
	 * 必选先设置当前风格
	 * @see setCurrentStyle(HSSFCellStyle)
	 * @param style
	 * @param color
	 * 必须来自HSSFColor，例如：HSSFColor.WHITE.index
	 * @param bold
	 * @return
	 */
	public PoiExcelHelper2 addFontStyle( short color, boolean bold){
		if( this.currentStyle == null){
			return null;
		}
		
		
		HSSFFont font = workbook.createFont();
		if(color > 0){
			font.setColor(color);
		}
	    
	    if(bold){
	    	font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
	    }
	    
	    this.currentStyle.setFont(font);
	    
		return this;
	}
	
	/**
	 * 必选先设置当前风格
	 * @see setCurrentStyle(HSSFCellStyle)
	 * @param style
	 * @param color
	 * HSSFCellStyle.ALIGN_CENTER
	 * @return
	 */
	public PoiExcelHelper2 addBorderStyle(short color){
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
		
		this.currentStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		this.currentStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		this.currentStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		this.currentStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		
		return this;
	}
	
	/**
	 * 仅水平居中
	 * @param align
	 * @return
	 */
	public PoiExcelHelper2 addAlignmentStyle(short align) {
		return this.addAlignmentStyle(align, (short) -1);
	}
	/**
	 * 必选先设置当前风格
	 * @see setCurrentStyle(HSSFCellStyle)
	 * @param align
	 * @return
	 */
	public PoiExcelHelper2 addAlignmentStyle(short align,short valign) {
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
	 * 必选先设置当前风格
	 * @see setCurrentStyle(HSSFCellStyle)
	 * @param formatString
	 * build in style
	 * @return
	 */
	public PoiExcelHelper2 addDataFormatStyle(String formatString){
		if( this.currentStyle == null){
			return null;
		}

		
		if(StringUtils.isBlank(formatString)){
			return this;
		}
		
//		HSSFDataFormat format = workbook.createDataFormat();   
		
		this.currentStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat(formatString));
		
		return this;
	}
//	/**
//	 * 克隆主要风格：
//	 * @param style
//	 * @return
//	 * @deprecated 无法良好实现
//	 */
//	public HSSFCellStyle cloneCellStyle(HSSFCellStyle style){
//		if( this.currentStyle == null || style == null){
//			return null;
//		}
//		
//		HSSFCellStyle clonedStyle = workbook.createCellStyle();
//		
//		this.addAlignmentStyle(style.getAlignment());
//		this.addBorderStyle(style.getLeftBorderColor());//复制左边的颜色
//		this.addDataFormatStyle(style.get);
//		this.addFontStyle(style.getFontIndex(), bold)
//		this.addForegroundColorStyle(style.getFillForegroundColor());
//		
//		return clonedStyle;
//	}
	public static HSSFWorkbook readFromFile(String fileName){
		return readFromFile( fileName, false);//use absolute specified path
	}
	/**
	 * 
	 * @param fileName
	 * @param underClasspath
	 * search only classpath if true
	 * @return
	 */
	public static HSSFWorkbook readFromFile(String fileName, boolean underClasspath){
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
			POIFSFileSystem fs      =
			    new POIFSFileSystem(new FileInputStream(fileName));
				return new HSSFWorkbook(fs);
		} catch (FileNotFoundException e) {
			log.error(e);
			return null;
		} catch (IOException e) {
			log.error(e);
			return null;
		}
	}
	
	public static HSSFWorkbook readFromFile(File file){
		try {
			POIFSFileSystem fs      =
			    new POIFSFileSystem(new FileInputStream(file));
				return new HSSFWorkbook(fs);
		} catch (FileNotFoundException e) {
			log.error(e);
			return null;
		} catch (IOException e) {
			log.error(e);
			return null;
		}
	}
	
	public static void saveToStream(HSSFWorkbook workbook, OutputStream out) throws IOException{
		if(workbook == null){
			throw new IllegalArgumentException("workbook required");
		}
		if(out == null){
			throw new IllegalArgumentException("Output Stream required");
		}
		workbook.write(out);
//		out.flush();
	}
	
	public static void saveToFile(HSSFWorkbook workbook, File file){
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
	
	public static void saveToFile(HSSFWorkbook workbook, String fileName){
		//write to file
		File file = new File(fileName);
		saveToFile(workbook, file);
		
		
	}
	
	public static String[] getSheetNames(HSSFWorkbook workbook){
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
	public static HSSFSheet getSheet(HSSFWorkbook workbook, String sheetName, boolean exact){
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
	
	public static HSSFSheet getSheetAt(HSSFWorkbook workbook,int index){
		if(workbook == null){
			throw new IllegalArgumentException("workbook required");
		}

		return workbook.getSheetAt(index);
	}

	//-------------------------------------------------------------------------
	/**
	 * @deprecated @see reserveSheets(HSSFWorkbook workbook, String[] sheetNames)
	 */
	public static int reserveSheets(HSSFWorkbook workbook, int[] sheetIndice){
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
	 * @deprecated @see reserveSheets(HSSFWorkbook workbook, String[] sheetNames)
	 * @param workbook
	 * @param sheetIndice
	 * @return
	 */
	public static int removeSheets(HSSFWorkbook workbook, int[] sheetIndice){
		if(workbook == null){
			throw new IllegalArgumentException("workbook required");
		}
		if(sheetIndice == null || sheetIndice.length == 0){
			return 0;
		}
		int c = 0;
//		HSSFSheet sheet = null;
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
	 * reserve specified sheets
	 * @param workbook
	 * @param sheetNames
	 * @param exact
	 * exact or fuzzy(using regular expression)
	 * @return
	 */
	public static int reserveSheets(HSSFWorkbook workbook, String[] sheetNames, boolean exact){
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
	 * @deprecated @see reserveSheets(HSSFWorkbook workbook, String[] sheetNames)
	 * @param workbook
	 * @param sheetNames
	 * @return sheets removed
	 */
	public static int removeSheets(HSSFWorkbook workbook, String[] sheetNames){
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
	
	public static void main(String[] args) {
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet();
		PoiExcelHelper2 excelUtils= new PoiExcelHelper2(workbook, sheet);
		
		HSSFCellStyle style = workbook.createCellStyle();
		
		excelUtils.setCurrentStyle(style);
		
		excelUtils.addBorderStyle(HSSFColor.WHITE.index)
				.addAlignmentStyle(HSSFCellStyle.ALIGN_CENTER)
				.addDataFormatStyle("##0.00")
				.addFontStyle(HSSFColor.CORNFLOWER_BLUE.index, true)
				.addForegroundColorStyle(HSSFColor.CORNFLOWER_BLUE.index);
		
		style = excelUtils.getCurrentStyle();
		
		HSSFCell cell = excelUtils.getCell(0, 2);
		excelUtils.getRightCell(0, 2, 4);
		
		HSSFCellStyle titleStyle = workbook.createCellStyle();
		excelUtils.setCurrentStyle(titleStyle)
					.addForegroundColorStyle(HSSFColor.DARK_YELLOW.index)
					.addFontStyle(HSSFColor.WHITE.index, true)
					.addAlignmentStyle(HSSFCellStyle.ALIGN_CENTER)
					.addBorderStyle(HSSFColor.BLACK.index);
		
		HSSFCell firstCell = excelUtils.mergeCellsInOneRow(0, 0, 1);
		excelUtils.setCell(firstCell,"受理时段:",titleStyle);
		PoiExcelHelper2.saveToFile(workbook, "c:/test2.xls");
		
//		String fileName="g:/test";
//		workbook = PoiExcelUtils.readFromFile(fileName);
//		excelUtils= new PoiExcelUtils(workbook, sheet);
	}


	
}
