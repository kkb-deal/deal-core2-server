package cn.deal.core.customerswarm.dao.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import cn.deal.core.customerswarm.dao.SwarmShareDao;
import cn.deal.core.customerswarm.dao.impl.mapper.SwarmShareRowMapper;
import cn.deal.core.customerswarm.domain.SwarmShare;

@Repository
public class SwarmShareDaoMySqlImpl implements SwarmShareDao {
	
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public List<SwarmShare> fetchBySwarmId(String appId, String swarmId) {
		String sql = "select share.*, swarm.kuickUserId kuickUserId from swarm_share share "
				+ " left join customer_swarm swarm on swarm.id=share.swarmId where share.appId=? and swarmId=? and swarm.`status`=1 ";
		List<SwarmShare> shareList = jdbcTemplate.query(sql, new Object[]{appId, swarmId}, new SwarmShareRowMapper());
		return shareList;
	}
	
	@Override
	public int batchInsert(String appId, String swarmId, int targetType, List<String> targetIds){
		String sql = " insert into swarm_share (id, appId, swarmId, targetType, targetId, createdAt) select ?,?,?,?,?,? from dual "
				+ " where not exists (select id from swarm_share where appId=? and swarmId=? and targetId=?)";
		jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
			Date cur = new Date();
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ps.setString(1, UUID.randomUUID().toString());
				ps.setString(2, appId);
				ps.setString(3, swarmId);
				ps.setInt(4, targetType);
				ps.setString(5, targetIds.get(i));
				ps.setTimestamp(6, new Timestamp(cur.getTime()));
				ps.setString(7, appId);
				ps.setString(8, swarmId);
				ps.setString(9, targetIds.get(i));
			}
			
			@Override
			public int getBatchSize() {
				return targetIds.size();
			}
		});
		return targetIds.size();
	}

	@Override
	public void deleteBySwarmIdAndTargetId(String appId, String swarmId, int targetType, List<String> targetIds) {
		String sql = "delete from swarm_share where appId=? and swarmId=? and targetId=? and targetType=?";
		jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ps.setString(1, appId);
				ps.setString(2, swarmId);
				ps.setString(3, targetIds.get(i));
				ps.setInt(4, targetType);
			}
			
			@Override
			public int getBatchSize() {
				return targetIds.size();
			}
		});
		
	}

	@Override
	public int deleteBySwarmIdAndTargetType(String appId, String swarmId, int targetType) {
		String sql = " delete from swarm_share where appId=? and swarmId=? and targetType=? ";
		List<Object> params = new ArrayList<>();
		params.add(appId);
		params.add(swarmId);
		params.add(targetType);
		int update = jdbcTemplate.update(sql, params.toArray(new Object[params.size()]));
		return update;
	}

	@Override
	public int deleteBySwarmId(String appId, String swarmId) {
		String sql = " delete from swarm_share where appId=? and swarmId=? ";
		List<Object> params = new ArrayList<>();
		params.add(appId);
		params.add(swarmId);
		int rows = jdbcTemplate.update(sql, params.toArray(new Object[params.size()]));
		return rows;
	}

	@Override
	public List<SwarmShare> fetchByTargetIdAndSwarmId(String appId, String kuickUserId, String swarmId) {
		String sql1 = " (select * from swarm_share where appId=:appId and targetType=:appType and targetId=:appId) ";
		String sql2 = " (select * from swarm_share where appId=:appId and targetType=:appMemberType and targetId=:kuickUserId) ";
		String sql = sql1 + " union " + sql2;
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("appId", appId);
		paramMap.put("kuickUserId", kuickUserId);
		paramMap.put("appType", SwarmShare.TargetType.APP.getVal());
		paramMap.put("appMemberType", SwarmShare.TargetType.APP_MEMBER.getVal());
		NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
		List<SwarmShare> shareList = namedParameterJdbcTemplate.query(sql, paramMap, new SwarmShareRowMapper());
		return shareList;
	}
}
