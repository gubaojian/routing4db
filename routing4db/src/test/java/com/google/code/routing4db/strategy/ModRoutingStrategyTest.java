package com.google.code.routing4db.strategy;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.util.ReflectionUtils;

import com.google.code.routing4db.dao.User;
import com.google.code.routing4db.dao.UserDao;
import com.google.code.routing4db.dao.UserDaoImpl;
import com.google.code.routing4db.holder.RoutingHolder;
import com.google.code.routing4db.strategy.impl.ModRoutingStrategy;

public class ModRoutingStrategyTest extends BaseRoutingStrategyTest{
	
	@Test
	public void testModRoutingDataSource(){
		ModRoutingStrategy strategy = new ModRoutingStrategy();
		Map<Integer, String> dataSourceKeyMap = new HashMap<Integer,String>();
		for(int i=0; i<8; i++){
			dataSourceKeyMap.put(i, "dataSource" + i);
		}
		strategy.setDataSourceKeyMap(dataSourceKeyMap);
		strategy.setDataSourceNum(dataSourceKeyMap.size());
		strategy.setPropertyName("id");
		UserDao userDao = new UserDaoImpl();
		Method insert = ReflectionUtils.findMethod(UserDao.class, "insert", User.class);
		Method getUserById = ReflectionUtils.findMethod(UserDao.class, "getUserById", null);
		for(int i=-10; i<100; i++){
			User user = new User();
			user.setId(i + 10L);
			//Ð´
			strategy.route(userDao, insert, new Object[]{user});
			Assert.assertTrue(RoutingHolder.getCurrentDataSourceKey().contains(Math.abs(user.getId())%dataSourceKeyMap.size() + ""));

			//¶Á
			strategy.route(userDao, getUserById, new Object[]{user.getId()});
			Assert.assertTrue(RoutingHolder.getCurrentDataSourceKey().contains(Math.abs(user.getId())%dataSourceKeyMap.size() + ""));

			
		}
		
		for(int i=-20; i<100; i++){
			User user = new User();
			user.setId(i + 5L);
			
			//Ð´
			strategy.route(userDao, insert, new Object[]{user});
			Assert.assertTrue(RoutingHolder.getCurrentDataSourceKey().contains(Math.abs(user.getId())%dataSourceKeyMap.size() + ""));

			
			
			//¶Á
			strategy.route(userDao, getUserById, new Object[]{user.getId()});
			Assert.assertTrue(RoutingHolder.getCurrentDataSourceKey().contains(Math.abs(user.getId())%dataSourceKeyMap.size() + ""));

			
		}

	}

}
