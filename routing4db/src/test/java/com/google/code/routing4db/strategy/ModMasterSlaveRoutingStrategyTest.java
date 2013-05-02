package com.google.code.routing4db.strategy;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.util.ReflectionUtils;

import com.google.code.routing4db.holder.RoutingHolder;
import com.google.code.routing4db.strategy.impl.ModMasterSlaveRoutingStrategy;

public class ModMasterSlaveRoutingStrategyTest extends  BaseRoutingStrategyTest{
	
	
	
	@Test
	public void testRouting(){
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
		Method insert = ReflectionUtils.findMethod(UserDao.class, "insert", User.class);
		Method getUserById = ReflectionUtils.findMethod(UserDao.class, "getUserById", null);
		for(int i=-10; i<100; i++){
			User user = new User();
			user.setId(i + 10L);
			//Ð´
			strategy.route(userDao, insert, new Object[]{user});
			Assert.assertTrue(RoutingHolder.getCurrentDataSourceKey().contains(Math.abs(user.getId())%dataSourceKeyMap.size() + ""));
			Assert.assertTrue(RoutingHolder.getCurrentDataSourceKey().contains("master"));
			
			//¶Á
			strategy.route(userDao, getUserById, new Object[]{user.getId()});
			Assert.assertTrue(RoutingHolder.getCurrentDataSourceKey().contains(Math.abs(user.getId())%dataSourceKeyMap.size() + ""));
			Assert.assertTrue(RoutingHolder.getCurrentDataSourceKey().contains("slave"));
			
		}
		
		for(int i=-20; i<100; i++){
			User user = new User();
			user.setId(i + 5L);
			
			//Ð´
			strategy.route(userDao, insert, new Object[]{user});
			Assert.assertTrue(RoutingHolder.getCurrentDataSourceKey().contains(Math.abs(user.getId())%dataSourceKeyMap.size() + ""));
			Assert.assertTrue(RoutingHolder.getCurrentDataSourceKey().contains("master"));
			
			
			//¶Á
			strategy.route(userDao, getUserById, new Object[]{user.getId()});
			Assert.assertTrue(RoutingHolder.getCurrentDataSourceKey().contains(Math.abs(user.getId())%dataSourceKeyMap.size() + ""));
			Assert.assertTrue(RoutingHolder.getCurrentDataSourceKey().contains("slave"));
		}

	}

}
