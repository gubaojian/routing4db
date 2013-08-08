package com.google.code.routing4db.strategy.impl;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.PatternMatchUtils;

import com.google.code.routing4db.holder.RoutingHolder;


/**
 * 实现Master-Slave路由策略。 其中Master是默认数据源， Slave根据默认数据源指定。
 * 写Master， 读Slave。 可通过把Master的key放到Slave中实现写Master，读Master-Slave
 * 多个Slave对象，则随机选择一个。
 * */
public class MasterSlaveStrategy  extends AbstractRoutingStrategy implements  InitializingBean{
	
	/**
	 * 读的方法列表， 采用正则表达式匹配, 仅支持*
	 * */
	private List<String> readMethodPatterns;
	
	/**
	 * Master数据源的key。 未设置则采用默认数据源作为master的数据源
	 * */
	private String masterDataSourceKey;
	
	
	
	/**
	 * 随机数, 随机选择一个Slave
	 * */
	private Random random;
	
	
	public MasterSlaveStrategy(){
		random = new Random();
	}

	/**
	 * 执行Master-Slave路由策略。如果是写，则选用master数据源，否则，从配置的数据源中随机选择一个进行读。
	 * */
	public void executeRoute(Object target, Method method, Object[] args) {
		boolean isReadMethod = false;
		String methodName = method.getName();
		for(String pattern : readMethodPatterns){
			if(PatternMatchUtils.simpleMatch(pattern, methodName)){
				isReadMethod = true;
				break;
			}
		}
		if(!isReadMethod){
			if(logger.isDebugEnabled()){
				logger.debug("method: " +  methodName + " --> routing to master datasource: " + masterDataSourceKey);
			}
			RoutingHolder.setCurrentDataSourceKey(masterDataSourceKey);
			return;
		}
		int mapKey = random.nextInt(dataSourceKeyMap.size());
		List<String> keys = dataSourceKeyMap.get(mapKey);
		int index = random.nextInt(keys.size());
		String slaveDataSourceKey = keys.get(index);
		if(logger.isDebugEnabled()){
		    logger.debug("method: " +  methodName + " --> routing to slave datasource: " + slaveDataSourceKey);
		}
		RoutingHolder.setCurrentDataSourceKey(slaveDataSourceKey);
	}


	public void setReadMethodPatterns(List<String> readMethodPatterns) {
		this.readMethodPatterns = ValidateUtils.validReadMethodPatterns(readMethodPatterns);
	}

	public void setMasterDataSourceKey(String masterDataSourceKey) {
		this.masterDataSourceKey = masterDataSourceKey;
	}

	public void afterPropertiesSet() throws Exception {
		if(readMethodPatterns == null || dataSourceKeyMap == null){
			throw new IllegalArgumentException("readMethodPatterns and slaveDataSourceKeyMap arugment must not be null");
		}
	}

}
