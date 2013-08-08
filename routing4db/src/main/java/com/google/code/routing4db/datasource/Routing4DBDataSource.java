package com.google.code.routing4db.datasource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import com.google.code.routing4db.holder.RoutingHolder;


/**
 * 常规数据源路由
 * */
public class Routing4DBDataSource extends AbstractRoutingDataSource {
	
	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	
	public Routing4DBDataSource(){
		this.setLenientFallback(false); // 根据key找不到对应数据源时，不进行容错处理，直接抛出找不到对应数据源的错误。 防止路由时因数据源配置不对，而才有默认数据源导致的错误
	}
	

	@Override
	protected Object determineCurrentLookupKey() {
		String dataSourceKey = RoutingHolder.getCurrentDataSourceKey();
		if(logger.isDebugEnabled()){
		   if(dataSourceKey == null){
			   logger.debug("none routing key, choose defaultDataSource for current connection");
		   }else{
			   logger.debug("choose dataSource for current connection by routing key " +  dataSourceKey );
		   }
		}
		return dataSourceKey;
	}
}
