package unicom.common;


import java.io.UnsupportedEncodingException;

import org.apache.commons.lang.StringUtils;

public class CharHelper {
	public static String decodeUCS2(String src) {  
		if(StringUtils.isBlank(src)){
			return null;
		}

		try {
			return new String(src.getBytes(), "UnicodeBigUnmarked");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}

	}  


	public static String bytesToHexStr(byte[] b){
		if (b == null) return "";
		StringBuffer strBuffer = new StringBuffer(b.length * 3);
		for(int i = 0; i < b.length; i++)
		{
			strBuffer.append(Integer.toHexString(b[i] & 0xff));
			strBuffer.append(" ");
		}
		return strBuffer.toString();
	}


    // GENERAL_PUNCTUATION 判断中文的“号

    // CJK_SYMBOLS_AND_PUNCTUATION 判断中文的。号

    // HALFWIDTH_AND_FULLWIDTH_FORMS 判断中文的，号

    public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
            return true;
        }
        return false;
    }

    public static boolean isEnglish(char c){
        if(c >= 'a' && c<='z'){
            return true;
        }
        if(c >= 'A' && c<='Z'){
            return true;
        }
        return false;
    }

    public static boolean isNumber(char c){
        if(c >= '0' && c <= '9'){
            return true;
        }
        return false;
    }
    public static boolean isASCII(char c){
        if(c >= 20 && c<=126){
            return true;
        }
        return false;
    }
    public static boolean containsChinese(String s){
        for(int i = 0,n = s.length();i< n;i++){
            if(isChinese(s.charAt(i))){
                return true;
            }
        }

        return false;
    }

	/**
	 * 仅仅判断中英文
	 * @param str
	 * @return
	 */
	public static int countBytes(String str){
		if(StringUtils.isBlank(str)){
			return 0;
		}
		
		int c = 0;
		for (int i = 0; i < str.length(); i++) {
			if(isChinese(str.charAt(i))){
				c += 2;
			}else{
				c += 1;
			}
		}
		 
		return c;
	}
	
}
