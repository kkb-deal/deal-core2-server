package cn.deal.core.app.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.google.common.base.Joiner;

import cn.deal.component.UserComponent;
import cn.deal.component.domain.User;
import cn.deal.core.app.domain.DealApp;


@Repository
public class DealAppDao {

	private static final Log logger = LogFactory.getLog(DealAppDao.class);

	@Autowired
	private JdbcTemplate dealJdbcTemplate;

    public static class DealAppsRowMapper implements RowMapper<DealApp> {

		@Override
		public DealApp mapRow(ResultSet rs, int rowNum) throws SQLException {
			DealApp dealApp = new DealApp();

			dealApp.setId(rs.getString("id"));
			dealApp.setAndroidPackageName(rs.getString("androidPackageName"));
			dealApp.setConferenceGroupId(rs.getString("conferenceGroupId"));
			dealApp.setConferenceId(rs.getInt("conferenceId"));
			dealApp.setCreatorId(rs.getString("creatorId"));
			dealApp.setDescription(rs.getString("description"));
			dealApp.setCreateTime(rs.getTimestamp("createTime"));
			dealApp.setEditTime(rs.getTimestamp("editTime"));
			dealApp.setIconHeight(rs.getInt("iconHeight"));
			dealApp.setIconURL(rs.getString("iconURL"));
			dealApp.setIconWidth(rs.getInt("iconWidth"));
			dealApp.setIosBundleId(rs.getString("iosBundleId"));
			dealApp.setName(rs.getString("name"));
			dealApp.setRedirectUri(rs.getString("redirectUri"));
			dealApp.setSecret(rs.getString("secret"));
			dealApp.setStatus(rs.getInt("status"));
			dealApp.setType(rs.getString("type"));

			return dealApp;
		}
	}
	
	/**
	 * @param keyword
    * @return int
    */
	public int getCount(final Map<String,String> keywords) {
		int count = dealJdbcTemplate.execute(new PreparedStatementCreator() {
			@SuppressWarnings("deprecation")
			@Override
			public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
				String sql = "select count(*) from  deal_app d ";
				List<Object> args = new ArrayList<Object>();
				if(keywords !=null && keywords.size() > 0){
		            String id = keywords.get("id");
		            String name = keywords.get("name");
		            String creatorIds = keywords.get("creatorIds");
		            
					sql += "  where d.id = ? or d.name like ? ";
					args.add(id);
		            args.add(name+"%");
		            
		            if ( StringUtils.isNotBlank(creatorIds)){
		                sql += " or d.creatorId in (?)";
		                args.add(creatorIds);
		            }
				}				
				
				PreparedStatement ps = conn.prepareStatement(sql);
				logger.info("sql: " + sql + " args: " + args.size());
				logger.info("ps count: " + ps.getParameterMetaData().getParameterCount());
				for (int i = 0; i < ps.getParameterMetaData().getParameterCount(); i++) {
					logger.info("ps agrgs[i]: " + args.get(i));

					ps.setObject(i+1, args.get(i));
				}
				
				return ps;
			}

		}, new PreparedStatementCallback<Integer>() {
			@Override
			public Integer doInPreparedStatement(PreparedStatement pstmt) throws SQLException, DataAccessException {
				pstmt.execute();
				ResultSet rs = pstmt.getResultSet();
				rs.next();
				return rs.getInt(1);
			}
		});

		return count;
	}
	
	/** 
	* @Title: getAllApps 
	* @Description: TODO(获取所有项目分页) 
	* @param startIndex 默认0
	* @param count 默认20，最大100
	* @return 设定文件 
	* @return List<DealApp>    返回类型 
	* @throws 
	*/
	public List<DealApp> getAllApps(Integer startIndex, Integer count,Map<String,String> keywords){
		List<Object> args = new ArrayList<Object>();
		String sql = "select d.* from  deal_app d ";
		
		if(keywords !=null && keywords.size() > 0){
            String id = keywords.get("id");
            String name = keywords.get("name");
            String creatorIds = keywords.get("creatorIds");
            
            sql += "  where d.id = ? or d.name like ? ";
            args.add(id);
            args.add(name+"%");
            
            if ( StringUtils.isNotBlank(creatorIds)){
                sql += " or d.creatorId in (?)";
                args.add(creatorIds);
            }
		}
		
		sql +=" ORDER BY d.createTime DESC ";
		if ( startIndex != null && count != null) {
			if(count>100) count=100;
			
			sql += " limit ?,?";
			args.add(startIndex);
			args.add(count);
		}
		
		logger.info("sql: " + sql + " args: " + args.toString());
		
		return dealJdbcTemplate.query(sql, args.toArray(new Object[args.size()]), new DealAppsRowMapper());
	}
	
	public List<DealApp> getAllApps(Map<String,String> keywords, String settingKey, String settingValue, Integer startIndex, Integer count){
		List<Object> args = new ArrayList<Object>();
		String sql = "select da.* from  deal_app da"
				+ " LEFT JOIN app_setting ase on da.id=ase.appId ";
			
		
		if(keywords !=null && keywords.size() > 0){
		    String id = keywords.get("id");
		    String name = keywords.get("name");
		    String creatorIds = keywords.get("creatorIds");
		    
		    sql += "  where da.id = ? or da.name like ? ";
            args.add(id);
            args.add(name+"%");
            
            if ( StringUtils.isNotBlank(creatorIds)){
                sql += " or da.creatorId in (?)";
                args.add(creatorIds);
            }
		}
		
		if(StringUtils.isNotBlank(settingKey)){
			sql += " and ase.`key`=? ";
			args.add(settingKey);
		}

		if(StringUtils.isNotBlank(settingValue)){
			sql += " and ase.`value`=? ";
			args.add(settingValue);
		}

		sql +=" ORDER BY da.createTime DESC ";
		if ( startIndex != null && count != null) {
			if(count>100) count=100;
			
			sql += " limit ?,?";
			args.add(startIndex);
			args.add(count);
		}
		
		logger.info("sql: " + sql + " args: " + args.toString());
		
		return dealJdbcTemplate.query(sql, args.toArray(new Object[args.size()]), new DealAppsRowMapper());
	}
	
    public List<DealApp> getKuickUserApps(String kuickUserId, String keyword){
	        List<Object> args = new ArrayList<Object>();
	        String sql = "select da.* from deal_app da " +
	                "left join app_member am on am.appId = da.id " +
	                "where am.kuickUserId = ? and da.`status` = 0 and am.`status` = 0 ";
	        args.add(kuickUserId);     
	        
            if(StringUtils.isNotBlank(keyword)){
                sql += " and ( da.id = ? or da.name like ? )";
                args.add(keyword);
                args.add(keyword+"%");
            }
            
            sql += "order by am.createTime desc ";
	        
	        logger.info("getKuickUserApps sql: " + sql + " args: " + args.toString());
	       
	        return dealJdbcTemplate.query(sql, args.toArray(new Object[args.size()]), new DealAppsRowMapper());
	   }
	   
}
