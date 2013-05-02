package com.google.code.routing4db.mybatis;

import org.mybatis.spring.mapper.MapperFactoryBean;

import com.google.code.routing4db.proxy.RountingProxyFactory;
import com.google.code.routing4db.strategy.RoutingStrategy;

public class RoutingMapperFactoryBean<T> extends MapperFactoryBean<T> {

	/**
	 * 路由规则
	 * */
	private RoutingStrategy routingStrategy;
	
	@Override
	public T getObject() throws Exception {
		T target = super.getObject();
		Class<T> interfaceClass = this.getObjectType();
		return RountingProxyFactory.proxy(target, interfaceClass, routingStrategy);
	}


	
	public void setRoutingStrategy(RoutingStrategy routingStrategy) {
		this.routingStrategy = routingStrategy;
	}
	
	 @Override
 	 protected void checkDaoConfig() {
		 super.checkDaoConfig();
		 if(routingStrategy == null){
			  throw new IllegalArgumentException("routingStrategy must not be null");
		 }
	 }
	
}
