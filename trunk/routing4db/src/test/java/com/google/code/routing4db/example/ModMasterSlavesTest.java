package com.google.code.routing4db.example;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

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
@ContextConfiguration(locations="classpath:mod-master-slaves-example.xml")
public class ModMasterSlavesTest extends TestCase{
	
	@Resource
	UserDao userDao;
	
	@Resource
	JdbcTemplate jdbcTemplate;
	
	static List<String> dataSources = new ArrayList<String>();
	static{
		dataSources.add("dataSourceOneMaster");
		dataSources.add("dataSourceTwoMaster");
	}
	/**
	 * 清除数据
	 * */
	@Test
	public void cleanData(){
		for(String dataSource : dataSources){
			for(int i=0; i<10; i++){
				long id = i;
				RoutingHolder.setCurrentDataSourceKey(dataSource); 
				jdbcTemplate.execute("delete from user where id = " + id);
			}
			for(int i= 10000;  i < 10008; i++){
				long id = i;
				RoutingHolder.setCurrentDataSourceKey(dataSource); 
				jdbcTemplate.execute("delete from user where id = " + id);
			}
		}
	}
	
	@Test
	public void testInsert(){
		  for(int i=1; i<10; i++){
			   User user = new User();
			   long id = i;
			   user.setId(id);
			   user.setName("User" + i);
			   //插入
			   userDao.insert(user);
			   System.out.println(user.getName());
		   }	
	}
	
	
	@Test
	public void testGetById(){
		  for(int i=1; i<10; i++){
			   long id = i;
			   User user = userDao.getUserById(id);
			   System.out.println(user.getName());
		   }	
	}
	
	
	@Test
	public void testTransaction(){
		for(int i= 10000;  i < 10080; i++){
			 long id = i;
			 User user = new User();
			 user.setId(id);
			 user.setName("User" + i);
			 try{
				 //插入master， 事务生效，插入失败
				 userDao.insertWithTransaction(user);
			 }catch(Exception e){
				 
			 }
			
		}
	}
	

}
