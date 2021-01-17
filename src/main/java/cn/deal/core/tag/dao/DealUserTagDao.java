package cn.deal.core.tag.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import cn.deal.core.tag.domain.DealUserTag;


@Repository
public class DealUserTagDao {
	
	@Autowired
	private JdbcTemplate dealJdbcTemplate;
	
	public List<String> getTagsByDealUserId(String dealUserId, String startTime, String endTime){
		String sql = "select count(id) as num, tag from deal_user_tag where dealUserId = ?";
		
        List<Object> args = new ArrayList<Object>();
        args.add(dealUserId);
        if(!"".equals(startTime)&&!"".equals(endTime)){
        	args.add(startTime);
            args.add(endTime);
            sql +=" and createdAt >= ? and createdAt <= ? ";
        }
        
        
        sql +=" group by tag order by num desc";
        
        System.out.println("sql is "+sql);
		List<String> jList = dealJdbcTemplate.query(sql, args.toArray(new Object[args.size()]), new RowMapper<String>(){
			@Override
			public String mapRow(ResultSet rs, int rowNum) throws SQLException {
				String obj =rs.getString("tag")+"_"+rs.getString("num");
				return obj;
			}
		});
		return jList;
	}
	
}
