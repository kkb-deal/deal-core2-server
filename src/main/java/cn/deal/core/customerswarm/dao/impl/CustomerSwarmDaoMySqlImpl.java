package cn.deal.core.customerswarm.dao.impl;

import cn.deal.core.customerswarm.dao.CustomerSwarmDao;
import cn.deal.core.customerswarm.dao.impl.mapper.CustomerSwarmRowMapper;
import cn.deal.core.customerswarm.domain.CustomerSwarm;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class CustomerSwarmDaoMySqlImpl implements CustomerSwarmDao {

	private static final Logger logger = LoggerFactory.getLogger(CustomerSwarmDaoMySqlImpl.class);
	
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public List<CustomerSwarm> getSharedCustomerSwarmList(String appId, String kuickUserId, int startIndex, int count, String keyword) {
        //共享给app的
		String sql1 = " select cs.* from customer_swarm cs, swarm_share sms where cs.appId=:appId and cs.id=sms.swarmId and sms.targetId=:appId and sms.targetType=1 and cs.kuickUserId<>:kuickUserId and cs.`status`=1 ";
        //共享给appmember的
		String sql2 = " select cs.* from customer_swarm cs, swarm_share sms where cs.appId=:appId and cs.id=sms.swarmId and sms.targetId=:kuickUserId and sms.targetType=0 and cs.`status`=1 ";
		if (StringUtils.isNotBlank(keyword)) {
			String keywordSql = "and  (`name` like '%" + keyword + "%' or `comment` like '%" + keyword + "%') ";
			sql1 += keywordSql;
			sql2 += keywordSql;
		}
		String orderSql = " order by cs.createdAt desc ";
		sql1 += orderSql;
		sql2 += orderSql;
		String sql = "("+sql1+")" + " union " + "(" + sql2 + ")";
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("appId", appId);
		paramMap.put("kuickUserId", kuickUserId);
		
		NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        return namedParameterJdbcTemplate.query(sql, paramMap, new CustomerSwarmRowMapper());
	}

	@Override
	public List<CustomerSwarm> getSharedAndSelfCustomerSwarmList(String appId, String kuickUserId, int startIndex, int count, String keyword) {
		//共享给app的
		String sql1 = " select cs.* from customer_swarm cs, swarm_share sms where cs.appId=:appId and cs.id=sms.swarmId and sms.targetId=:appId and sms.targetType=1 and cs.kuickUserId<>:kuickUserId and cs.`status`=1 ";
		//共享给appmember的
		String sql2 = " select cs.* from customer_swarm cs, swarm_share sms where cs.appId=:appId and cs.id=sms.swarmId and sms.targetId=:kuickUserId and sms.targetType=0 and cs.`status`=1 ";
		//自己的
		String sql3 = " select * from customer_swarm where appId=:appId and kuickUserId=:kuickUserId and `status`=1 ";
		if (StringUtils.isNotBlank(keyword)) {
			String keywordSql = "and  (`name` like '%" + keyword + "%' or `comment` like '%" + keyword + "%') ";
			sql1 += keywordSql;
			sql2 += keywordSql;
			sql3 += keywordSql;
		}

		String sql = "("+sql3+") union (" + sql1 + ") union (" + sql2 + " )";
		sql = "select * from (" + sql +  ") t order by t.createdAt desc limit :startIndex, :pagesize";
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("appId", appId);
		paramMap.put("kuickUserId", kuickUserId);
		paramMap.put("startIndex", startIndex);
		paramMap.put("pagesize", count);
		logger.info("sql:{}, args:{}", sql, paramMap);
		NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
		return namedParameterJdbcTemplate.query(sql, paramMap, new CustomerSwarmRowMapper());
	}
	
	@Override
	public List<CustomerSwarm> getSelfCustomerSwarmList(String appId, String kuickUserId, int startIndex, int count, String keyword) {
		String sql = " select * from customer_swarm where appId=:appId and kuickUserId=:kuickUserId and `status`=1 ";
		if (StringUtils.isNotBlank(keyword)) {
			String keywordSql = "and  (`name` like '%" + keyword + "%' or `comment` like '%" + keyword + "%') ";
			sql += keywordSql;
		}
		sql += " order by createdAt asc ";
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("appId", appId);
		paramMap.put("kuickUserId", kuickUserId);
		NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
		List<CustomerSwarm> swarmList = namedParameterJdbcTemplate.query(sql, paramMap, new CustomerSwarmRowMapper());
		return swarmList;
	}
	
	@Override
	public List<String> getIdsWithPage(int startIndex, int count) {
		String sql = " select id from customer_swarm limit ?,? ";
		return jdbcTemplate.query(sql, new Object[]{startIndex, count},new RowMapper<String>(){
			@Override
			public String mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs.getString("id");
			}
		});
	}
}
