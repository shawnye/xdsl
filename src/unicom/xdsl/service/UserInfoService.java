package unicom.xdsl.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.FileWriterWithEncoding;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import unicom.common.Constants;
import unicom.common.DateHelper;
import unicom.common.FileHelper;
import unicom.common.MapHelper;
import unicom.common.MapValueGetter;
import unicom.common.Page;
import unicom.common.PoiExcelHelper2;
import unicom.common.ProcessHelper;
import unicom.common.ResourceLoadingHelper;
import unicom.common.SearchConditions;
import unicom.common.SearchKey;
import unicom.common.SystemEnvironment;
import unicom.common.mssql.BcpBuilder;
import unicom.common.port.ExportConfig;
import unicom.common.port.ExportFileException;
import unicom.common.port.FieldConverter;
import unicom.common.port.FileExporter;
import unicom.common.port.FileImporter;
import unicom.common.port.ImportConfig;
import unicom.common.port.ImportFileException;
import unicom.dao.BaseDaoInterface;
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

/**
 * 用户信息(正常情况下一定有机房信息)
 * @author yexy6
 *
 */
@Service(value="userInfoService")
@SuppressWarnings("rawtypes")
public class UserInfoService extends AbstractService implements FileExporter,FileImporter{
	private BaseDaoInterface baseDao;

	private int batchSizeOfProductCode = 50 ;//一次同时可以查询N个号码

//	private LogService logService;//无法记录操作人

	@Autowired
	public void setBaseDao(BaseDaoInterface baseDao) {
		this.baseDao = baseDao;
	}
	
//	@Autowired
//	public void setLogService(LogService logService) {
//		this.logService = logService;
//	}

	private OntService ontService;
	 
	@Autowired
	public void setOntService(OntService ontService) {
		this.ontService = ontService;
	}

	public Page<Map> listAsMap(SearchConditions searchCondition, int pageNo, int pageSize) {

		Map<SearchKey, Object> conditions = searchCondition.getConditions();
		int size = conditions.size();
		Object[] args = new Object[size];

		//导入用户信息，可能没有关联机房信息
		String sqlFetch = "select u.U_id as u_id, u.j_id as j_id, ont_id, username , p_id,old_p_id ,address, user_no, password, area,tel,begin_date,jx,sbh,type,board_type,slot,sb_port,mdf_port,mac,open_date,finish_date,state,branch,remark ,inner_vlan,outer_vlan,ip,sn" +
				" from  user_info u left join jx_info j on j.j_id=u.j_id where 1=1 ";
		String sqlCount = "select count(*) from user_info u left join jx_info j on j.j_id=u.j_id where 1=1 ";
		int i=0;
		for (SearchKey searchKey : conditions.keySet()) {
			args[i++] = conditions.get(searchKey);

			String str = args[i-1].toString();
			if(StringUtils.isBlank(str)){//其实在 UserInfoListServlet 已经处理过了（去掉emptied,trim)
				continue;
			}

			String theta = searchKey.getTheta();
			String field = searchKey.getField();
			if(field.equalsIgnoreCase("j_id")){
				field = "u.j_id";//避免出现错误：列名 'J_id' 不明确。
			}

			if(field.equalsIgnoreCase("u_id")){
				field = "u.u_id";//避免出现错误：列名 'u_id' 不明确。
			}

			if(field.equalsIgnoreCase("ui_batch_num")){
				field = "u.batch_num";
			}

			String[] split = str.split("\\s+");//问题是多选必须是string
			if("p_id".equals(field) && split.length>1){//仅仅针对号码, like ==> eq, 查询效率大大提升
				sqlFetch += " and (";
				sqlCount += " and (";

				Object[] expandedParams = {};

				if(theta.equals("like")){
					theta = "=";
				}
				for (int j = 0; j < split.length && expandedParams.length < batchSizeOfProductCode; j++) {
					if(StringUtils.isNotBlank(split[j])){
						sqlFetch +=  field + " " + theta + " ? " ;
						sqlCount +=  field + " " + theta + " ? " ;

						if(j != split.length - 1 && StringUtils.isNotBlank(split[j+1]) && expandedParams.length < batchSizeOfProductCode-1 ){//确保下一个不空
							sqlFetch += " or ";
							sqlCount += " or ";
						}
						String finalstr = split[j].trim().replace("'", "");

						expandedParams = ArrayUtils.add(expandedParams,  finalstr );
//						if(theta.equals("like")){
//							expandedParams = ArrayUtils.add(expandedParams, "%" + finalstr + "%");
//						}else{
//							expandedParams = ArrayUtils.add(expandedParams,  finalstr );
//						}
					}


				}
				sqlFetch += ")";
				sqlCount += ")";


				//插入新增的条件
				Object[] subarray1 = ArrayUtils.subarray(args, 0, i-1);//不包括i-1
				Object[] subarray2 = {};
				if(i < args.length){
					subarray2 = ArrayUtils.subarray(args, i, args.length);
				}

				args = ArrayUtils.addAll(subarray1, expandedParams);
				args = ArrayUtils.addAll(args, subarray2);

				i += expandedParams.length - 1;
			}else{
				sqlFetch += " and " + field + " " + theta + " ? " ;
				sqlCount += " and " + field + " " + theta + " ? " ;

				if(theta.equals("like")){
					args[i-1] = "%" + str.trim().replace("'", "") + "%";
				}

			}

		}//for

		return this.baseDao.listAsMap(sqlCount , sqlFetch , args , pageNo, pageSize);
	}

