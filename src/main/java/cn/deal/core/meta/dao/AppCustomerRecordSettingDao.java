package cn.deal.core.meta.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import cn.deal.core.meta.domain.AppCustomerRecordSetting;

/**   
*    
* 项目名称：deal-core-server2   
* 类名称：AppCustomerRecordSettingDao   
* 类描述：项目记录项接口服务
*/
@Repository
public class AppCustomerRecordSettingDao { 
    private static final Log log = LogFactory.getLog(AppCustomerRecordSettingDao.class);

    @Autowired
    private JdbcTemplate dealJdbcTemplate;
    
    public static class AppCustomerRecordSettingsRowMapper implements RowMapper<AppCustomerRecordSetting> {

        @Override
        public AppCustomerRecordSetting mapRow(ResultSet rs, int rowNum) throws SQLException {
            AppCustomerRecordSetting appCustomerRecordSetting = new AppCustomerRecordSetting();

            appCustomerRecordSetting.setId(rs.getString("id"));
            appCustomerRecordSetting.setAppId(rs.getString("appId"));
            appCustomerRecordSetting.setIndex(rs.getInt("index"));
            appCustomerRecordSetting.setName(rs.getString("name"));
            appCustomerRecordSetting.setDescription(rs.getString("description"));
            appCustomerRecordSetting.setAction(rs.getString("action"));
            appCustomerRecordSetting.setStatus(rs.getInt("status"));
            appCustomerRecordSetting.setCreatedAt(rs.getTimestamp("createdAt"));
            return appCustomerRecordSetting;
        }
    }
    
    /** 
    * @Title: getReName 
    * @Description: TODO(根据参数查询是否重名的配置项存在) 
    * @param appId
    * @param name
    * @param action
    * @return 设定文件 
    * @return List<AppCustomerRecordSetting>    返回类型 
    * @throws 
    */
    public List<AppCustomerRecordSetting> getAppCustomerRecordSettings(String appId, String name, String action, Integer status) {
        List<Object> args = new ArrayList<Object>();
        String sql = "select acrs.* from app_customer_record_setting acrs where acrs.appId=? ";
        args.add(appId);
        
        if (StringUtils.isNotBlank(name)) {
            sql += " and (acrs.name= ? or acrs.`action`= ?) ";
            args.add(name);
            args.add(action);
        }

        if (StringUtils.isNotBlank(action)) {
            sql += " and acrs.`action`= ? ";
            args.add(action);
        }

        if(status !=null){
            sql += " and acrs.status = ? ";
            args.add(status);
        }
        
        sql += " order by acrs.`index`,acrs.createdAt desc ";
        
        log.info("\n getReName sql:"+ sql );
        log.info("\n getReName args:"+ args );
        return dealJdbcTemplate.query(sql, args.toArray(new Object[args.size()]),
                new AppCustomerRecordSettingsRowMapper());
    }
    
   public Integer getMaxIndex( String appId){
       String sql = "select max(acrs.`index`) as maxIndex  from app_customer_record_setting acrs where acrs.appId=?  and acrs.status = 1 ";
       
       List<Object> args = new ArrayList<Object>();
       args.add(appId);
       
       Object[] para = args.toArray(new Object[args.size()]);
       log.info("\n getReName sql:"+ sql );
       log.info("\n getReName args:"+ args );
       Integer index = dealJdbcTemplate.queryForObject(sql, para, Integer.class);
       index = index!=null ? index+1 : 0;
       return index;
   }
}
