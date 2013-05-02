package com.google.code.routing4db.proxy;

import java.lang.reflect.Proxy;

import com.google.code.routing4db.strategy.RoutingStrategy;

/**
 * 创建实际对象的代理，返回代理对象。
 * */
public abstract class RountingProxyFactory {
	
    /***
     * 返回对象的路由代理对象
     * */
	@SuppressWarnings("unchecked")
	public static <T> T proxy(Object target, Class<T> interfaceClass, RoutingStrategy routingStrategy) {
		if(target == null || interfaceClass == null || routingStrategy == null){
			throw new IllegalArgumentException("arugments must not be null");
		}
		RoutingInvocationHanlder handler = new RoutingInvocationHanlder(target, interfaceClass, routingStrategy);
		return (T) Proxy.newProxyInstance(handler.getClass().getClassLoader(), new Class[]{interfaceClass}, handler);
	}
	
}
