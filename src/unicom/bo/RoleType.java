package unicom.bo;

public enum RoleType {
	ADMIN("管理员"),
	DAI_WEI("代维"),
	JIAN_KONG("监控"),
	HAO_XIAN("号线"),
	GU_ZHANG("故障"),
	SHU_JU("数据"),
	JI_XIANG("集响"),
	JIAO_HUAN("交换"),
	HAO_XIAN_DUAN_KOU("号线端口"),
	XIAN_CHANG_WEI_HU("现场维护支撑"),
	CHUAN_SHU("传输"),
	JING_LI("经理");
	 
	private String label;

	private RoleType(String label) {
		this.label = label;
	}
	
	public String getLabel(){
		return label;
	}
	
	public String getName(){
		return this.name();
	}
}
