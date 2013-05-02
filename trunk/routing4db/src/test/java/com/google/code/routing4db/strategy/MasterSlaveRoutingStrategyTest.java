package com.google.code.routing4db.strategy;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;
import org.springframework.util.ReflectionUtils;

import com.google.code.routing4db.holder.RoutingHolder;
import com.google.code.routing4db.strategy.impl.MasterSlaveStrategy;

public class MasterSlaveRoutingStrategyTest extends BaseRoutingStrategyTest{
	
	
	
	@Test
	public void testMasterSlave(){
		MasterSlaveStrategy strategy = new MasterSlaveStrategy();
		Map<Integer, String> dataSourceKeyMap = new HashMap<Integer,String>();
		for(int i=0; i<6; i++){
			dataSourceKeyMap.put(i, "slaves" + i);
		}
		
		List<String> readMethodPatterns = new ArrayList<String>();
		readMethodPatterns.add("get*");
		strategy.setDataSourceKeyMap(dataSourceKeyMap);
		strategy.setReadMethodPatterns(readMethodPatterns);
		UserDao userDao = new UserDaoImpl();
		Method insert = ReflectionUtils.findMethod(UserDao.class, "insert", User.class);
		Method getUserById = ReflectionUtils.findMethod(UserDao.class, "getUserById", null);
		for(int i=0; i<100; i++){
			User user = new User();
			user.setId(i + 10L);
			//Ð´
			strategy.route(userDao, insert, new Object[]{user});
			Assert.assertNull(RoutingHolder.getCurrentDataSourceKey());
			
			//¶Á
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
