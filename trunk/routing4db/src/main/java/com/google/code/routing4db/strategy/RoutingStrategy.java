package com.google.code.routing4db.strategy;

import java.lang.reflect.Method;

/**
 * 数据源路由策略
 * */
public interface RoutingStrategy {
	
	/**
	 * 执行此策略，选择对应的数据源，并将其key设置到RoutingHolder中，如果采用默认数据源
	 * 则设置currentDataSourekey为null。
	 * */
	public void route(Object target, Method method, Object[] args);

}
