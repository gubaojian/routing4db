package com.google.code.routing4db.strategy;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.util.ReflectionUtils;

import com.google.code.routing4db.dao.User;
import com.google.code.routing4db.dao.UserDao;
import com.google.code.routing4db.dao.UserDaoImpl;
import com.google.code.routing4db.holder.RoutingHolder;
import com.google.code.routing4db.strategy.impl.AbstractRoutingStrategy;
import com.google.code.routing4db.strategy.impl.MasterSlaveStrategy;
import com.google.code.routing4db.strategy.impl.ModRoutingStrategy;

import junit.framework.Assert;
import junit.framework.TestCase;


//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration()
public class BaseRoutingStrategyTest extends TestCase{
	
	 protected static String masterDataSourceKey = "masterDataSource";
	 
	 protected static List<String> excludeMethodPatterns = new ArrayList<String>();
	 static{
		 excludeMethodPatterns.add("exclude*");
	 }
	
	 /**
	  * 测试master-slave策略模式下排除方法生效
	  * */
	 @Test
	 public void testExcludeMethodForMasterSalve(){
		 UserDao userDao = new UserDaoImpl();
		 Method excludeMethod = ReflectionUtils.findMethod(UserDao.class, "excludeMethod");
		 AbstractRoutingStrategy strategy = this.createMasterSlaveRoutingStrategy();
		 for(int i=0; i<100; i++){
			 
			 //未设置过滤，执行master-slave策略
			 strategy.setExcludeMethodPatterns(null);
			 RoutingHolder.setCurrentDataSourceKey(null);
			 Assert.assertNull(RoutingHolder.getCurrentDataSourceKey());	
			 strategy.route(userDao, excludeMethod, new Object[]{});
			 Assert.assertEquals( masterDataSourceKey, RoutingHolder.getCurrentDataSourceKey());
			 
			 //设置过滤，不执行master-slave策略
			 strategy.setExcludeMethodPatterns(excludeMethodPatterns);
			 RoutingHolder.setCurrentDataSourceKey(null);
			 Assert.assertNull(RoutingHolder.getCurrentDataSourceKey());	
			 strategy.route(userDao, excludeMethod, new Object[]{});
			 Assert.assertNull(RoutingHolder.getCurrentDataSourceKey());	
			 
		 }
	 }
	 
	 @Test
	 public void testExcludeMethodForMod(){
		 UserDao userDao = new UserDaoImpl();
		 Method excludeMethod = ReflectionUtils.findMethod(UserDao.class, "excludeMethod");
		 AbstractRoutingStrategy strategy = this.createModRoutingStrategy();
		 for(int i=0; i<100; i++){
			 
			 //未设置过滤，执行mod策略, 抛出异常
			 Exception routingException = null;
			 try{
				 strategy.setExcludeMethodPatterns(null);
				 RoutingHolder.setCurrentDataSourceKey(null);
				 Assert.assertNull(RoutingHolder.getCurrentDataSourceKey());	
				 strategy.route(userDao, excludeMethod, new Object[]{});
				 Assert.assertNotNull(RoutingHolder.getCurrentDataSourceKey());
			 }catch(Exception e){
				 routingException = e;
			 }
			 Assert.assertNotNull(routingException);
			
			 
			 //设置过滤，不执行mod策略
			 strategy.setExcludeMethodPatterns(excludeMethodPatterns);
			 RoutingHolder.setCurrentDataSourceKey(null);
			 Assert.assertNull(RoutingHolder.getCurrentDataSourceKey());	
			 strategy.route(userDao, excludeMethod, new Object[]{});
			 Assert.assertNull(RoutingHolder.getCurrentDataSourceKey());	
			 
		 }
		 
	 }
	 
	 
	 
	 
	 
	 
	 
	 
	 protected AbstractRoutingStrategy createModRoutingStrategy(){
		 ModRoutingStrategy strategy = new ModRoutingStrategy();
			Map<Integer, String> dataSourceKeyMap = new HashMap<Integer,String>();
			for(int i=0; i<8; i++){
				dataSourceKeyMap.put(i, "dataSource" + i);
			}
			strategy.setDataSourceKeyMap(dataSourceKeyMap);
			strategy.setDataSourceNum(dataSourceKeyMap.size());
			strategy.setPropertyName("id");
		return  strategy;
	 }
	 
	 
	 protected AbstractRoutingStrategy createMasterSlaveRoutingStrategy(){
		   MasterSlaveStrategy strategy = new MasterSlaveStrategy();
			  
			Map<Integer, String> dataSourceKeyMap = new HashMap<Integer,String>();
			for(int i=0; i<6; i++){
				dataSourceKeyMap.put(i, "slaves" + i);
			}
			
			List<String> readMethodPatterns = new ArrayList<String>();
			readMethodPatterns.add("get*");
			strategy.setDataSourceKeyMap(dataSourceKeyMap);
			strategy.setReadMethodPatterns(readMethodPatterns);
			strategy.setMasterDataSourceKey(masterDataSourceKey);
		   return  strategy;
	 }
	 

}
