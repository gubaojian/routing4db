package com.google.code.routing4db.dao;



public interface UserDao {
	

	public int insert(User user);
	
	
	public User getUserById(long id);
	
	

	public void insertWithTransaction(User user);
	
	
	public void excludeMethod();

}
