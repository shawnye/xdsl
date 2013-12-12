package unicom.common.port;

public interface FieldConverter {
	/**
	 * 相关字段
	 * @return
	 */
	public String getField();
	
	public ImportConfig getImportConfig();
	/**
	 * 
	 * @param targetValue
	 * @return
	 * @throws FieldConvertException
	 */
	public Object convertFrom(Object[] availableValues) throws FieldConvertException;
}
