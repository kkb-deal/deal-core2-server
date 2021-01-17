package cn.deal.core.meta.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import cn.deal.core.meta.dao.mapper.BehaviourMetaDataRowMapper;
import cn.deal.core.meta.domain.BehaviourMetaData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


@Repository
public class BehaviourMetaDataDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<BehaviourMetaData> getBehaviourMetaDataList(String appId, String action, Integer startIndex, Integer count) {
        String sql = "select * from behaviour_meta_data " +
                "where appId = ? and action = ? ";
        List<Object> args = new ArrayList();
        args.add(appId);
        args.add(action);

        sql += "order by updatedAt desc ";
        if(startIndex != null && count != null){
            sql += "limit ?, ? ";
            args.add(startIndex);
            args.add(count);
        }
        List<BehaviourMetaData> results = jdbcTemplate.query(sql, new BehaviourMetaDataRowMapper(),
                args.toArray(new Object[args.size()]));
        return results;
    }

    public Integer getConfigMetaDataCount(String appId, String action) {

        String sql = "select count(id) as total from behaviour_meta_data " +
                "where appId = ? and action = ? and type is not null";

        List<Object> args = new ArrayList();
        args.add(appId);
        args.add(action);
        List<Integer> results = jdbcTemplate.query(sql, new RowMapper<Integer>() {
            @Override
            public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                Integer total = rs.getInt("total");
                if(total == null){
                    total = 0;
                }
                return total;
            }
        }, args.toArray(new Object[args.size()]));

        return DataAccessUtils.singleResult(results);
    }
}
