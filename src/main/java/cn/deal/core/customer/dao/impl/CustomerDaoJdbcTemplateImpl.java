package cn.deal.core.customer.dao.impl;

import cn.deal.component.domain.filter.Condition;
import cn.deal.component.utils.FilterUtils;
import cn.deal.component.utils.JsonUtil;
import cn.deal.component.utils.ValidatorUtil;
import cn.deal.core.customer.dao.CustomerDao;
import cn.deal.core.customer.dao.impl.mapper.*;
import cn.deal.core.customer.domain.Customer;
import cn.deal.core.customer.domain.CustomerId;
import cn.deal.core.customer.domain.vo.CustomerSearchVO;
import cn.deal.core.customer.engine.validators.PhoneValidator;
import cn.deal.core.dealuser.domain.CustomerWithDealuserId;
import cn.deal.core.meta.domain.CustomerMetaData;
import com.alibaba.fastjson.JSON;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;


@Repository
public class CustomerDaoJdbcTemplateImpl implements CustomerDao {

    private Logger logger = LoggerFactory.getLogger(CustomerDaoJdbcTemplateImpl.class);

    @Autowired
    private JdbcTemplate dealJdbcTemplate;

    @Autowired
    private PhoneValidator phoneValidator;

    @Override
    public List<Customer> findCustomersByConditions(String appId, List<Condition> conditions,
                                                    String customerSortByUpdatedat, Integer startIndex, Integer count) {

        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("select c.*, cg.`name` as groupName, scr.whetherMerge, scr.isNew, scr.kuickUserId, scr.newCount, scr.updatedAt assignMemberTime ");
        sqlBuilder.append(" from customer c ");
        sqlBuilder.append(" left join customer_group cg on cg.id = c.groupId ");
        sqlBuilder.append(" left join sales_customer scr on scr.customerId = c.id ");
        sqlBuilder.append(" where c.`status` = 1 and scr.kuickUserId IS NOT NULL and c.appId = ? ");

        List<Object> args = new ArrayList<Object>();
        args.add(appId);

        if (conditions != null && !conditions.isEmpty()) {
            for (int i = 0; i < conditions.size(); i++) {
                Condition condition = conditions.get(i);
                String name = condition.getName();
                String range = condition.getRange();
                int type = condition.getType();
                if (StringUtils.isNotBlank(name) && StringUtils.isNotBlank(range)) {
                    String andCondition = null;
                    if (StringUtils.equals("kuickUserId", name)) {
                        if (type == 0) {
                            andCondition = " and scr." + name + " = ? ";
                            args.add(range);
                        } else if (type == 1) {
                            andCondition = " and scr." + name + " in (" + range + ") ";
                        }
                        sqlBuilder.append(andCondition);
                    } else if (StringUtils.equals("createdAt", name)) {
                        List<String> timeRange = FilterUtils.getTimeRange(type, range);
                        if (timeRange != null && timeRange.size() == 2) {
                            andCondition = " and c." + name + " >= ? and c." + name + " <= ? ";
                            sqlBuilder.append(andCondition);
                            args.add(timeRange.get(0));
                            args.add(timeRange.get(1));
                        }
                    } else if (StringUtils.equals("groupId", name)) {
                        if (StringUtils.equals("unknown", range)) {
                            andCondition = " and c." + name + " is null ";
                        } else {
                            andCondition = " and c." + name + " = ? ";
                            args.add(range);
                        }
                        sqlBuilder.append(andCondition);
                    } else {
                        andCondition = " and c." + name + " = ? ";
                        sqlBuilder.append(andCondition);
                        args.add(range);
                    }
                }
            }
        }
        if ("1".equals(customerSortByUpdatedat)) {
            sqlBuilder.append(" order by scr.updatedAt desc ");
        }
        if (startIndex != null && count != null) {
            sqlBuilder.append(" limit ?, ? ");
            args.add(startIndex);
            args.add(count);
        }
        String sql = sqlBuilder.toString();
        logger.info("screeningCustomerSql: {}", sql);
        logger.info("screeningCustomerParams: {}", args);

        return dealJdbcTemplate.query(sql, new CustomerRowMapper(), args.toArray(new Object[args.size()]));
    }

