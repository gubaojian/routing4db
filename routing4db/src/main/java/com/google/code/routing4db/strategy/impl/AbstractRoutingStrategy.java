package com.google.code.routing4db.strategy.impl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.PatternMatchUtils;
import org.springframework.util.StringUtils;

import com.google.code.routing4db.strategy.RoutingStrategy;

public abstract class AbstractRoutingStrategy implements RoutingStrategy{

	/**
	 * 节点的编号及数据源key的映射。编号从零开始，依次递增。
	 * 一个编号下，对应多个key可用逗号分隔
	 * */
	protected Map<Integer,List<String>> dataSourceKeyMap;
	
	/**
	 * 不执行路由的方法列表， 采用正则表达式匹配, 仅支持*
	 * */
	private List<String> excludeMethodPatterns;
	
	/**
	 * logger
	 * */
	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	
	/**
	 * 过滤方法列表，对于列表中得方法不执行路由
	 * */
	public  void route(Object target, Method method, Object[] args) {
		boolean needRoute = true;
		if(excludeMethodPatterns != null){ //判断知否需要执行路由
			String methodName = method.getName();
			for(String pattern : excludeMethodPatterns){
				if(PatternMatchUtils.simpleMatch(pattern, methodName)){
					needRoute = false;
					break;
				}
			}
		}
		if(needRoute){
			this.executeRoute(target, method, args);
		}else{
			if(logger.isDebugEnabled()){
				logger.debug(method.getName() + " match excludeMethodPatterns for routing, no routing for this method");
			}
	   }
	}
	
	/**
	 * 对于需要执行路由的方法执行路由
	 * */
	public abstract void executeRoute(Object target, Method method, Object[] args);
	
	
	
	/**
	 * 设置需要接口中不进行路由的方法列表, 仅支持这几种形式  "xxx*", "*xxx" and "*xxx*"
	 * */
	public void setExcludeMethodPatterns(List<String> excludeMethodPatterns) {
        if(excludeMethodPatterns != null){
        	//spring's typical "xxx*", "*xxx" and "*xxx*" pattern styles.
    		//仅仅支持上门的匹配格式
    		List<String> compiledPattern = new ArrayList<String>(excludeMethodPatterns.size());
    		for(String readMethodPattern : excludeMethodPatterns){
    			if(StringUtils.countOccurrencesOf(readMethodPattern, "*") > 2){
    				throw new IllegalArgumentException("excludeMethodPatterns only suppoer follows pattern style: \"xxx*\", \"*xxx\", \"*xxx*\" and \"xxx*yyy\"  must not be null");
    			}
    			int first = readMethodPattern.indexOf('*');
    			int last = readMethodPattern.lastIndexOf('*');
    			if(first >0 && last >0  && (first + 1) == last){
    				throw new IllegalArgumentException("excludeMethodPatterns only suppoer follows pattern style: \"xxx*\", \"*xxx\", \"*xxx*\" and \"xxx*yyy\"  must not be null");
    			}
    			String tmp = readMethodPattern.trim();
    			compiledPattern.add(tmp);
    		}
        }
		this.excludeMethodPatterns = excludeMethodPatterns;
	}

	/**
	 * 设置实际数据源与key的映射
	 * */
	public void setDataSourceKeyMap(Map<Integer, String> dataSourceKeyMap) {
		if(dataSourceKeyMap == null){
			throw new IllegalArgumentException("dataSourceKeyMap arugment must not be null");
		}
		if(dataSourceKeyMap.size() <= 0){
			throw new IllegalArgumentException("dataSourceKeyMap size must be big than zero");
		}
		//check num
	    for(int i=0; i<dataSourceKeyMap.size(); i++){
	    	if(dataSourceKeyMap.get(i) == null){
	    		throw new IllegalArgumentException("dataSourceKeyMap key must be serial num start with zero, ends with dataSourceKeyMap.size()-1. such 0 -->ka  1--> kb 2-->kc ");
	    	}
	    }
	    
	    //解析逗号分隔
	    Map<Integer,List<String>> dataSourceKeyMapTarget = new HashMap<Integer,List<String>>();
	    for(Entry<Integer,String> entry : dataSourceKeyMap.entrySet()){
	    	Integer num = entry.getKey();
	    	String value = entry.getValue();
	    	String[] values = value.split(",");
	    	List<String> valueList = new ArrayList<String>(values.length);
	    	for(String vl : values){
	    		if(vl.trim().length() == 0){
	    			continue;
	    		}
	    		valueList.add(vl.trim());
	    	}
	    	if(valueList.size() == 0){
	    		throw new IllegalArgumentException("key " + num + " --> must have dataSources");
	    	}
	    	dataSourceKeyMapTarget.put(num, valueList);
	    }

		this.dataSourceKeyMap = dataSourceKeyMapTarget;
	}

}
