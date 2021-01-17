package cn.deal.core.customer.dao.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import cn.deal.core.customer.dao.CustomerLinkMergeCustomerDao;
import cn.deal.core.customer.dao.impl.mapper.CustomerLinkMergeCustomerRowsMapper;
import cn.deal.core.customer.domain.CustomerLinkMergeCustomer;


@Repository
public class CustomerLinkMergeCustomerDaoJdbcImpl implements CustomerLinkMergeCustomerDao {

    private final Logger logger = LoggerFactory.getLogger(CustomerLinkMergeCustomerDao.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<CustomerLinkMergeCustomer> getByCustomerId(String customerId) {
        String sql = "select clmc.*, c.`name`, c.email, c.phone from customer_link_merge_customer clmc" +
                " left join customer c on c.id = clmc.mergeCustomerId" +
                " where clmc.customerId = ? and clmc.`status` = 1 ";
        ArrayList<String> args = new ArrayList<String>();
        args.add(customerId);

        logger.info("CustomerLinkMergeCustomerDao_sql:{}", sql);
        logger.info("CustomerLinkMergeCustomerDao_args:{}", customerId);

        List<CustomerLinkMergeCustomer> customerLinkMergeCustomerList =
                jdbcTemplate.query(sql,
                        args.toArray(new Object[args.size()]),
                        new CustomerLinkMergeCustomerRowsMapper());
        
        return customerLinkMergeCustomerList;
    }

	@Override
	public void batchAddCustomerLinkMergeCustomer(String customerId, List<String> mergedCustomers) {
		String sql = "insert into customer_link_merge_customer(id, customerId, mergeCustomerId, status, createdAt, updatedAt) ";
		sql+= "values(?,?,?,?,?,?)";
	    
		final Timestamp createdAt = new Timestamp(System.currentTimeMillis());
		final Timestamp updatedAt = createdAt;
				
		logger.debug("batchAddCustomerLinkMergeCustomer customerId:{}, mergedCustomers:{}", customerId, mergedCustomers);
		
		jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
	
			@Override
			public int getBatchSize() {
				return mergedCustomers.size();
			}
			
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ps.setString(1, UUID.randomUUID().toString());
				ps.setString(2, customerId);
				ps.setString(3, mergedCustomers.get(i));
				ps.setInt(4, 1);
				ps.setTimestamp(5, createdAt);
				ps.setTimestamp(6, updatedAt);
			}
		});
	}

}
