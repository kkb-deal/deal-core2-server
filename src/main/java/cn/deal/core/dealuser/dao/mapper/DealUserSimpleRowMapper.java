package cn.deal.core.dealuser.dao.mapper;

import cn.deal.core.dealuser.domain.DealUser;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 简化版DealUserRowMapper
 */
public class DealUserSimpleRowMapper implements RowMapper<DealUser> {

    @Override
    public DealUser mapRow(ResultSet rs, int rowNum) throws SQLException {
        DealUser dealUser = new DealUser();
        dealUser.setId(rs.getString("id"));
        dealUser.setOpenid(rs.getString("openid"));
        dealUser.setDeviceId(rs.getString("deviceId"));
        dealUser.setDeviceId2(rs.getString("deviceId2"));
        return dealUser;
    }
}
