package cn.deal.core.customerswarm.dao.impl.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.springframework.jdbc.core.RowMapper;

import cn.deal.core.customerswarm.domain.CustomerSwarm;

public class CustomerSwarmRowMapper implements RowMapper<CustomerSwarm> {
	
	@Override
	public CustomerSwarm mapRow(ResultSet rs, int rowNum) throws SQLException {
		String id = rs.getString("id");
		String appId = rs.getString("appId");
		String kuickUserId = rs.getString("kuickUserId");
		String name = rs.getString("name");
		String photoUrl = rs.getString("photoUrl");
		String comment = rs.getString("comment");
		Integer status = rs.getInt("status");
		Date createdAt = rs.getTimestamp("createdAt");
		Date updatedAt = rs.getTimestamp("updatedAt");
		Integer type = rs.getInt("type");
		String filterId = rs.getString("filterId");
		return new CustomerSwarm(id, appId, kuickUserId, name, photoUrl, comment, status, createdAt, updatedAt, type, filterId);
	}
	
}
