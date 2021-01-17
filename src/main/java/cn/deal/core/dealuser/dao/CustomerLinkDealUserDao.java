package cn.deal.core.dealuser.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class CustomerLinkDealUserDao {

    private static final Logger logger = LoggerFactory.getLogger(CustomerLinkDealUserDao.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    
	/**
	 * 批量添加客户关联的DealUsers
	 * 
	 * @param id
	 * @param dealUsers
	 */
	public void batchAddCustomerLinkDealUser(final String customerId, final List<String> dealUsers) {
		String sql = "insert into customer_link_deal_user(id, customerId, dealUserId, createdAt, updatedAt) ";
			sql+= "values(?,?,?,?,?)";
			
		final Timestamp createdAt = new Timestamp(System.currentTimeMillis());
		final Timestamp updatedAt = createdAt;
				
		logger.debug("batchAddCustomerLinkDealUser customerId:{}, dealUsers:{}", customerId, dealUsers);
		
		jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

			@Override
			public int getBatchSize() {
				return dealUsers.size();
			}
			
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ps.setString(1, UUID.randomUUID().toString());
				ps.setString(2, customerId);
				ps.setString(3, dealUsers.get(i));
				ps.setTimestamp(4, createdAt);
				ps.setTimestamp(5, updatedAt);
			}
		});
	}

}
