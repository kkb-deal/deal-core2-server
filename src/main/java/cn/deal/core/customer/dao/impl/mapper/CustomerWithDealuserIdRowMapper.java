package cn.deal.core.customer.dao.impl.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import cn.deal.core.dealuser.domain.CustomerWithDealuserId;


public class CustomerWithDealuserIdRowMapper implements RowMapper<CustomerWithDealuserId> {

    @Override
    public CustomerWithDealuserId mapRow(ResultSet rs, int rowNum) throws SQLException {
        CustomerWithDealuserId customer = new CustomerWithDealuserId();
        customer.setId(rs.getString("id"));
        customer.setAppId(rs.getString("appId"));
        customer.setName(rs.getString("name"));
        customer.setTitle(rs.getString("title"));
        customer.setEmail(rs.getString("email"));
        customer.setPhone(rs.getString("phone"));
        customer.setCompany(rs.getString("company"));
        customer.setHeadportraitUrl(rs.getString("headportraitUrl"));
        customer.setStatus(rs.getInt("status"));
        customer.setMergedCustomerId(rs.getString("mergedCustomerId"));
        customer.setCreatedAt(rs.getTimestamp("createdAt"));
        customer.setUpdatedAt(rs.getTimestamp("updatedAt"));
        customer.setGroupId(rs.getString("groupId"));
        customer.setSex(rs.getInt("sex"));
        customer.setAddress(rs.getString("address"));
        customer.setAgeState(rs.getInt("ageState"));
        customer.setFixedPhone(rs.getString("fixedPhone"));
        customer.setProvince(rs.getString("province"));
        customer.setCity(rs.getString("city"));
        customer.setCounty(rs.getString("county"));
        customer.setLeadSource(rs.getString("leadSource"));
        customer.setGrade(rs.getInt("grade"));
        customer.setIndustry(rs.getString("industry"));
        customer.setIntentionality(rs.getInt("intentionality"));
        customer.setSource(rs.getString("source"));
        customer.setFrom(rs.getString("from"));
        customer.setGetWay(rs.getString("getWay"));
        customer.setPromoterId(rs.getString("promoterId"));
        customer.setFromContentTitle(rs.getString("fromContentTitle"));
        customer.setFromContentLink(rs.getString("fromContentLink"));
        customer.setSearchKeyword(rs.getString("searchKeyword"));
        customer.setBuyedKeyword(rs.getString("buyedKeyword"));
        customer.setPlatform(rs.getString("platform"));
        customer.setCreateWay(rs.getInt("createWay"));
        customer.setFromProvince(rs.getString("fromProvince"));
        customer.setFromCity(rs.getString("fromCity"));
        customer.setUtmMedium(rs.getString("utmMedium"));
        customer.setUtmCampaign(rs.getString("utmCampaign"));
        customer.setUtmContent(rs.getString("utmContent"));
        customer.setWhetherMerge(rs.getInt("whetherMerge"));
        customer.setIsNew(rs.getInt("isNew"));
        customer.setKuickUserId(rs.getString("kuickUserId"));
        customer.setGroupName(rs.getString("groupName"));
        customer.setNewCount(rs.getInt("newCount"));
        customer.setIsOfficialAccountFans(rs.getInt("isOfficialAccountFans"));
        customer.setPhoneProvince(rs.getString("phoneProvince"));
        customer.setPhoneCity(rs.getString("phoneCity"));
        customer.setPhoneISP(rs.getString("phoneISP"));
        customer.setDealUserId(rs.getString("dealUserId"));
        return customer;
    }
}