	public List<Map> listAllAsMap(SearchConditions searchCondition) {
		Map<SearchKey, Object> conditions = searchCondition.getConditions();
		int size = conditions.size();
		Object[] args = new Object[size];
		String sqlFetch = "select u.U_id as u_id, u.j_id as j_id, ont_id, username , p_id,old_p_id ,address, user_no, password, area,tel,begin_date,jx,sbh,type,board_type,slot,sb_port,mdf_port,mac,open_date,finish_date,state,branch,remark ,inner_vlan,outer_vlan,ip,ont_id,sn" +
				" from user_info u left join jx_info j on j.j_id=u.j_id where 1=1 ";
		int i=0;
		for (SearchKey searchKey : conditions.keySet()) {
			args[i++] = conditions.get(searchKey);

			String str = args[i-1].toString();
			if(StringUtils.isBlank(str)){
				continue;
			}

			String theta = searchKey.getTheta();
			String field = searchKey.getField();

			if(field.equalsIgnoreCase("j_id")){
				field = "u.j_id";//避免出现错误：列名 'J_id' 不明确。
			}

			if(field.equalsIgnoreCase("u_id")){
				field = "u.u_id";//避免出现错误：列名 'u_id' 不明确。
			}

			if(field.equalsIgnoreCase("ui_batch_num")){
				field = "u.batch_num";
			}
			
			String[] split = str.split("\\s+");//问题是多选必须是string
			if("p_id".equals(field) && split.length>1){//仅仅针对号码
				sqlFetch += " and (";

				Object[] expandedParams = {};

				if(theta.equals("like")){
					theta = "=";
				}

				for (int j = 0; j < split.length && expandedParams.length < batchSizeOfProductCode; j++) {
					if(StringUtils.isNotBlank(split[j])){
						sqlFetch +=  field + " " + theta + " ? " ;

						if(j != split.length - 1 && StringUtils.isNotBlank(split[j+1]) && expandedParams.length < batchSizeOfProductCode-1){//确保下一个不空
							sqlFetch += " or ";
						}
						String finalstr = split[j].trim().replace("'", "");
						expandedParams = ArrayUtils.add(expandedParams,  finalstr );

//						if(theta.equals("like")){
//							expandedParams = ArrayUtils.add(expandedParams, "%" + finalstr + "%");
//						}else{
//							expandedParams = ArrayUtils.add(expandedParams,  finalstr );
//						}
					}


				}
				sqlFetch += ")";


				//插入新增的条件
				Object[] subarray1 = ArrayUtils.subarray(args, 0, i-1);//不包括i-1
				Object[] subarray2 = {};
				if(i < args.length){
					subarray2 = ArrayUtils.subarray(args, i, args.length);
				}

				args = ArrayUtils.addAll(subarray1, expandedParams);
				args = ArrayUtils.addAll(args, subarray2);

				i += expandedParams.length - 1;
			}else{
				sqlFetch += " and " + field + " " + theta + " ? " ;

				if(theta.equals("like")){
					args[i-1] = "%" + str.trim().replace("'", "") + "%";
				}

			}

		}//for


		return this.baseDao.listAllAsMap( sqlFetch , args );
	}

	public int restoreServie(String p_id) {
		if(StringUtils.isBlank(p_id)){
			return 0;
		}

		String sqlUpdate = "update user_info \r\n" +
				"set username=SUBSTRING (u.username,0, CHARINDEX('[', u.username)-1 )\r\n" +
				"from user_info u\r\n" +
				"where (u.username like '%停机%' or u.username like '%待停机%') and u.p_id =? ";

		log.debug("[复机] " + p_id);
		return this.baseDao.update(sqlUpdate, new Object[]{p_id});

	}

	public int stopAllWaitedService(){
		String sqlUpdate = "update user_info\r\n" +
		"set username= replace( u.username, '待停机','停机') " +
		"from user_info u where u.username like '%待停机%'" ;

		log.debug("[待停机 转 停机] ");
		return this.baseDao.update(sqlUpdate, new Object[]{});
	}
	/**
	 * 停机不等于拆机，线路仍然保留，但是冻结端口。
	 * @param wait
	 * 是否待停机
	 * @param u_id
	 * @param stopDate
	 * @return
	 */
	public int stopService(boolean wait, String p_id, Date stopDate) {
		if(StringUtils.isBlank(p_id)){
			return 0;
		}

		String stopStr = wait ? "待停机" : "停机";
		String stopDateStr = stopDate == null ? "" : DateFormatUtils.format(stopDate, "yyyy-MM-dd");

		//如果有[xxx]，则替换之,否则添加
		String sqlUpdate = "update user_info\r\n" +
				"set username= (case when  CHARINDEX('[', u.username)<>0 then SUBSTRING(u.username,0, CHARINDEX('[', u.username)-1 ) else u.username end)  + ' [" +  stopDateStr  + " "+stopStr+"]' \r\n" +
				"from user_info u \r\n" +
				"where u.p_id=?";

		log.debug("[" + stopStr + "] " + p_id);
		return this.baseDao.update(sqlUpdate, new Object[]{p_id});
	}

	/**
	 * FIXME 无法利用 batch update
	 *
	 * 停复机: 停机/待停机/复机
	 *
	 * 停复机/产品号码/修改时间（可选）
	 * @param file
	 * csv file
	 * @return
	 */
	public int updateServiceStatus(File file, StringBuffer msg) {
		CSVReader reader = null;
		try {
			reader = new CSVReader(new FileReader(file));
		} catch (FileNotFoundException e1) {
			log.error("无法读取文件:" + file,e1);
			return 0;
		} catch (Exception e1) {
			log.error("无法读取文件:" + file,e1);
			return 0;
		}
		int line = 0;
		int updated = 0;
		String[] nextLine = null;
		int j = 0;
		try {
			while((nextLine  = reader.readNext()) != null){
				line++;
				if(line == 1){
					continue;
				}
//				if(line%50 == 0){
//					try {
//						Thread.sleep(1000);
//					} catch (InterruptedException e) {
//					}
//				}

				if(nextLine.length < 2){
					msg.append("缺少状态或产品号码，忽略：\t" + StringUtils.join(nextLine,","));
					msg.append("\n");
					continue;
				}
				Date stopDate = null;
				if(nextLine.length > 2 ){
					stopDate = DateHelper.toDate(nextLine[2], Constants.DEFAULT_DATE_PATTERNS);

				}

				if(nextLine[0].trim().equals("停机")){
					j = this.stopService(false, nextLine[1], stopDate);
					if(j == 0){
						msg.append("号码不存在或者已停机，忽略：\t" + nextLine[1]);
						msg.append("\n");
					}

				}else if(nextLine[0].trim().equals("待停机")){
					j = this.stopService(true, nextLine[1], stopDate);
					if(j == 0){
						msg.append("号码不存在或者已停机，忽略：\t" + nextLine[1]);
						msg.append("\n");
					}
				}else if(nextLine[0].trim().equals("复机")){
					j = this.restoreServie(nextLine[1]);
					if(j == 0){
						msg.append("号码不存在或者未停机，忽略：\t" + nextLine[1]);
						msg.append("\n");
					}
				}
				updated += j;
			}
		} catch (IOException e) {
			log.error("无法读取csv: " + line,e);
			return 0;
		} catch (Exception e) {
			log.error("无法读取csv: " + line,e);
			return 0;
		}

		msg.append("成功读入行数:" + line);
		msg.append("，成功更新行数:" + updated);
		return updated;
	}
	/**
	 * 
	 * @param file
	 * @param limitState
	 * 限制状态
	 * @param msg
	 * @return
	 */
	public int updateUserAField(File file,String fieldName, String fieldDefaultValue, StringBuffer msg) {
		CSVReader reader = null;
		try {
			reader = new CSVReader(new FileReader(file));
		} catch (FileNotFoundException e1) {
			log.error("无法读取文件:" + file,e1);
			return 0;
		} catch (Exception e1) {
			log.error("无法读取文件:" + file,e1);
			return 0;
		}
		int line = 0;
		int updated = 0;
		String[] nextLine = null;
		int j = 0;
		String p_id;
		String fieldValue;
		
		String sqlUpdate = "update user_info\r\n" +
				"set "+fieldName+"=?\r\n" +
					"where p_id=?";
		
		try {
			while((nextLine  = reader.readNext()) != null){
				line++;
				if(line == 1){//忽略标题
					continue;
				}
 

				if(nextLine.length < 1){
					msg.append("缺少产品号码，忽略：\t" + StringUtils.join(nextLine,","));
					msg.append("\n");
					continue;
				}
				p_id=nextLine[0].trim().replace("'", ""); 
				
				if(StringUtils.isBlank(fieldDefaultValue)){
					fieldValue=nextLine.length>1 ? nextLine[1] : null; 
				}else{
					fieldValue=fieldDefaultValue;
				}
				if(StringUtils.isBlank(fieldValue) ){//空的不更新
					continue;
				}
				 
				j = this.baseDao.batchUpdateASql(sqlUpdate, new Object[]{fieldValue, p_id});
// 				j = this.baseDao.update(sqlUpdate, new Object[]{state, p_id});
				 
				updated += j;
			}
		} catch (IOException e) {
			log.error("无法读取csv: " + line,e);
			return 0;
		} catch (Exception e) {
			log.error("无法读取csv: " + line,e);
			return 0;
		}

		msg.append("成功读入行数:" + line);
		msg.append("，成功更新行数:" + updated);
		return updated;
	}	

