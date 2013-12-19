package unicom.xdsl.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import au.com.bytecode.opencsv.CSVReader;

import unicom.common.Constants;
import unicom.common.port.FieldConverter;
import unicom.common.port.ImportConfig;
import unicom.common.port.ImportFileException;
import unicom.dao.BaseDaoInterface;

@SuppressWarnings("rawtypes")
@Service(value = "cutoverService")
public class CutoverService {
	protected static Log log = LogFactory.getLog(CutoverService.class);
	
	private BaseDaoInterface baseDao;

	@Autowired
	public void setBaseDao(BaseDaoInterface baseDao) {
		this.baseDao = baseDao;
	}
	
	/**
	 * 支持FTTH导入！暂时不支持FTTH割接更新（无ont_id信息！）
	 */
	public boolean importFile(File file, ImportConfig importConfig,Map<String,Object> updateInfo,
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
		log.debug("^--------------------割接资源表导入、更新开始--------------------------^");

		
		int line = 0;
		int updated = 0;
		String[] nextLine = null;
		//state=未分配
//		String sqlUpdate = "insert into user_info (username,p_id,address,user_no,password,area,tel,begin_date,state) values (?,?,?,?,?,?,?,?,?) ";
		Collection<String> fields = importConfig.getImportFields();
		fields.add("batch_num");//最后一个是batch_num
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
//					this.baseDao.batchUpdate(new String[]{updateJxInfoSql});
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
			 
//				System.out.println(objs.length + ",all=" + objs2.length);
				for (String field : fields2) { 
					Integer order = importConfig.getFieldIndex(field);
					if(order != null ){
						if( order < nextLine.length ){
							objs2[i] = nextLine[order]; 
						}else{
							objs2[i] = null;
						}
						
						if(! field.startsWith("@")){ 
							
							objs[j] = objs2[i]; 
							j++;//相对插入属性而言
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

				objs[objs.length-1] = defatulBatchNum;//最后一个是batchnum
 
 				
				args.add(objs);
			}

			if(args.size() > 0){
				int[] is = this.baseDao.batchUpdateWithSameSql(insertSql.toString(), args);
				int j = 0;
				for (int i = 0; i < is.length; i++) {
					j += is[i];
				}

				updated += j;

				args.clear();
			}

//============================================================================
			//更新导入基础信息
			String updateSql =
				"update cutover\r\n" +
				"set updated=0,remark=?,creater=?,create_time=?\r\n" + 
				"where batch_num=?";
			
			this.baseDao.update(updateSql, new Object[]{updateInfo.get("remark"),
					updateInfo.get("creater"),
					updateInfo.get("create_time"),
					defatulBatchNum
					});
			
			updateSql =
					"update cutover\r\n" +
					"set new_jx=old_jx\r\n" + 
					"where batch_num=? and (new_jx is null or new_jx ='' )";
				
				this.baseDao.update(updateSql, new Object[]{
						defatulBatchNum
						});
			
			//update old j_id ,u_id with account_or_pid
			updateSql =
					"update cutover\r\n" + 
					"set u_id=u.u_id,old_j_id=u.j_id\r\n" + 
					"from user_info u\r\n" + 
					"where u.user_no=account_or_pid or u.p_id=account_or_pid\r\n" + 
					"and cutover.batch_num=?";
				
			int updates0 = this.baseDao.update(updateSql, new Object[]{
						defatulBatchNum
						});
			
			//update new_j_id with <jx,new_mdf_port>
			updateSql =
					"update cutover\r\n" + 
					"set new_j_id=j.j_id\r\n" + 
					"from jx_info j\r\n" + 
					"where j.jx=cutover.new_jx and j.mdf_port=cutover.new_mdf_port\r\n" + 
					"and cutover.batch_num=?";
				
			int updates = this.baseDao.update(updateSql, new Object[]{
						defatulBatchNum
						});
			//注:以上更新有些可能是NULL，导致更新数不等于导入行数
//--------------------------------------------------------------------------------			
			//更新用户端口
			updateSql ="update user_info\r\n" + 
					"set j_id=new_j_id\r\n" + 
					"from cutover c\r\n" + 
					"where user_info.u_id=c.u_id\r\n" + 
					"and c.batch_num=?";
			
			updates = this.baseDao.update(updateSql, new Object[]{
						defatulBatchNum
					});
			
			msg.append("更新用户信息J_ID数：" + updates);
			msg.append("\n");
			
			//--确保不占用冲突(但置坏怎么办)
			updateSql ="update jx_info\r\n" + 
					"set used=1\r\n" + 
					"from cutover c\r\n" + 
					"where jx_info.j_id=c.new_j_id and jx_info.used=0  \r\n" + 
					"and c.batch_num=?";
			updates = this.baseDao.update(updateSql, new Object[]{
					defatulBatchNum
					});
			
			if(updates0 > updates){
				log.error("实际占用新端口数 < 需要占用新端口数（可能新端口已经占用或置坏）：" + updates +"<"+ updates0);

				throw new Exception("实际占用新端口数 < 需要占用新端口数（可能新端口已经占用或置坏）：" + updates +"<"+ updates0);
			}

			//释放原端口
			updateSql ="update jx_info\r\n" + 
					"set used=0\r\n" + 
					"from cutover c\r\n" + 
					"where jx_info.j_id=c.old_j_id\r\n" + 
					"and c.batch_num=?";
			
			updates = this.baseDao.update(updateSql, new Object[]{
					defatulBatchNum
					});
			msg.append("释放原端口总数:" + updates); 
			msg.append("\n");
			//更新状态
			updateSql =
					"update cutover\r\n" + 
					"set updated=1\r\n" + 
					"where batch_num=?";
				
			updates = this.baseDao.update(updateSql, new Object[]{
						defatulBatchNum
						});
			log.debug("$--------------------割接资源表导入、更新完毕--------------------------$");
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
}
