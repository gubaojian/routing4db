package com.google.code.routing4db.datasource;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import javax.sql.DataSource;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.datasource.AbstractDataSource;
import org.springframework.jdbc.datasource.lookup.DataSourceLookup;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;

public class MasterStrandbyDataSource extends AbstractDataSource implements InitializingBean{

	/**
	 * 主库和备库数据源，
	 * */
	private Object masterDataSource;
	
	private Object standbyDataSource;
	
	
	private DataSourceLookup dataSourceLookup = new JndiDataSourceLookup();
	
	/**
	 * 解析后的标准数据源
	 * */
	private DataSource resolvedMasterDataSource;
	
	private DataSource resolvedStandbyDataSource;

	/**
	 * 当前可用连接
	 * */
	protected DataSource currentDataSource;
	

	/**
	 * 整体配置文件，所有配置属性通过此属性进行配置，如checkTimeInterval、checkAvailableSql
	 * */
	private Properties configProperties;
	
	
	/**单个配置字段， 暂时不对外公开，外面采用configProperties 进行配置*/
	/**
	 * 检查时间间隔, 单位ms 默认10秒
	 * */
	private long checkTimeInterval = 10000;
	
	/**
	 * 检查数据源是否可用的语句,默认是select 1 。 oracle下请修改为 select 1 from dual 
	 * */
	private String checkAvailableSql = "select 1";
	
	
	/**
	 * 无阻塞原子计数锁，用于数据源切换，确保有且仅有一个线程执行数据源切换
	 * */
	private AtomicInteger lock = new AtomicInteger(0);
	
	
	/**
	 * 重写获取连接的方法，在当前数据源不正常时，进行数据源切换
	 * */
	@Override
	public Connection getConnection() throws SQLException {
		DataSource sessionDataSource = this.getCurrentDataSource(); //本次获取连接的数据源
		try{
			return sessionDataSource.getConnection();
		}catch(SQLException sqle){
			 logger.error("Get Connection Exception " + currentDataSource , sqle);
			 if(sessionDataSource == this.getCurrentDataSource()){ //多线程环境有可能已经切换
				    this.switchToAvailableDataSource(); //自动切换
			 }
			 throw sqle;
		}
	}
	
	/**
	 * 重写获取连接的方法，在当前数据源不正常时，进行数据源切换
	 * */
	@Override
	public Connection getConnection(String username, String password)throws SQLException {
		DataSource sessionDataSource = this.getCurrentDataSource(); //本次获取连接的数据源
		try{
		  return sessionDataSource.getConnection(username, password);
		}catch(SQLException sqle){
			 logger.error("Get Connection With Args Exception " + currentDataSource , sqle);
			 if(sessionDataSource == this.getCurrentDataSource()){ //多线程环境有可能已经切换
			        this.switchToAvailableDataSource(); 
			 }
			 throw sqle;
		}
	}

		
	/**
	 * 检查数据源有效性，根据策略进行主备库切换。策略如下：
	 * 1、如果当前连接的是备库，检查主库是否可用，如果可用，切换到主库。
	 * 2、如果当前连接是主库，检查主库是否可用，如果不可用，切换到备库
	 * */
	protected void switchToAvailableDataSource(){
		try{
			if(lock.incrementAndGet() > 1){ //仅允许一个线程去检查数据源是否有效，并进行切换
				return;
			}
			
			if(currentDataSource == resolvedStandbyDataSource){
				if(this.isDataSourceAvailable(resolvedMasterDataSource)){
					currentDataSource = resolvedMasterDataSource;
				}
			}else{
				currentDataSource = resolvedMasterDataSource;
				if(!this.isDataSourceAvailable(resolvedMasterDataSource)){
					currentDataSource =  resolvedStandbyDataSource;
				}
			}
		}finally{
			lock.decrementAndGet();
		}
	}
	
	
	
	/**
	 * 检查连接是否可用, 必须吃掉所有异常。不准抛异常
	 * */
	protected boolean isDataSourceAvailable(DataSource dataSource){
		Connection  conn = null;
		try{
			 conn = dataSource.getConnection();
			 Statement stmt = conn.createStatement();
			 boolean success = stmt.execute(checkAvailableSql); //如果执行成功，会返回结果
			 stmt.close();
			 return success;
		}catch(SQLException e){
			logger.error("CheckDataSourceAvailable Exception", e);
			return false;
		}finally{
			if(conn != null){
				try {
					conn.close();
				} catch (SQLException e) {
					logger.error("Close Connection Exception", e);
				}
			}
		}
	}

