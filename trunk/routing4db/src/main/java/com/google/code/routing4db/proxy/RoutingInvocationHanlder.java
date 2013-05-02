package com.google.code.routing4db.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.google.code.routing4db.holder.RoutingHolder;
import com.google.code.routing4db.strategy.RoutingStrategy;


/**
 * 动态代理拦截接口的请求
 * */
public class RoutingInvocationHanlder implements InvocationHandler {

	/**
	 * 代理对象
	 * */
	private Object proxyTarget;
	
	/**
	 * 代理接口上的方法
	 * */
	private Map<Method,Object> proxyInterfaceMethods;
	
	/**
	 * 路由策略
	 * */
	private RoutingStrategy routingStrategy;
	
	
	/**
	 * @param proxyTarget 代理对象
	 * @param interfaceClass 接口class
	 * @param routingStrategy 路由策略
	 * */
	public RoutingInvocationHanlder(Object proxyTarget,Class<?> interfaceClass, RoutingStrategy routingStrategy) {
		super();
		if(proxyTarget == null){
			throw new IllegalArgumentException("proxyTarget must not be null");
		}
		if(interfaceClass == null){
			throw new IllegalArgumentException("interfaceClass must be interface class");
		}
		if(routingStrategy == null){
			throw new IllegalArgumentException("routingStrategy must not be null");
		}
		
		this.proxyTarget = proxyTarget;
		this.routingStrategy = routingStrategy;
		
		/**
		 * 处理接口上的方法，仅对接口上的方法执行路由逻辑
		 * */
		if(!interfaceClass.isInterface()){ 
			throw new IllegalArgumentException("interfaceClass must be interface class");
		}
		proxyInterfaceMethods = new HashMap<Method,Object>();
		for(Method method : interfaceClass.getMethods()){
			proxyInterfaceMethods.put(method, null);
		}
		for(Class<?> parentInterface : interfaceClass.getInterfaces()){
			for(Method method : parentInterface.getMethods()){
				proxyInterfaceMethods.put(method, null);
			}
		}
	}

    /**
     * 对接口上的方法执行路由逻辑，然后委托给实际对象，其它方法直接委托给代理对象。
     * */
	public Object invoke(Object proxy, Method method, Object[] args)throws Throwable {
		if(!proxyInterfaceMethods.containsKey(method)){
			return method.invoke(proxyTarget, args);
		}
		try{
			routingStrategy.route(proxyTarget, method, args);
			return method.invoke(proxyTarget, args);
		}finally{
			RoutingHolder.clean();
		}
	}

}