    @Override
    public List<Customer> findCustomersByExactMatch(String appId, String phone, String email, int startIndex, int count) {
        CustomerSearchVO params = CustomerSearchVO.builder().appId(appId).phone(phone).email(email).startIndex(startIndex).count(count).build();
        return findCustomersByExactMatchV2(params);
    }


    @Override
    public List<Customer> findCustomersByExactMatchV2(CustomerSearchVO param) {
        String sql = " select c.*, cg.`name` as groupName, sc.whetherMerge, sc.kuickUserId, sc.isNew, sc.newCount, sc.updatedAt assignMemberTime "
                + " from customer c "
                + " left join customer_group cg on cg.id = c.groupId "
                + " left join sales_customer sc on sc.customerId = c.id ";

        if (StringUtils.isNotBlank(param.getUnionId())) {
            sql += " left join customer_link_deal_user cldu on c.id = cldu.customerId ";
            sql += " left join deal_user du on cldu.dealUserId = du.id";

        }

        Map<String, Object> params = new HashMap<>();
        sql += " where c.appId=:appId and c.`status`=1 ";
        params.put("appId", param.getAppId());

        String conditionSql = "";
        if (StringUtils.isNotBlank(param.getPhone())) {
            conditionSql += " and c.phone=:phone ";
            params.put("phone", param.getPhone());
        }

        if (StringUtils.isNotBlank(param.getEmail())) {
            conditionSql += " and c.email=:email ";
            params.put("email", param.getEmail());
        }

        if (StringUtils.isNotBlank(param.getUnionId())) {
            conditionSql += " and du.appId = :appId and du.unionid = :unionId ";
            params.put("unionId", param.getUnionId());
        }

        sql += conditionSql;
        sql += "order by c.createdAt desc limit :start,:count ";

        params.put("start", param.getStartIndex());
        params.put("count", param.getCount());

        logger.info("findCustomersByExactMatch sql: {}", sql);
        logger.info("findCustomersByExactMatch params: {}", params);

        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dealJdbcTemplate);
        List<Customer> customers = namedParameterJdbcTemplate.query(sql, params, new CustomerRowMapper());
        logger.info("findCustomersByExactMatch result: {}", customers);

