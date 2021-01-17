package cn.deal.core.customerswarm.dao.impl.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import cn.deal.core.customerswarm.domain.SwarmMember;

public class SwarmMemberRowMapper implements RowMapper<SwarmMember> {

	@Override
	public SwarmMember mapRow(ResultSet rs, int rowNum) throws SQLException {
		SwarmMember member = new SwarmMember();
		member.setId(rs.getString("Id"));
		member.setAppId(rs.getString("appId"));
		member.setSwarmId(rs.getString("swarmId"));
		member.setCustomerId(rs.getString("customerId"));
		member.setCreatedAt(rs.getTimestamp("createdAt"));
		return member;
	}
	
}
