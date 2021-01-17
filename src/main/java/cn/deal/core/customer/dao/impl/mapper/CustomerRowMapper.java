package cn.deal.core.customer.dao.impl.mapper;

import cn.deal.component.utils.JsonUtil;
import cn.deal.core.customer.domain.Customer;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


public class CustomerRowMapper implements RowMapper<Customer> {

    protected static final Map<String, String> UNMODIFIABLE_EMPTY_MAP = Collections.unmodifiableMap(new LinkedHashMap<String, String>());

	@Override
    public Customer mapRow(ResultSet rs, int rowNum) throws SQLException {
        Customer customer = new Customer();
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

        if (isExistColumn(rs, "assignMemberTime")) {
            customer.setAssignMemberTime(rs.getTimestamp("assignMemberTime"));
        }

        String extensionsStr = rs.getString("extensions");
        customer.setUniqueKey1(rs.getString("uniqueKey1"));
        customer.setUniqueKey2(rs.getString("uniqueKey2"));
        customer.setUniqueKey3(rs.getString("uniqueKey3"));
        if (StringUtils.isNotBlank(extensionsStr) && !"null".equals(extensionsStr) && extensionsStr.trim().startsWith("{")) {
        	try {
	        	Map<String, String> extensions = JsonUtil.jsonToMap(extensionsStr);
	        	customer.setExtensions(extensions);
        	} catch(Exception e) {
        		customer.setExtensions(new HashMap<String, String>());
        	}
        } else {
        	customer.setExtensions(new HashMap<String, String>());
        }
        
        return customer;
	}
	
	public String getExtensions(Customer customer) throws JsonProcessingException {
		Map<String, String> map = customer.getExtensions();
		return JsonUtil.toJson(map);
	}

    private boolean isExistColumn(ResultSet rs, String columnName) {
        try {
            if (rs.findColumn(columnName) > 0 ) {
                return true;
            }
        }
        catch (SQLException ignored) {
            return false;
        }

        return false;
    }

}