	/**
	 * Resolve the specified data source object into a DataSource instance.
	 * <p>The default implementation handles DataSource instances and data source
	 * names (to be resolved via a {@link #setDataSourceLookup DataSourceLookup}).
	 * @param dataSource the data source value object as specified in the
	 * {@link #setTargetDataSources targetDataSources} map
	 * @return the resolved DataSource (never <code>null</code>)
	 * @throws IllegalArgumentException in case of an unsupported value type
	 */
	protected DataSource resolveSpecifiedDataSource(Object dataSource) throws IllegalArgumentException {
		if (dataSource instanceof DataSource) {
			return (DataSource) dataSource;
		}
		else if (dataSource instanceof String) {
			return this.dataSourceLookup.getDataSource((String) dataSource);
		}
		else {
			throw new IllegalArgumentException(
					"Illegal data source value - only [javax.sql.DataSource] and String supported: " + dataSource);
		}
	}
	
	/**
	 * Set the DataSourceLookup implementation to use for resolving data source
	 * name Strings in the {@link #setTargetDataSources targetDataSources} map.
	 * <p>Default is a {@link JndiDataSourceLookup}, allowing the JNDI names
	 * of application server DataSources to be specified directly.
	 */
	public void setDataSourceLookup(DataSourceLookup dataSourceLookup) {
		this.dataSourceLookup = (dataSourceLookup != null ? dataSourceLookup : new JndiDataSourceLookup());
	}
	
	protected DataSource getCurrentDataSource(){
		return currentDataSource;
	}
	
	public void setMasterDataSource(Object masterDataSource) {
		this.masterDataSource = masterDataSource;
	}

	public void setStandbyDataSource(Object standbyDataSource) {
		this.standbyDataSource = standbyDataSource;
	}
	

	public void setConfigProperties(Properties configProperties) {
		this.configProperties = configProperties;
	}
	

	
	@Override
	public void afterPropertiesSet() throws Exception {
		if (this.masterDataSource == null) {
			throw new IllegalArgumentException("Property 'masterDataSource' is required");
		}
		if(this.standbyDataSource == null){
			throw new IllegalArgumentException("Property 'standbyDataSource' is required");
		}
		
		if(configProperties != null){
			String checkTimeIntervalStr = configProperties.getProperty("checkTimeInterval");
			if(checkTimeIntervalStr == null){
				logger.info("configProperties --> checkTimeInterval property not config, use default " + checkTimeInterval);
			}else{
				checkTimeInterval = Long.parseLong(checkTimeIntervalStr);
				logger.info("configProperties --> checkTimeInterval property config value " + checkTimeInterval);
			}
			
			if(checkTimeInterval <= 0){ //检查时间间隔
				throw new IllegalArgumentException("Property checkTimeInterval must above zero");
			}
			
			String checkAvailableSqlStr = configProperties.getProperty("checkAvailableSql");
			if(checkAvailableSqlStr == null){
				logger.debug("configProperties --> checkAvailableSql property not config, use default sql( select 1). if you use oracle please config it with a right sql sucn as select 1 from dual");
			}else{
				checkAvailableSql =  checkAvailableSqlStr;
				logger.info("configProperties --> checkAvailableSql config sql is " + checkAvailableSql);
			}
		}else{
			logger.info("configProperties not configed, use default config");
			logger.info("configProperties --> checkAvailableSql property not config, use default " + checkAvailableSql);
			logger.info("configProperties --> checkTimeInterval property not config, use default " + checkTimeInterval);
		}
		
		//解析后的数据源
		resolvedMasterDataSource = this.resolveSpecifiedDataSource(masterDataSource);
		resolvedStandbyDataSource = this.resolveSpecifiedDataSource(standbyDataSource);
		currentDataSource  = this.resolvedMasterDataSource;
		//启动Daemon线程
		Thread thread = new CheckMasterAvailableDaemonThread();
		thread.start();
	}




	/**
	 * 检查线程，切换到备库后，如果主库可用，则切换到主库
	 * */
	private class CheckMasterAvailableDaemonThread extends Thread{
		public CheckMasterAvailableDaemonThread(){
			this.setDaemon(true);
			this.setName("MasterStandbyCheckMasterAvailableDaemonThread");
		}
		 @Override
		 public void run() {
			 while(true){
				 switchToAvailableDataSource();
				 try {
					Thread.sleep(checkTimeInterval);
				} catch (InterruptedException e) {
					logger.warn("Check Master InterruptedException", e);
				}
			 }
		 }
	}
	
	
}
