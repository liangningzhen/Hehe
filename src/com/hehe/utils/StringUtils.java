package com.hehe.utils;

public class StringUtils {
	
	
	public static String AddURL(int page) {
		StringBuffer sb = new StringBuffer();
		sb.append(Constants.URL_).append(String.valueOf(page)).append(Constants.htm);
		return sb.toString();
	}
	
}
