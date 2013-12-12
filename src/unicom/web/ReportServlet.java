package unicom.web;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import unicom.bo.Report;
import unicom.common.CharHelper;
import unicom.common.MyPinyinHelper;
import unicom.common.SearchConditions;
import unicom.common.service.ReportService;

/**
 * Servlet implementation class ReportServlet
 */
public class ReportServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ReportServlet() {
		super();
	}
	
	
	

	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		Enumeration parameterNames = request.getParameterNames();

		SearchConditions searchCondition = new SearchConditions();

		for (; parameterNames.hasMoreElements();) {
			String name = (String) parameterNames.nextElement();
			if (name.startsWith("@") || name.startsWith("page_")) {
				// request.setAttribute(name,
				// request.getParameter(name));//reset
				continue;// 忽略@
			}
			String value = request.getParameter(name);
			if (StringUtils.isNotBlank(value)) {
				value = value.trim();

				searchCondition.addCondition(name, value);
				String ef = searchCondition.getEchoField(name);
				
				if (CharHelper.containsChinese(ef)) {// 中文字段改为拼音回显
					ef = ef.replace("[", "").replace("]", "").replace(".", "");// 例如：a.[资产状态]
																				// ==》
																				// azczt
					// System.err.println(MyPinyinHelper.getFirstLetters(ef).toLowerCase());
					request.setAttribute(MyPinyinHelper.getFirstLetters(ef)
							.toLowerCase(), value);// 回显
				} else {
					request.setAttribute(ef, value);// 回显
				}
				
				request.setAttribute(ef, value);// 回显
			}

		}

		Boolean defaultCond = Boolean.valueOf(request.getParameter("@default"));
		if (defaultCond) {
			// 设置默认条件
			this.setDefaultCondition(request, searchCondition);
		}
		String reportId = request.getParameter("@id");
		ReportService reportService = (ReportService) wc
				.getBean("reportService");

		InputStream inputStream = null;
		List lines = null;
		List lines2 = null;
		try {
			inputStream = Thread.currentThread().getContextClassLoader()
					.getResourceAsStream(
							"config/sql/report_" + reportId + ".sql");

			if (inputStream != null) {
				lines = IOUtils.readLines(inputStream, "utf-8");
			}

			inputStream = Thread.currentThread().getContextClassLoader()
					.getResourceAsStream(
							"config/sql/report_" + reportId + ".link");
			if (inputStream != null) {
				lines2 = IOUtils.readLines(inputStream, "utf-8");
			}

		} catch (Exception e) {
			e.printStackTrace();

			request.getRequestDispatcher("/error505.jsp").forward(request,
					response);
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
		}

		String sqlFetch = StringUtils.join(lines, "\n");

		Integer pageLimit = null;
		Integer maxPageLimit = null;
		try {
			pageLimit = Integer.parseInt(request.getParameter("page_limit"));
		} catch (NumberFormatException e) {
			pageLimit = 100;
		}
		try {
			maxPageLimit = Integer.parseInt(request.getParameter("@maxPageLimit"));
		} catch (NumberFormatException e) {
			maxPageLimit = 1000;
		}
		if(maxPageLimit < 1){
			maxPageLimit = 1000;
		}
		
		if (pageLimit < 1) {
			pageLimit = 100;
		}else if (pageLimit > maxPageLimit) {
			pageLimit = maxPageLimit;
		}
		request.setAttribute("page_limit", pageLimit);

		//format: index:1:0; all is required
		String columnsDisplayed = request.getParameter("@columnsDisplayed");
		Report report = reportService.getReport(reportId, sqlFetch, lines2,
				searchCondition, columnsDisplayed, pageLimit);//columnsDisplayed 空不解析
		report.setId(reportId);

		if(StringUtils.isBlank(columnsDisplayed)){
			//FIXME get report x default displayed columns from account profile <account, reportId, columnsDisplayed>

			List<Boolean> columnsDisplayedlist = (List<Boolean>) request.getSession().getAttribute("columnsDisplayed" + reportId);
			if(columnsDisplayedlist == null){
				report.displayAllColumns();
			}else{
				report.setColumnsDisplayed(columnsDisplayedlist);
			}
		}

		request.getSession().setAttribute("columnsDisplayed" + reportId, report.getColumnsDisplayed());

		request.setAttribute("report", report);

		this.setSelectItems(request);

//		this.setJxList(request);//jx list 
		
		request.getRequestDispatcher(
				"/WEB-INF/report/report_" + reportId + ".jsp").forward(request,
				response);

	}

	/**
	 * 子类可以设置其他选项，用于下拉选择
	 * @param request
	 */
	protected void setSelectItems(HttpServletRequest request){

	}

	protected void setDefaultCondition(HttpServletRequest request,
			SearchConditions searchCondition) {
		// String key = "begin_date>=";
		// searchCondition.addCondition(key
		// ,DateHelper.getLastMonthStart(5));//半年内
		//
		// request.setAttribute(searchCondition.getFieldStart(key),
		// DateFormatUtils.format(searchCondition.getConditionValueAsDate(key),
		// "yyyy-MM-dd"));

		Enumeration parameterNames = request.getParameterNames();
		for (; parameterNames.hasMoreElements();) {
			String name = (String) parameterNames.nextElement();
			if (name.startsWith("@_")  ) {
				String key = name.substring(2);
				
				String value = request.getParameter(name);
				if (value != null && StringUtils.isNotBlank(value)) {
					value = value.trim();
					searchCondition.addCondition(key,value);
					String ef = searchCondition.getEchoField(key);
					//if is chinese?
					request.setAttribute(ef, value);
				}
				
			}
			
		}
	}
	/**
	 * reserved
	 * @param request
	 */
	public void setJxList(HttpServletRequest request){
		List<String>  jxList = (List<String>) request.getSession().getServletContext().getAttribute("jxList");
		if(jxList == null){//load jxList and set to app
			ReportService reportService = (ReportService) wc
					.getBean("reportService");
			jxList = reportService.getJxList();
		} 
	}
}
