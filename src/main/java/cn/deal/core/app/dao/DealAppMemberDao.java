package cn.deal.core.app.dao;

import cn.deal.core.app.domain.AppMember;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository
@CacheConfig(cacheNames = "appmember", cacheManager = "redis")
public class DealAppMemberDao {

    private static final Log logger = LogFactory.getLog(DealAppMemberDao.class);

    @Autowired
    private JdbcTemplate dealJdbcTemplate;

    public static class DealAppMembersRowMapper implements RowMapper<AppMember> {

        /**
         * 2017年12月14日上午11:08:36
         * panpan
         *
         * @see org.springframework.jdbc.core.RowMapper#mapRow(java.sql.ResultSet, int)
         * TODO
         */
        @Override
        public AppMember mapRow(ResultSet rs, int rowNum) throws SQLException {
            AppMember dealAppMember = new AppMember();

            dealAppMember.setId(rs.getString("id"));
            dealAppMember.setAppId(rs.getString("appId"));
            dealAppMember.setConferenceId(rs.getInt("conferenceId"));
            dealAppMember.setKuickUserId(rs.getInt("kuickUserId"));
            dealAppMember.setCreateTime(rs.getTimestamp("createTime"));
            dealAppMember.setEditTime(rs.getTimestamp("editTime"));
            dealAppMember.setPostRoles(rs.getString("postRoles"));
            dealAppMember.setRole(rs.getString("role"));
            dealAppMember.setStatus(rs.getInt("status"));

            return dealAppMember;
        }
    }
    @CacheEvict(key = "'appMember:appId:'+#p0")
    public int updateAppMember(String appId, Integer kuickUserId, Integer conferenceId) {
        List<Object> args = new ArrayList<Object>();
        String sql = "update app_member set conferenceId = ?, editTime = ? where appId = ? and kuickUserId = ?";

        args.add(conferenceId);
        args.add(new Date());
        args.add(appId);
        args.add(kuickUserId);

        logger.info("sql: " + sql + " args: " + args.toString());

        return dealJdbcTemplate.update(sql, args.toArray(new Object[args.size()]));
    }

    public List<AppMember> getDealAppMembers(String appId, String departmentIds, List<String> roles, String keyword, Integer startIndex, Integer count) {
        List<Object> args = new ArrayList<>();
        String sql = "select d.* from app_member d left join user u on d.kuickUserId = u.id where d.appid = ? and d.status = 0 ";
        args.add(appId);

        if (StringUtils.isNotBlank(departmentIds)) {
            sql += " and d.departmentId in (?) ";
            args.add(departmentIds);
        }

        if (roles != null && !roles.isEmpty()) {
            StringBuffer r = new StringBuffer();
            roles.forEach(role -> {
                r.append("'").append(role).append("',");
            });
            r.deleteCharAt(r.length() - 1);
            sql += " and d.role in ("+r.toString()+") ";
        }

        if (StringUtils.isNotBlank(keyword)) {
            sql += " and (u.name like '" + keyword + "%' or u.phoneNum = '" + keyword + "' or u.email = '" + keyword + "') ";
        }

        sql += " ORDER BY d.createTime DESC ";
        if (startIndex != null && count != null) {
            if (count > 1000) {
                count = 1000;
            }
            sql += " limit ?,?";
            args.add(startIndex);
            args.add(count);
        }

        logger.info("sql: " + sql + " args: " + args.toString());
        return dealJdbcTemplate.query(sql, args.toArray(new Object[args.size()]), new DealAppMembersRowMapper());
    }

    public List<AppMember> getDealAppMembers(String appId, Integer startIndex, Integer count) {
        List<Object> args = new ArrayList<Object>();
        String sql = "select d.* from  app_member d  where d.appid=? and status = 0 ";

        args.add(appId);

        sql += " ORDER BY d.createTime DESC ";

        if (startIndex != null && count != null) {
            if (count > 100) {
                count = 100;
            }
            sql += " limit ?,?";
            args.add(startIndex);
            args.add(count);
        }

        logger.info("sql: " + sql + " args: " + args.toString());

        return dealJdbcTemplate.query(sql, args.toArray(new Object[args.size()]), new DealAppMembersRowMapper());
    }

    public List<AppMember> getDealAppMembers(String appId) {
        List<Object> args = new ArrayList<Object>();
        String sql = "select d.* from  app_member d  where d.appid = ? and status = 0 ";

        args.add(appId);

        logger.info("sql: " + sql + " args: " + args.toString());
        return dealJdbcTemplate.query(sql, args.toArray(new Object[args.size()]), new DealAppMembersRowMapper());
    }

    /**
     * @param appId
     * @return int
     */
    public int getAppMemberCount(final String appId) {
        return dealJdbcTemplate.execute(conn -> {
            String sql = "select count(*)  from app_member app " +
                    "left join user u on app.kuickUserId = u.id " +
                    "where app.appId= ? and app.status = 0 and u.status <> 2 ";

            List<Object> args = new ArrayList<Object>();
            args.add(appId);

            PreparedStatement ps = conn.prepareStatement(sql);
            logger.info("sql: " + sql + " args: " + args.size());
            logger.info("ps count: " + ps.getParameterMetaData().getParameterCount());
            for (int i = 0; i < ps.getParameterMetaData().getParameterCount(); i++) {
                logger.info("ps agrgs[i]: " + args.get(i));

                ps.setObject(i + 1, args.get(i));
            }

            return ps;
        }, (PreparedStatementCallback<Integer>) pstmt -> {
            pstmt.execute();
            ResultSet rs = pstmt.getResultSet();
            rs.next();
            return rs.getInt(1);
        });
    }

}
