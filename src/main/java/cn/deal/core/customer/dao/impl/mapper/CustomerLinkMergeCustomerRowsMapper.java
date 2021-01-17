package cn.deal.core.customer.dao.impl.mapper;

import cn.deal.core.customer.domain.CustomerLinkMergeCustomer;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;


public class CustomerLinkMergeCustomerRowsMapper implements RowMapper<CustomerLinkMergeCustomer> {

    @Override
    public CustomerLinkMergeCustomer mapRow(ResultSet rs, int rowNum) throws SQLException {
        CustomerLinkMergeCustomer customerLinkMergeCustomer = new CustomerLinkMergeCustomer();
        customerLinkMergeCustomer.setId(rs.getString("id"));
        customerLinkMergeCustomer.setCustomerId(rs.getString("customerId"));
        customerLinkMergeCustomer.setMergeCustomerId(rs.getString("mergeCustomerId"));
        customerLinkMergeCustomer.setStatus(rs.getInt("status"));
        customerLinkMergeCustomer.setCreatedAt(rs.getTimestamp("createdAt"));
        customerLinkMergeCustomer.setUpdatedAt(rs.getTimestamp("updatedAt"));
        customerLinkMergeCustomer.setName(rs.getString("name"));
        customerLinkMergeCustomer.setEmail(rs.getString("email"));
        customerLinkMergeCustomer.setPhone(rs.getString("phone"));
        return customerLinkMergeCustomer;
    }
}
