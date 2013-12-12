package unicom.common.service;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.apache.commons.io.output.FileWriterWithEncoding;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import unicom.bo.Report;
import unicom.common.MapValueGetter;
import unicom.common.PoiExcelHelper2;
import unicom.common.SearchConditions;
import unicom.common.SearchKey;
import unicom.common.SystemEnvironment;
import unicom.common.port.ExportConfig;
import unicom.common.port.ExportFileException;
import unicom.dao.BaseDaoInterface;
import au.com.bytecode.opencsv.CSVWriter;

@SuppressWarnings("unchecked")
@Service(value="reportService")
public  class ReportService  {
	public static final String NULL_MARK = "#";//表示null
	public static final String SELECT_ITEMS_PATTERN = "(\\S+)\\s+as\\s+\\\"([^\\\"]+)\\\"\\s*,?(\r?\n)?";
	protected Log log = LogFactory.getLog(this.getClass());

	protected BaseDaoInterface baseDao;

	@Autowired
	public void setBaseDao(BaseDaoInterface baseDao) {
		this.baseDao = baseDao;
	}

	/**
	 * can be overrided
	 * @param ids
	 * @param updateValues
	 */
	public  int updateAll(String[] ids, SearchConditions updateValues){
		return 0;
	}

	public Report getReport(String id, String sqlFetch, SearchConditions searchCondition,String columnsDisplayedStr, Integer limit) {
		return this.getReport(id, sqlFetch, null, searchCondition,columnsDisplayedStr, limit);
	}

	public Report getReport(String id, String sqlFetch, SearchConditions searchCondition,List<Boolean> columnsDisplayed, Integer limit) {
		return this.getReport(id, sqlFetch, null, searchCondition,columnsDisplayed, limit);
	}

	public Report getReport(String id, String sqlFetch,List<String> links, SearchConditions searchCondition, String columnsDisplayedStr, Integer limit) {
		Report report = Report.parse(sqlFetch, links);
		report.setId(id);
		report.parseColumnsDisplayedString(columnsDisplayedStr);
		report.setLimit(limit);
		report.setData(this.listAllAsMap(report, searchCondition));

		return report;
	}

	public Report getReport(String id, String sqlFetch,List<String> links, SearchConditions searchCondition, List<Boolean> columnsDisplayed, Integer limit) {
		Report report = Report.parse(sqlFetch, links);
		report.setId(id);
		report.setColumnsDisplayed(columnsDisplayed);
		report.setLimit(limit);
		report.setData(this.listAllAsMap(report, searchCondition));

		return report;
	}

	public Report countReport(String id, String sqlFetch, SearchConditions searchCondition) {
		Report report = Report.parse(sqlFetch, null);
		report.setId(id);

		report.setTotalCount(this.totalCount(report, searchCondition));

		return report;
	}