	/**
	 * 新装机==========================================================
	 */
	public boolean importFile(File file, ImportConfig importConfig,
			StringBuffer msg) throws ImportFileException {

		CSVReader reader = null;
		try {
			reader = new CSVReader(new FileReader(file));
		} catch (FileNotFoundException e1) {
			log.error("无法读取文件:" + file,e1);
			msg.append("无法读取导入文件\n");
			return false;
		} catch (Exception e1) {
			log.error("无法读取文件:" + file,e1);
			msg.append("无法读取导入文件\n");
			return false;
		}
		int line = 0;
		int updated = 0;
		String[] nextLine = null;
		//state=未分配
//		String sqlUpdate = "insert into user_info (username,p_id,address,user_no,password,area,tel,begin_date,state) values (?,?,?,?,?,?,?,?,?) ";
		Collection<String> fields = importConfig.getImportFields();
		if(fields.size() == 0){
			msg.append("配置有误：没有对应数据库插入字段\n");
			return false;
		}
		Collection<String> fields2 = importConfig.getFields();//all field
		
		String[] questions = new String[fields.size()];
		Arrays.fill(questions, "?");

		StringBuffer insertSql = new StringBuffer("insert into ");
		insertSql.append(importConfig.getTable());
		insertSql.append(" (");
		insertSql.append(StringUtils.join(fields,","));
		insertSql.append(") values (");
		insertSql.append(StringUtils.join(questions,","));
		insertSql.append(") ");

		List<Object[]> args = new ArrayList<Object[]>(Constants.UPDATE_BATCH_SIZE);

		//更新导入时间印记
		String updateJxInfoSql =
			"update jx_info\r\n" +
			"set u_id=u.u_id, used=1,sn={SN}\r\n" + //error ,必须针对FTTH做特殊处理
			"from user_info u where u.j_id=jx_info.j_id and u.remark like '%" + DateFormatUtils.format(importConfig.getStamp(), "yyyy-MM-dd HH:mm") + "%新装导入%'";//@see UserInfoRemarkFieldConverter

		String defatulBatchNum = importConfig.getDefatulBatchNum();

		try {
			while((nextLine  = reader.readNext()) != null){
				line++;
				if(line == 1){
					continue;
				}

				boolean notEmpty = false;
				for (int i = 0; i < nextLine.length; i++) {
					if(StringUtils.isNotBlank(nextLine[i])){
						notEmpty=true;
						break;
					}
				}

				if(nextLine.length < 8){
					log.error("此行少于8列：行" + line);
					continue;
				}


				if(!notEmpty){
					//empty line means closed
					break;
				}

				//do update
				if(args.size() > 0 && args.size() % Constants.UPDATE_BATCH_SIZE == 0){
					int[] is = this.baseDao.batchUpdateWithSameSql(insertSql.toString(), args);
					this.baseDao.batchUpdate(new String[]{updateJxInfoSql});
					int j = 0;
					for (int i = 0; i < is.length; i++) {
						j += is[i];
					}

					updated += j;

					args.clear();
				}

				//-----------------------
				Object[] objs = new Object[fields.size()];//插入属性insert
				Object[] objs2 = new Object[fields2.size()];//所有属性,包含@属性
				int i=0,j=0;
				int j_id_idx =-1;
				int p_id_idx = -1;
				int user_no_idx = -1;
				int sn_idx = -1;
//				System.out.println(objs.length + ",all=" + objs2.length);
				for (String field : fields2) { 
					Integer order = importConfig.getFieldIndex(field);
					if(order != null ){
						if( order < nextLine.length ){
							objs2[i] = nextLine[order]!=null ? nextLine[order].trim():null; 
						}else{
							objs2[i] = null;
						}
						
						if(! field.startsWith("@")){ 
							if("j_id".equals(field)){
								j_id_idx = j;//相对插入属性而言
							}else if("p_id".equals(field)){
								p_id_idx = j;//相对插入属性而言
							}else if("user_no".equals(field)){
								user_no_idx = j;//相对插入属性而言
							}
							objs[j] = objs2[i]; 
							j++;//相对插入属性而言
//							System.err.println("objs = " + j);
						} else{
							if("@sn".equals(field)){
								sn_idx = i;//相对‘所有’属性而言
							}
						}
					}
					
					FieldConverter fieldConverter = importConfig.getFieldConverterByField(field);
					if(fieldConverter != null){
						objs2[i] = fieldConverter.convertFrom(objs2); 
						if(! field.startsWith("@")){ //忽略@开头 的属性导入
							objs[j-1] = objs2[i]; 
						} 
					}
					i++;

				}//for fields2


				if(j_id_idx != -1 && objs[j_id_idx] == null){//无法预分配端口,忽略
					msg.append( "[行" + line + "]根据VLAN端口无法查找到或查到多个‘未占用’端口(J_ID), 忽略:"  + objs[0]);
					msg.append("\n");
					continue;
				}
				
				if(p_id_idx != -1 && objs[p_id_idx] == null){
					msg.append( "[行" + line + "]产品号码不能为空, 忽略:"  + objs[0]);
					msg.append("\n");
					continue;
				}
				
				String p_id = (String) objs[p_id_idx];
				Map ui =  this.findByPid(p_id);
				if(ui != null){
					msg.append( "[行" + line + "]产品号码["+p_id+"]已经存在, 忽略:"  + objs[0]);
					msg.append("\n");
					continue;
				}
				
				String user_no = (String) objs[user_no_idx];
				ui =  this.findByUserNo(user_no);
				if(ui != null){
					msg.append( "[行" + line + "]帐号["+user_no+"]已经存在, 忽略:"  + objs[0]);
					msg.append("\n");
					continue;
				}

				String sn = "sn";//不变
				if(sn_idx!=-1){
					sn = (String) objs2[sn_idx];
					if(StringUtils.isBlank(sn)){
						sn = "sn";
					}else{
						sn = "'" + sn + "'";
					}
				}
				 
				updateJxInfoSql = updateJxInfoSql.replace("{SN}", sn);
				
				args.add(objs);
			}

			if(args.size() > 0){
				int[] is = this.baseDao.batchUpdateWithSameSql(insertSql.toString(), args);
				this.baseDao.batchUpdate(new String[]{updateJxInfoSql});
				int j = 0;
				for (int i = 0; i < is.length; i++) {
					j += is[i];
				}

				updated += j;

				args.clear();
			}

		} catch (IOException e) {
//			log.error("无法读取csv: " + line,e);
			msg.append("无法读取文件行：" + line + ", 详细说明：\n\n");
			msg.append(e.getMessage());
			msg.append("\n\n");
			throw new ImportFileException(e);
		} catch (Exception e) {
//			log.error("无法读取csv: " + line,e);
			msg.append("无法读取文件行：" + line + ", 详细说明：\n\n");
			msg.append(e.getMessage());
			msg.append("\n\n");

			throw new ImportFileException(e);
		}
		//update jx_info


		msg.append("成功读入行数:" + line);
		msg.append("，成功导入行数:" + updated);
		msg.append("，导入批次:" + defatulBatchNum);

 
		return true;
	}

	

