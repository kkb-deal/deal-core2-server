package cn.deal.core.meta.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import cn.deal.core.meta.dao.mapper.BehaviourRowMapper;
import cn.deal.core.meta.domain.Behaviour;

import javax.management.AttributeList;
import java.util.ArrayList;
import java.util.List;


@Repository
public class BehaviourDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Behaviour> getBehavioursByType(String appId, Integer type, Integer startIndex, Integer count) {

        String sql = "select * from behaviour where appId = ? ";
        List<Object> args = new ArrayList();
        args.add(appId);

        if(type != null){
            sql += " and type = ? ";
            args.add(type);
        }

        sql += " order by createdAt desc ";
        if(startIndex != null && count != null){
            sql += "limit ?, ? ";
            args.add(startIndex);
            args.add(count);
        }
        List<Behaviour> results = jdbcTemplate.query(sql, new BehaviourRowMapper(), args.toArray(new Object[args.size()]));
        return results;
    }
}