	public Long totalCount(Report report, SearchConditions searchCondition) {
		Map<SearchKey, Object> conditions = searchCondition.getConditions();

		List<Object> args = new ArrayList<Object>();

		List<String> condStr = new ArrayList<String>();
 		for (SearchKey searchKey : conditions.keySet()) {

			String theta = searchKey.getTheta();
			String field = searchKey.getField();

			String val = conditions.get(searchKey).toString().trim();
			if(StringUtils.isBlank(val)){
				continue;
			}
			String[] split = val.split("[\r\n]+");//windows only?
			boolean containsCL = split != null && split.length>1 ;

			if(val.equals(NULL_MARK)){//blank or empty
				condStr.add(" and (" + field + " is null or "+ field+ " = \'\')" );
 			}else if(containsCL){//换行多项精确查询
 				if(theta.equals("like") ){
 					StringBuffer ors = new StringBuffer();
 					for (int j = 0; j < split.length; j++) {
 						if(val.contains("%")){
 							args.add(split[j]);
 						}else{
 							args.add("%" +split[j]+ "%");
 						}
 						
 						
 						if(!StringUtils.isBlank(ors.toString())){
 							ors.append(" or ");
 						}
 						ors.append(field + " " + theta + " ? ");
 					}
 					
 					condStr.add(" and (" + ors + ") ");
 				}else{//精确查询
 					StringBuffer in = new StringBuffer();
 					for (int j = 0; j < split.length; j++) {
 						args.add(split[j]);
 						if(!StringUtils.isBlank(in.toString())){
 							in.append(",");
 						}
 						in.append("?");
 					}
 					in.append( ")" );
 					in = new StringBuffer(" in (" ).append(in) ;
 					condStr.add(" and " + field + in );
 				}
			}else{

				condStr.add(" and " + field + " " + theta + " ? " );
				if(theta.equals("like") && !val.contains("%")){
					args.add("%" + val + "%");
				}else{
					args.add(conditions.get(searchKey));//可能非字符！
				}
			}

		}


 		String sqlFetch = report.getSqlFetch();
		if(condStr.size() > 0){
 	 		sqlFetch  = MessageFormat.format(sqlFetch.replace("'", "''"), condStr.toArray());//'是对MessageFormat来说是特殊字符
 		}
 		sqlFetch = sqlFetch.replaceAll("\\{\\d+\\}", "");//删除{数字}

 		//change 'select xxx,yyy from T' to select 1 a from T
// 		sqlFetch = sqlFetch.replaceFirst(SELECT_ITEMS_PATTERN, " 1 one ")
// 			.replaceAll(SELECT_ITEMS_PATTERN, "");

		// 			.replaceAll("(\r)?\n", "");//无效?
 		
 		//找到第一层select -- from 对
 		sqlFetch = sqlFetch.toLowerCase();
 		Stack<Integer> selectFrom = new Stack<Integer>();
 	 
 		int nextSelect = 0;
 		nextSelect = sqlFetch.indexOf("select", nextSelect);
 		int nextFrom = 0;
 		if(nextSelect!=-1){
 			selectFrom.push(nextSelect);
 		}
 		
 		//select (select ...from ),xx,yy from (select (select x from),zz from table)
 		while(nextSelect !=-1 && selectFrom.size()>0 ){
 			nextSelect = sqlFetch.indexOf("select", nextSelect + 6);
 			nextFrom = sqlFetch.indexOf("from ", nextSelect + 6);
 			
 			if( nextSelect!=-1 && nextSelect < nextFrom){
 				selectFrom.push(nextSelect);
 			}else{
 				selectFrom.pop();
 			}
 		}
 		sqlFetch = sqlFetch.substring(nextFrom + 5);
 	 

 		//必须有select 1 one，避免group 返回多值！
 		//remove last order by(小写)
 		String sqlCount = "select count(*) from (select 1 one from " + sqlFetch.trim().replaceFirst("order\\s+by(.+)$" , "") + ") x" ;

 		return this.baseDao.count(sqlCount , args.toArray() );
	}

