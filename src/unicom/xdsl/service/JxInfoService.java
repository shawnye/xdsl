package unicom.xdsl.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.output.FileWriterWithEncoding;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import unicom.common.Constants;
import unicom.common.DateHelper;
import unicom.common.MapValueGetter;
import unicom.common.Page;
import unicom.common.SearchConditions;
import unicom.common.SearchKey;
import unicom.common.port.ExportConfig;
import unicom.common.port.ExportFileException;
import unicom.common.port.FieldConverter;
import unicom.common.port.FileExporter;
import unicom.common.port.ImportConfig;
import unicom.common.port.ImportFileException;
import unicom.dao.BaseDaoInterface;
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

/**
 * 机房信息(未必有用户信息,未占用)
 *
 * @author yexy6
 *
 */
@SuppressWarnings("rawtypes")
 @Service(value = "jxInfoService")
public class JxInfoService extends AbstractService implements FileExporter{
	private BaseDaoInterface baseDao;

	@Autowired
	public void setBaseDao(BaseDaoInterface baseDao) {
		this.baseDao = baseDao;
	}
	
	private OntService ontService;
	 
	@Autowired
	public void setOntService(OntService ontService) {
		this.ontService = ontService;
	}

	public Page<Map> listAsMap(SearchConditions searchCondition, int pageNo,
			int pageSize) {

		Map<SearchKey, Object> conditions = searchCondition.getConditions();
//		int size = conditions.size();
//		Object[] args = new Object[size];
		List<Object> args2 = new ArrayList<Object>();
		
		String sqlFetch = "select j.[j_id] j_id, [jx], [sbh], [type], [slot], [sb_port], [mdf_port], [used], [board_type], [flag], [bb], [ip], [inner_vlan], [outer_vlan], [used_user_no], [same_user_no], [mask], [olt], [used_remark], [jremark], [ont_ports], [used_ont_ports], j.[batch_num] jx_batch_num,sn," +
				"u.[U_id] u_id, [username], [p_id], [address], [user_no], [password], [area], [tel], [begin_date], [Mac], [open_date], [finish_date], [state], [remark], [branch], [back_date], [del_date], [conf_del], [finish_del], [old_p_id], [ont_id] ,u.[batch_num] ui_batch_num , datediff(d,begin_date,getdate()) elapse_days " +
				"from jx_info j left join user_info u on j.j_id=u.j_id where (mask is null or mask = '') ";
		String sqlCount = "select count(*) from jx_info j left join user_info u on j.j_id=u.j_id where (mask is null or mask = '') ";
		String jxCount = "select j.j_id from jx_info j left join user_info u on j.j_id=u.j_id where (mask is null or mask = '') ";

		int i = 0;
		for (SearchKey searchKey : conditions.keySet()) {
//			args[i++] = conditions.get(searchKey);
			 
			args2.add(conditions.get(searchKey));
			i = args2.size();

			String theta = searchKey.getTheta();
			String field = searchKey.getField();
			if(field.equalsIgnoreCase("j_id")){
				field = "j.j_id";//避免出现错误：列名 'J_id' 不明确。
			}
			if(field.equalsIgnoreCase("u_id")){
				field = "u.u_id";//避免出现错误：列名 'u_id' 不明确。
			}
			if(field.equalsIgnoreCase("jx_batch_num")){
				field = "j.batch_num";
			}
			if(field.equalsIgnoreCase("ui_batch_num")){
				field = "u.batch_num";
			}
			
//			if(field.equalsIgnoreCase("remark")){
//				field = "j.remark";//避免出现错误：列名 'remark' 不明确。
//			}
//			
			String v = args2.get(i-1).toString().trim();
			if(v.matches("\\[.+\\]")){//支持is null 之类的
				sqlFetch += " and " + field + " " + v.replace("[", "").replace("]", "");
				sqlCount += " and " + field + " " + v.replace("[", "").replace("]", "");
				jxCount += " and " + field + " " + v.replace("[", "").replace("]", "");
				if(i > 0){
					args2.remove(i-1);
				}
				
				continue;
			}
			
			sqlFetch += " and " + field + " " + theta + " ? ";
			sqlCount += " and " + field + " " + theta + " ? ";
			jxCount += " and " + field + " " + theta + " ? ";
			
			if (theta.equals("like")) {
//				args[i - 1] = "%" + args[i - 1].toString() + "%";
				args2.set(i-1, "%" + v + "%");
			}
		}

		Page<Map> page = this.baseDao.listAsMap(sqlCount, sqlFetch, args2.toArray(), pageNo,
				pageSize);
		
		jxCount = "select count(*) c from (" + jxCount + " group by j.j_id) a ";
		Map count = this.baseDao.findUnique(jxCount, args2.toArray());
		Object object = count.get("c");
		
		page.addAdditionInfo("jxCount", object);
		
		return page;
		
	}

