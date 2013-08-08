package com.google.code.routing4db.strategy;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;
import org.springframework.util.ReflectionUtils;

import com.google.code.routing4db.dao.User;
import com.google.code.routing4db.dao.UserDao;
import com.google.code.routing4db.dao.UserDaoImpl;
import com.google.code.routing4db.holder.RoutingHolder;
import com.google.code.routing4db.strategy.impl.MasterSlaveStrategy;

public class MasterSlaveRoutingStrategyTest extends BaseRoutingStrategyTest{
	
	
	
	
	
	
	@Test
	public void testMasterSlave(){
		MasterSlaveStrategy strategy =  (MasterSlaveStrategy)this.createMasterSlaveRoutingStrategy();
		
		strategy.setMasterDataSourceKey(null);
		UserDao userDao = new UserDaoImpl();
		Method insert = ReflectionUtils.findMethod(UserDao.class, "insert", User.class);
		Method getUserById = ReflectionUtils.findMethod(UserDao.class, "getUserById", null);
		for(int i=0; i<100; i++){
			User user = new User();
			user.setId(i + 10L);
			//Ð´
			 RoutingHolder.setCurrentDataSourceKey(null);
			 Assert.assertNull(RoutingHolder.getCurrentDataSourceKey());	
			 strategy.route(userDao, insert, new Object[]{});
			 Assert.assertNull(RoutingHolder.getCurrentDataSourceKey());	
			
			//¶Á
			 RoutingHolder.setCurrentDataSourceKey(null);
			 strategy.route(userDao, getUserById, new Object[]{i});
			 Assert.assertTrue(RoutingHolder.getCurrentDataSourceKey().contains("slave"));
			
		}
		
		String masterDataSource = "masterDataSource";
		strategy.setMasterDataSourceKey(masterDataSource);
		for(int i=0; i<100; i++){
			User user = new User();
			user.setId(i + 10L);
			
			//Ð´
			strategy.route(userDao, insert, new Object[]{user});
			Assert.assertEquals(masterDataSource, RoutingHolder.getCurrentDataSourceKey());
			
			
			//¶Á
			strategy.route(userDao, getUserById, new Object[]{i});
			Assert.assertTrue(RoutingHolder.getCurrentDataSourceKey().contains("slave"));
			
		}
	}

}
