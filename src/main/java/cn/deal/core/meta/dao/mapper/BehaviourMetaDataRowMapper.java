package cn.deal.core.meta.dao.mapper;

import org.springframework.jdbc.core.RowMapper;

import cn.deal.core.meta.domain.BehaviourMetaData;

import java.sql.ResultSet;
import java.sql.SQLException;


public class BehaviourMetaDataRowMapper implements RowMapper<BehaviourMetaData> {

    @Override
    public BehaviourMetaData mapRow(ResultSet rs, int rowNum) throws SQLException {
        BehaviourMetaData metaData = new BehaviourMetaData();
        metaData.setId(rs.getString("id"));
        metaData.setAppId(rs.getString("appId"));
        metaData.setAction(rs.getString("action"));
        metaData.setName(rs.getString("name"));
        metaData.setType(rs.getString("type"));
        metaData.setTitle(rs.getString("title"));
        metaData.setIndex(rs.getInt("index"));
        metaData.setDefaultValue(rs.getString("defaultValue"));
        metaData.setOptionValues(rs.getString("optionValues"));
        metaData.setReadonly(rs.getInt("readonly"));
        metaData.setCreatedAt(rs.getTimestamp("createdAt"));
        metaData.setUpdatedAt(rs.getTimestamp("updatedAt"));
        return metaData;
    }
}