	public List<Map> listAllAsMap(Report report, SearchConditions searchCondition) {
		Map<SearchKey, Object> conditions = searchCondition.getConditions();

		List<Object> args = new ArrayList<Object>();

		List<String> condStr = new ArrayList<String>();
 		for (SearchKey searchKey : conditions.keySet()) {

			String theta = searchKey.getTheta();
			String field = searchKey.getField();

			String val = conditions.get(searchKey).toString().trim();
			if(StringUtils.isBlank(val)){
				continue;
			}
			String[] split = val.split("\r\n");//windows only?
			boolean containsCL = split != null && split.length>1 ;

			if(val.equals(NULL_MARK)){//blank or empty
				condStr.add(" and (" + field + " is null or "+ field+ " = \'\')" );
 			}else if(containsCL){//换行多项
 				if(theta.equals("like") ){
 					StringBuffer ors = new StringBuffer();
 					for (int j = 0; j < split.length; j++) {
 						if(val.contains("%")){
 							args.add(split[j]);
 						}else{
 							args.add("%" +split[j]+ "%");
 						}
 						
 						
 						if(!StringUtils.isBlank(ors.toString())){
 							ors.append(" or ");
 						}
 						ors.append(field + " " + theta + " ? ");
 					}
 					
 					condStr.add(" and (" + ors + ") ");
 				}else{//精确查询
 					StringBuffer in = new StringBuffer();
 					for (int j = 0; j < split.length; j++) {
 						args.add(split[j]);
 						if(!StringUtils.isBlank(in.toString())){
 							in.append(",");
 						}
 						in.append("?");
 					}
 					in.append( ")" );
 					in = new StringBuffer(" in (" ).append(in) ;
 					condStr.add(" and " + field + in );
 				}
				
			}else{

				condStr.add(" and " + field + " " + theta + " ? " );
				if(theta.equals("like")  && !val.contains("%")){
					args.add("%" + val + "%");
				}else{
					args.add(conditions.get(searchKey));//可能非字符！
				}
			}

		}

 		String sqlFetch = report.getLimit()!=null && report.getLimit() > 0 ? report.getLimitedSqlFetch() : report.getSqlFetch();

 		//支持SQL server 链接服务器1
		sqlFetch = sqlFetch.replace("{db.linked_server1}", SystemEnvironment.getProperty("db.linked_server1",""));
		sqlFetch = sqlFetch.replace("{db.linked_server1.username}", SystemEnvironment.getProperty("db.linked_server1.username",""));
		sqlFetch = sqlFetch.replace("{db.linked_server1.password}", SystemEnvironment.getProperty("db.linked_server1.password",""));
		
		
		if(condStr.size() > 0){
			log.debug("请确保SQL语句预留足够多的条件{0} {1} {2}...");
 	 		sqlFetch  = MessageFormat.format(sqlFetch.replace("'", "''"), condStr.toArray());//'是对MessageFormat来说是特殊字符
 		}
		
	
 		sqlFetch = sqlFetch.replaceAll("\\{\\d+\\}", "");//删除{数字}


 		report.setCurrentSql(sqlFetch);

		return this.baseDao.listAllAsMap(sqlFetch , args.toArray() );
	}

	public void exportFile(String  sqlFetch,  File destFile, ExportConfig exportConfig)
	throws ExportFileException {
		String format = exportConfig.getFormat();

		long start = System.currentTimeMillis();
		if ("csv".equalsIgnoreCase(format)) {
			this.exportCsvFile(sqlFetch, destFile, exportConfig);
		} else if ("xls".equalsIgnoreCase(format)) {
			this.exportExcelFile(sqlFetch, destFile, exportConfig);
		}
		log.debug("导出耗时：" + ((System.currentTimeMillis() - start) / 1000)
				+ " 秒");
	}



	protected void exportCsvFile(String  sqlFetch, File destFile, ExportConfig exportConfig) throws ExportFileException{
		if(StringUtils.isBlank(sqlFetch)){
			throw new ExportFileException("Sql 语句为空!");
		}
		CSVWriter csvWriter = null;
		try {
			csvWriter = new CSVWriter(new FileWriterWithEncoding(destFile, "gbk"));

//			List<Map> list = this.listAllAsMap(report.getSqlFetch(), exportConfig.getSearchCondition());
			Report report = this.getReport(exportConfig.getId(), sqlFetch,exportConfig.getSearchCondition(), exportConfig.getColumnsDisplayedString() ,exportConfig.getLimit());
			//报表标题行的所有标题

			csvWriter.writeNext(report.getDisplayedTitles().toArray(new String[0]) );

//			String[] line = null;
			List<String> line = null;//动态大小
			MapValueGetter mvg = null;
			for (Map map : report.getData()) {
//				line = new String[map.size()];
				line = new ArrayList<String>();
				int i = 0;//col counter
				for (Object obj : map.values()) {
					if(!report.getColumnsDisplayed().get(i)){//不显示此列
						i++;
						continue;
					}
					if(obj == null){
//						line[i] = "";
						line.add("");
					}else{
						String val = obj.toString().trim();

						if(Date.class.isAssignableFrom(obj.getClass())){
//							line[i] = DateFormatUtils.format((Date)obj, "yyyy-MM-dd HH:mm");
							line.add(DateFormatUtils.format((Date)obj, "yyyy-MM-dd HH:mm"));
						}else if(String.class.isAssignableFrom(obj.getClass()) && (val.length()>11 || val.matches("^0\\d[\\d\\.\\,，]+")) //csv超过11个数字会自动显示科学记数法,或者以0开头的,如020
								&& val.matches("[\\d\\.\\,，]+")){
//							line[i] = "'" + val;
							line.add("'" + val);
						}else{
//							line[i] = ObjectUtils.toString(obj);
							line.add(ObjectUtils.toString(obj));
						}

					}

					i++;
				}

				csvWriter.writeNext((String[]) line.toArray(new String[0]) );
			}

			csvWriter.flush();
			csvWriter.close();

		} catch (IOException e) {
			throw new ExportFileException(e);
		} finally{
			if(csvWriter != null){
				try {
					csvWriter.close();
				} catch (IOException e) {
				}
			}
		}
	}