        return customers;
    }

    @Override
    public List<Customer> findCustomersBySwarm(String appId, String swarmId, String groupId, List<String> kuickUserIds,
                                               String keyword, int startIndex, int count) {
        String sql = " select c.*, cg.`name` as groupName, sc.whetherMerge, sc.kuickUserId, sc.isNew, sc.newCount "
                + " from swarm_member swarm "
                + " left join customer c "
                + " on swarm.customerId=c.id "
                + " left join customer_group cg "
                + " on cg.id=c.groupId "
                + " left join sales_customer sc "
                + " on c.id=sc.customerId "
                + " where swarm.appId=:appId and c.`status`=1 "
                + " and swarm.swarmId=:swarmId "
                + " and sc.kuickUserId in (:kuickUserIds) ";

        Map<String, Object> params = new HashMap<>();
        params.put("appId", appId);
        params.put("swarmId", swarmId);
        params.put("kuickUserIds", kuickUserIds);

        if (StringUtils.isNotBlank(groupId) && !"all".equals(groupId)) {
            sql += " and c.groupId=:groupId ";
            params.put("groupId", groupId);
        }
        if (StringUtils.isNotBlank(keyword)) {
            sql += " and (c.name like :keyword or c.phone like "
                    + " :keyword or c.email like :keyword or c.company like :keyword) ";
            params.put("keyword", "%" + keyword + "%");
        }

        if(count >= 100) {
            count = 100;
        }

        if(startIndex >= 0 || count >= 0){
            sql += " limit :startIndex, :count ";
            params.put("startIndex", startIndex);
            params.put("count", count);
        }
        logger.info("findCustomersBySwarm params:{}", params);
        logger.info("findCustomersBySwarm sql: {}", sql);
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dealJdbcTemplate);
        List<Customer> customers = namedParameterJdbcTemplate.query(sql, params, new CustomerRowMapper());
        logger.info("findCustomersBySwarm result: {}", JSON.toJSONString(customers));
        return customers;
    }


    @SuppressWarnings("rawtypes")
    @Override
    public List findCustomersBySwarmAndAttributes(String appId, String swarmId, Map<String, Integer> attributesMap, int startIndex, int count) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("appId", appId);
        paramMap.put("swarmId", swarmId);
        paramMap.put("startIndex", startIndex);
        paramMap.put("count", count);

        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dealJdbcTemplate);

        if (attributesMap == null) {
            String sqlAll = " SELECT c.* FROM swarm_member s "
                    + " left join customer c "
                    + " on s.customerId=c.id "
                    + " WHERE s.swarmId = :swarmId AND s.appId = :appId "
                    + " ORDER BY s.createdAt DESC LIMIT :startIndex,:count";

            return namedParameterJdbcTemplate.query(sqlAll, paramMap, new CustomerListRowMapper());
        } else {

            StringBuilder sql = new StringBuilder("SELECT ");
            int i = 0;
            for (String key : attributesMap.keySet()) {
                i++;
                if (attributesMap.size() != i) {
                    sql.append(" c.").append(key).append(",");
                } else {
                    sql.append(" c.").append(key);
                }
            }
            sql.append(" FROM swarm_member s "
                    + " left join customer c "
                    + " on s.customerId=c.id "
                    + " WHERE s.swarmId = :swarmId AND s.appId = :appId ");
            for (String key : attributesMap.keySet()) {
                Integer value = attributesMap.get(key);
                if (value == 1) {
                    sql.append(" AND ").append(" c.").append(key).append(" != '' ");
                }
            }
            sql.append(" ORDER BY s.createdAt DESC LIMIT :startIndex,:count");

            return namedParameterJdbcTemplate.query(sql.toString(), paramMap, new CustomerAutoListMapper());
        }
    }

    @SuppressWarnings("rawtypes")
    @Override
    public List findCustomersByAttributes(String appId, int startIndex, int count, Map<String, Integer> attributesMap) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("appId", appId);
        paramMap.put("startIndex", startIndex);
        paramMap.put("count", count);

        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dealJdbcTemplate);

        if (attributesMap == null) {
            String sqlAll = " SELECT c.* FROM swarm_member s "
                    + " left join customer c "
                    + " on s.customerId=c.id "
                    + " WHERE s.appId = :appId "
                    + " ORDER BY s.createdAt DESC LIMIT :startIndex,:count";

            return namedParameterJdbcTemplate.query(sqlAll, paramMap, new CustomerListRowMapper());
        } else {

            StringBuilder sql = new StringBuilder("SELECT ");
            int i = 0;
            for (String key : attributesMap.keySet()) {
                i++;
                if (attributesMap.size() != i) {
                    sql.append(" c.").append(key).append(",");
                } else {
                    sql.append(" c.").append(key);
                }
            }
            sql.append(" FROM swarm_member s "
                    + " left join customer c "
                    + " on s.customerId=c.id "
                    + " WHERE s.appId = :appId ");
            for (String key : attributesMap.keySet()) {
                Integer value = attributesMap.get(key);
                if (value == 1) {
                    sql.append(" AND ").append(" c.").append(key).append(" != '' ");
                }
            }
            sql.append(" ORDER BY s.createdAt DESC LIMIT :startIndex,:count");

            return namedParameterJdbcTemplate.query(sql.toString(), paramMap, new CustomerAutoListMapper());
        }
    }

    @Override
    public List<CustomerId> findCustomerIdsBySwarm(String appId, String swarmId, int startIndex, int count) {
        String sql = " SELECT s.customerId id FROM swarm_member s "
                + " join (select id from swarm_member WHERE swarmId = :swarmId "
                + " ORDER BY id LIMIT :startIndex,:count ) t on t.id=s.id";

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("swarmId", swarmId);
        paramMap.put("startIndex", startIndex);
        paramMap.put("count", count);

        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dealJdbcTemplate);
        return namedParameterJdbcTemplate.query(sql, paramMap, new CustomerIdRowMapper());
    }

    @Override
    public List<CustomerId> findCustomerIds(String appId, int startIndex, int count) {
        String sql = " SELECT s.customerId id FROM swarm_member s "
                + " WHERE s.appId = :appId "
                + " ORDER BY s.createdAt DESC LIMIT :startIndex,:count";

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("appId", appId);
        paramMap.put("startIndex", startIndex);
        paramMap.put("count", count);

        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dealJdbcTemplate);
        return namedParameterJdbcTemplate.query(sql, paramMap, new CustomerIdRowMapper());
    }

    @Override
    public long getCustomerCountByAppId(String appId, boolean withSales) {
        String sql = " select count(s.customerId) count from customer c "
                + " left join sales_customer s "
                + " on s.customerId = c.id "
                + " where c.appId=?";

        if (withSales) {
            sql += " and c.`status`=1 and s.customerId is not null";
        }

        List<String> params = new ArrayList<>();
        params.add(appId);

        Object[] para = params.toArray(new Object[params.size()]);

        return dealJdbcTemplate.queryForObject(sql.toString(), para, Long.class);
    }

    @Override
    public long getSwarmMemberCountBySwarmAndAppId(String appId, List<String> swarmIdsList) {
        StringBuilder sql;

        sql = new StringBuilder(" select count(m.customerId) count from swarm_member m " +
                " left join sales_customer s " +
                " on s.customerId = m.customerId " +
                " where m.appId=? AND m.swarmId in( ");
        for (int i = 0; i < swarmIdsList.size(); i++) {
            if (swarmIdsList.size() != i + 1) {
                sql.append("'").append(swarmIdsList.get(i)).append("'").append(",");
            } else {
                sql.append("'").append(swarmIdsList.get(i)).append("'").append(")");
            }
        }

        logger.info("查询sql:" + String.valueOf(sql));

        List<String> params = new ArrayList<>();
        params.add(appId);

        Object[] para = params.toArray(new Object[params.size()]);

        return dealJdbcTemplate.queryForObject(sql.toString(), para, Long.class);
    }

    /**
     * 2018年2月5日下午2:37:53
     * panpan
     *
     * @see cn.deal.core.customer.dao.CustomerDao findCustomersByTag(java.lang.String, java.util.List, java.lang.String, java.lang.Integer)
     * TODO 获取标签下的客户列表
     */
    @Override
    public List<Customer> findCustomersByTag(String appId, List<String> kuickUserIds, List<String> tags, Integer greateTagCount) {
        String sql = "select * FROM ( "
                + " select c.*,cg.`name` as groupName,sc.whetherMerge,sc.isNew,sc.newCount,sc.kuickUserId, sc.updatedAt assignMemberTime"
                + " count(dut.id) as tagCount from customer c "
                + " left join customer_link_deal_user cldu on cldu.customerId = c.id "
                + " left join deal_user_tag dut on dut.dealUserId = cldu.dealUserId "
                + " inner join sales_customer  sc on c.id = sc.customerId "
                + " left join customer_group cg on cg.id = c.groupId "
                + " where dut.tag in (:tags) and c.`status` = 1 and c.appId = :appId  "
                + " and  sc.kuickUserId in (:kuickUserIds) "
                + " group by c.id "
                + " order by tagCount desc )  temp ";


        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("tags", tags);
        params.addValue("appId", appId);
        params.addValue("kuickUserIds", kuickUserIds);

        if (greateTagCount != null && greateTagCount > 0) {
            sql += "where temp.tagCount > :greateTagCount";
            params.addValue("greateTagCount", greateTagCount);
        }

        logger.info("getTagCustomers   sql result:" + sql);
        logger.info("getTagCustomers   params result:" + params);

        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dealJdbcTemplate);
        List<Customer> customers = namedParameterJdbcTemplate.query(sql, params, new CustomerRowMapper());

        logger.info("getTagCustomers   sql customers:" + customers);
        return customers;
    }

    /**
     * 2018年2月5日下午2:37:29
     * panpan
     *
     * @see cn.deal.core.customer.dao.CustomerDao#getCustomerCountByTag(java.lang.String, java.lang.String)
     * TODO 根据项目id和 tag标签 获取客户数
     */
    @Override
    public long getCustomerCountByTag(String appId, String tag) {
        String sql = "select count(DISTINCT c.id) from customer c "
                + " left join customer_link_deal_user cldu on cldu.customerId = c.id "
                + " left join deal_user_tag dut on dut.dealUserId = cldu.dealUserId and dut.appId=c.appId"
                + " inner join  sales_customer sc on sc.customerId =c.id "
                + " where dut.tag =? and c.`status` = 1 and c.appId=? ";

        List<String> params = new ArrayList<>();
        params.add(tag);
        params.add(appId);
        Object[] para = params.toArray(new Object[params.size()]);
        long l = dealJdbcTemplate.queryForObject(sql, para, Long.class);
        logger.info("getTagCustomerCount   sql: {}, params: {}, result: {}", sql, params, l);
        return l;
    }

    @Override
    public Customer getCustomerById(String customerId) {
        String sql = " select c.*, cg.`name` as groupName, sc.whetherMerge, sc.kuickUserId, sc.isNew, sc.newCount from customer c "
                + " left join customer_group cg on cg.id=c.groupId "
                + " left join sales_customer sc on c.id=sc.customerId "
                + " where c.id=?";

        List<Customer> customers = dealJdbcTemplate.query(sql, new Object[]{customerId}, new CustomerRowMapper());
        if (customers != null && customers.size() > 0) {
            return customers.get(0);
        }

        return null;
    }

    @Override
    public List<Customer> findCustomerByDealUserId(String dealUserId) {
        String sql = " select c.* from customer c inner join customer_link_deal_user cl on c.id = cl.customerId where cl.dealUserId = ? ";
        return dealJdbcTemplate.query(sql, new Object[]{dealUserId}, new CustomerListRowMapper());
    }

    @Override
    public List<CustomerWithDealuserId> getCustomerListByDealuserIds(List<String> dealuserIdList) {
        if (null == dealuserIdList || dealuserIdList.isEmpty()) {
            return new ArrayList<>();
        }
        String sql = " select cl.dealUserId, c.*, cg.`name` as groupName, sc.whetherMerge, sc.kuickUserId, sc.isNew, sc.newCount "
                + " from customer c inner join customer_link_deal_user cl "
                + " on c.id = cl.customerId "
                + " left join customer_group cg "
                + " on cg.id=c.groupId "
                + " left join sales_customer sc "
                + " on c.id=sc.customerId "
                + " where cl.dealUserId in (:dealuserIds) ";
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dealJdbcTemplate);
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("dealuserIds", dealuserIdList);
        return namedParameterJdbcTemplate.query(sql, paramMap, new CustomerWithDealuserIdRowMapper());
    }

    @Override
    public long getRawCustomerCount(String appId) {
        String sql = " select count(*) count from customer where appId = ? ";
        return dealJdbcTemplate.query(sql, new Object[]{appId}, new RowMapper<Long>() {
            @Override
            public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getLong("count");
            }

        }).get(0);
    }

    @Override
    public List<Customer> getRawCustomers(String appId, Integer startIndex, Integer count) {
        String sql = " select c.*, cg.`name` as groupName, sc.whetherMerge, sc.kuickUserId,sc.isNew, sc.kuickUserId, sc.newCount, sc.updatedAt assignMemberTime "
                + " from customer c "
                + " left join ( "
                + " select id from customer where appId=:appId  order by createdAt desc limit :start,:count "
                + " ) tmp on c.id = tmp.id "
                + " left join customer_group cg on cg.id = c.groupId "
                + " left join sales_customer sc "
                + " on c.id = sc.customerId "
                + " where c.appId=:appId and c.`status`=1 and sc.customerId is not null ";
        Map<String, Object> params = new HashMap<>();
        params.put("appId", appId);
        params.put("start", startIndex);
        params.put("count", count);
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dealJdbcTemplate);
        List<Customer> customers = namedParameterJdbcTemplate.query(sql, params, new CustomerRowMapper());
        return customers;
    }


	/**
	 * 分页查询客户
	 * 
	 * @param appId
	 * @param kuickUserIds
	 * @param phone
	 * @param customerGroupId
	 * @param sortByUpdateTime
	 * @param startIndex
	 * @param count
	 * @return
	 */
	@SuppressWarnings("unchecked")
    @Override
	public List<Customer> findCustomersBySales(String appId, String[] kuickUserIds, String phone, String email,
			String customerGroupId, boolean sortByUpdateTime, int startIndex, int count) {
		Map<String, Object> params = new HashMap<>();
		String sql = "select c.*, sc.whetherMerge, sc.isNew,  sc.kuickUserId, cg.name as groupName, sc.newCount, sc.updatedAt assignMemberTime " +
            "from customer c " +
	        "left join sales_customer sc on c.id = sc.customerId " +
	        "left join customer_group cg on c.groupId = cg.id " +
	        "where c.status = 1 and sc.kuickUserId is not null ";
			        
		// 项目ID
		sql+="and c.appId = :appId ";
		params.put("appId", appId);
		
		// 根据销售过滤
		if (kuickUserIds==null || kuickUserIds.length==0) {
			return ListUtils.EMPTY_LIST;
		} else {
			String kuickUserIdsStr = "'" + StringUtils.join(kuickUserIds, "','") + "'";
			sql+="and sc.kuickUserId in (" + kuickUserIdsStr + ") ";
		}
		
		// 根据手机号过滤
		if (StringUtils.isNoneBlank(phone)) {
			sql+= "and c.phone = :phone ";
			params.put("phone", phone);
		}
	
		// 根据邮箱过滤
		if (StringUtils.isNoneBlank(email)) {
			sql+= "and c.email = :email ";
			params.put("email", email);
		}
				
		// 根据分组过滤
		if (StringUtils.isNotBlank(customerGroupId) && !"all".equals(customerGroupId)) {
			if(!customerGroupId.equals("null")) {
				sql += "and c.groupId=:groupId ";
				params.put("groupId", customerGroupId);
	        } else {
	        	sql += "and c.groupId is null ";
	        }
		}
		
		// 排序
		if(sortByUpdateTime){
	        sql += "order by sc.updatedAt desc ";
	    }
		
		// 分页
		sql += "limit :startIndex, :count ";
		params.put("startIndex", startIndex);
		params.put("count", count);
		
		logger.info("findCustomersBySales sql:{}", sql);
		logger.info("findCustomersBySales params:{}", params);
		
		// 查询并返回结果 
		NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dealJdbcTemplate);
		List<Customer> result = namedParameterJdbcTemplate.query(sql, params, new CustomerRowMapper());
		logger.info("findCustomersBySales result:{}", result);
		
		return result;
	}

	@Override
	public List<Customer> findCustomerByIdsAndPage(String appId, String[] customerIds, int startIndex, int count) {
		Map<String, Object> params = new HashMap<>();
		String sql = " select c.*, cg.`name` as groupName, sc.whetherMerge, sc.kuickUserId, sc.isNew, sc.newCount, sc.updatedAt assignMemberTime "
                + " from customer c "
				+ " left join customer_group cg on cg.id=c.groupId "
				+ " left join sales_customer sc on c.id=sc.customerId ";
				
		// 客户列表
		String customerIdsStr = "'" + StringUtils.join(customerIds, "','") + "'";
		sql += " where c.id in " + customerIdsStr;
		
		// 分页
		sql += "limit :startIndex, :count ";
		params.put("startIndex", startIndex);
		params.put("count", count);
				
		return dealJdbcTemplate.query(sql, new Object[]{}, new CustomerRowMapper());
	}

	@Override
	public List<Customer> findCustomerIds(String[] customerIds) {
		String sql = " select c.*, cg.`name` as groupName, sc.whetherMerge, sc.kuickUserId, sc.isNew, sc.newCount, sc.updatedAt assignMemberTime "
                + " from customer c "
				+ " left join customer_group cg on cg.id=c.groupId "
				+ " left join sales_customer sc on c.id=sc.customerId ";
				
		// 客户列表
		String customerIdsStr = "('" + StringUtils.join(customerIds, "','") + "')";
		sql += " where c.id in " + customerIdsStr;
				
		return dealJdbcTemplate.query(sql, new Object[]{}, new CustomerRowMapper());
	}

	@Override
	public List<Customer> findCustomerByPropNameAndPage(String appId, CustomerMetaData metaData, String value, int startIndex,
                                                        int count) {
		String sql = " select c.*, cg.`name` as groupName, sc.whetherMerge, sc.kuickUserId, sc.isNew, sc.newCount, sc.updatedAt assignMemberTime "
                + " from customer c "
				+ " left join customer_group cg on cg.id = c.groupId "
				+ " left join sales_customer sc on sc.customerId = c.id ";
		
		Map<String, Object> params = new HashMap<>();
		sql += " where c.appId=:appId and c.`status`=1 ";
		params.put("appId", appId);

        String conditionSql = "";
        if(Objects.nonNull(metaData)) {
            String propName = metaData.getName();
            if (!metaData.getIsExt() && StringUtils.isBlank(metaData.getUniqueSlot())) {
                conditionSql += " and " + selectName(propName) + "." + propName + "=:" + propName + " ";
                params.put(propName, value);
            } else if (StringUtils.isNotBlank(metaData.getUniqueSlot())) {
                conditionSql += " and " + selectName(propName) + "." + metaData.getUniqueSlot() + "=:" + metaData.getUniqueSlot() + " ";
                params.put(metaData.getUniqueSlot(), propName + ":" + value);
            }
        }
		
		sql += conditionSql;
	    sql += "order by c.createdAt desc limit :start,:count ";
	    
	    params.put("start", startIndex);
	    params.put("count", count);
	    
	    logger.info("findCustomerByPropNameAndPage sql: {}", sql);
        logger.info("findCustomerByPropNameAndPage params: {}", params);
        
		NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dealJdbcTemplate);
		List<Customer> customers = namedParameterJdbcTemplate.query(sql, params, new CustomerRowMapper());
		logger.info("findCustomerByPropNameAndPage result: {}", customers);
		
		return customers;
	}

    private String selectName(String propName) {
	    switch (propName) {
            case "kuickUserId" : return "sc";
            default: return  "c";
        }
    }

    @Override
	public void updateExtensions(String id, Map<String, String> extensions) {
		String SQL = "update customer set extensions = ? where id = ?";
		
		String extsStr = null;
		if (extensions!=null) {
			extsStr = JsonUtil.toJson(extensions);
		}
		
		dealJdbcTemplate.update(SQL, extsStr, id);
	}

    @SuppressWarnings("unchecked")
    @Override
    public List<Customer> findCustomersByMergeCustomserIds(String appId, String[] customerIds, int startIndex,
                                                           int count) {
        Map<String, Object> params = new HashMap<>();
        String sql = "select c.*, sc.whetherMerge, sc.isNew,  sc.kuickUserId, cg.name as groupName, sc.newCount, sc.updatedAt assignMemberTime  " +
                "from customer c " +
                "left join sales_customer sc on c.id = sc.customerId " +
                "left join customer_group cg on c.groupId = cg.id " +
                "where c.status = 1 and sc.kuickUserId is not null ";

        // 项目ID
        sql += "and c.appId = :appId ";
        params.put("appId", appId);

        // 根据销售过滤
        if (customerIds == null || customerIds.length == 0) {
            return ListUtils.EMPTY_LIST;
        } else {
            String customerIdsStr = "'" + StringUtils.join(customerIds, "','") + "'";
            sql += "and c.id in (" + customerIdsStr + ") ";
        }

        // 分页
        sql += "limit :startIndex, :count ";
        params.put("startIndex", startIndex);
        params.put("count", count);

        logger.info("findCustomersBySales sql:{}", sql);
        logger.info("findCustomersBySales params:{}", params);

        // 查询并返回结果
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dealJdbcTemplate);
        List<Customer> result = namedParameterJdbcTemplate.query(sql, params, new CustomerRowMapper());
        logger.info("findCustomersBySales result:{}", result);

        return result;
    }


    @Override
    public List<Customer> findByAppIdAndKuickUserIds(String appId, List<String> kuickUserIds, int hasPhone, String condition, String customerGroupId) {
        StringBuilder sb = new StringBuilder("select c.*, '' as groupName, sc.whetherMerge, sc.isNew, sc.kuickUserId, sc.newCount, sc.updatedAt assignMemberTime  " +
                "from customer c, sales_customer sc" +
                " where sc.customerId = c.id  and sc.kuickUserId in(:kuickUserIds) " +
                "and c.appId = :appId ");
        Map<String, Object> args = new HashMap<>();
        args.put("appId", appId);
        args.put("kuickUserIds", kuickUserIds);
        if (StringUtils.isNotBlank(condition)) {
            sb.append(" and ");
            if (phoneValidator.isValid(condition)) {
                sb.append(" c.phone like ");
            } else if (ValidatorUtil.isValidEmail(condition)){
                sb.append(" c.email like ");
            } else {
                sb.append(" c.name like ");
            }
            sb.append(" concat(:condition,'%') ");
            args.put("condition", condition);
        }

        if("null".equals(customerGroupId)) {
            sb.append(" and c.groupId is null ");
        } else if(!"all".equals(customerGroupId)) {
            sb.append(" and c.groupId = :customerGroupId ");
            args.put("customerGroupId", customerGroupId);
        }

        if(hasPhone == 1){
            sb.append(" and c.phone is not null and c.phone != '' ");
        }
        sb.append(" limit 0,50 ");
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dealJdbcTemplate);
        return namedParameterJdbcTemplate.query(sb.toString(), args, new CustomerRowMapper());
    }

    @Override
    public List<CustomerId> findByTag(String appId, String tag, long startIndex, int count) {
        String sql = "select DISTINCT cldu.customerId as id from customer_link_deal_user cldu "
                + " join (select dealUserId from deal_user_tag where appId = :appId and tag = :tag order by id asc limit :startIndex, :count) t" +
                " on t.dealUserId = cldu.dealUserId ";
        Map<String, Object> params = new HashMap<>(4);
        params.put("appId", appId);
        params.put("tag", tag);
        params.put("startIndex", startIndex);
        params.put("count", count);
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dealJdbcTemplate);
        List<CustomerId> customers = namedParameterJdbcTemplate.query(sql, params, new CustomerIdRowMapper());
        logger.info("findByTag. sql: {}, param: {}", sql, params);
        return customers;
    }

    @Override
    public long countByCondition(String appId, List<Condition> conditions) {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("select count(distinct c.id) as id from customer c ");
        sqlBuilder.append(" left join customer_group cg on cg.id = c.groupId ");
        sqlBuilder.append(" left join sales_customer scr on scr.customerId = c.id ");
        sqlBuilder.append(" where c.`status` = 1 and scr.kuickUserId IS NOT NULL and c.appId = ? ");

        List<Object> args = new ArrayList<>();
        args.add(appId);

        if (conditions != null && !conditions.isEmpty()) {
            for (int i = 0; i < conditions.size(); i++) {
                Condition condition = conditions.get(i);
                String name = condition.getName();
                String range = condition.getRange();
                int type = condition.getType();
                if (StringUtils.isNotBlank(name) && StringUtils.isNotBlank(range)) {
                    String andCondition = null;
                    if (StringUtils.equals("kuickUserId", name)) {
                        if (type == 0) {
                            andCondition = " and scr." + name + " = ? ";
                            args.add(range);
                        } else if (type == 1) {
                            andCondition = " and scr." + name + " in (" + range + ") ";
                        }
                        sqlBuilder.append(andCondition);
                    } else if (StringUtils.equals("createdAt", name)) {
                        List<String> timeRange = FilterUtils.getTimeRange(type, range);
                        if (timeRange != null && timeRange.size() == 2) {
                            andCondition = " and c." + name + " >= ? and c." + name + " <= ? ";
                            sqlBuilder.append(andCondition);
                            args.add(timeRange.get(0));
                            args.add(timeRange.get(1));
                        }
                    } else if (StringUtils.equals("groupId", name)) {
                        if (StringUtils.equals("unknown", range)) {
                            andCondition = " and c." + name + " is null ";
                        } else {
                            andCondition = " and c." + name + " = ? ";
                            args.add(range);
                        }
                        sqlBuilder.append(andCondition);
                    } else {
                        andCondition = " and c." + name + " = ? ";
                        sqlBuilder.append(andCondition);
                        args.add(range);
                    }
                }
            }
        }
        String sql = sqlBuilder.toString();
        long l = dealJdbcTemplate.queryForObject(sql, Long.class, args.toArray(new Object[args.size()]));
        logger.info("countByCondition. sql: {}, param: {}, result: {}", sql, args, l);
        return l;
    }

}
