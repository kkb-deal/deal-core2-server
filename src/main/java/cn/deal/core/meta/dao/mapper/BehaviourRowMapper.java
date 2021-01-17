package cn.deal.core.meta.dao.mapper;

import org.springframework.jdbc.core.RowMapper;

import cn.deal.core.meta.domain.Behaviour;

import java.sql.ResultSet;
import java.sql.SQLException;


public class BehaviourRowMapper implements RowMapper<Behaviour> {

    @Override
    public Behaviour mapRow(ResultSet rs, int rowNum) throws SQLException {
        Behaviour behaviour = new Behaviour();
        behaviour.setId(rs.getString("id"));
        behaviour.setAppId(rs.getString("appId"));
        behaviour.setType(rs.getInt("type"));
        behaviour.setAction(rs.getString("action"));
        behaviour.setName(rs.getString("name"));
        behaviour.setDescription(rs.getString("description"));
        behaviour.setCreatedAt(rs.getTimestamp("createdAt"));
        behaviour.setUpdatedAt(rs.getTimestamp("updatedAt"));
        return behaviour;
    }
}