	protected void exportExcelFile(String  sqlFetch, File destFile, ExportConfig exportConfig) throws ExportFileException{
		if(StringUtils.isBlank(sqlFetch)){
			throw new ExportFileException("Sql 语句为空!");
		}
		Report report = this.getReport(exportConfig.getId(), sqlFetch,exportConfig.getSearchCondition(), exportConfig.getColumnsDisplayedString() ,exportConfig.getLimit());


		HSSFWorkbook workbook = null;//PoiExcelHelper.readFromFile("config/xlstpl/report"+exportConfig.getId()+".xls", true);
		HSSFSheet sheet = null;
		if(workbook == null){
			workbook = new HSSFWorkbook();//java.lang.NoClassDefFoundError: org/apache/poi/hssf/usermodel/HSSFWorkbook ??

			sheet = workbook.createSheet("查询结果");
			sheet.setDefaultColumnWidth(15);
		}else{
			sheet = workbook.getSheetAt(0);
		}
		PoiExcelHelper2 ph = new PoiExcelHelper2(workbook, sheet);
		//set titles
		int j=0;

		HSSFCellStyle tt = workbook.createCellStyle();
		ph.setCurrentStyle(tt).addAlignmentStyle(HSSFCellStyle.ALIGN_CENTER).addFontStyle(HSSFColor.BLACK.index, true);
		for (String t : report.getDisplayedTitles()) {
			ph.setCell(0,j++, t,tt);
		}

		//write data
		MapValueGetter mvg = null;
		int k = 1;
		for (Map map : report.getData()) {
			int i=0 ,m = 0;//i是所有列的索引，m是视图列的索引
			Object d = null;
			for (Object obj : map.values()) {
				if(!report.getColumnsDisplayed().get(i)){//不显示此列
					i++;
					continue;
				}


				if(obj == null){
					d  = "";
				}else{
					String val = obj.toString().trim();

					if(Date.class.isAssignableFrom(obj.getClass())){
						d  = DateFormatUtils.format((Date)obj, "yyyy-MM-dd HH:mm");//excel默认显示数字！
//					}else if(String.class.isAssignableFrom(obj.getClass()) && (val.length()>11 || val.matches("^0\\d[\\d\\.\\,，]+")) //csv超过11个数字会自动显示科学记数法,或者以0开头的,如020
//							&& val.matches("[\\d\\.\\,，]+")){
//						d  = "'" + val;//超长数字
					}else{
						d  = obj;
					}

				}
				ph.setCell(k, m++, d );
				i++;
			}
			k++;
		}

		PoiExcelHelper2.saveToFile(workbook, destFile);
	}

	public List<String> getJxList() {
		String sqlFetch = "select jx from jx_info group by jx order by jx";
		List<Map> maps = this.baseDao.listAllAsMap(sqlFetch, null);
		
		List<String> jxs = new ArrayList<String>();
		for (Map map : maps) {
			jxs.add((String) map.get("jx"));
		}
		return jxs;
	}
}
