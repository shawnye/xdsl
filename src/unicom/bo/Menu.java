package unicom.bo;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class Menu {
	private String code;//0 means all
	private String name;
	private String path;
	private String state;
	private String remark;
	private String style;
	private Boolean popup;
	
	private List<Menu> submenus = new ArrayList<Menu>(0);

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	public String getParentCode(){
		int dot = code.lastIndexOf(".");
		if(dot == -1 || dot == 0 || dot == code.length()-1){
			return "ROOT";
		}
		
		return code.substring(0,dot);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public List<Menu> getSubmenus() {
		return submenus;
	}

	public void setSubmenus(List<Menu> submenus) {
		this.submenus = submenus;
	}

	public boolean isRoot() {
 		return "ROOT".equalsIgnoreCase(code);
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public Boolean getPopup() {
		return popup;
	}

	public void setPopup(Boolean popup) {
		this.popup = popup;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Menu other = (Menu) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		return true;
	}

	/**
	 * start from 0 =ROOT
	 * @return
	 */
	public int getLevel(){
		if(isRoot()){
			return 0;
		}
		//count dots in code
		return StringUtils.countMatches(code, ".") + 1;
		
	}
	/**
	 * 
	 * add by level,必须先有父母节点
	 * @param menu
	 * @param recusively 
		是否添加子节点
	 */
	public void addSubmenu(Menu menu, boolean recusively){
//		int level = menu.getLevel();
//		 
//		int thisLevel = this.getLevel();
//		if(level - thisLevel <= 0){//同级不加
//			return;
//		}
		
		//必须先有父母节点
		Menu parentMenu = this.findParentMenu(this, menu); 
		if(parentMenu != null){
			if(!parentMenu.getSubmenus().contains(menu)){
				parentMenu.getSubmenus().add(menu);
			}
			if(recusively && menu.getSubmenus().size() > 0){
				for (Menu submenu : menu.getSubmenus()) {
					this.addSubmenu(submenu, recusively);
				}
			}
			
		}else{//create parent menu ? 
			System.err.println("当前上下文找不到父菜单:" + menu);
		}
	}
	//树形迭代
	public Menu findParentMenu(Menu context, Menu target){
		if(target.getParentCode().equals(context.getCode())){
			 return context;
		}
		
		List<Menu> sm = context.submenus;
		for (Menu m : sm) {
			if(target.getCode().startsWith(m.getCode())){//找到祖先节点 
				return findParentMenu(m, target);
			}
		}
		
		return null;
	}
	
	public void removeSubmenu(Menu menu){
		Menu parentMenu = this.findParentMenu(this, menu); 
		if(parentMenu != null){
			parentMenu.getSubmenus().remove(menu);
		}
	}
	
	public static void main(String[] args) {
		System.out.println( StringUtils.countMatches("20.23.11", "."));
	}

	@Override
	public String toString() {
		return "Menu [code=" + code + ", name=" + name + "]";
	}
	
	
}
