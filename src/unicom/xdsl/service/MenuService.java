package unicom.xdsl.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import unicom.bo.Menu;
import unicom.bo.Role;
import unicom.bo.RoleType;
import unicom.common.Page;
import unicom.common.SearchConditions;
import unicom.common.SearchKey;
import unicom.dao.BaseDaoInterface;

/**
 * 菜单表
 * @author yexy6
 *
 */
@Service
@SuppressWarnings({"unchecked","rawtypes"})
public class MenuService extends AbstractService /*implements FileExporter*/{
	private BaseDaoInterface baseDao;
	private RoleService roleService;

	@Autowired
	public void setBaseDao(BaseDaoInterface baseDao) {
		this.baseDao = baseDao;
	}
	
	
	public void setRoleService(RoleService roleService) {
		this.roleService = roleService;
	}


	public Menu findByCode(String code) {
		if(StringUtils.isBlank(code)){
			return null;
		}
		code = code.trim();
		Object[] objects = new Object[]{code};
		String sqlFetch = "select * from menu where code=? and state <> 0 order by code ";
		Menu map = null;
		try {
			map = (Menu) this.baseDao.findUnique(sqlFetch, objects, Menu.class);
		} catch (Exception e) {
			log.warn(e.getMessage());
		}
		return map;
	}
	
	
	public List<Menu> findSubmenusByCode(String code) {
		if(StringUtils.isBlank(code)){
			return null;
		}
		code = code.trim();
		
		Object[] objects = new Object[]{code, code + "%"};
		String sqlFetch = "select * from menu where code<>? and code like ? and state <> 0 order by code ";
		List<Menu> map = null;
		try {
			map = this.baseDao.findList(sqlFetch, objects, Menu.class);
		} catch (Exception e) {
			log.warn(e.getMessage());
		}
		return map;
	}
	
	public Menu getMenu(RoleType role){
		Menu root = new Menu();
		root.setCode("ROOT");
		
		List<Role> roles = this.roleService.findByCode(role.getName());
		for (Role r : roles) {
			if("0".equals(r.getMenuCode())){//FIXME
				//get all menu
				List<Menu> menus = this.find();
				
				for (Menu menu : menus) {
					root.addSubmenu(menu, false);//自动根据编码分派到合适的层级：要求menus必须按照code顺序！
				}
				
//				root.getSubmenus().addAll(menus);
				break;
			}else{
				addMenu(root, r); 
			}
		}
		
		
		return root;
	}
	
	private void addMenu(Menu context, Role r) {
		String menuCode = r.getMenuCode();//30
		Menu menu = this.findByCode(menuCode);
		
		
		List<Menu> submenus = this.findSubmenusByCode(menuCode);//包含子孙菜单！
		String includeOnly = r.getIncludeOnly();//01,02,04
		String excludeOnly = r.getExcludeOnly();//99
		
		List<Menu> submenus_ = new ArrayList<Menu>();
		submenus_.addAll(submenus);//shallow copy, avoid同步删除错误
		if(StringUtils.isNotBlank(includeOnly)){
			String[] subMenuCode = includeOnly.split("\\s*\\,\\s*");
			for (int i = 0; i < subMenuCode.length; i++) {
				subMenuCode[i] = menuCode + "." + subMenuCode[i];
			}
			
			for (Menu m : submenus_) {
				boolean contains = ArrayUtils.contains(subMenuCode, m.getCode());
				if(!contains){
					submenus.remove(m);
				}
				//ERROR!
//				String c = m.getCode(); 
//				for (String string : subMenuCode) {
//					if(! c.equals(menuCode + "." + string)){//不包含的，删除
//						submenus.remove(m);
//					}
//				}
			} 
		}
		
		//在包含的基础上再删除
		if(StringUtils.isNotBlank(excludeOnly)){
			String[] subMenuCode = excludeOnly.split("\\s*\\,\\s*");
			for (int i = 0; i < subMenuCode.length; i++) {
				subMenuCode[i] = menuCode + "." + subMenuCode[i];
			}
			
			for (Menu m : submenus_) {
				boolean contains = ArrayUtils.contains(subMenuCode, m.getCode());
				if(contains){
					submenus.remove(m);
				}
				 
			} 
		}
		
		for (Menu menu2 : submenus) {
			menu.addSubmenu(menu2, false);//自动处理子孙菜单的位置
		}
//		menu.getSubmenus().addAll(submenus);
		
		//不含孙菜单
//		for (Menu submenu2 : submenus) {//第3级 
//			List<Menu> submenus3 = this.findSubmenusByCode(submenu2.getCode());
//			submenu2.getSubmenus().addAll(submenus3);
//		}
		
		context.addSubmenu(menu, true);//问题是menu未必是第一层级的！
	}


