package cn.deal.core.customerswarm.dao.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import cn.deal.component.utils.StringUtils;
import cn.deal.core.customerswarm.dao.SwarmMemberDao;
import cn.deal.core.customerswarm.dao.impl.mapper.SwarmMemberRowMapper;
import cn.deal.core.customerswarm.domain.SwarmMember;

@Repository
public class SwarmMemberDaoMySqlImpl implements SwarmMemberDao {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public int batchInsert(String appId, String swarmId, List<String> customerIdList) {
	    int affectRows = 0;
	    for(String customerId : customerIdList){
	        int row = singleInsert(appId, swarmId, customerId);
	        affectRows += row;
	    }
	    
		return affectRows;
	}

	@Override
    public int singleInsert(String appId, String swarmId, String customerId) {
        String sql = "insert into swarm_member (id, appId, swarmId, customerId, createdAt) select ?,?,?,?,? from dual "
				+ " where not exists(select id from swarm_member where appId=? and swarmId=? and customerId=?) "
				+ " and exists(select id from customer where id=? and appId=?) ";
        Date cur = new Date();
        List<Object> params = new ArrayList<>();
        params.add(UUID.randomUUID().toString());
        params.add(appId);
        params.add(swarmId);
        params.add(customerId);
        params.add(new Timestamp(cur.getTime()));
        params.add(appId);
        params.add(swarmId);
        params.add(customerId);
        params.add(customerId);
        params.add(appId);
        int affectRows = jdbcTemplate.update(sql, params.toArray(new Object[params.size()]));
		return affectRows;
    }

	@Override
	public int batchDelete(String appId, String swarmId, List<String> custIdList) {
		String sql = "delete from swarm_member where appId=? and swarmId=? and customerId=?";
		int[] batchUpdate = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ps.setString(1, appId);
				ps.setString(2, swarmId);
				ps.setString(3, custIdList.get(i));
			}
			
			@Override
			public int getBatchSize() {
				return custIdList.size();
			}
		});
		int affectRows = 0;
		for(int rows : batchUpdate){
			affectRows += rows;
		}
		return affectRows;
	}

	@Override
	public List<SwarmMember> fetchByCustomerIds(String appId, List<String> customerIds) {
		if (customerIds != null && customerIds.size() > 0) {
			String sql = "select * from swarm_member where appId=:appId and customerId in (:customerIds)";
			Map<String, Object> params = new HashMap<>();
			params.put("appId", appId);
			params.put("customerIds", customerIds);
			NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
			return namedParameterJdbcTemplate.query(sql, params, new SwarmMemberRowMapper());
		}
		return new ArrayList<>();
	}

	@Override
	public int batchInsert(String appId, List<String> swarmIds, String customerId) {
		String sql = "insert into swarm_member (id, appId, swarmId, customerId, createdAt) select ?,?,?,?,? from dual "
				+ " where not exists(select id from swarm_member where appId=? and swarmId=? and customerId=?) ";
		int[] batchUpdate = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
			Date cur = new Date();
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ps.setString(1, UUID.randomUUID().toString());
				ps.setString(2, appId);
				ps.setString(3, swarmIds.get(i));
				ps.setString(4, customerId);
				ps.setTimestamp(5, new Timestamp(cur.getTime()));
				ps.setString(6, appId);
				ps.setString(7, swarmIds.get(i));
				ps.setString(8, customerId);
			}
			
			@Override
			public int getBatchSize() {
				return swarmIds.size();
			}
		});
		return batchUpdate.length;
	}

	@Override
	public int batchDelete(List<SwarmMember> members) {
		String sql = " delete from swarm_member where id=? ";
		int[] batchUpdate = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ps.setString(1, members.get(i).getId());
			}
			
			@Override
			public int getBatchSize() {
				return members.size();
			}
		});
		return batchUpdate.length;
	}

	@Override
	public int getCountBySwarmId(String id) {
		String sql = "select count(*) AS total from swarm_member where swarmId=? ";
		List<Integer> result = jdbcTemplate.query(sql, new Object[]{id},new RowMapper<Integer>(){
			@Override
			public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs.getInt("total");
			}
		});
		return result.get(0);
	}
	
	@Override
	public long getMergedCountBySwarmIds(List<String> ids) {
		String sql = "select count(distinct clmc.mergeCustomerId) count from swarm_member sm inner join customer_link_merge_customer clmc on sm.customerId=clmc.customerId where sm.swarmId in (:ids) and clmc.`status`=1 ";
		NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
		String idsStr = StringUtils.join(ids, ",");
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("ids", idsStr);
		List<Long> result = namedParameterJdbcTemplate.query(sql, paramMap, new RowMapper<Long>() {
			@Override
			public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs.getLong("count");
			}
		});
		return result.get(0);
	}
	
	@Override
	public Map<String, Integer> countEverySwarmMember(List<String> swarmIds) {
		String sql = " select swarmId, count(id) `count` from swarm_member where swarmId in (:swarmIds) group by swarmId ";
		Map<String, Integer> resMap = new ConcurrentHashMap<>();
		NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("swarmIds", swarmIds);
		namedParameterJdbcTemplate.query(sql, paramMap, new RowMapper<Integer>(){
			@Override
			public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
				resMap.put(rs.getString("swarmId"), rs.getInt("count"));
				return rowNum;
			}
		});
		return resMap;
	}

}
