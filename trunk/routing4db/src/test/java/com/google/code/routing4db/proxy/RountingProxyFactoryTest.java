package com.google.code.routing4db.proxy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.code.routing4db.dao.User;
import com.google.code.routing4db.dao.UserDao;
import com.google.code.routing4db.dao.UserDaoImpl;
import com.google.code.routing4db.holder.RoutingHolder;
import com.google.code.routing4db.strategy.impl.ModMasterSlaveRoutingStrategy;

import junit.framework.Assert;
import junit.framework.TestCase;

public class RountingProxyFactoryTest extends TestCase{
	
	
	
	public void testRoutingProxy(){
		ModMasterSlaveRoutingStrategy strategy = new ModMasterSlaveRoutingStrategy();
		Map<Integer, String> dataSourceKeyMap = new HashMap<Integer,String>();
		for(int i=0; i<8; i++){
			String slaves = ",slavea" + i;
			 slaves += ",slaveb" + i;
			 slaves += ",slavec" + i;
			dataSourceKeyMap.put(i, "masterDataSource" + i   +  slaves);
		}
		strategy.setDataSourceKeyMap(dataSourceKeyMap);
		strategy.setDataSourceNum(dataSourceKeyMap.size());
		strategy.setPropertyName("id");
		
		List<String> readMethodPatterns = new ArrayList<String>();
		readMethodPatterns.add("get*");
		strategy.setReadMethodPatterns(readMethodPatterns);
		
		UserDao userDao = new UserDaoImpl();
		UserDao proxyUserDao = RountingProxyFactory.proxy(userDao, UserDao.class, strategy);
		
		//直接调用
		for(int i=-10; i<10; i++){
			User user = new User();
			long id = i;
			user.setId(id);
			RoutingHolder.setCurrentDataSourceKey("DataSource1");
			Assert.assertNotNull(RoutingHolder.getCurrentDataSourceKey());
			userDao.insert(user);
			Assert.assertNotNull(RoutingHolder.getCurrentDataSourceKey());
			
			
			Assert.assertNotNull(RoutingHolder.getCurrentDataSourceKey());
			userDao.getUserById(id);
			Assert.assertNotNull(RoutingHolder.getCurrentDataSourceKey());
		}
		
		//代理调用
		for(int i=-100; i<100; i++){
			User user = new User();
			long id = i;
			user.setId(id);
			RoutingHolder.setCurrentDataSourceKey("DataSource1");
			Assert.assertNotNull(RoutingHolder.getCurrentDataSourceKey());
			proxyUserDao.insert(user);
			Assert.assertNull(RoutingHolder.getCurrentDataSourceKey());
			
	
			
			RoutingHolder.setCurrentDataSourceKey("DataSource1");
			Assert.assertNotNull(RoutingHolder.getCurrentDataSourceKey());
			proxyUserDao.getUserById(id);
			Assert.assertNull(RoutingHolder.getCurrentDataSourceKey());
		}
		
	}

}
