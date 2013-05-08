package com.google.code.routing4db.strategy;

import java.lang.reflect.Method;

/**
 * 数据源路由策略
 * */
public interface RoutingStrategy {
	/**
	 * 执行此策略，选择对应的数据源，并将其key设置到RoutingHolder中，如果未设置，则采用默认数据源
	 * @param  target   代理的DAO对象
	 * @param  method   DAO对象上执行的方法
	 * @param  args     方法执行所需的参数
	 * */
	public void route(Object target, Method method, Object[] args);

}
