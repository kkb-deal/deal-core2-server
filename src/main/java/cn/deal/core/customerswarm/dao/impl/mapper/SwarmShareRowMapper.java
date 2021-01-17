package cn.deal.core.customerswarm.dao.impl.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import cn.deal.core.customerswarm.domain.SwarmShare;

public class SwarmShareRowMapper implements RowMapper<SwarmShare> {

	@Override
	public SwarmShare mapRow(ResultSet rs, int rowNum) throws SQLException {
		SwarmShare share = new SwarmShare();
		share.setId(rs.getString("id"));
		share.setAppId(rs.getString("appId"));
		share.setSwarmId(rs.getString("swarmId"));
		share.setTargetType(rs.getInt("targetType"));
		share.setTargetId(rs.getString("targetId"));
		share.setCreatedAt(rs.getTimestamp("createdAt"));
		try{
			if (rs.findColumn("kuickUserId") > 0) {
				share.setKuickUserId(rs.getString("kuickUserId"));
			}
		} catch (SQLException e){
		}
		return share;
	}
	
}
