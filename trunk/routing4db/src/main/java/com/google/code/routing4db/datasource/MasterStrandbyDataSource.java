package com.google.code.routing4db.datasource;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.datasource.AbstractDataSource;
import org.springframework.jdbc.datasource.lookup.DataSourceLookup;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;

public class MasterStrandbyDataSource extends AbstractDataSource implements InitializingBean{

	/**
	 * 主库和备库，
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
	 * 检查时间间隔, 单位ms 默认10秒
	 * */
	private long checkTimeInterval = 10000;
	/**
	 * 整体配置文件
	 * */
	private Properties configProperties;
	
	@Override
	public Connection getConnection() throws SQLException {
		try{
			return this.getCurrentDataSource().getConnection();
		}catch(SQLException sqle){
			 logger.error("Get Connection Exception " + currentDataSource , sqle);
			 this.switchToAvailableDataSource(); //自动切换
			 throw sqle;
		}
	}

	@Override
	public Connection getConnection(String username, String password)throws SQLException {
		try{
		  return this.getCurrentDataSource().getConnection(username, password);
		}catch(SQLException sqle){
			 logger.error("Get Connection With Args Exception " + currentDataSource , sqle);
			 this.switchToAvailableDataSource(); //自动切换
			 throw sqle;
		}
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
			if(checkTimeIntervalStr != null){
				checkTimeInterval = Long.parseLong(checkTimeIntervalStr);
			}
		}
		//解析后的数据源
		resolvedMasterDataSource = this.resolveSpecifiedDataSource(masterDataSource);
		resolvedStandbyDataSource = this.resolveSpecifiedDataSource(standbyDataSource);
		currentDataSource  = this.resolvedMasterDataSource;
		Thread thread = new CheckMasterAvailableDaemonThread();
		thread.start();
	}


	
	/**
	 * 如果未连接备库，如果主库可用，连接主库，如果不可用，否则连接备库. 
	 * 如果已经连接到备库，如果主库可用，切换到主库
	 * */
	protected void switchToAvailableDataSource(){
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
	}
	
	
	
	/**
	 * 检查连接是否可用, 必须吃掉所有异常。不准抛异常
	 * */
	protected boolean isDataSourceAvailable(DataSource dataSource){
		Connection  conn = null;
		String select = "select 1";
		try{
			conn = dataSource.getConnection();
			 Statement stmt = conn.createStatement();
			 if(stmt.execute(select)){
				 return true;
			 }
			 stmt.close();
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
		return false;
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
