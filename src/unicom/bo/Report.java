package unicom.bo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

@SuppressWarnings({"rawtypes"})
public class Report {
	/**
	 * Logger for this class
	 */
	private static final Log logger = LogFactory.getLog(Report.class);

	public static final Pattern TITLE_PATTERN = Pattern.compile("(\\S+)\\s+as\\s+\\\"([^\\\"]+)\\\"");//as "标题名", as与"之间不能有换行
	private static final String KEY_MARK = "*";
	//报表标题行的所有标题
	String id;

	String keyField;//用来唯一选择行的字段

	Integer keyIndex;

	List<String> titles = new ArrayList<String>();

	/**
	 * 显示列索引，不能交换列
	 */
	private List<Boolean> columnsDisplayed = new ArrayList<Boolean>();

	Map<String,Link> links = new HashMap<String, Link>();

	List<Map> data = null;

	Long totalCount = 0L;//>=dataSize

	String sqlFetch;

	String currentSql;//with condition

//	Boolean doLimit = false;

	Integer limit = null;//查询限制可见数,导出不限制

	Date sqlFirstUsedTime;//优化之用

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getSqlFirstUsedTime() {
		return sqlFirstUsedTime;
	}

	public void setSqlFirstUsedTime(Date sqlFirstUsedTime) {
		this.sqlFirstUsedTime = sqlFirstUsedTime;
	}

	public long getSqlUsedTime(){
		if(sqlFirstUsedTime == null){
			return -1;
		}
		return new Date().getTime() - sqlFirstUsedTime.getTime();
	}

	private Report(){};

	public List<String> getDisplayedTitles() {
		if(this.columnsDisplayed == null || this.columnsDisplayed.size() ==0){
			this.displayAllColumns();
		}
		List<String> rs = new ArrayList<String>();
		for (int i = 0; i < this.columnsDisplayed.size(); i++) {
			if(this.columnsDisplayed.get(i)){
				rs.add(this.titles.get(i));
			}
		}
		return rs;
	}


	public List<String> getTitles() {
		return titles;
	}

	 void setTitles(List<String> titles) {
		this.titles = titles;
	}


	public String getSqlFetch() {
		return sqlFetch;
	}


     void setSqlFetch(String sqlFetch) {
		this.sqlFetch = sqlFetch;
	}

     /**
      * sql server only
      * @return
      */
 	public String getLimitedSqlFetch() {
 		if(this.limit != null && this.limit > 0){
 			String t = sqlFetch.toLowerCase();
 			int i = t.indexOf("select");
 			if(i > -1){
 				return sqlFetch.substring(0, i+ 7 )+ " top " + this.limit + " " + sqlFetch.substring(i+7);
 			}
 		}

		return sqlFetch;
	}

 	public String getSqlCount(){
 		return "select count(*) from (\n " + this.sqlFetch.trim().replaceFirst(";$", "") + " )";
 	}

	public List<Map> getData() {
		return data;
	}


	public void setData(List<Map> data) {
		this.data = data;
	}

	public Integer getDataSize(){
		return data != null ? data.size() : -1;
	}

	public Long getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(Long totalCount) {
		this.totalCount = totalCount;
	}

	public String getCurrentSql() {
		return currentSql;
	}

	public void setCurrentSql(String currentSql) {
		this.currentSql = currentSql;
	}


//	public Boolean getDoLimit() {
//		return doLimit;
//	}
//
//	public void setDoLimit(Boolean doLimit) {
//		this.doLimit = doLimit;
//	}

	public String getKeyField() {
		return keyField;
	}

	public Integer getKeyIndex() {
		return keyIndex;
	}

	public void setKeyIndex(Integer keyIndex) {
		this.keyIndex = keyIndex;
	}

	public void setKeyField(String keyField) {
		this.keyField = keyField;
	}

	public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}


	public Map<String, Link> getLinks() {
		return links;
	}

	public static Report parse(String sqlFetch){
		return parse(sqlFetch,null);
	}

	/**
	 * 可显示列的索引，实时设置，默认全部显示
	 * @return
	 */
	public List<Boolean> getColumnsDisplayed() {
		return columnsDisplayed;
	}

	public void setColumnsDisplayed(List<Boolean> columnsDisplayed) {
		this.columnsDisplayed = columnsDisplayed;
	}

	public void displayAllColumns(){
		this.columnsDisplayed.clear();

		for (int i = 0; i < this.titles.size(); i++) {
			this.columnsDisplayed.add(true);
		}
	}

	/**
	 * 格式： index1:1;index2:0; ...
	 * @param str
	 */
	public void parseColumnsDisplayedString(String str){
		if(StringUtils.isBlank(str)){
			return;
		}
		String[] pairs = str.split(";");
		if(pairs.length != this.titles.size()){
			System.err.println("[配置失败]配置列数与标题数不一致：" + pairs.length + ":" +this.titles.size());
			return;
		}
		this.displayAllColumns();//preset all

		for (int i = 0; i < pairs.length; i++) {
			String[] pair = pairs[i].split("\\:");
			if(pair.length !=2){
				System.err.println("[配置失败]第[" + (i+1) + "]项配置非法:" + pairs[i]);
				return;
			}
			int index = Integer.parseInt(pair[0].trim());
			int val = Integer.parseInt(pair[1].trim());
			if(val == 1 ){
				this.columnsDisplayed.set(index, true);
			}else{
				this.columnsDisplayed.set(index, false);
			}
		}
	}

	public static Report parse(String sqlFetch,  List<String> links){
		if(sqlFetch == null){
			return null;
		}
		Report r = new Report();

		//是否先删除换行？
		Matcher matcher = TITLE_PATTERN.matcher(sqlFetch);
//		int cols = 1;
		StringBuffer sb = new StringBuffer();
		while(matcher.find()){
			String field = matcher.group(1);
			String title = matcher.group(2);
			//do str
			if(field.trim().endsWith(KEY_MARK)){
				field = field.replace(KEY_MARK, "");
				r.setKeyField(field.replace("[", "").replace("]", "").replaceFirst("\\w+\\.", "") );// 删除前缀别名，针对中文字段删除[]
				r.setKeyIndex(r.getTitles().size());
			}
			r.getTitles().add(title);

			matcher.appendReplacement(sb, field + " as \"" + title + "\"");//FIXME "as c_" + cols避免出现相同的列名(i.status, b.status 会出错),但是Mysql下不通过
//			cols++;
		}
		matcher.appendTail(sb);
		r.setSqlFetch(sb.toString());

		r.setSqlFirstUsedTime(new Date());

		if(links != null){
			for (String link : links) {

				if(StringUtils.isNotBlank(link)){
					link = link.trim();
					if(link.startsWith("#")){
						continue;
					}
					int eq = link.indexOf('=');
					if(eq == -1 || eq == link.length()-1){
						continue;
					}
					String t = link.substring(0,eq);
					String l = link.substring(eq+1);
					int comma = l.indexOf(',');
					if(comma == -1 || comma == l.length() -1 ){//no title
						r.getLinks().put(t, new Link(null, l.trim().replace(KEY_MARK, r.getKeyField())));
					}else{
						r.getLinks().put(t, new Link(l.substring(0,comma).trim(), l.substring(comma+1).trim().replace(KEY_MARK, r.getKeyField())));
					}
				}
			}
		}

		logger.debug("解析语句结果：标题" + r.getTitles().size() + "个，链接"+r.getLinks().size()+"个，其中关键列是["+r.getKeyField()+"]，其索引号是：" + r.getKeyIndex());

		return r;

	}


}
