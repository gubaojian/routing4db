package com.google.code.routing4db.holder;


/**
 * 存放当前数据源路由对应的key
 * */
public class RoutingHolder {
	
	/**
	 * 放置数据源路由对应的key
	 * */
	private static final ThreadLocal<String> routingKeyHolder = new ThreadLocal<String>();
	
	public static String getCurrentDataSourceKey(){
		return routingKeyHolder.get();
	}
	
	/**
	 * 设置数据源的路由key
	 * */
	public static void setCurrentDataSourceKey(String dataSourceKey){
		routingKeyHolder.set(dataSourceKey);
	}
	
	public static void clean(){
		routingKeyHolder.set(null);
	}

}
