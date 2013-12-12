package unicom.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 用于本地调用
 * @author yexy6
 *
 *
 * FIXME @see http://commons.apache.org/exec/
 */
public class ProcessHelper {
	/**
	 * Logger for this class
	 */
	private static final Log logger = LogFactory.getLog(ProcessHelper.class);

	public static void call(File workingDir , List<String> command){
		call(workingDir, command, System.getProperty("file.encoding"));
	}
	/**
	 *
	 * @param workingDir
	 * @param command
	 * @param inputEncoding
	 */
	public static void call(File workingDir , List<String> command, String inputEncoding){
		if(command == null || command.size() == 0){
			logger.error("未输入执行命令。");
			return ;
		}
		logger.debug("执行命令:\t" + StringUtils.join(command," "));
		logger.debug("\t--起始目录:\t" + workingDir.getAbsolutePath());


		ProcessBuilder builder = new ProcessBuilder();

		builder.command(command);
		builder.directory(workingDir);
		builder.redirectErrorStream(true);

		Process process = null;

		logger.debug("****程序开始执行：" + command.get(0) + " ****");
		try {
			process = builder.start();
		} catch (IOException e) {
			logger.error("程序启动失败：" + StringUtils.join(command," "), e);
			return;
		}
		InputStream inputStream = process.getInputStream();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(inputStream,inputEncoding));
		} catch (UnsupportedEncodingException e) {
			logger.error("无法显示执行结果： 不支持编码：" + inputEncoding , e);
		}
		LineIterator lineIterator = IOUtils.lineIterator(reader);

		while(lineIterator.hasNext()){
			String nextLine = lineIterator.nextLine();
			logger.debug(nextLine);
		}
		logger.debug("----程序执行结束：" + command.get(0) + " ----");

		LineIterator.closeQuietly(lineIterator);
	}

	/**
	 * teminated by space!
	 * note: cmd "c:\doc and abc\test.txt" is of no use!
	 * @param cmdstr
	 * @return
	 */
	public static List<String> toCommand(String cmdstr){
		if(StringUtils.isBlank(cmdstr)){
			return new ArrayList<String>(0);
		}

		return Arrays.asList(cmdstr.trim().split("\\s+"));
	}

	public static void main(String[] args) {

		List<String> command = new ArrayList<String>();
		command.add("cmd");
		command.add("/C");//[the window]Carries out the command specified by String and then stops.
		command.add("notepad");
//		command.add("");
		File workingDir = new File(".");

		ProcessHelper.call(workingDir, command);

		workingDir = new File("config/bat");
		command.clear();
		command.add("cmd");
		command.add("/C");
		command.add("test.bat");
		command.add("test ~param");
		command.add("test !@#$%");
		ProcessHelper.call(workingDir, command);
	}
}
