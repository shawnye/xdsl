package unicom.common;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * null-safe
 * @author yexy6
 *
 */
public class DateHelper {
	private static final Log  log= LogFactory.getLog(DateHelper.class);
	
	public static String format(Date date ,String pattern){
		if(date == null){
			return "";
		}
		return DateFormatUtils.format(date, pattern);
	}

	public static String format(long date, String pattern) {
		if(date < 0){
			return "";
		}
		return DateFormatUtils.format(date, pattern);
	}
	
	public static String getCompactToday(){
		return format(new Date(), "yyyyMMdd");
	}
	
	public static Date toDate(String dateStr, String[] patterns){
		if(StringUtils.isBlank(dateStr)){
			return null;
		}
		try {
			return DateUtils.parseDate(dateStr, patterns);
		} catch (Exception e) {
			log.warn(e);
			return null;
		}
	}
	
	public static Date toDate(String dateStr, String pattern){
		return toDate(dateStr, new String[]{pattern});
	}
	
	public static Date getDateStart(Date date){
		if(date == null){
			return null;
		}
		return DateUtils.truncate(date, Calendar.DAY_OF_MONTH);
	}
	
	public static Date getDateEnd(Date date){
		if(date == null){
			return null;
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DAY_OF_MONTH, 1);
		cal = DateUtils.truncate(cal, Calendar.DAY_OF_MONTH);
		cal.add(Calendar.SECOND, -1);//- 1 s;
		return cal.getTime();
	}
	/**
	 * 获得上N个月的起始日期, 0 表示当月, 1-上月
	 * @param lastNum
	 * @return
	 */
	public static Date getLastMonthStart(int lastNum) {
		Calendar cal = Calendar.getInstance();
		if(lastNum > 0){
			cal.add(Calendar.MONTH, -lastNum);
		}
	
		return getMonthStart(cal.getTime());
	}
	
	public static Date getMonthStart(Date date) {
		return DateUtils.truncate(date, Calendar.MONTH);
	}

	public static Date getMonthEnd(Date date){
		if(date == null){
			return null;
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MONTH, 1);
		cal = DateUtils.truncate(cal, Calendar.MONTH);
		cal.add(Calendar.SECOND, -1);//- 1 s;
		return cal.getTime();
	}

	/**
	 * 
	 * @param date
	 * @param delta
	 * @return
	 */
	public static Date addDate(Date date, int delta) {
		if(date == null){
			return null;
		}
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, delta);
		return calendar.getTime();
	}

	public static Date getYestoday() {
		return addDate(new Date(),-1);
	}
	
	public static java.sql.Date toSqlDate(Date date){
		if(date == null){
			return null;
		}
 		return new java.sql.Date(date.getTime());
	}
	
	public static Timestamp toSqlTimestamp(Date date){
		if(date == null){
			return null;
		}
		return new Timestamp(date.getTime());
 	}
	/**
	 * 之所有要包装，是因为容错：当 time1>time2不至于死循环！
	 * @param time1
	 * @param time2
	 * @param pattern
	 * @return
	 */
	public static String formatPeriod(long time1, long time2, String pattern) {
		if(time1 <= time2){
			return DurationFormatUtils.formatPeriod(time1, time2, pattern);		
		}else{
			return "-" + DurationFormatUtils.formatPeriod(time2 , time1, pattern);	
		}
	}

	
	public static void main(String[] args) {
		System.out.println(DateHelper.format(new Date(), "yyyyMMdd_HHmm"));
		System.out.println(DateHelper.getMonthStart(new Date()));
		System.out.println(DateHelper.getMonthEnd(new Date()));
		
		System.out.println(DateHelper.getLastMonthStart(1));
		
	}

	


}
