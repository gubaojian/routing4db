package com.google.code.routing4db.datasource;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import com.google.code.routing4db.holder.RoutingHolder;


/**
 * 数据源路由
 * */
public class Routing4DBDataSource extends AbstractRoutingDataSource {

	@Override
	protected Object determineCurrentLookupKey() {
		return RoutingHolder.getCurrentDataSourceKey();
	}

}
