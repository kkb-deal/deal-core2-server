package cn.deal.core.customer.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import cn.deal.core.customer.dao.MergedCustomerDao;


@Repository
public class MergedCustomerDaoJdbcImpl implements MergedCustomerDao {

    private Logger logger = LoggerFactory.getLogger(MergedCustomerDaoJdbcImpl.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;
    
	@Override
	@SuppressWarnings("unchecked")
	public List<String> getMergedCustomerIds(String appId, String[] customerIds) {
		if (customerIds==null || customerIds.length==0) {
			return Collections.EMPTY_LIST;
		}
		
		String customserIdsIn = "'" + String.join("','", customerIds) + "'";
		
		StringBuilder sql = new StringBuilder();
		ArrayList<String> params = new ArrayList<>();
		
		sql.append("select DISTINCT(c.id) as customerId from customer c ");
		sql.append("left join customer_link_merge_customer clmc on clmc.mergeCustomerId=c.id ");
		sql.append("where c.appId=? ");
			sql.append("and clmc.customerId in (" + customserIdsIn + ") ");
			sql.append("and clmc.`status`=1 ");
		
		params.add(appId);
		
		logger.info("sql:" + sql.toString());
		logger.info("params:" + params.toString());
		
		return jdbcTemplate.query(sql.toString(), params.toArray(new Object[params.size()]), new RowMapper<String>(){

			@Override
			public String mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs.getString("customerId");
			}
			
		});
	}

	@Override
	public List<String> getCustomerIdByMergedCustomerId(String customerId) {
		String sql = " select distinct customerId from customer_link_merge_customer where mergeCustomerId=? ";
		logger.info("sql: {}", sql);
		logger.info("param: {}", customerId);
		return jdbcTemplate.query(sql, new Object[]{customerId}, new RowMapper<String>(){

			@Override
			public String mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs.getString("customerId");
			}
			
		});
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<String> getMergedCustomerIdsWithPage(String appId, String swarmId, int startIndex, int count) {
		StringBuilder sql = new StringBuilder();
		ArrayList<Object> params = new ArrayList<>();
		
		sql.append(" select DISTINCT(c.id) as customerId from customer c ")
			.append(" left join customer_link_merge_customer clmc on clmc.mergeCustomerId=c.id ")
			.append(" join ( SELECT s.customerId id FROM swarm_member s join ( ")
			.append(" select id from swarm_member where swarmid = ? limit ?, ?) t2 ")
			.append(" on t2.id=s.id ")
			.append(" ) t on t.id = clmc.customerid")
			.append(" where c.appId=? and clmc.`status`=1 ");
		
		params.add(swarmId);
		params.add(startIndex);
		params.add(count);
		params.add(appId);
		
		logger.info("sql:" + sql.toString());
		logger.info("params:" + params.toString());
		
		return jdbcTemplate.query(sql.toString(), params.toArray(new Object[params.size()]), new RowMapper<String>(){

			@Override
			public String mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs.getString("customerId");
			}
			
		});
	}

	@Override
	public int getMergedCustomerCount(String appId) {
		String sql = "select count(DISTINCT mergeCustomerId) count from customer_link_merge_customer clmc join customer c on clmc.customerId=c.id where c.appId=?";
		List<Integer> res = jdbcTemplate.query(sql, new Object[]{appId}, new RowMapper<Integer>(){

			@Override
			public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs.getInt("count");
			}
			
		});
		if(null != res && !res.isEmpty()){
			return res.get(0);
		} else {
			return 0;
		}
	}

}
