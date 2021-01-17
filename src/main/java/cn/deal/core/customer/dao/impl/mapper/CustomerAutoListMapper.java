package cn.deal.core.customer.dao.impl.mapper;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class CustomerAutoListMapper implements RowMapper<Map<String, Object>> {

    @Override
    public Map<String, Object> mapRow(ResultSet rs, int rowNum) throws SQLException {

        Map<String, Object> map = new HashMap<>();

        int ColumnCount = rs.getMetaData().getColumnCount();
        for (int i = 1; i < ColumnCount + 1; i++) {
            String ColumnName = rs.getMetaData().getColumnName(i);
            map.put(ColumnName, rs.getString(ColumnName));
        }

        return map;
    }
}