	//==================================================================

	private boolean canExportSpecifiedInfo=true;

	public synchronized boolean isCanExportSpecifiedInfo() {
		return canExportSpecifiedInfo;
	}

	public synchronized void setCanExportSpecifiedInfo(
			boolean canExportSpecifiedInfo) {
		this.canExportSpecifiedInfo = canExportSpecifiedInfo;
	}

	public synchronized void exportSpecifiedInfo(File destFile, String[] pidArray) throws ExportFileException {
		if(! isCanExportSpecifiedInfo()){
			throw new ExportFileException("有人在使用指定用户信息导出功能请等待...");
		}
		if(pidArray == null || pidArray.length == 0){
			this.setCanExportSpecifiedInfo(true);
			return;
		}

		this.setCanExportSpecifiedInfo(false);

		CSVWriter csvWriter = null;

		try {
			//truncate table product_code_cache and insert into new p_id
			int[] b = this.baseDao.batchUpdate(new String[]{"truncate table product_code_cache;"});

			String sqlUpdate = "insert into product_code_cache(p_id) values(?);";


			List<Object[]> args = new ArrayList<Object[]>();
			for (int i = 0; i < pidArray.length; i++) {
				if(i>0 && i%30 == 0){
					 this.baseDao.batchUpdateWithSameSql(sqlUpdate , args);
					 args.clear();
				}

				if(StringUtils.isBlank(pidArray[i])){
					continue;//忽略空行
				}
				String pid = pidArray[i].replaceAll("\\\"", "").replaceAll("\\'", "").trim();

				args.add(new Object[]{pid});
			}

			if(args.size() > 0){
				this.baseDao.batchUpdateWithSameSql(sqlUpdate , args);
			}

			log.debug("导入产品号码数量：" + args.size());
			//do export
			String sqlFetch = "select c.p_id as search_id,u.p_id as p_id, u.U_id as u_id, u.j_id as j_id, ip, inner_vlan,outer_vlan,used_user_no, username  ,address, user_no, password, area,tel,begin_date,jx,sbh,type,board_type,slot,sb_port, dbo.createNMport(sbh, ip,slot,sb_port) nm_port,mdf_port,mac,open_date,finish_date,state,branch,remark " +
			" from product_code_cache c left join user_info u on (u.p_id=c.p_id or u.user_no like c.p_id + '%' ) left join jx_info j on j.j_id=u.j_id where 1=1 ";


			List<Map> list = this.baseDao.listAllAsMap( sqlFetch , new Object[0] );

			csvWriter = new CSVWriter(new FileWriterWithEncoding(destFile, "gbk"));
			String[] line = null;
			line = new String[]{
					"待查号码或帐号",
					"U_ID",
					"停机状态",
					"产品号码",
					"旧产品号码",
					"客户名称",
					"帐号",

					"地址",
					"区域",

					"机房",
					"设备号",
					"类型",
					"板类型",
					"槽号",
					"设备端口",
					"网管设备端口",//还原网管原始 板-槽-端口 格式，不同类型设备格式不同
					
					"内层VLAN",
					"外层VLAN",
					
					"IP",

					"MDF端口",
					"FTTH SN",

					"J_ID",
					"录入时间",
					"数据配置时间",
					"竣工时间",
					"状态",
					"备注"

			};
			csvWriter.writeNext(line );

			String username = null;
			String stopStatus = null;
			MapValueGetter mvg = null;
			String uid=null;
			for (Map map : list) {
				mvg = new MapValueGetter(map);
				username = mvg.getAsTrimedString("username");
				if(username != null && username.contains("待停机")){
					stopStatus = "待停机";
				}else if(username != null && username.contains("停机")){
					stopStatus = "停机";
				} else{
					stopStatus = null;
				}

				uid=mvg.getAsString("u_id");
				line = new String[]{
						"'" + mvg.getAsTrimedString("search_id"),
						uid == null ? "!!找不到用户资料" : uid,
						stopStatus,
						"'" + mvg.getAsTrimedString("p_id"),
						"'" + mvg.getAsTrimedString("old_p_id"),
						username,
						mvg.getAsTrimedString("user_no"),

						mvg.getAsTrimedString("address"),
						mvg.getAsTrimedString("area"),

						mvg.getAsTrimedString("jx"),

						mvg.getAsTrimedString("sbh"),
						mvg.getAsTrimedString("type"),
						mvg.getAsTrimedString("board_type"),
						mvg.getAsTrimedString("slot"),
						mvg.getAsTrimedString("sb_port"),
						mvg.getAsTrimedString("nm_port"),

						mvg.getAsTrimedString("inner_vlan"),
						mvg.getAsTrimedString("outer_vlan"),
						mvg.getAsTrimedString("ip"),

						mvg.getAsTrimedString("mdf_port"),
						mvg.getAsTrimedString("sn"),

						mvg.getAsTrimedString("j_id"),
						DateHelper.format(mvg.getAsDate("begin_date"), "yyyy-MM-dd"),
						mvg.getAsTrimedString("open_date"),
						DateHelper.format(mvg.getAsDate("finish_date"), "yyyy-MM-dd"),
						mvg.getAsTrimedString("state"),
						mvg.getAsTrimedString("remark"),
				};
				csvWriter.writeNext(line );
			}

			csvWriter.flush();
			csvWriter.close();

		} catch (Exception e) {
			throw new ExportFileException("无法导出指定用户信息", e);
		} finally{
			if(csvWriter != null){
				try {
					csvWriter.close();
				} catch (IOException e) {
				}
			}

			this.setCanExportSpecifiedInfo(true);
		}

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

	//FIXME not finished!
	private void exportExcelFile(File destFile, ExportConfig exportConfig) throws ExportFileException{
//		HSSFWorkbook workbook = PoiExcelUtils.readFromFile("config/xlstpl/use_info.xls" , true);
//		if(workbook == null){
//			throw new ExportFileException("未找到模板文件 :config/xlstpl/use_info.xls !" );
//		}
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet();
//		HSSFSheet sheet = workbook.getSheetAt(0);
		sheet.setSelected(true);

		PoiExcelHelper2 excelUtils= new PoiExcelHelper2(workbook, sheet);


	}

	@SuppressWarnings("unchecked")
	private void exportCsvFile(File destFile, ExportConfig exportConfig) throws ExportFileException{
		CSVWriter csvWriter = null;
		try {
			csvWriter = new CSVWriter(new FileWriterWithEncoding(destFile, "gbk"));

			List<Map> list = this.listAllAsMap(exportConfig.getSearchCondition());

			String[] line = null;
			line = new String[]{
					"U_ID",
					"停机状态",
					"客户名称",
					"产品号码",
					"旧产品号码",
					"帐号",

					"地址",
					"区域",

					"机房",
					"设备号",
					"类型",
					"板类型",
					"槽号",
					"设备端口",
					"ONT端口",

					"OLT名称",
					"内层VLAN",
					"外层VLAN",
					"IP",

					"MDF端口",

					"J_ID",
					"录入时间",
					"数据配置时间",
					"竣工时间",
					"状态",
					"备注"

			};
			csvWriter.writeNext(line );

			String username = null;
			String stopStatus = null;
			MapValueGetter mvg = null;
			for (Map map : list) {
				mvg = new MapValueGetter(map);
				username = mvg.getAsTrimedString("username");
				if(username != null && username.contains("待停机")){
					stopStatus = "待停机";
				}else if(username != null && username.contains("停机")){
					stopStatus = "停机";
				}else{
					stopStatus = null;
				}
				line = new String[]{
						mvg.getAsString("u_id"),
						stopStatus,
						username,
						"'" + mvg.getAsTrimedString("p_id"),
						"'" + mvg.getAsTrimedString("old_p_id"),
						mvg.getAsTrimedString("user_no"),

						mvg.getAsTrimedString("address"),
						mvg.getAsTrimedString("area"),

						mvg.getAsTrimedString("jx"),

						mvg.getAsTrimedString("sbh"),
						mvg.getAsTrimedString("type"),
						mvg.getAsTrimedString("board_type"),
						mvg.getAsTrimedString("slot"),
						mvg.getAsTrimedString("sb_port"),
						mvg.getAsTrimedString("ont_id"),

						mvg.getAsTrimedString("olt"),
						mvg.getAsTrimedString("inner_vlan"),
						mvg.getAsTrimedString("outer_vlan"),
						mvg.getAsTrimedString("ip"),

						mvg.getAsTrimedString("mdf_port"),

						mvg.getAsTrimedString("j_id"),
						DateHelper.format(mvg.getAsDate("begin_date"), "yyyy-MM-dd"),
						mvg.getAsTrimedString("open_date"),
						DateHelper.format(mvg.getAsDate("finish_date"), "yyyy-MM-dd"),
						mvg.getAsTrimedString("state"),
						mvg.getAsTrimedString("remark"),
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

	/**
	 *
	 * @param destFile
	 * @param roomArray
	 * @throws ExportFileException
	 */
	public void exportSpecifiedAccessRoomInfo(File destFile, String[] roomArray, int sqlFileId) throws ExportFileException {
		if(! isCanExportSpecifiedInfo()){
			throw new ExportFileException("有人在使用指定接入间信息导出功能请等待...");
		}
		if(roomArray == null || roomArray.length == 0){
			this.setCanExportSpecifiedInfo(true);
			return;
		}

		this.setCanExportSpecifiedInfo(false);

		File file = ResourceLoadingHelper.loadFileFromClasspath("config/sql/export_access_room_"+sqlFileId+".sql");
		if(file == null){
			throw new ExportFileException("找不到模板文件：config/sql/export_access_room_"+sqlFileId+".sql" );
		}
		//make bcp and run it
		BcpBuilder bb = new BcpBuilder();
 
		bb.setAuth(SystemEnvironment.getProperty("db.server"), SystemEnvironment.getProperty("db.username"),SystemEnvironment.getProperty("db.password"));
		File tmpFile = new File(destFile.getAbsolutePath()+".tmp");
		FileHelper.createFile(tmpFile);
		bb.setFilePath(tmpFile.getAbsolutePath());
		try {
			String sql = FileUtils.readFileToString(file, "utf-8");
			StringBuffer b = new StringBuffer();
			for (int i = 0; i < roomArray.length; i++) {
				b.append("'" + roomArray[i].replace("\"", "").replace("'", "").replace(",", "").trim() + "'");
				if(i != roomArray.length -1){
					b.append(",");
				}
			}
			bb.setSql(sql.replace("{0}", b.toString()));
		} catch (IOException e) {
			throw new ExportFileException("无法读取模板文件：config/sql/export_access_room_"+sqlFileId+".sql" ,e);
		}

		String queryout = bb.queryout();
//		log.debug("bcp: \n" + queryout);


		ProcessHelper.call(new File("."), ProcessHelper.toCommand(queryout));
		
		
		//添加表情
		String[] titles = bb.getTitlesFromSql();// "j_id,机房,设备号,槽号,端口号,板卡类型,外层VLAN,内层VLAN,横列端口,u_id,产品号码,客户名称,账号,地址\r\n" ;

		if(titles == null){
			throw new ExportFileException("没有在SQL中找到标题，必须在注释行中,第一个出现优先,格式：--<TITLE>xxxx,xxxx,xxx</TITLE>");
		}
		
		OutputStream output = null;
		InputStream input = null;

		try {
//			FileUtils.writeStringToFile(destFile, title,"utf-8");
			input = new FileInputStream(tmpFile);
			output = new FileOutputStream(destFile);
			IOUtils.write(StringUtils.join(titles ,bb.getTerminal()), output);
			IOUtils.write(IOUtils.LINE_SEPARATOR_WINDOWS, output);
			
			IOUtils.copyLarge(input, output);
			output.flush();
		} catch (Exception e) {
			throw new ExportFileException("导出失败",e );
		} finally{
			IOUtils.closeQuietly(output);
			IOUtils.closeQuietly(input);
 
		}

 
		this.setCanExportSpecifiedInfo(true);

	}
	
	public void exportAllFtthInfo(File destFile,int sqlFileId) throws ExportFileException {
		if(! isCanExportSpecifiedInfo()){
			throw new ExportFileException("有人在使用指定接入间信息导出功能请等待...");
		}
		 
		this.setCanExportSpecifiedInfo(false);

		File file = ResourceLoadingHelper.loadFileFromClasspath("config/sql/export_ftth_"+sqlFileId+".sql");
		if(file == null){
			throw new ExportFileException("找不到模板文件：config/sql/export_ftth_"+sqlFileId+".sql" );
		}
		//make bcp and run it
		BcpBuilder bb = new BcpBuilder();
 
		bb.setAuth(SystemEnvironment.getProperty("db.server"), SystemEnvironment.getProperty("db.username"),SystemEnvironment.getProperty("db.password"));
		File tmpFile = new File(destFile.getAbsolutePath()+".tmp");
		FileHelper.createFile(tmpFile);
		bb.setFilePath(tmpFile.getAbsolutePath());
		try {
			String sql = FileUtils.readFileToString(file, "utf-8");
			bb.setSql(sql);
		} catch (IOException e) {
			throw new ExportFileException("无法读取模板文件：config/sql/export_ftth_"+sqlFileId+".sql" ,e);
		}

		String queryout = bb.queryout();
//		log.debug("bcp: \n" + queryout);


		ProcessHelper.call(new File("."), ProcessHelper.toCommand(queryout));
		
		
		//添加标题
		String[] titles = bb.getTitlesFromSql();// "j_id,机房,设备号,槽号,端口号,板卡类型,外层VLAN,内层VLAN,横列端口,u_id,产品号码,客户名称,账号,地址\r\n" ;

		if(titles == null){
			throw new ExportFileException("没有在SQL中找到标题，必须在注释行中,第一个出现优先,格式：--<TITLE>xxxx,xxxx,xxx</TITLE>");
		}
		
		OutputStream output = null;
		InputStream input = null;

		try {
//			FileUtils.writeStringToFile(destFile, title,"utf-8");
			input = new FileInputStream(tmpFile);
			output = new FileOutputStream(destFile);
			IOUtils.write(StringUtils.join(titles ,bb.getTerminal()), output);
			IOUtils.write(IOUtils.LINE_SEPARATOR_WINDOWS, output);
			
			IOUtils.copyLarge(input, output);
			output.flush();
		} catch (Exception e) {
			throw new ExportFileException("导出失败",e );
		} finally{
			IOUtils.closeQuietly(output);
			IOUtils.closeQuietly(input);
 
		}

 
		this.setCanExportSpecifiedInfo(true);

	}
	
	public void exportAllAccessRoomInfo(File destFile,int sqlFileId) throws ExportFileException {
		if(! isCanExportSpecifiedInfo()){
			throw new ExportFileException("有人在使用指定接入间信息导出功能请等待...");
		}
		 
		this.setCanExportSpecifiedInfo(false);

		File file = ResourceLoadingHelper.loadFileFromClasspath("config/sql/export_all_access_room_"+sqlFileId+".sql");
		if(file == null){
			throw new ExportFileException("找不到模板文件：config/sql/export_all_access_room_"+sqlFileId+".sql" );
		}
		//make bcp and run it
		BcpBuilder bb = new BcpBuilder();
 
		bb.setAuth(SystemEnvironment.getProperty("db.server"), SystemEnvironment.getProperty("db.username"),SystemEnvironment.getProperty("db.password"));
		File tmpFile = new File(destFile.getAbsolutePath()+".tmp");
		FileHelper.createFile(tmpFile);
		bb.setFilePath(tmpFile.getAbsolutePath());
		try {
			String sql = FileUtils.readFileToString(file, "utf-8");
			bb.setSql(sql);
		} catch (IOException e) {
			throw new ExportFileException("无法读取模板文件：config/sql/export_all_access_room_"+sqlFileId+".sql" ,e);
		}

		String queryout = bb.queryout();
//		log.debug("bcp: \n" + queryout);


		ProcessHelper.call(new File("."), ProcessHelper.toCommand(queryout));
		
		
		//添加表情
		String[] titles = bb.getTitlesFromSql();// "j_id,机房,设备号,槽号,端口号,板卡类型,外层VLAN,内层VLAN,横列端口,u_id,产品号码,客户名称,账号,地址\r\n" ;

		if(titles == null){
			throw new ExportFileException("没有在SQL中找到标题，必须在注释行中,第一个出现优先,格式：--<TITLE>xxxx,xxxx,xxx</TITLE>");
		}
		
		OutputStream output = null;
		InputStream input = null;

		try {
//			FileUtils.writeStringToFile(destFile, title,"utf-8");
			input = new FileInputStream(tmpFile);
			output = new FileOutputStream(destFile);
			IOUtils.write(StringUtils.join(titles ,bb.getTerminal()), output);
			IOUtils.write(IOUtils.LINE_SEPARATOR_WINDOWS, output);
			
			IOUtils.copyLarge(input, output);
			output.flush();
		} catch (Exception e) {
			throw new ExportFileException("导出失败",e );
		} finally{
			IOUtils.closeQuietly(output);
			IOUtils.closeQuietly(input);
 
		}

 
		this.setCanExportSpecifiedInfo(true);

	}

	/**
	 * 前提条件：端口坏的不能换@see unicom.web.ChangePortServlet
	 * 如果是FTTH,SN也要迁移，相同光猫的用户也迁移《相当于割接》？
	 * @param u_id
	 * @param new_j_id
	 * @param makeFault
	 * 是否置坏
	 * @param remark
	 * @param account
	 * @throws Exception
	 */
	public void changePort(String u_id, String new_j_id, String new_ont_id, boolean makeFault, String remark, String account) throws Exception {
		 Map userInfo = this.findByKey(u_id);//old channel
		 if(userInfo == null){
			 throw new Exception("非法单号(u_id): " + u_id);
		 }
		 Integer old_j_id = (Integer) userInfo.get("j_id");

//		 if(old_j_id.toString().equals(new_j_id)){
//			 throw new Exception("端口未变: 产品号码=" + userInfo.get("p_id") + ", U_ID=" + u_id);
//		 }
		 
		 
		 //禁止占用,servlet已经判断
		 Map override_user_info = this.findByJid(new_j_id, new_ont_id);
		 if(override_user_info != null){
			 throw new Exception("端口已经占用: 产品号码=" + override_user_info.get("p_id") + ", U_ID=" + override_user_info.get("u_id")+ ", J_ID=" + override_user_info.get("j_id")+ ", ONT端口=" + override_user_info.get("ont_id"));
		 }
		  
		 
		 //更新user_info的j_id关联（即重新分派端口）
		 String update1 = "update user_info  set j_id=?,ont_id=? where u_id=? ";
		//更新jx_info的旧端口信息
		 String update2 = null; 
		 if(makeFault){//置坏
			 String used_remark = account +  DateHelper.format(new Date(), "[yyyy-MM-dd HH:mm]")+ "[置坏]" + ObjectUtils.toString(remark); 
			 update2 = "update jx_info set used=null,u_id=null,used_remark='"+used_remark+"' where j_id=? ";
			 if(remark == null){
				 remark = "[原机房端口置坏]";
			 }else{
//				 remark = "[原端口置坏,原J_ID=" + old_j_id + "]" + remark;
				 remark = "[原机房端口置坏]" + remark;
			 }
		 }else{
			 update2 = "update jx_info set used=0,u_id=null where j_id=? ";
		 }
		//更新jx_info的新端口信息,转移新的SN端口
		 String update3 = "update jx_info set used=1,u_id=?,sn=? where j_id=? ";
		
		 //记录端口更新历史(port_change_hist)
		 String update4 = "insert into port_change_hist( p_id, u_id ,username, old_j_id, new_j_id, override_u_id, override_p_id, old_ont_id, new_ont_id, remark, change_time, changer) " +
		 		"values(?,?,?,?,?,?,?,?,?,?,?,?)";
		 
		 
		this.baseDao.batchUpdateASql(update1, new Object[]{new_j_id, new_ont_id, u_id});
		
		this.baseDao.batchUpdateASql(update2, new Object[]{old_j_id});
		
		this.baseDao.batchUpdateASql(update3, new Object[]{u_id,userInfo.get("sn"),new_j_id});
		
//		Map map = this.findByKey(u_id);//why reread?
		String old_ont_id = (String)userInfo.get("ont_id");
		
		this.baseDao.batchUpdateASql(update4, new Object[]{(String)userInfo.get("p_id"), u_id, (String)userInfo.get("username"), old_j_id, new_j_id,null,null, old_ont_id,new_ont_id, remark, new Date(), account});

		
//		this.updateUsedOntPorts(old_j_id.toString());
//		this.updateUsedOntPorts(new_j_id);
  
 		if(StringUtils.isNotBlank(old_ont_id)){
 			//updateUsedOntPorts已经包含
			this.ontService.updateStatus(old_j_id, old_ont_id, 0);
 		}
 		
 		if(StringUtils.isNotBlank(new_ont_id)){
			this.ontService.updateStatus(new Integer(new_j_id), new_ont_id, 1);
 		}
		
		 
	}

	
	public Map findByPid(String p_id) {
		if(StringUtils.isBlank(p_id)){
			return null;
		}
		Object[] objects = new Object[]{p_id.trim()};
		String sqlFetch = "select u.U_id as u_id, u.j_id as j_id, username , p_id,address, user_no, password, area,tel,begin_date,jx,sbh,type,board_type,slot,sb_port,mdf_port,mac,open_date,finish_date,state,branch,remark ,inner_vlan,outer_vlan,ip,sn,ont_id,ont_ports,used_ont_ports" +
				" from  user_info u left join jx_info j on j.j_id=u.j_id where u.p_id=? ";
		Map map = null;
		try {
			map = this.baseDao.findUnique(sqlFetch, objects);
		} catch (Exception e) {
			log.warn(e.getMessage());
		}
		return map;
	}
	
	public Map findByUserNo(String user_no) {
		if(StringUtils.isBlank(user_no)){
			return null;
		}
		Object[] objects = new Object[]{user_no.trim()};
		String sqlFetch = "select u.U_id as u_id, u.j_id as j_id, username , p_id,address, user_no, password, area,tel,begin_date,jx,sbh,type,board_type,slot,sb_port,mdf_port,mac,open_date,finish_date,state,branch,remark ,inner_vlan,outer_vlan,ip,sn" +
				" from  user_info u left join jx_info j on j.j_id=u.j_id where u.user_no=? ";
		Map map = null;
		try {
			map = this.baseDao.findUnique(sqlFetch, objects);
		} catch (Exception e) {
			log.warn(e.getMessage());
		}
		
		return map;
	}
	
	/**
	 * 包含通路
	 * @param u_id
	 * @return
	 */
	public Map findByKey(String u_id) {
		Object[] objects = new Object[]{u_id};
		String sqlFetch = "select u.U_id as u_id, u.j_id as j_id, ont_id, username , p_id,address, user_no, password, area,tel,begin_date,jx,sbh,type,board_type,slot,sb_port,mdf_port,used,mac,open_date,finish_date,state,branch,remark ,inner_vlan,outer_vlan,ip,sn,ont_ports,used_ont_ports" +
				" from  user_info u left join jx_info j on j.j_id=u.j_id where u.u_id=? ";
		Map map = null;
		try {
			map = this.baseDao.findUnique(sqlFetch, objects);
			MapHelper.trimAllStringValue(map); 
		} catch (Exception e) {
			log.warn(e.getMessage());
		}
		
		return map;
	}

	/**
	 * 
	 * @param j_id
	 * @param ont_id
	 * FTTH必须提供
	 * @return
	 */
	public Map findByJid(String j_id, String ont_id) {
		Object[] objects = new Object[]{j_id};
		
		String sqlFetch = "select u.U_id as u_id, u.j_id as j_id, username , p_id,address, user_no, password, area,tel,begin_date,jx,sbh,type,board_type,slot,sb_port,mdf_port,mac,open_date,finish_date,state,branch,remark ,inner_vlan,outer_vlan,ip,sn, ont_id" +
				" from  user_info u left join jx_info j on j.j_id=u.j_id where u.j_id=? ";
		
		if(StringUtils.isNotBlank(ont_id)){
			objects = new Object[]{j_id,ont_id};
			sqlFetch += " and u.ont_id=?";
		}
		
		Map map = null;
		try {
			map = this.baseDao.findUnique(sqlFetch, objects);
		} catch (Exception e) {
			log.warn(e.getMessage());
		}
		return map;
	}

	/**
	 * 0-拆机: 删除所有关联，并将用户信息转移到user_info_log表中
	 * 1-退单: 删除所有关联，并删除用户信息.
	 */
	public void deleteUserInfo(String u_id, String deleteType) throws Exception{
		if(StringUtils.isBlank(u_id)){
			return ;
		}
		
		Map userInfo = this.findByKey(u_id);
		if(userInfo == null){
			log.warn("U_ID不存在：" + userInfo);
			return;
		}
		String state = (String) userInfo.get("state");
		if("0".equals(deleteType) && !"预拆机".equals(state)){
			throw new Exception("不能拆机，用户状态不是预拆机：" + userInfo.get("p_id"));
		}
		
		String updateSql = null;
		if("0".equals(deleteType) ){
			Object[] args = new Object[]{new Date(),u_id};
	 		updateSql = "update user_info set state ='拆机竣工', finish_del=?" + 
					" where u_id=?";
	 		this.baseDao.batchUpdateASql(updateSql, args);
		}
 	
		//删除关联。。。
 			
 		String ont_id = (String) userInfo.get("ont_id");
 		Integer j_id = (Integer) userInfo.get("j_id");
 		String type = (String) userInfo.get("type");
 		if("FTTH".equals(type)){//更新jx_info.used_ont_ports， ont.used
 			updateSql = "update ont set used =0 " + 
	 					" where j_id=? and ont_id=?";
 			this.baseDao.batchUpdateASql(updateSql, new Object[]{j_id,ont_id});
 			/**
 			 * 自动计算ONT端口占用数
update jx_info 
set used_ont_ports =(select sum(used)
from ont o 
where o.j_id=jx_info.j_id
and jx_info.type='FTTH')
where  jx_info.type='FTTH'
 			 */
// 			updateSql = "update jx_info set used_ont_ports =(select sum(used) from ont o where o.j_id=jx_info.j_id and jx_info.type='FTTH') where  jx_info.type='FTTH' and jx_info.j_id=? " ; 
// 			this.baseDao.batchUpdateASql(updateSql, new Object[]{j_id});
 			
 			this.updateUsedOntPorts(j_id.toString());//更新占用端口数
 		}
 		
 		//更新端口关联 
	 	updateSql = "update jx_info set used =0 , u_id=null " + 
	 					" where j_id=? and (used_ont_ports is null or used_ont_ports =0 )";
	 	this.baseDao.batchUpdateASql(updateSql, new Object[]{j_id});
 		
	 	//如果没有占有，清空SN
	 	updateSql = "update jx_info set sn=null " + 
					" where j_id=? and used_ont_ports =0";
	 	this.baseDao.batchUpdateASql(updateSql, new Object[]{j_id});
	 	 
	 	if("0".equals(deleteType) ){//拆机保留用户信息
	 		updateSql = "insert into userinfo_log " + 
					" select * from user_info where u_id=?";
	 		this.baseDao.batchUpdateASql(updateSql, new Object[]{u_id});
	
	 	}
		
		updateSql = "delete from user_info where u_id=?";
		this.baseDao.batchUpdateASql(updateSql, new Object[]{u_id});
		  
//		this.baseDao.batchUpdateASql(updateSql, args);
		
	}

	/**
	 * 
	 * @param userInfo
	 * @return
	 * 问题新增用户怎么获得u_id?
	 * @throws Exception
	 */
	public String save(Map userInfo) throws Exception {
		MapHelper.trimAllStringValue(userInfo);
		
		String u_id = (String) userInfo.remove("u_id");
		List<Object> argList = new ArrayList<Object>();
		StringBuilder sqlUpdate = null;
		if(StringUtils.isBlank(u_id)){//create new one
			String p_id = (String) userInfo.get("p_id");
			Map userInfo2 = this.findByPid(p_id);
			if(userInfo2 != null){
				throw new Exception("产品号码已经存在：" + p_id);
			}
			String user_no = (String) userInfo.get("user_no");
			userInfo2 = this.findByUserNo(user_no);
			if(userInfo2 != null){
				throw new Exception("帐号已经存在：" + user_no);
			}
			 
			sqlUpdate = new StringBuilder("insert into user_info ( " );
			for (Object key : userInfo.keySet()) {
				sqlUpdate.append(key + ",");
				argList.add(userInfo.get(key));
			} 
			sqlUpdate.deleteCharAt(sqlUpdate.length()-1);//remove last ,
			sqlUpdate.append(") values (");
			for (int i = 0; i < userInfo.size(); i++) {
				sqlUpdate.append("?,");
			}
			sqlUpdate.deleteCharAt(sqlUpdate.length()-1);//remove last ,

			sqlUpdate.append(")");
			
 		}else{//update
  			
			sqlUpdate = new StringBuilder("update user_info set " );
			for (Object key : userInfo.keySet()) {
				Object value = userInfo.get(key);
				sqlUpdate.append(key + "=?,");
				argList.add(value);
			}
			
			sqlUpdate.deleteCharAt(sqlUpdate.length()-1);//remove last ,
			
			sqlUpdate.append( " where u_id=?" );
			argList.add(u_id);
			 
			
		}
 		
		this.baseDao.update(sqlUpdate.toString() , argList.toArray());
		 
		
		String updateJx = "update jx_info set used=1 where j_id=?";
		this.baseDao.update(updateJx.toString() , new Object[]{userInfo.get("j_id")});
		
		String ont_id = (String) userInfo.get("ont_id");
		if(StringUtils.isNotBlank(ont_id)){
			//已经包含updateUsedOntPorts
			this.ontService.updateStatus((Integer)userInfo.get("j_id"), ont_id, 1);
//			this.updateUsedOntPorts(userInfo.get("j_id").toString());
		}
		
			
		log.info("成功保存用户信息(同时更新端口占用信息)");
		
		return u_id;//

	}

	/**
	 * 自动计算ONT端口占用数
update jx_info 
set used_ont_ports =(select sum(used)
from ont o 
where o.j_id=jx_info.j_id
and jx_info.type='FTTH')
where  jx_info.type='FTTH'
	 */
	public void updateUsedOntPorts(String j_id){
		
		String updateJx = "update jx_info set used_ont_ports =(select sum(used) from ont o where o.j_id=jx_info.j_id and jx_info.type like 'FTTH%') where  jx_info.type like 'FTTH%' and jx_info.j_id=? " ; 
			
		this.baseDao.batchUpdateASql(updateJx, new Object[]{j_id});
	}

	public void updateAField(Object u_id, String fieldName, Object fieldValue) {
		Object[] args = new Object[]{fieldValue,u_id};
 		String updateSql = "update user_info set "+fieldName+" =? " + 
				" where u_id=?";
 		this.baseDao.update(updateSql, args);
	}
	
	 
}
