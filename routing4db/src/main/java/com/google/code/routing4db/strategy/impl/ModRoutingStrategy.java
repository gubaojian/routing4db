package com.google.code.routing4db.strategy.impl;

import java.lang.reflect.Method;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;

import com.google.code.routing4db.exception.RoutingException;
import com.google.code.routing4db.holder.RoutingHolder;
import com.google.code.routing4db.util.ReflectionUtils;

/**
 * 按属性取模进行路由, 支持long int short byte类型。默认路由的master上。
 * */
public class ModRoutingStrategy  extends AbstractRoutingStrategy implements InitializingBean{

	/**
	 * 数据源取余的基数
	 * */
	private int dataSourceNum;
	
	/**
	 * 取余的属性名
	 * */
	private String propertyName;
	
	public  void executeRoute(Object target, Method method, Object[] args) {
		if(args.length == 0){
			 throw new IllegalArgumentException("method must have routing parameter and default mod routing strategy only support one routing parameter or one java bean paramter that contains routing property name");
		}
		if(args.length > 1){
			if(logger.isDebugEnabled()){
			  logger.debug(target + "--> method: " + method.getName() + "has mutl parameter, choose first parameter as routing parameter");
			}
		}
		
		Object routingArgs = args[0];
		long routingIdentify = 0;
		Class<?> type = routingArgs.getClass();
		if(this.isSupportType(type)){
			try{
				routingIdentify = Long.parseLong(routingArgs.toString());
			}catch(NumberFormatException nfe){
				throw new IllegalArgumentException("routing parameter type only support: long(Long), int(Integer),short(Short), byte(Byte) type.", nfe);
			}
		}else{
			Object value = ReflectionUtils.getFieldValue(routingArgs, propertyName);
			if(value == null){
				throw new IllegalArgumentException("java bean paramter must contains routing property name value that not null");
			}
		    Class<?> valueType = value.getClass();
		    if(!this.isSupportType(valueType)){
		    	throw new IllegalArgumentException("routing parameter type only support: long(Long), int(Integer),short(Short), byte(Byte) type.");
		    }
		    try{
				routingIdentify = Long.parseLong(value.toString());
			}catch(NumberFormatException nfe){
				throw new IllegalArgumentException("routing parameter type only support: long(Long), int(Integer),short(Short), byte(Byte) type.", nfe);
			}
		}

		int modKey = 0;
		if(routingIdentify < 0){
			routingIdentify = Math.abs(routingIdentify);
		}
		if(routingIdentify != 0){
			modKey =(int)(routingIdentify%dataSourceNum);
		}
		if(logger.isDebugEnabled()){
		   logger.debug(method.getName() + " routing parameter value --> " + routingIdentify  + "  mod value --> " + modKey);
		}
		this.routeForModValue(modKey, target, method, args);
	}

	
	/**
	 * 路由到对象的mod节点
	 * */
	protected void routeForModValue(int modKey, Object target, Method method, Object[] args){
		List<String> dataSources = dataSourceKeyMap.get(modKey);
		if(dataSources == null || dataSources.size() == 0){
			throw new RoutingException("Find no datasource for mod value : " + modKey);
		}
		//always return master ？
		String dataSourceKey = dataSources.get(0);
		if(logger.isDebugEnabled()){
			logger.debug("method: " + method.getName() + " --> reslove routing parameter mod value: " + modKey + " --> routing to datasource: " + dataSourceKey);
		}
		RoutingHolder.setCurrentDataSourceKey(dataSourceKey); // always return master
	}

	
	/**
	 * 支持的类型
	 * */
	protected boolean isSupportType(Class<?> type){
		return type.isPrimitive()
		|| type == Long.class
		|| type == Integer.class
		|| type == Short.class
		|| type == Byte.class;
	}
	
	
	
	
	public void setDataSourceNum(int dataSourceNum) {
		this.dataSourceNum = dataSourceNum;
	}


	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName.trim();
	}

	public void afterPropertiesSet() throws Exception {
	   if( dataSourceNum <= 0 || propertyName == null){
		   throw new IllegalArgumentException("dataSourceNum must be bigger than zero and propertyName must not be null.");
	   }
	   
	   if(dataSourceNum != dataSourceKeyMap.size()){
		   throw new IllegalArgumentException("dataSourceNum must be equal with dataSourceKeyMap size");
	   }
	}

}
