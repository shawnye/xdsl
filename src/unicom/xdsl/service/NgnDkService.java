package unicom.xdsl.service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.output.FileWriterWithEncoding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import unicom.common.MapValueGetter;
import unicom.common.Page;
import unicom.common.SearchConditions;
import unicom.common.SearchKey;
import unicom.common.port.ExportConfig;
import unicom.common.port.ExportFileException;
import unicom.common.port.FileExporter;
import unicom.dao.BaseDaoInterface;
import au.com.bytecode.opencsv.CSVWriter;

/**
 * NGN端口表
 * @author yexy6
 *
 */
@Service
@SuppressWarnings({"rawtypes"})
public class NgnDkService extends AbstractService implements FileExporter{
	private BaseDaoInterface phoneBaseDao;
	
	
	@Autowired
	public void setPhoneBaseDao(BaseDaoInterface phoneBaseDao) {
		this.phoneBaseDao = phoneBaseDao;
	}

	@Override
	public Page<Map> listAsMap(SearchConditions searchCondition, int pageNo,
			int pageSize) {

		Map<SearchKey, Object> conditions = searchCondition.getConditions();
		int size = conditions.size();
		Object[] args = new Object[size];
		
		//导入用户信息，可能没有关联机房信息
		String sqlFetch = "select * from  ngn_dk  where 1=1 ";
		String sqlCount = "select count(*) from ngn_dk where 1=1 ";
		int i=0;
		for (SearchKey searchKey : conditions.keySet()) {
			args[i++] = conditions.get(searchKey);

			String theta = searchKey.getTheta();
			String field = searchKey.getField();
			sqlFetch += " and " + field + " " + theta + " ? " ;
			sqlCount += " and " + field + " " + theta + " ? " ;
			
			if(theta.equals("like")){
				args[i-1] = "%" + args[i-1].toString() + "%";
			}
		}
		
		return this.phoneBaseDao.listAsMap(sqlCount , sqlFetch , args , pageNo, pageSize);
	}

	public List<Map> listAllAsMap(SearchConditions searchCondition) {
		Map<SearchKey, Object> conditions = searchCondition.getConditions();
		int size = conditions.size();
		Object[] args = new Object[size];
		String sqlFetch = "select * from  ngn_dk  where 1=1 ";
		int i=0;
		for (SearchKey searchKey : conditions.keySet()) {
			args[i++] = conditions.get(searchKey);

			String theta = searchKey.getTheta();
			String field = searchKey.getField();
			sqlFetch += " and " + field + " " + theta + " ? " ;
			
			if(theta.equals("like")){
				args[i-1] = "%" + args[i-1].toString() + "%";
			}
		}
		
		return this.phoneBaseDao.listAllAsMap( sqlFetch , args );
	}

	public void exportFile(File destFile, ExportConfig exportConfig)
	throws ExportFileException {
		String format = exportConfig.getFormat();

		long start = System.currentTimeMillis();
		if("csv".equalsIgnoreCase(format)){
			this.exportCsvFile(destFile, exportConfig);
		}else if("excel".equalsIgnoreCase(format)){
			this.exportExcelFile(destFile, exportConfig);
		}
		log.debug("导出耗时：" + ((System.currentTimeMillis()-start)/1000) + " 秒");
	}
	private void exportExcelFile(File destFile, ExportConfig exportConfig) throws ExportFileException{
		
	}

		
	private void exportCsvFile(File destFile, ExportConfig exportConfig) throws ExportFileException{
		CSVWriter csvWriter = null;
		try {
			csvWriter = new CSVWriter(new FileWriterWithEncoding(destFile, "gbk"));
			
			List<Map> list = this.listAllAsMap(exportConfig.getSearchCondition());
			
			String[] line = null;
			line = new String[]{
					"机房名称",
					"槽号",
					"端口号",
					
					"域名",
					"终端标识",
					"MDF横列",
					
					"是否反极",
					"产品号码",
					
					"设备名称"
					
			};
			csvWriter.writeNext(line );
			
			MapValueGetter mvg = null;
			for (Map map : list) {
				mvg = new MapValueGetter(map);
				line = new String[]{
						mvg.getAsString("jfmc"),
						
						mvg.getAsIntegerString("ch"),
						mvg.getAsIntegerString("dkh"),
						
						mvg.getAsTrimedString("eid"),
						mvg.getAsTrimedString("tid"),
						
						mvg.getAsTrimedString("hl"),
						
						mvg.getAsTrimedString("jixing"),
						mvg.getAsTrimedString("haoma"),
						
						mvg.getAsTrimedString("sbmc"),
				};
				csvWriter.writeNext(line );
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
}
