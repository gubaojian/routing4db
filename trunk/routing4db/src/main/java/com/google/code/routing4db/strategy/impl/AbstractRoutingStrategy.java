package com.google.code.routing4db.strategy.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.routing4db.strategy.RoutingStrategy;

public abstract class AbstractRoutingStrategy implements RoutingStrategy{

	/**
	 * 节点的编号及数据源key的映射。编号从零开始，依次递增。
	 * 一个编号下，对应多个key可用逗号分隔
	 * */
	protected Map<Integer,List<String>> dataSourceKeyMap;
	
	/**
	 * logger
	 * */
	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	/**
	 * 设置数据源映射
	 * */
	public void setDataSourceKeyMap(Map<Integer, String> dataSourceKeyMap) {
		if(dataSourceKeyMap == null){
			throw new IllegalArgumentException("slaveDataSourceKeyMap arugment must not be null");
		}
		if(dataSourceKeyMap.size() <= 0){
			throw new IllegalArgumentException("slaveDataSourceKeyMap size must be big than zero");
		}
		//check num
	    for(int i=0; i<dataSourceKeyMap.size(); i++){
	    	if(dataSourceKeyMap.get(i) == null){
	    		throw new IllegalArgumentException("slaveDataSourceKeyMap must be num and datasource key map. and num must start with zero and inscrment serial. such 0 -->ka  1--> kb 2-->kc  ");
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
