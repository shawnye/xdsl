package unicom.common;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MyPinyinHelper {
	/**
	 * Logger for this class
	 */
	private static final Log logger = LogFactory.getLog(MyPinyinHelper.class);

	private MyPinyinHelper() {
	}

	/**
	 *
	 * @param src
	 * @param delim
	 * @param camel
	 * @return
	 * 默认小写
	 */
	public static String getPinyin(String src, String delim, boolean camel) {
		if (StringUtils.isBlank(src)) {
			return "";
		}
		char[] srcChar;
		srcChar = src.toCharArray();

		HanyuPinyinOutputFormat hanYuPinOutputFormat = new HanyuPinyinOutputFormat();
		// 输出设置，大小写，音标方式等
		hanYuPinOutputFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);// eg:li
		hanYuPinOutputFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);// eg:
																			// li
																			// not
																			// li3
		hanYuPinOutputFormat.setVCharType(HanyuPinyinVCharType.WITH_V);// eg: lv

		// char[] temp = new char[srcChar.length];
		StringBuffer temp = new StringBuffer();
		for (int i = 0; i < srcChar.length; i++) {
			char c = srcChar[i];
			try {
				String[] hanyuPinyinStringArray = PinyinHelper
						.toHanyuPinyinStringArray(c,
								hanYuPinOutputFormat);
				if (hanyuPinyinStringArray == null) {
					temp.append(c);
				} else {
					if(delim != null && temp.length() > 0){
						temp.append(delim);
					}
					for (int j = 0; j < hanyuPinyinStringArray.length; j++) {//
						for (int k = 0; k < hanyuPinyinStringArray[j].length(); k++) {
							if(camel && k == 0){
								temp.append(Character.toUpperCase(hanyuPinyinStringArray[j].charAt(k)));
							}else{
								temp.append(hanyuPinyinStringArray[j].charAt(k));
							}
						}
					}
				}
			} catch (BadHanyuPinyinOutputFormatCombination e) {
				logger.error("不能转换为拼音：" + src, e);
			} catch (Exception e) {
				logger.error("不能转换为拼音：" + src, e);
			}
		}

		return temp.toString();
	}

	public static String getFirstLetter(char src) {
		return getFirstLetters(String.valueOf(src));
	}
	/**
	 *
	 * @param src
	 * @return
	 * 默认大写
	 */
	public static String getFirstLetters(String src) {
		if (StringUtils.isBlank(src)) {
			return "";
		}
		char[] srcChar;
		srcChar = src.toCharArray();

		HanyuPinyinOutputFormat hanYuPinOutputFormat = new HanyuPinyinOutputFormat();
		// 输出设置，大小写，音标方式等
		hanYuPinOutputFormat.setCaseType(HanyuPinyinCaseType.UPPERCASE);// eg:
																		// LI
		hanYuPinOutputFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);// eg:
																			// li
																			// not
																			// li3
		hanYuPinOutputFormat.setVCharType(HanyuPinyinVCharType.WITH_V);// eg: lv

		char[] temp = new char[srcChar.length];
		for (int i = 0; i < srcChar.length; i++) {
			char c = srcChar[i];
			try {
				String[] hanyuPinyinStringArray = PinyinHelper
						.toHanyuPinyinStringArray(srcChar[i],
								hanYuPinOutputFormat);
				if (hanyuPinyinStringArray == null) {
					temp[i] = c;
				} else {
					temp[i] = hanyuPinyinStringArray[0].charAt(0);// 首字母
				}
			} catch (BadHanyuPinyinOutputFormatCombination e) {
				logger.error("不能转换为拼音首字母：" + src, e);
			} catch (Exception e) {
				logger.error("不能转换为拼音：" + src, e);
			}
		}

		return String.valueOf(temp);
	}

	public static void main(String[] args) {
		System.out.println("首字母：" + MyPinyinHelper.getFirstLetters("我爱 手机HTC IPHONE"));
		System.out.println("全拼(camel)：" + MyPinyinHelper.getPinyin("我爱 手机HTC"," ", true));
		System.out.println("全拼：" + MyPinyinHelper.getPinyin("我爱  手机HTC",",", false));

	}
}
