package unicom.xdsl.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import unicom.common.MapValueGetter;
import unicom.common.Page;
import unicom.common.SearchConditions;
import unicom.dao.BaseDaoInterface;

@Service(value = "ontService")
@SuppressWarnings({"rawtypes"})
public class OntService extends AbstractService{
	protected Log log = LogFactory.getLog(this.getClass());

	private BaseDaoInterface baseDao;

	private JxInfoService jxInfoService;
	
	@Autowired
	public void setBaseDao(BaseDaoInterface baseDao) {
		this.baseDao = baseDao;
	} 

	@Autowired
	public void setJxInfoService(JxInfoService jxInfoService) {
		this.jxInfoService = jxInfoService;
	}
 

	@Override
	public Page<Map> listAsMap(SearchConditions searchCondition, int pageNo,
			int pageSize) {
 		throw new UnsupportedOperationException();
	}

	/**
	 * 同时更新used_ont_ports
	 * @param j_id
	 * @param ontId
	 * @param used
	 * 同时清空SN
	 * @return
	 */
	public int updateStatus(Integer j_id, String ontId, int used) {
		 String update = "update ont set used=? where j_id=? and ont_id=?";

		int i= this.baseDao.update(update, new Object[]{used,j_id,ontId});		
		
		
		update = "update jx_info set used_ont_ports=(select sum(used) from ont where ont.j_id=jx_info.j_id) where j_id=?";
		this.baseDao.update(update, new Object[]{j_id});	
		
		Map jxInfo  = this.jxInfoService.findByKey("" + j_id);
		Integer used_ont_ports = (Integer) jxInfo.get("used_ont_ports");
		Integer ont_ports = (Integer) jxInfo.get("ont_ports");

		if(used_ont_ports == null){
			used_ont_ports = 0;
		}
		if(ont_ports == null){
			ont_ports = 0;
		}
		if(ont_ports>0 && used_ont_ports==0){
			this.jxInfoService.updateAField(j_id, "sn", null);//set null
		}
		 
		
 		log.info("更新ONT端口状态：jid="+j_id+",ontId="+ontId+",used=" + used);
 		
 		return i;

 	}
	
	public void createPortsByJid(Integer j_id, int startNo, int ontPorts) {
		String sqlUpdate ="insert into ont (ont_id,j_id,used,create_time) values(?,?,?,?)";
		List<Object[]> args = new ArrayList<Object[]>(ontPorts);
		for (int i = 0; i < ontPorts; i++) {
			args.add(new Object[]{i+startNo,j_id,0,new Date()});
		}
		 
		int[] results = this.baseDao.batchUpdateWithSameSql(sqlUpdate.toString(), args);
		
	}
	
	/**
	 * 导入jxInfo后根据jx.type，如果是ftth,则自动生成端口号,默认4个
	 * @param defatulBatchNum
	 */
	public void createPortsByBatchNum(String defatulBatchNum) {
 		List<Map> jxInfos = this.jxInfoService.findByBatchNum(defatulBatchNum);
 		if(jxInfos == null){
 			return;
 		}
 		MapValueGetter mvg = null;
 		for (Map map : jxInfos) {
 			mvg = new MapValueGetter(map);
 			
 			Integer j_id = mvg.getAsInteger("j_id");
 			Integer ont_ports = mvg.getAsInteger("ont_ports");
 			if(ont_ports == null){
 				ont_ports = 4;//default
 			}
 			this.createPortsByJid(j_id, 1, ont_ports);
		}
	}

	public Map find(Integer j_id, String ont_id) {
		if(j_id == null || StringUtils.isBlank(ont_id)){
			return null;
		}
		String sql = "select * from ont where j_id=? and ont_id=?";
		Object[] args = new Object[]{j_id, ont_id};
		try {
			return this.baseDao.findUnique(sql, args);
		} catch (Exception e) {
			log.error(e);
			return null;
		}
	}

	
}
