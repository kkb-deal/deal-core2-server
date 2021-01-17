package cn.deal.core.license.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import cn.deal.core.license.domain.AppLicense;

/**
 * 项目名称：deal-core-server2
 * 类名称：AppLicenseDao
 */

/**
 * 项目名称：deal-core-server2
 * 类名称：AppLicenseDao
 */
@Repository
public class AppLicenseDao {
    @Autowired
    private JdbcTemplate dealJdbcTemplate;

    public static class AppLicensesRowMapper implements RowMapper<AppLicense> {

        @Override
        public AppLicense mapRow(ResultSet rs, int rowNum) throws SQLException {
            AppLicense appLicense = new AppLicense();
            appLicense.setId(rs.getString("id"));
            appLicense.setAppId(rs.getString("appId"));
            appLicense.setMaxAppCount(rs.getInt("maxAppCount"));
            appLicense.setMaxAppMemberCount(rs.getInt("maxAppMemberCount"));
            appLicense.setMaxBIUserCount(rs.getInt("maxBIUserCount"));
            appLicense.setMaxCallerCount(rs.getInt("maxCallerCount"));
            appLicense.setMaxDemoMemberCount(rs.getInt("maxDemoMemberCount"));
            appLicense.setMaxFileSenderCount(rs.getInt("maxFileSenderCount"));
            appLicense.setMaxOfficialAccountsCount(rs.getInt("maxOfficialAccountsCount"));
            appLicense.setMaxMailSenderCount(rs.getInt("maxMailSenderCount"));
            appLicense.setMaxWebSiteCount(rs.getInt("maxWebSiteCount"));
            if (StringUtils.isNotBlank(rs.getString("maxWeixinAppCount"))){
                appLicense.setMaxWeixinAppCount(rs.getInt("maxWeixinAppCount"));
            }
            if (StringUtils.isNotBlank(rs.getString("maxYouzanShopCount"))){
                appLicense.setMaxYouzanShopCount(rs.getInt("maxYouzanShopCount"));
            }
            if (StringUtils.isNotBlank(rs.getString("maxLiukeWeixinCount"))){
                appLicense.setMaxLiukeWeixinCount(rs.getInt("maxLiukeWeixinCount"));
            }
            if (StringUtils.isNotBlank(rs.getString("maxLiukeWeixinMemberCount"))){
                appLicense.setMaxLiukeWeixinMemberCount(rs.getInt("maxLiukeWeixinMemberCount"));
            }
            appLicense.setSign(rs.getString("sign"));
            appLicense.setVersion(rs.getInt("version"));
            appLicense.setExpiresTime(rs.getString("expiresTime"));
            try {
                appLicense.setCreatedAt(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(rs.getString("createdAt")));
            } catch (ParseException e) {
                throw new SQLException(e.getMessage(), e);
            }
            appLicense.setUpdatedAt(rs.getTimestamp("updatedAt"));
            appLicense.setType(rs.getString("app_type"));
            appLicense.setEdition(rs.getString("edition"));
            appLicense.setIsTrial(rs.getBoolean("isTrial"));
            appLicense.setIncludeModules(rs.getString("includeModules"));

            return appLicense;
        }
    }

    /**
     * @param appId
     * @return List<AppLicense>    返回类型
     * @throws
     * @Title: getAppLicenseList
     * @Description: TODO(这里用一句话描述这个方法的作用)
     */
    public List<AppLicense> getAppLicenseList(String appId) {
        List<Object> args = new ArrayList<Object>();
        String sql = "select app.* from app_license app where app.appId=?  order by app.createdAt desc";
        args.add(appId);

        List<AppLicense> list = dealJdbcTemplate.query(sql, args.toArray(new Object[args.size()]), new AppLicensesRowMapper());

        return list;
    }

    public int deleteById(String id) {
        String sql = "delete from app_license where id = ?";

        List<Object> args = new ArrayList<>();
        args.add(id);

        return dealJdbcTemplate.update(sql, args.toArray(new Object[0]));
    }

    public AppLicense getAppLicenseByIdAndAppId(String id, String appId) {
        List<Object> args = new ArrayList<Object>();
        String sql = "select app.* from app_license app where app.id=? and app.appId=?";
        args.add(id);
        args.add(appId);
        List<AppLicense> list = dealJdbcTemplate.query(sql, args.toArray(new Object[args.size()]),
                new AppLicensesRowMapper());
        return (list != null && list.size() > 0) ? list.get(0) : null;
    }

    public int insert(AppLicense al) {
        String sql = "INSERT INTO `app_license` (`id`, `appId`, `maxAppMemberCount`, `maxOfficialAccountsCount`, `maxWebSiteCount`, `maxAppCount`, `maxCallerCount`, `maxMailSenderCount`, `maxFileSenderCount`, `maxDemoMemberCount`, `maxWeixinAppCount`, `maxBIUserCount`, `maxYouzanShopCount`, `maxLiukeWeixinCount`, `maxLiukeWeixinMemberCount`, `expiresTime`, `version`, `sign`, `createdAt`, `updatedAt`, `app_type`, `edition`, `includeModules`, `isTrial`)\n" +
                "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP(3), ?, ?, ?, ?, ?);";
        Object[] args = {al.getId(), al.getAppId(), al.getMaxAppMemberCount(), al.getMaxOfficialAccountsCount(), al.getMaxWebSiteCount(), al.getMaxAppCount(), al.getMaxCallerCount(), al.getMaxMailSenderCount(), al.getMaxFileSenderCount(), al.getMaxDemoMemberCount(), al.getMaxWeixinAppCount(), al.getMaxBIUserCount(), al.getMaxYouzanShopCount(), al.getMaxLiukeWeixinCount(), al.getMaxLiukeWeixinMemberCount(), al.getExpiresTime(), al.getVersion(), al.getSign(), al.getUpdatedAt(), al.getType(), al.getEdition(), al.getIncludeModules(), al.getIsTrial()};
        return dealJdbcTemplate.update(sql, args);
    }
}
