package cn.deal.core.customer.dao.impl.mapper;

import cn.deal.core.customer.domain.CustomerId;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomerIdRowMapper implements RowMapper<CustomerId> {

    @Override
    public CustomerId mapRow(ResultSet rs, int rowNum) throws SQLException {
        CustomerId customerId = new CustomerId();
        customerId.setId(rs.getString("id"));
        return customerId;
    }
}