package unicom.common;

public class SearchKey {
	private String key;//=field+theta
	private String field;
	private String theta;
	
	public SearchKey(String key) {
		this(key, null);
	}
	
	public SearchKey(String key, String defaultTheta) {
		this.key = key;
		
		this.field = this.getField(key);
		this.theta = this.getTheta(key, defaultTheta);
		
	}
	
	protected String getField(String key){
		if(key == null){
			return null;
		}
		
		return key.trim().replace("<", "").replace(">", "")
							.replace("=", "").replace("~", "")
							.replace("&gt;", "").replace("&lt;", "");
	}
	
	protected String getTheta(String key, String defaultTheta){
		if(key == null){
			return null;
		}
		
		if(key.endsWith(">=") || key.endsWith("&gt;=")){
			return ">=";
		}else if(key.endsWith("<=") || key.endsWith("&lt;=")){
			return "<=";
		}else if(key.endsWith(">") || key.endsWith("&gt;")){
			return ">";
		}else if(key.endsWith("<") || key.endsWith("&lt;")){
			return "<";
		}else if(key.endsWith("=") ){
			return "=";
		}else if(key.endsWith("~")){
			return "like";
		}else{
			return defaultTheta;
		}
		
	}
	
	/**
	 * 有大于的情况时
	 * @param key
	 * @return
	 */
	public String getFieldStart(){
		if(this.theta == null){
			return null;
		}
		
		if(!this.theta.equals(">=") && !this.theta.equals(">")){
			throw new RuntimeException("not have 'start' field: " + key);
		}
		
		return this.field + "_start";
		
	}
	
	public String getFieldEnd(){
		if(this.theta == null){
			return null;
		}
		
		if(!this.theta.equals("<=") && !this.theta.equals("<")){
			throw new RuntimeException("not have 'end' field: " + key);
		}
		
		return this.field + "_end";
	}
	

	
	public String getKey() {
		return key;
	}
	
	public String getField() {
		return field;
	}
	
	public String getTheta() {
		return theta;
	}

	/**
	 *  一般替换Null运算符号
	 * @param theta
	 */
	void setThetaIfNull(String theta) {
		if(this.theta == null){
			this.theta = theta;
		}
	}

	@Override
	public String toString() {
		return this.key;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
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
		SearchKey other = (SearchKey) obj;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		return true;
	}
	
	
}