	public List<Map> listAllAsMap(SearchConditions searchCondition) {
		Map<SearchKey, Object> conditions = searchCondition.getConditions();
//		int size = conditions.size();
//		Object[] args = new Object[size];
		List<Object> args2 = new ArrayList<Object>();

		String sqlFetch = "select j.[j_id] j_id, [jx], [sbh], [type], [slot], [sb_port], [mdf_port], [used], [board_type], [flag], [bb], [ip], [inner_vlan], [outer_vlan], [used_user_no], [same_user_no], [mask], [olt], [used_remark], [jremark], [ont_ports], [used_ont_ports], j.[batch_num] jx_batch_num,sn, " +
				"u.[U_id] u_id, [username], [p_id], [address], [user_no], [password], [area], [tel], [begin_date], [Mac], [open_date], [finish_date], [state], [remark], [branch], [back_date], [del_date], [conf_del], [finish_del], [old_p_id], [ont_id], u.[batch_num] ui_batch_num " +
				"from jx_info j left join user_info u on j.j_id=u.j_id where (mask is null or mask = '') ";
		int i=0;
		for (SearchKey searchKey : conditions.keySet()) {
//			args[i++] = conditions.get(searchKey);
		 
			args2.add(conditions.get(searchKey));
			i = args2.size();
			
			String theta = searchKey.getTheta();
			String field = searchKey.getField();

			if(field.equalsIgnoreCase("j_id")){
				field = "j.j_id";//避免出现错误：列名 'J_id' 不明确。
			}
			if(field.equalsIgnoreCase("u_id")){
				field = "u.u_id";//避免出现错误：列名 'u_id' 不明确。
			}
			if(field.equalsIgnoreCase("remark")){
				field = "j.remark";//避免出现错误：列名 'remark' 不明确。
			}
			
			if(field.equalsIgnoreCase("jx_batch_num")){
				field = "j.batch_num";
			}
			if(field.equalsIgnoreCase("ui_batch_num")){
				field = "u.batch_num";
			}
			
			String v = args2.get(i-1).toString().trim();
			if(v.matches("\\[.+\\]")){//支持is null 之类的
				sqlFetch += " and " + field + " " + v.replace("[", "").replace("]", "");
 				
				if(i > 0){
					args2.remove(i-1);
				}
				
				continue;
			}
			
			sqlFetch += " and " + field + " " + theta + " ? ";
 
			if (theta.equals("like")) {
//				args[i - 1] = "%" + args[i - 1].toString() + "%";
				args2.set(i-1, "%" + v + "%");
			}
			 
		}

		return this.baseDao.listAllAsMap( sqlFetch , args2.toArray() );
	}

	public void exportFile(File destFile, ExportConfig exportConfig)
			throws ExportFileException {
		String format = exportConfig.getFormat();

		long start = System.currentTimeMillis();
		if ("csv".equalsIgnoreCase(format)) {
			this.exportCsvFile(destFile, exportConfig);
		} else if ("excel".equalsIgnoreCase(format)) {
			this.exportExcelFile(destFile, exportConfig);
		}
		log.debug("导出耗时：" + ((System.currentTimeMillis() - start) / 1000)
				+ " 秒");
	}



