package unicom.common.port;

import unicom.common.SearchConditions;

public class ExportConfig {
	String id;

	private String format = "csv";

	private SearchConditions searchCondition;

	Integer limit = null;//查询限制可见数,导出不限制

	private String columnsDisplayedString;//显示列配置，@see report.parseColumnsDisplayedString()


	public SearchConditions getSearchCondition() {
		return searchCondition;
	}

	public void setSearchCondition(SearchConditions searchCondition) {
		this.searchCondition = searchCondition;
	}


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	/**
	 *导出格式：csv, excel
	 * @return
	 */
	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	public String getColumnsDisplayedString() {
		return columnsDisplayedString;
	}

	public void setColumnsDisplayedString(String columnsDisplayedString) {
		this.columnsDisplayedString = columnsDisplayedString;
	}

}
