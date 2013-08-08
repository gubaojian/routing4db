package com.google.code.routing4db.standby;

import javax.annotation.Resource;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.code.routing4db.dao.User;
import com.google.code.routing4db.dao.UserDao;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:master-standby.xml")
public class MasterStandbyDataSourceTest extends TestCase{
	
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
				jdbcTemplate.execute("delete from user where id = " + id);
			}
	}
	
	
	@Test
	public void testMasterStandby() throws InterruptedException{
		 for(int i=1; i<10; i++){
			   User user = new User();
			   long id = i;
			   user.setId(id);
			   user.setName("User" + i);
			   //插入
			   try{
				   userDao.insert(user);
			   }catch(Exception e){
				   e.printStackTrace();
				   Thread.sleep(20*1000);
			   }
			   
			  
			   System.out.println(user.getName());
		 }	
	}

}
