package com.google.code.routing4db.strategy.impl;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Random;

import org.springframework.util.PatternMatchUtils;

import com.google.code.routing4db.holder.RoutingHolder;

public class ModMasterSlaveRoutingStrategy extends ModRoutingStrategy{
	
	/**
	 * 读的方法列表， 采用正则表达式匹配, 仅支持*
	 * */
	private List<String> readMethodPatterns;
	

	/**
	 * 随机数, 随机选择一个Slave
	 * */
	private Random random = new Random();

	@Override
	protected void routeForModValue(int modKey, Object target, Method method,
			Object[] args) {
		List<String> dataSources = dataSourceKeyMap.get(modKey);
		String methodName = method.getName();

		//多个datasource，选择性路由
		boolean isReadMethod = false;
		for(String pattern : readMethodPatterns){
			if(PatternMatchUtils.simpleMatch(pattern, methodName)){
				isReadMethod = true;
				break;
			}
		}

		//write to master
		if(!isReadMethod){
			String masterDataSource = dataSources.get(0);  // always return master
			logger.debug("method: " +  methodName + " --> reslove routing parameter mod value: " + modKey + " --> routing to master datasource: " + masterDataSource);
			RoutingHolder.setCurrentDataSourceKey(masterDataSource);
			return;
		}
		
		
		//如果是read方法，从slave列表中随机选择一个
		int index = random.nextInt(dataSources.size() - 1);
		String slaveDataSourceKey = dataSources.get(index + 1); 
		logger.debug("method: " +  methodName+ " --> reslove routing parameter mod value: " + modKey +  " --> routing to slave datasource: " + slaveDataSourceKey);
		RoutingHolder.setCurrentDataSourceKey(slaveDataSourceKey);
	}



	public void afterPropertiesSet() throws Exception {
		super.afterPropertiesSet();
		if(readMethodPatterns == null){
			throw new IllegalArgumentException("readMethodPatterns  arugment must not be null");
		}
	}


	public void setReadMethodPatterns(List<String> readMethodPatterns) {
		this.readMethodPatterns = StrategyUtils.validReadMethodPatterns(readMethodPatterns);
	}

}