	private List<Menu> find() {
 		String sqlFetch = "select * from menu where state <> 0 order by code ";
		List<Menu> map = null;
		try {
			map = this.baseDao.findList(sqlFetch,null, Menu.class);
		} catch (Exception e) {
			log.warn(e.getMessage());
		}
		return map;
	}


	public String getRootMenuHtml(RoleType role, String contextPath)   {
 		Menu menu = getMenu(role);
 
		return getMenuHtml(menu , contextPath);
	}
	
	/**
	 * 获得menu的Html <ul><li>列表， ul id='ROOT_MENU'
	 * @param menu
	 * @return
	 * @throws Exception 
	 */
	public String getMenuHtml(Menu menu,String contextPath)  { 
		if(menu == null){
			throw new RuntimeException("no menu object");
		}
		StringBuilder sb = new StringBuilder();
		
		sb.append("<ul id='ROOT_MENU' >\n");//class='dropdown'
		
		generateMenuHtml(menu,sb,contextPath, 0);//使用递归
		 
		sb.append("</ul>");
		return sb.toString();
	}

	private void generateMenuHtml(Menu m, StringBuilder sb,String contextPath, int level) {
		List<Menu> sms = m.getSubmenus(); 
		//ROOT菜单不必显示了
		if(! m.isRoot() ){
			String path = "javascript:void(0);";
			//do stuff
			//if http:// or http://开头作为外部链接，否则作为相对链接，需要附加contextPath
			if(StringUtils.isNotBlank(m.getPath())){
				path = m.getPath().trim();
				
				if(path.length() > 5){
					String s = path.substring(0, 5).toLowerCase();
					if(!(s.startsWith("http")|| s.startsWith("https"))){
						//not external link!
						if(StringUtils.isNotBlank(contextPath) && !"/".equals(contextPath.trim())){
							path = contextPath.trim() + path;
						}
					}
				}else{
					if(StringUtils.isNotBlank(contextPath) && !"/".equals(contextPath.trim())){
						path = contextPath.trim() + path;
					}
				}
				
				
				
			} 
			
			appendPrefixTab(sb,level);
			sb.append("<li"); 
			
			if(StringUtils.isNotBlank(m.getCode())){
				sb.append(" id='"+m.getCode() + "'");
			}
			sb.append(">"); 
			
			sb.append("<a href='" + path + "'"); 
			
			if(StringUtils.isNotBlank(m.getStyle())){
				sb.append(" style='"+m.getStyle()+"'");
			}
			if(StringUtils.isNotBlank(m.getRemark())){
				sb.append(" title='"+m.getRemark()+"'");
			}
			if(StringUtils.isNotBlank(m.getPath()) && m.getPopup() != null && m.getPopup()){
				sb.append(" target='_blank'");
			}
			
			sb.append(">" + m.getName() + "</a>\n");
			
			
			//不能放到循环里面！
			if(sms != null && sms.size() > 0){//branch
				appendPrefixTab(sb,level);
				sb.append("<ul>\n");
			} 
		}
		
		for (Menu sm : sms) { 
			//do staff
			generateMenuHtml(sm,  sb , contextPath, level+1);
			 
		} 
		
		if(! m.isRoot() ){ 
			//不能放到循环里面！
			if(sms != null && sms.size() > 0){//branch
				appendPrefixTab(sb,level); 
				sb.append("</ul>\n");
			} 
			
			if(StringUtils.isNotBlank(m.getRemark())){
				sb.append("<div class=\"summary\">--" );
				sb.append(ObjectUtils.toString(m.getRemark()));
				sb.append("</div>");
			}
			
			
			appendPrefixTab(sb,level); 
			
 
			sb.append("</li>\n"); 
		}
		 
		
	}
	

