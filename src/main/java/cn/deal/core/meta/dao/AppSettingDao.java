package cn.deal.core.meta.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import cn.deal.core.meta.domain.AppSetting;

@Repository
public class AppSettingDao {

	private static final Log logger = LogFactory.getLog(AppSettingDao.class);

	@Autowired
	private JdbcTemplate dealJdbcTemplate;
	
	public static class AppSettingsRowMapper implements RowMapper<AppSetting> {

		@Override
		public AppSetting mapRow(ResultSet rs, int rowNum) throws SQLException {
			AppSetting appSetting = new AppSetting();

			appSetting.setId(rs.getString("id"));
			appSetting.setType(rs.getString("type"));
			appSetting.setKey(rs.getString("key"));
			appSetting.setValue(rs.getString("value"));
			appSetting.setDefaultValue(rs.getString("defaultValue"));
			appSetting.setCreatedAt(rs.getTimestamp("createdAt"));
			appSetting.setUpdatedAt(rs.getTimestamp("updatedAt"));
			appSetting.setAppId(rs.getString("appId"));
			return appSetting;
		}
	}
	
	/** 
	* @Title: getAppSettingByAppIdAndKey 
	* @Description: TODO(校验重名) 
	* @param key
	* @param appId
	* @return 设定文件 
	* @return AppSetting    返回类型 
	* @throws 
	*/
	public AppSetting getAppSettingByAppIdAndKey(String appId, String key) {
		String sql = "select d.* from  app_setting d  where d.appId=? and d.key=? limit 0,1";

		List<Object> args = new ArrayList<Object>();
		args.add(appId);
		args.add(key);

		logger.info("getAppSettingByAppIdAndKey sql:" + sql);
		logger.info("getAppSettingByAppIdAndKey args:" + args.size());

		List<AppSetting> settings = dealJdbcTemplate.query(sql, args.toArray(new Object[args.size()]),
				new AppSettingsRowMapper());

		return DataAccessUtils.singleResult(settings);
	}
}
