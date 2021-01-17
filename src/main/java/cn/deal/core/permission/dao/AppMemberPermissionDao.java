package cn.deal.core.permission.dao;

import cn.deal.core.permission.domain.AppMemberPermission;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class AppMemberPermissionDao {
	private static final Logger logger = LoggerFactory.getLogger(AppMemberPermissionDao.class);

	@Autowired
	private JdbcTemplate dealJdbcTemplate;
	public static class AppMemberPermissionsRowMapper implements RowMapper<AppMemberPermission> {

		@Override
		public AppMemberPermission mapRow(ResultSet rs, int rowNum) throws SQLException {
			AppMemberPermission appMemberPermission = new AppMemberPermission();

			appMemberPermission.setId(rs.getString("id"));
			appMemberPermission.setAppId(rs.getString("appId"));
			appMemberPermission.setKuickUserId(rs.getInt("kuickUserId"));
			appMemberPermission.setDomainId(rs.getString("domainId"));
			appMemberPermission.setDomainType(rs.getString("domainType"));
			appMemberPermission.setPerm(rs.getString("perm"));
			appMemberPermission.setCreatedAt(rs.getTimestamp("createdAt"));
			return appMemberPermission;
		}
	}
	
	/** 
	* @Title: getAppMemberPermissionList 
	* @Description: TODO(筛选kuickUser菜单权限) 
	* @param appId
	* @param kuickUserId
	* @param domainTypes
	* @param domainIds
	* @return 设定文件 
	* @return List<AppMemberPermission>    返回类型 
	* @throws 
	*/
	public List<AppMemberPermission> getAppMemberPermissionList(String appId,  int kuickUserId, List<String> domainTypes, List<String> domainIds){
		List<Object> args = new ArrayList<>();
		String sql = "select app.* from app_member_permission app where app.appId=? and kuickUserId=? ";
		args.add(appId);
		args.add(kuickUserId);

		if (domainTypes != null && !domainTypes.isEmpty()) {
			sql += " and domainType in ( ? ) ";
			args.add(StringUtils.join(domainTypes, ","));
		}

		if (domainIds != null && !domainIds.isEmpty()) {
			sql += " and domainId in ( ? ) ";
			args.add(StringUtils.join(domainIds, ","));
		}
		
		return dealJdbcTemplate.query(sql, args.toArray(new Object[args.size()]), new AppMemberPermissionsRowMapper());
	}
	
	public List<AppMemberPermission> getAppMemberPermissionByDomainTypeAndKuickuserids(String appId, List<String> kuickUserIds, String domainType){
		logger.info( "appId:{}, kuickUserIds: {}, domainType: {}", appId, kuickUserIds, domainType);
		if (StringUtils.isBlank(appId) || StringUtils.isBlank(domainType) || kuickUserIds==null || kuickUserIds.size()==0) {
			logger.error("缺少参数");
			return new ArrayList<>();
		}
		String sql = "select * from app_member_permission where appId = :appId and domainType = :domainType and kuickUserId in (:kuickUserIds) ";
		List<Object> args = new ArrayList<>();
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("appId", appId);
		paramMap.put("domainType", domainType);
		paramMap.put("kuickUserIds", kuickUserIds);
		NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dealJdbcTemplate);
		List<AppMemberPermission> result = namedParameterJdbcTemplate.query(sql, paramMap, new AppMemberPermissionsRowMapper());
		return result;
	}

	public List<AppMemberPermission> getAppMemberPermissionByKuickUserIds(String appId, List<Integer> kuickUserIds) {
		logger.info("appId:{}, kuickUserIds: {}, domainType: {}", appId, kuickUserIds);
		if (StringUtils.isBlank(appId) || kuickUserIds == null || kuickUserIds.size() == 0) {
			logger.error("缺少参数");
			return new ArrayList<>();
		}
		String sql = "select * from app_member_permission where appId = :appId and kuickUserId in (:kuickUserIds) ";
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("appId", appId);
		paramMap.put("kuickUserIds", kuickUserIds);
		NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dealJdbcTemplate);
		List<AppMemberPermission> result = namedParameterJdbcTemplate.query(sql, paramMap, new AppMemberPermissionsRowMapper());
		return result;
	}

}

