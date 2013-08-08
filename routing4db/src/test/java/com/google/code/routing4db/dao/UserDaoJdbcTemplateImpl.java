package com.google.code.routing4db.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.annotation.Resource;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.annotation.Transactional;

import com.google.code.routing4db.holder.RoutingHolder;

public class UserDaoJdbcTemplateImpl implements UserDao{

	@Resource
	JdbcTemplate jdbcTemplate;
	
	public int insert(User user) {
		jdbcTemplate.execute("delete from user where id = " + user.getId());
		String sql = "insert user(id, name) values("+ user.getId() + ",'" + user.getName() + RoutingHolder.getCurrentDataSourceKey() +"')";
		jdbcTemplate.execute(sql);
		 return 1;
	}

	public User getUserById(long id) {
			User user = jdbcTemplate.queryForObject("select id, name from user where id = ?", new Object[]{id}, UserRowMapper.instance());
			user.setName(user.getName() + " read user from " + RoutingHolder.getCurrentDataSourceKey());
			return user;
		
	}
	
	/**
	 * ∂‘œÛ”≥…‰
	 * */
	public static class UserRowMapper implements RowMapper<User>{
		
		private static UserRowMapper mapper = new UserRowMapper();
		
		public static final UserRowMapper instance(){
			return mapper;
		}
		public User mapRow(ResultSet rs, int rowNum) throws SQLException {
		    User user = new User();
		    user.setId(rs.getLong("id"));
		    user.setName(rs.getString("name"));
			return user;
		}
	}
	@Transactional(rollbackFor={Exception.class})
	public void insertWithTransaction(User user) {
		String sql = "insert user(id, name) values("+ user.getId() + ",'" + user.getName() + RoutingHolder.getCurrentDataSourceKey() +"')";
		jdbcTemplate.execute(sql);
		throw new RuntimeException("Transaction test");
	}

	@Override
	public void excludeMethod() {}
}
