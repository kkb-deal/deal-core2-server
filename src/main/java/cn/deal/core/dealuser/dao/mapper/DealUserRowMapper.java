package cn.deal.core.dealuser.dao.mapper;

import org.springframework.jdbc.core.RowMapper;

import cn.deal.core.dealuser.domain.DealUser;

import java.sql.ResultSet;
import java.sql.SQLException;


public class DealUserRowMapper implements RowMapper<DealUser> {

    @Override
    public DealUser mapRow(ResultSet rs, int rowNum) throws SQLException {
        DealUser dealUser = new DealUser();
        dealUser.setId(rs.getString("id"));
        dealUser.setAppId(rs.getString("appId"));
        dealUser.setName(rs.getString("name"));
        dealUser.setTitle(rs.getString("title"));
        dealUser.setEmail(rs.getString("email"));
        dealUser.setPhone(rs.getString("phoneNum"));
        dealUser.setCompany(rs.getString("company"));
        dealUser.setAppUserId(rs.getString("appUserId"));
        dealUser.setStatus(rs.getInt("status"));
        dealUser.setPhotoURL(rs.getString("photoURL"));
        dealUser.setCreateTime(rs.getTimestamp("createTime"));
        dealUser.setEditTime(rs.getTimestamp("editTime"));
        dealUser.setLastLoginTime(rs.getTimestamp("lastLoginTime"));
        dealUser.setPassword(rs.getString("password"));
        dealUser.setPhotoWidth(rs.getString("photoWidth"));
        dealUser.setPhotoHeight(rs.getString("photoHeight"));
        dealUser.setUnionid(rs.getString("unionid"));
        dealUser.setOpenid(rs.getString("openid"));
        dealUser.setDeviceId(rs.getString("deviceId"));
        dealUser.setFromType(rs.getString("fromType"));
        dealUser.setIsNamed(rs.getInt("isNamed"));
        dealUser.setDeviceId2(rs.getString("deviceId2"));
        dealUser.setFromInfo(rs.getString("fromInfo"));
        dealUser.setUtmSource(rs.getString("utmSource"));
        dealUser.setUtmTerm(rs.getString("utmTerm"));
        dealUser.setFromProvince(rs.getString("fromProvince"));
        dealUser.setFromCity(rs.getString("fromCity"));
        dealUser.setUtmMedium(rs.getString("utmMedium"));
        dealUser.setUtmCampaign(rs.getString("utmCampaign"));
        dealUser.setUtmContent(rs.getString("utmContent"));
        dealUser.setSearchedKeyword(rs.getString("searchedKeyword"));
        return dealUser;
    }
}