	private void appendPrefixTab(StringBuilder sb,int level) {
		for (int i = 0; i < level; i++) {
			sb.append("\t");
		} 
	}

	
	
	
	
 
	@Override
	public Page<Map> listAsMap(SearchConditions searchCondition, int pageNo,
			int pageSize) {

		Map<SearchKey, Object> conditions = searchCondition.getConditions();
		int size = conditions.size();
		Object[] args = new Object[size];
		
		//导入用户信息，可能没有关联机房信息
		String sqlFetch = "select * from  Menu  where 1=1 ";
		String sqlCount = "select count(*) from Menu where 1=1 ";
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
		
		return this.baseDao.listAsMap(sqlCount , sqlFetch , args , pageNo, pageSize);
	}

	public List<Map> listAllAsMap(SearchConditions searchCondition) {
		Map<SearchKey, Object> conditions = searchCondition.getConditions();
		int size = conditions.size();
		Object[] args = new Object[size];
		String sqlFetch = "select * from  Menu  where 1=1 ";
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
		
		return this.baseDao.listAllAsMap( sqlFetch , args );
	}

//	public void exportFile(File destFile, ExportConfig exportConfig)
//	throws ExportFileException {
//		String format = exportConfig.getFormat();
//
//		long start = System.currentTimeMillis();
//		if("csv".equalsIgnoreCase(format)){
//			this.exportCsvFile(destFile, exportConfig);
//		}else if("excel".equalsIgnoreCase(format)){
//			this.exportExcelFile(destFile, exportConfig);
//		}
//		log.debug("导出耗时：" + ((System.currentTimeMillis()-start)/1000) + " 秒");
//	}
//	private void exportExcelFile(File destFile, ExportConfig exportConfig) throws ExportFileException{
//		
//	}
//
//		
//	private void exportCsvFile(File destFile, ExportConfig exportConfig) throws ExportFileException{
//		CSVWriter csvWriter = null;
//		try {
//			csvWriter = new CSVWriter(new FileWriterWithEncoding(destFile, "gbk"));
//			
//			List<Map> list = this.listAllAsMap(exportConfig.getSearchCondition());
//			
//			String[] line = null;
//			line = new String[]{
//					"编码",
//					
//					"名称",
//					"路径",
//					"等级",
//					 
//					"状态", 
//					"描述",
//					 
//					
//			};
//			csvWriter.writeNext(line );
//			
//			MapValueGetter mvg = null;
//			for (Map map : list) {
//				mvg = new MapValueGetter(map);
//				line = new String[]{
//						mvg.getAsString("code"),
//						
//						mvg.getAsIntegerString("name"),
//						mvg.getAsIntegerString("path"),
//						
//						mvg.getAsIntegerString("level"),
//						
//						mvg.getAsIntegerString("state"),
//						mvg.getAsIntegerString("remark"),
//						 
//						
//				};
//				csvWriter.writeNext(line );
//			}
//			
//			csvWriter.flush();
//			csvWriter.close();
//
//		} catch (IOException e) {
//			throw new ExportFileException(e);
//		} finally{
//			if(csvWriter != null){
//				try {
//					csvWriter.close();
//				} catch (IOException e) {
//				}
//			}
//		}
//	}
}
