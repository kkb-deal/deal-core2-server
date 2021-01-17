package cn.deal.core.dealuser.dao;

import cn.deal.core.dealuser.dao.mapper.DealUserCustomerRowMapper;
import cn.deal.core.dealuser.dao.mapper.DealUserRowMapper;
import cn.deal.core.dealuser.dao.mapper.DealUserSimpleRowMapper;
import cn.deal.core.dealuser.domain.DealUser;
import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Repository
public class DealUserDao {

    private static final String SQL_SELECT_TINY_BY_CUSTOMER = "select d.id, d.openid, d.deviceId, d.deviceId2 from deal_user d left join customer_link_deal_user c on d.id = c.dealUserId where d.appId = ? AND c.customerId = ? ORDER BY d.createTime DESC LIMIT ?, ? ";
    private static final String SQL_SELECT_BY_CUSTOMER = "select d.* from deal_user d left join customer_link_deal_user c on d.id = c.dealUserId where d.appId = ? AND c.customerId = ? ORDER BY d.createTime DESC LIMIT ?, ? ";
    
    private static final Logger logger = LoggerFactory.getLogger(DealUserDao.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    public List<DealUser> getDealUsersByIds(List<String> dealUserIds) {
        String sql = " select du.* from deal_user du where du.id in( :ids ) ";
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("ids", dealUserIds);

        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        logger.info("getDealUsersByIds sql: " + sql + " args: " + paramMap);
        
        List<DealUser>  list = namedParameterJdbcTemplate.query(sql, paramMap, new DealUserRowMapper());
        
        logger.info("getDealUsersByIds result : " + list );
        
        return list;
    }

    public List<DealUser> getDealUsersByIdsWithAppId(String appId, List<String> dealUserIds) {
        String sql = " select du.* from deal_user du where du.appId =:appId and  du.id in( :ids ) ";
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("appId", appId);
        paramMap.put("ids", dealUserIds);

        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        logger.info("getDealUsersByIds sql: " + sql + " args: " + paramMap);
        
        List<DealUser>  list = namedParameterJdbcTemplate.query(sql, paramMap, new DealUserRowMapper());
        
        logger.info("getDealUsersByIds result : " + list );
        
        return list;
    }
    
    public DealUser findOneByOption(DealUser dealUser) {
        String sql = " select du.* from deal_user du where appId = :appId ";
        Map<String, Object> paramMap = Maps.newHashMap();
        paramMap.put("appId", dealUser.getAppId());

        if (StringUtils.isNotBlank(dealUser.getId())) {
            sql += " and du.id = :id";
            paramMap.put("id", dealUser.getId());
        }

        if (StringUtils.isNotBlank(dealUser.getDeviceId())) {
            sql += " and deviceId = :deviceId";
            paramMap.put("deviceId", dealUser.getDeviceId());
        }

        if (StringUtils.isNotBlank(dealUser.getDeviceId2())) {
            sql += " and deviceId2 = :deviceId2";
            paramMap.put("deviceId2", dealUser.getDeviceId2());
        }

        if (StringUtils.isNotBlank(dealUser.getUnionid())) {
            sql += " and unionid = :unionid";
            paramMap.put("unionid", dealUser.getUnionid());
        }

        if (StringUtils.isNotBlank(dealUser.getOpenid())) {
            sql += " and openid = :openid";
            paramMap.put("openid", dealUser.getOpenid());
        }

        if (StringUtils.isNotBlank(dealUser.getAppUserId())) {
            sql += " and appUserId = :appUserId";
            paramMap.put("appUserId", dealUser.getAppUserId());
        }

        sql += " limit 1";

        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        logger.info("getDealUsersByIds sql: " + sql + " args: " + paramMap);

        DealUser du;
        try {
            du = namedParameterJdbcTemplate.queryForObject(sql, paramMap, new DealUserRowMapper());
        } catch (EmptyResultDataAccessException e) {
            du = null;
        }
        logger.info("getDealUsersByIds result : " + du);
        return du;
    }

    public List<DealUser> findAllByOption(DealUser dealUser) {
        String sql = " select du.*, c.id as customerId, c.name as customerName, " +
                "c.headportraitUrl, c.email as customerEmail, c.company as customerCompany, c.phone from deal_user du " +
                "left join customer_link_deal_user cldu on du.id = cldu.dealUserId " +
                "left join customer c on cldu.customerId = c.id " +
                "where du.appId = :appId ";

        Map<String, Object> paramMap = Maps.newHashMap();
        paramMap.put("appId", dealUser.getAppId());

        if (StringUtils.isNotBlank(dealUser.getDeviceId())) {
            sql += " and deviceId = :deviceId";
            paramMap.put("deviceId", dealUser.getDeviceId());
        }

        if (StringUtils.isNotBlank(dealUser.getUnionid())) {
            sql += " and unionid = :unionid";
            paramMap.put("unionid", dealUser.getUnionid());
        }

        if (StringUtils.isNotBlank(dealUser.getOpenid())) {
            sql += " and openid = :openid";
            paramMap.put("openid", dealUser.getOpenid());
        }

        if (StringUtils.isNotBlank(dealUser.getAppUserId())) {
            sql += " and appUserId = :appUserId";
            paramMap.put("appUserId", dealUser.getAppUserId());
        }

        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        logger.info("getDealUsersByIds sql: " + sql + " args: " + paramMap);

        List<DealUser> list = namedParameterJdbcTemplate.query(sql, paramMap, new DealUserCustomerRowMapper());

        logger.info("getDealUsersByIds result : " + list );
        return list;
    }

    /**
     * 分页查询客户
     * 
     * @param appId
     * @param customerId
     * @param currentPage
     * @param pageSize
     * @return
     */
    public List<DealUser> findTinyDealUserByCustomerIdAndPage(String appId, String customerId, int currentPage, int pageSize) {
        List<DealUser> list = jdbcTemplate.query(SQL_SELECT_TINY_BY_CUSTOMER, new Object[]{appId, customerId, currentPage, pageSize}, new DealUserSimpleRowMapper());
        logger.info("findByCustomer. appId: {}, customerId: {}, currentPage: {}, pageSize: {}, result: {}", appId, customerId, currentPage, pageSize, list);
        return list;
    }
    
    
    public List<DealUser> findDealUserByCustomerIdAndPage(String appId, String customerId, int currentPage, int pageSize) {
        List<DealUser> list = jdbcTemplate.query(SQL_SELECT_BY_CUSTOMER, new Object[]{appId, customerId, currentPage, pageSize}, new DealUserRowMapper());
        logger.info("findByCustomer. appId: {}, customerId: {}, currentPage: {}, pageSize: {}, result: {}", appId, customerId, currentPage, pageSize, list);
        return list;
    }

    /**
     * 查询DealUser
     * 
     * @param dealUserId
     * @return
     */
	public DealUser getDealUserById(String dealUserId) {
		String sql = " select du.* from deal_user du where du.id=:id";
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("id", dealUserId);

        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        logger.info("getDealUserById sql: " + sql + " args: " + paramMap);
        
        List<DealUser>  list = namedParameterJdbcTemplate.query(sql, paramMap, new DealUserRowMapper());
        if (list!=null && list.size()>0) {
        	logger.info("getDealUserById result : " + list );
            return list.get(0);
        }
        
        return null;
	}
}