	private void exportCsvFile(File destFile, ExportConfig exportConfig) throws ExportFileException{
		CSVWriter csvWriter = null;
		try {
			csvWriter = new CSVWriter(new FileWriterWithEncoding(destFile, "gbk"));

			List<Map> list = this.listAllAsMap(exportConfig.getSearchCondition());

			String[] line = null;
			line = new String[]{
					"J_ID",
					"区域",
					"机房",
					"IP地址",

					"设备号",
					"AD类型",
					"板类型",
					"槽号",
					"端口号",
					"MDF横列",
					"OLT名称",
					"内层VLAN",
					"外层VLAN",
					
					"ONT端口总数",
					"ONT端口占用数",
					"U_ID",
					
					"帐号",//(OSS)
//					"帐号(省公司)",
//					"帐号一致",

					"是否占用",
					"置坏说明",
					
					"客户名称",
					"产品号码",
					"旧产品号码",
					"装机地址",
					"FTTH SN",
					"ONT端口",

					"录入时间",
					"数据配置时间",
					"竣工时间",
					"状态",
					"是否屏蔽",


			};
			csvWriter.writeNext(line );

			MapValueGetter mvg = null;
			for (Map map : list) {
				mvg = new MapValueGetter(map);

//				String same = "";
//
//				Integer a = mvg.getAsInteger("same_user_no") ;
//				if(a != null){
//					if(a == 1){
//						same = "是";
//					}else{
//						same = "否";
//					}
//				}
				
				String used = mvg.getAsString("used");
				if(used == null){
					used="已坏";
				}

				line = new String[]{
						mvg.getAsString("j_id"),
						mvg.getAsTrimedString("area"),
						mvg.getAsTrimedString("jx"),
						mvg.getAsTrimedString("ip"),

						mvg.getAsTrimedString("sbh"),
						mvg.getAsTrimedString("type"),
						mvg.getAsTrimedString("board_type"),
						mvg.getAsTrimedString("slot"),
						mvg.getAsTrimedString("sb_port"),
						mvg.getAsTrimedString("mdf_port"),

						mvg.getAsTrimedString("olt"),
						mvg.getAsTrimedString("inner_vlan"),
						mvg.getAsTrimedString("outer_vlan"),
						mvg.getAsTrimedString("ont_ports"),
						mvg.getAsTrimedString("used_ont_ports"),
						
						mvg.getAsTrimedString("u_id"),
						mvg.getAsTrimedString("user_no"),
//						mvg.getAsTrimedString("used_user_no"),
//						same,

						used,
						mvg.getAsTrimedString("used_remark"),

						mvg.getAsTrimedString("username"),
						mvg.getAsTrimedString("p_id")!=null ? "'" + mvg.getAsTrimedString("p_id") : null,
						mvg.getAsTrimedString("old_p_id")!=null ? "'" + mvg.getAsTrimedString("old_p_id") : null,

						mvg.getAsTrimedString("address"),
 						mvg.getAsTrimedString("sn")!=null ? "'" + mvg.getAsTrimedString("sn") : null,
 						mvg.getAsTrimedString("ont_id"),

						DateHelper.format(mvg.getAsDate("begin_date"), "yyyy-MM-dd"),
						mvg.getAsTrimedString("open_date"),
						DateHelper.format(mvg.getAsDate("finish_date"), "yyyy-MM-dd"),
						mvg.getAsTrimedString("state"),
						mvg.getAsTrimedString("mask"),
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

	private void exportExcelFile(File destFile, ExportConfig exportConfig) throws ExportFileException{
		// TODO Auto-generated method stub

	}

	/**
	 * 根据vlan查找jid
	 * @param jx
	 * @param outer_vlan
	 * @param inner_vlan
	 * @return
	 */
	public Integer findJid2(String jx, String outer_vlan, String inner_vlan) {
		String sqlFetch = "select j.j_id from jx_info j where j.jx=? and outer_vlan=? and inner_vlan=? ";
		Object[] objects = new Object[]{jx,outer_vlan,  inner_vlan};
		Map map = null;
		
		try {
			map = this.baseDao.findUnique(sqlFetch, objects);
		} catch (Exception e) {
			log.warn(e.getMessage());
		}
		if(map != null){
			return (Integer) map.get("j_id");
		} 
 		return null;
	}
	
	public Map find(String jx, String mdfPort, Boolean used) {
		String mdfPort2 = mdfPort.replaceFirst("\\-0+", "-");//0101-001 ==> 0101-1
		Object[] objects = new Object[]{jx,mdfPort,mdfPort2};
		String sqlFetch = "select j.j_id from jx_info j where j.jx=? and j.mdf_port in (?,?)";
		if(used != null){
			objects = ArrayUtils.add(objects, used?1:0); 
			if(used){
				sqlFetch += " and (j.used=? )";//FIXME 即使占用也未必占满ONT, 语句上无法支持
			}else{
 				sqlFetch += " and (j.used=? or (used = 1 and type like 'FTTH%' and used_ont_ports < ont_ports)) ";//空闲
			}
			
		}
		
		Map map = null;
		try {
			map = this.baseDao.findUnique(sqlFetch, objects);
		} catch (Exception e) {
			log.error("JxInfoService.find ERROR", e);
		}
		return map;
	}

	public Integer findJid(String jx, String mdfPort, Boolean used) {
		
		Map map = this.find(jx, mdfPort, used);
		if(map == null){
			return null;
		}
		return (Integer) map.get("j_id");
	}

	public Map find3(java.lang.String jx, java.lang.String sbh,
			java.lang.String slot, java.lang.String sb_port, Boolean used) {

		Object[] objects = new Object[]{jx,sbh,slot,sb_port};
		String sqlFetch = "select j.j_id from jx_info j where j.jx=? and j.sbh=? and j.slot=? and j.sb_port=?";
		if(used != null){
			objects = ArrayUtils.add(objects, used?1:0); 
			if(used){
				sqlFetch += " and (j.used=? )";//FIXME 即使占用也未必占满ONT, 语句上无法支持
			}else{
 				sqlFetch += " and (j.used=? or (used = 1 and type like 'FTTH%' and used_ont_ports < ont_ports)) ";//空闲
			}
			
		}
		
		Map map = null;
		try {
			map = this.baseDao.findUnique(sqlFetch, objects);
		} catch (Exception e) {
			log.error("JxInfoService.find ERROR", e);
		}
		return map;
	}
	
	public Integer findJid3(String jx, java.lang.String sbh,
			java.lang.String slot, java.lang.String sb_port, Boolean used) {
		
		Map map = this.find3(jx, sbh,slot,sb_port, used);
		if(map == null){
			return null;
		}
		return (Integer) map.get("j_id");
	}
	
	public Map findByKey(String j_id) {
		Object[] objects = new Object[]{j_id};
		String sqlFetch = "select * from jx_info j where j.j_id=? and (mask is null or mask = '') ";
		Map map = null;
		try {
			map = this.baseDao.findUnique(sqlFetch, objects);
		} catch (Exception e) {
			log.warn(e.getMessage());
		}
		return map;
	}
	
	/**
	 * 前提条件，确保j_id对应u_id为坏.
	 * @param j_id
	 * @param used
	 * @param remark
	 * @param account
	 */
	public void markUnused(String j_id, String remark, String account){
		 String used_remark = account +  DateHelper.format(new Date(), "[yyyy-MM-dd HH:mm]")+ "[置未占用]" + ObjectUtils.toString(remark); 

		 String update = "update jx_info set used=0,u_id=null,used_remark='"+used_remark+"' where j_id=? ";
 
		 this.baseDao.update(update, new Object[]{j_id});

	}
	
	
	/**
	 * 前提条件，确保j_id对应u_id为空.否则使用更新端口置坏
	 * @param j_id
	 * @param remark
	 * @param account
	 */
	public void markFault(String j_id, String remark, String account){
		 String used_remark = account + DateHelper.format(new Date(), "[yyyy-MM-dd HH:mm]")+ "[置坏]" + ObjectUtils.toString(remark); 

		 String update = "update jx_info set used=null,u_id=null,used_remark='"+used_remark+"' where j_id=? ";

		 this.baseDao.update(update, new Object[]{j_id});

	}
	
	/**
	 * delete jxInfo and ftth ports
	 * @param jids
	 */
	public void deleteByJids(Integer[] jids, boolean ignoreUsed){
		String sql = "delete from jx_info where j_id=? " ;
		if(!ignoreUsed){
			sql += " and used <> 1";
		}
		List<Object[]> args = new ArrayList<Object[]>(jids.length);

		for (int i = 0; i < jids.length; i++) {
			args.add( new Object[]{jids[i] } ); 
		}
		
		/*int[] results =*/ this.baseDao.batchUpdateWithSameSql(sql.toString(), args);
		
		sql = "delete from ont where j_id=? " ;
		if(!ignoreUsed){
			sql += " and 1 <>(select j.used from jx_info j where j.j_id=ont.j_id)";
		}
		/*results = */this.baseDao.batchUpdateWithSameSql(sql.toString(), args);
	}

	/**
	 * 新增端口
	 * @param file
	 * @param importConfig
	 * @param msg
	 * @return
	 * @throws ImportFileException
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
		Collection<String> fields = importConfig.getImportFields();//导入字段
		if(fields.size() == 0){
			msg.append("配置有误：没有对应数据库插入字段\n");
			return false;
		}
		Collection<String> fields2 = importConfig.getFields();//all field，含非导入字段，这里是@j_id
		
		Integer updateType = importConfig.getUpdateType();
		StringBuffer sqlUpdate = new StringBuffer();
		if(new Integer(0).equals(updateType)){//纯创建
			String[] questions = new String[fields.size()];
			Arrays.fill(questions, "?");

			sqlUpdate.append("insert into ");
			sqlUpdate.append(importConfig.getTable());
			sqlUpdate.append(" (");
			sqlUpdate.append(StringUtils.join(fields,","));
			sqlUpdate.append(") values (");
			sqlUpdate.append(StringUtils.join(questions,","));
			sqlUpdate.append(") ");
		}else if(new Integer(1).equals(updateType)){ //纯更新
			sqlUpdate.append("update ");
			sqlUpdate.append(importConfig.getTable());
			sqlUpdate.append(" set ");
			sqlUpdate.append(StringUtils.join(fields,"=?,"));
			sqlUpdate.append("=? ");//last
			sqlUpdate.append(" where ");
			sqlUpdate.append(" j_id=? ");
		}else{
			//不支持其他模式
			throw new ImportFileException("导入类型仅支持纯新建或纯更新：" + updateType);
		}
		
		 
	 

		List<Object[]> args = new ArrayList<Object[]>(Constants.UPDATE_BATCH_SIZE);

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
					int[] is = this.baseDao.batchUpdateWithSameSql(sqlUpdate.toString(), args);
//					this.baseDao.batchUpdate(new String[]{update});
					int j = 0;
					for (int i = 0; i < is.length; i++) {
						j += is[i];
					}

					updated += j;

					args.clear();
				}

				//-----------------------
				Object[] objs = new Object[fields.size()];
				Object[] objs2 = new Object[fields2.size()];//all
				int i=0,j=0;//i--相对对于obj2, j--相对对于obj, 长度是不同的！
				int jx_idx =-1;
//				int sbh_idx =-1;
//				int slot_idx =-1;
//				int sb_port_idx =-1;
				int inner_vlan_idx = -1;
				int outer_vlan_idx = -1;
//				boolean isFtth =false;
//				int ontPorts = 0;
//				int j_id_idx = -1;
				
 				for (String field : fields2) { 
					Integer order = importConfig.getFieldIndex(field);
					if(order != null ){
						if( order < nextLine.length ){
							objs2[i] = nextLine[order]!=null ? nextLine[order].trim():null; 
						}else{
							objs2[i] = null;
						}
						
						if(! field.startsWith("@")){ 
							if("jx".equals(field)){
								jx_idx = j;
							}
							else if("inner_vlan".equals(field)){
								inner_vlan_idx = j;
							}else if("outer_vlan".equals(field)){
								outer_vlan_idx = j;
							}
//							else if("type".equals(field)){
//								 if(objs2[i] != null && objs2[i].toString().equalsIgnoreCase("ftth")){
//									 isFtth = true;
//								 }
//							}else if("ont_ports".equals(field) && objs2[i] != null){
//								ontPorts =  new Integer(objs2[i].toString().trim());
//							}	
							objs[j] = objs2[i]; 
							j++;

						} else{
//							if("@j_id".equals(field)){
//								j_id_idx = i;
//							}
						}
					}
					
					FieldConverter fieldConverter = importConfig.getFieldConverterByField(field);
					if(fieldConverter != null){
						objs2[i] = fieldConverter.convertFrom(objs2); 
						if(! field.startsWith("@")){ 
							objs[j-1] = objs2[i]; 
						} 
					}
					i++;
 
				}//for fields
 				
 				if(new Integer(0).equals(updateType)){//纯创建
 				// 看看是否已经存在？
 	 				Integer j_id = this.findJid2((String)objs[jx_idx],(String)objs[outer_vlan_idx],(String)objs[inner_vlan_idx]);
 					if(j_id != null){
 						throw new Exception("导入端口已经存在：J_ID=" + j_id + "，机房(jx)=" + objs[jx_idx] + "，外层VLAN(outer_vlan/slan)=" + objs[outer_vlan_idx]+"，内存VLAN(inner_vlan/clan)=" +objs[inner_vlan_idx] );
 					}
// 					if(j_id_idx != -1 && objs[j_id_idx] == null){//无法预分配端口,忽略
// 						msg.append( "[行" + line + "]根据VLAN端口无法查找到/查到多个机房端口(J_ID), 忽略:"  + objs[0]);
// 						msg.append("\n");
// 						continue;
// 					}
 					//FIXME 如果是ftth在ont表新增ont端口表,从1~ont_ports ,J_ID is NULL!
// 					if(isFtth){
// 						this.ontService.createPortsByJid(j_id, 1,  ontPorts);
// 					}
 					args.add(objs);
 				}else if(new Integer(1).equals(updateType)){//纯更新
 					String j_id = nextLine[nextLine.length-1];//last 
 					if(StringUtils.isNotBlank(j_id)){
 						j_id = j_id.trim();
 	 					Map jxInfo = this.findByKey(j_id);
 	 					if(jxInfo == null){
 	 						throw new Exception("指定机房端口(J_ID)不存在：J_ID=" + j_id ); 
 	 					}
 	 					
 	 					args.add(ArrayUtils.add(objs, j_id)); 
  	 					
 	 				}else{
 	 					
	 						throw new Exception("机房端口(J_ID)为空，不能更新: 行 " + line); 

 	 				}
 				}
  				
 
 				

				
			}//while

			if(args.size() > 0){
				int[] is = this.baseDao.batchUpdateWithSameSql(sqlUpdate.toString(), args);
//				this.baseDao.batchUpdate(new String[]{update});
				int j = 0;
				for (int i = 0; i < is.length; i++) {
					j += is[i];
				}

				updated += j;

				args.clear();
			}

			if(new Integer(0).equals(updateType)){//纯创建
				this.ontService.createPortsByBatchNum(defatulBatchNum);//生成ont 
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

		
		//更新临时表：update_area_jx,update_jx_sbh
		this.baseDao.callWithoutParameter("dbo.update_area_jx");
		this.baseDao.callWithoutParameter("dbo.update_jx_sbh");
		log.debug("成功更新临时表：update_area_jx, update_jx_sbh");

		msg.append("成功读入行数:" + line);
		msg.append("，成功导入行数:" + updated);
		msg.append("，导入批次:" + defatulBatchNum);
		msg.append("\n");
		
		return true;
	}

	/**
	 * 仅仅返回j_id, ont_ports
	 * @param defatulBatchNum
	 * @return
	 */
	public List<Map> findByBatchNum(String defatulBatchNum) {
		Object[] args = new Object[]{defatulBatchNum};
		String sqlFetchRows = "select j_id, ont_ports from jx_info j where j.batch_num=? ";
 		try {
			return this.baseDao.listAllAsMap(sqlFetchRows, args);
		} catch (Exception e) {
			log.warn(e.getMessage());
		}
		return null;
	}

	/**
	 * 必须快速，使用临时表
	 * @param area
	 * @param jx
	 * @return
	 */
	public List findJxLike(String area, String jx) {
		Object[] args = new Object[]{area, "%" + jx.trim() + "%"};
		String sqlFetch = "select j.jx from area_jx j where j.area=? and j.jx like ? order by jx";
		return  this.baseDao.findList(sqlFetch, args);
	}

	public List findSbhLike(String jx, String type) {
		Object[] args = new Object[]{jx, type};
		String sqlFetch = "select j.sbh from jx_sbh j where j.jx=? and j.type = ? order by sbh";
		return  this.baseDao.findList(sqlFetch, args);
	}

	public List findUnusedMdfPort(String jx, String sbh, String mdf_port) {
		Object[] args = new Object[]{jx,  sbh ,"%" + mdf_port.trim() + "%"};
		String sqlFetch = "select j.mdf_port from jx_info j where j.jx=? and j.sbh = ? and j.mdf_port like ? and (used = 0 or (used = 1  and used_ont_ports < ont_ports)) order by mdf_port";
		return  this.baseDao.findList(sqlFetch, args);
	}

	/**
	 * 
	 */
	public void updateAField(Object j_id, String fieldName, Object fieldValue) {
		if(fieldValue == null){
			Object[] args = new Object[]{j_id};
	 		String updateSql = "update jx_info set "+fieldName+" =null " + 
					" where j_id=?";
	 		this.baseDao.update(updateSql, args);
		}else{
			Object[] args = new Object[]{fieldValue,j_id};
	 		String updateSql = "update jx_info set "+fieldName+" =? " + 
					" where j_id=?";
	 		this.baseDao.update(updateSql, args);
		}
		
	}

	/**
	 * 
	 * @param j_id
	 * @param ont_id
	 * FTTH必填
	 * @return
	 */
	public boolean portOccupied(String j_id, String ont_id) {
		if(j_id == null){
			return false;
		}
		Map jxInfo = this.findByKey(j_id);
		String type = (String) jxInfo.get("type");
		
		if(type!= null && type.trim().equalsIgnoreCase("FTTH")){
			Map ont = this.ontService.find(new Integer(j_id), ont_id);
			if(ont != null){
				Integer i = (Integer) ont.get("used");
				if(i != null && i.equals(1)){
					return true;
				}
			}

		}else{
			if(jxInfo != null){
				Boolean i = (Boolean) jxInfo.get("used");
				if(i != null && i){
					return true;
				}
 			}
		}

		return false;
	}
	/**
	 * 未占满的
	 * @param jx
	 * @param sbh
	 * @return
	 */
	public List findSlotsLike(String jx, String sbh) {
		Object[] args = new Object[]{jx, sbh};
		String sqlFetch = "select j.slot,max(board_type) board_type from jx_info j where (mask is null or mask = '') and j.jx=? and j.sbh = ? and j.used=0  group by slot order by slot";
		return  this.baseDao.findList(sqlFetch, args);
	}

	public List findSbportsLike(String jx, String sbh, String slot) {
		Object[] args = new Object[]{jx, sbh, slot};
		String sqlFetch = "select j.sb_port from jx_info j where (mask is null or mask = '') and j.jx=? and j.sbh = ? and slot=? and j.used=0  order by sb_port";
		return  this.baseDao.findList(sqlFetch, args);
	}

	public String extractArea(String str) {
		if(StringUtils.isBlank(str)){
			return null;
		}
		
		String[] areas = {"蓬江","江海","新会","开平","恩平","鹤山","台山"};
		for (int i = 0; i < areas.length; i++) {
			if(str.contains(areas[i])){
				return areas[i];
			}
		}
		return null;
	}

	
	
	
}
