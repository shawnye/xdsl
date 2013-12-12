package unicom.bo;

public class Role {
	private String code;
	private String name;
	private String menuCode;
	private String includeOnly;//仅仅包含的子菜单(优先)
	private String excludeOnly;//仅仅不包含的子菜单
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMenuCode() {
		return menuCode;
	}
	public void setMenuCode(String menuCode) {
		this.menuCode = menuCode;
	}
	public String getIncludeOnly() {
		return includeOnly;
	}
	public void setIncludeOnly(String includeOnly) {
		this.includeOnly = includeOnly;
	}
	public String getExcludeOnly() {
		return excludeOnly;
	}
	public void setExcludeOnly(String excludeOnly) {
		this.excludeOnly = excludeOnly;
	}
	@Override
	public String toString() {
		return "Role [code=" + code + ", name=" + name + "]";
	}
	
	
	
}
