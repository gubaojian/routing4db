package com.google.code.routing4db.strategy.impl;

import java.lang.reflect.Method;

import com.google.code.routing4db.strategy.RoutingStrategy;

public class NoneRoutingStrategy implements RoutingStrategy {

	public void route(Object target, Method method, Object[] args) {
		//DO noting
	}

}
