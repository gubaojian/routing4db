package com.google.code.routing4db.dao;

import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.transaction.annotation.Transactional;

public class UserDaoMybatisImpl extends SqlSessionDaoSupport implements UserDao{

	public int insert(User user) {
		return this.getSqlSession().insert("insert", user);
	}

	public User getUserById(long id) {
		return  this.getSqlSession().selectOne("getUserById", id);
	}

	@Transactional
	public void insertWithTransaction(User user) {
		this.getSqlSession().insert("insert", user);
		throw new RuntimeException("xxx");
	}

	@Override
	public void excludeMethod() {}

}
