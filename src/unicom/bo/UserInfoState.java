package unicom.bo;

public enum UserInfoState {
	NORMAL("正常"),
	PREOCCUPY("预分配"),
	UNOCCUPY("未分配"),
	PREDELETE("预拆机"),
	DELETED("拆机竣工");
	
	private String label;

	private UserInfoState(String label) {
		this.label = label;
	}
	
	public String getLabel(){
		return label;
	}
}
