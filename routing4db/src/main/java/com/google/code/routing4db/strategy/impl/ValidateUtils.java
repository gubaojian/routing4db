package com.google.code.routing4db.strategy.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.StringUtils;

class ValidateUtils {
	

	public static List<String> validReadMethodPatterns(List<String> readMethodPatterns){
		if(readMethodPatterns == null){
			throw new IllegalArgumentException("readMethodPatterns arugment must not be null");
		}
		//spring's typical "xxx*", "*xxx" and "*xxx*" pattern styles.
		//仅仅支持上门的匹配格式
		List<String> compiledPattern = new ArrayList<String>(readMethodPatterns.size());
		for(String readMethodPattern : readMethodPatterns){
			if(StringUtils.countOccurrencesOf(readMethodPattern, "*") > 2){
				throw new IllegalArgumentException("readMethodPatterns only suppoer follows pattern style: \"xxx*\", \"*xxx\", \"*xxx*\" and \"xxx*yyy\"  must not be null");
			}
			int first = readMethodPattern.indexOf('*');
			int last = readMethodPattern.lastIndexOf('*');
			if(first >0 && last >0  && (first + 1) == last){
				throw new IllegalArgumentException("readMethodPatterns only suppoer follows pattern style: \"xxx*\", \"*xxx\", \"*xxx*\" and \"xxx*yyy\"  must not be null");
			}
			String tmp = readMethodPattern.trim();
			compiledPattern.add(tmp);
		}
		return compiledPattern;
	}

}
