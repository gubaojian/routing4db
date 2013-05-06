package com.google.code.routing4db.example;

import javax.annotation.Resource;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.code.routing4db.dao.User;
import com.google.code.routing4db.dao.UserDao;
import com.google.code.routing4db.holder.RoutingHolder;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:write-master-read-slaves.xml")
public class WriteMasterReadSlavesTest extends TestCase{
	
	@Resource
	UserDao userDao;
	
	@Resource
	JdbcTemplate jdbcTemplate;
	
	
	/**
	 * 清除数据
	 * */
	@Test
	public void cleanData(){
		for(int i=0; i<10; i++){
			long id = i;
			RoutingHolder.setCurrentDataSourceKey(null); //Master
			jdbcTemplate.execute("delete from user where id = " + id);
		}
		for(int i= 10000;  i < 10008; i++){
			long id = i;
			RoutingHolder.setCurrentDataSourceKey(null); //Master
			jdbcTemplate.execute("delete from user where id = " + id);
		}
	}
	
	/**
	 * 测试读写分离
	 * */
	@Test
	public void testWriteMasterReadSlaves(){
	   for(int i=1; i<10; i++){
		   User user = new User();
		   long id = i;
		   user.setId(id);
		   user.setName("User" + i);
		   //插入master
		   userDao.insert(user);
		   
		   //插入成功
		   RoutingHolder.setCurrentDataSourceKey(null); //Master
		   int count =  jdbcTemplate.queryForInt("select count(*) from user where id = " + id);
		   Assert.assertEquals(1,count);
		   System.out.println(user.getName());
		   
		   //从slave读
		   user = userDao.getUserById(id);
           Assert.assertNotNull(user);
		   System.out.println(user.getName());
	   }	
	}
	
	/**
	 * 测试事务, 事务注解要放到子类上，才能生效。不可把事务注解放到接口上
	 * */
	@Test
	public void testTransaction(){
		for(int i= 10000;  i < 10008; i++){
			 long id = i;
			 User user = new User();
			 user.setId(id);
			 user.setName("User" + i);
			 try{
				 //插入master， 事务生效，插入失败
				 userDao.insertWithTransaction(user);
			 }catch(Exception e){}
			//插入master， 事务生效，插入失败 count 为0
			 RoutingHolder.setCurrentDataSourceKey(null); //Master
			 int count =  jdbcTemplate.queryForInt("select count(*) from user where id = " + id);
			 Assert.assertEquals(0, count);
		}
	}
	
	
	

}
