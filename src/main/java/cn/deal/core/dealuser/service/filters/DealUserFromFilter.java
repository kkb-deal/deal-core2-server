package cn.deal.core.dealuser.service.filters;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import com.fasterxml.jackson.databind.JsonMappingException;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.deal.component.domain.CustomerDomainEvent;
import cn.deal.component.utils.JsonUtil;
import cn.deal.core.customer.domain.Customer;
import cn.deal.core.customer.engine.filters.ActionFilterAdapter;
import cn.deal.core.customer.engine.helpers.CustomerFilterManager;
import cn.deal.core.customer.engine.interfaces.CustomerContext;
import cn.deal.core.dealuser.domain.DealUser;
import cn.deal.core.dealuser.service.DealUserService;
import cn.deal.core.meta.domain.PromotionChannel;
import cn.deal.core.meta.service.PromotionChannelService;

@Component
public class DealUserFromFilter extends ActionFilterAdapter {
	public static final String DEAL_USER_KEY = "__dealUser__";

	private Logger logger = LoggerFactory.getLogger(DealUserFromFilter.class);
	
	protected static final HashMap<String, String> EMTPY_HASH_MAP = new HashMap<String, String>();

	public static final String DEAL_USER_ID_KEY = "deal_user_id";
	
	@Autowired
	private DealUserService dealUserService;
	
	@Autowired
	private PromotionChannelService promotionChannelService;
	
	@Autowired
	private CustomerFilterManager customerFilterManger;
	
	@PostConstruct
	public void init() {
		customerFilterManger.register(this);
	}
		
	@Override
	public int getOrder() {
		return 10;
	}
	
	@Override
	public boolean isAsync() {
		return false;
	}

	@Override
	public void doBefore(Customer customer, CustomerContext ctx) {
		String ope = ctx.getOpe();
		if (!CustomerDomainEvent.CREATE.equals(ope)) {
			return;
		}
		
		String dealUserId = (String)ctx.get(DEAL_USER_ID_KEY);
		if (StringUtils.isNotBlank(dealUserId)) {
			DealUser dealUser = dealUserService.getDealUserById(dealUserId);
			ctx.put(DEAL_USER_KEY, dealUser);
			
			if (dealUser!=null && !dealUser.isNamed()) {
				setFromInfo(customer, dealUser, ctx);
			}
		}
	}

	@Override
	public void doAfter(Customer customer, CustomerContext ctx) {
		String ope = ctx.getOpe();
		if (!CustomerDomainEvent.CREATE.equals(ope)) {
			return;
		}
		
		String dealUserId = (String)ctx.get(DEAL_USER_ID_KEY);
		if (StringUtils.isNotBlank(dealUserId)) {
			DealUser dealUser = (DealUser)ctx.get(DEAL_USER_KEY);

			if (dealUser!=null && !dealUser.isNamed()) {
				dealUserService.bindCustomer(dealUser.getId(), customer.getId());
				logger.info("bindCustomer: {}, {} ok", dealUser.getId(), customer.getId());
			}
		}
	}
	
	private void setFromInfo(Customer customer, DealUser dealUser, CustomerContext ctx) {
		String fromType = getFromType(dealUser, ctx);
		Map<String, String> fromInfo = getFromInfo(dealUser, ctx);
		
		// 来源
		setFrom(customer, dealUser, fromType, fromInfo);
		
		// 创建方式
		setCreateWay(customer, fromType, ctx);
		
		if (fromInfo!=null && fromInfo.size()>0) {
			// 推广人
			if (fromInfo.containsKey("promoterId")) {
				customer.setPromoterId(fromInfo.get("promoterId"));
			}
			
			// 平台
			if (fromInfo.containsKey("platform")) {
				customer.setPlatform(fromInfo.get("platform"));
			}
			
			// 来源省份
			if (fromInfo.containsKey("fromProvince")) {
				customer.setFromProvince(fromInfo.get("fromProvince"));
			}
			
			// 来源城市
			if (fromInfo.containsKey("fromCity")) {
				customer.setFromCity(fromInfo.get("fromCity"));
			}
			
			// 搜索关键词
			if (fromInfo.containsKey("keyword")) {
				customer.setSearchKeyword(fromInfo.get("keyword"));
			}
			
			// 来源内容标题
			if (fromInfo.containsKey("fromContentTitle")) {
				customer.setFromContentTitle(fromInfo.get("fromContentTitle"));
			}
			
			// 来源内容链接
			if (fromInfo.containsKey("fromContentLink")) {
				customer.setFromContentLink(fromInfo.get("fromContentLink"));
			}
			
			// 购买的关键词
			if (fromInfo.containsKey("utm_term")) {
				customer.setBuyedKeyword(fromInfo.get("utm_term"));
			}
						
			// UTM 媒介
			if (fromInfo.containsKey("utm_medium")) {
				customer.setUtmMedium(fromInfo.get("utm_medium"));
			}
			
			// UTM Campaign
			if (fromInfo.containsKey("utm_campaign")) {
				customer.setUtmCampaign(fromInfo.get("utm_campaign"));
			}
			
			// UTM Campaign
			if (fromInfo.containsKey("utm_content")) {
				customer.setUtmContent(fromInfo.get("utm_content"));
			}
		}
		
		logger.info("setFromInfo result: %j", customer);
	}

	protected String getFromType(DealUser dealUser, CustomerContext ctx) {
		if (StringUtils.isNotBlank((String)ctx.get("from_type"))) {
			return (String)ctx.get("from_type");
		}
		
		return dealUser.getFromType();
	}

	public void setFrom(Customer customer, DealUser dealUser, String fromType, Map<String, String> fromInfo) {
		if ("link".equals(fromType)) {
			customer.setFrom("资料浏览");
		} else if ("rabbitpre_html5".equals(fromType)) {
			customer.setFrom("H5资料浏览");
		}
		
		if (fromInfo!=null && fromInfo.size()>0) {
			if (fromInfo.containsKey("from")) {
				customer.setFrom(fromInfo.get("from"));
			} else if (fromInfo.containsKey("utm_source")) {
				customer.setFrom(fromInfo.get("utm_source"));
			} else if (fromInfo.containsKey("channel_code")) {
				PromotionChannel pc = promotionChannelService.getChannelByCode(dealUser.getAppId(), fromInfo.get("channel_code"));
				if (pc!=null){
					customer.setFrom(fromInfo.get("utm_source"));
					customer.setGetWay(String.valueOf(pc.getPayed()));
				}
			}
		}
	}

	public void setCreateWay(Customer customer, String fromType, CustomerContext ctx) {
		// 应用传入的创建方式
		String createWay = (String)ctx.get("create_way");
		if (StringUtils.isNoneBlank(createWay)) {
			customer.setCreateWay(NumberUtils.createInteger(createWay));
			return;
		}
		
		// 根据fromType自动计算创建方式
		if("client_sdk".equals(fromType) || "deal_js_sdk".equals(fromType)){
			customer.setCreateWay(1);
        } else if("link_form".equals(fromType) || "remote_demo".equals(fromType)){
        	customer.setCreateWay(5);
        } else if("new_fans_subscribe".equals(fromType)){
        	customer.setCreateWay(3);
        } else if("sobot".equals(fromType)){
        	customer.setCreateWay(2);
        } else if("rabbitpre_html5".equals(fromType)){
        	customer.setCreateWay(5);
        } else if("deal_admin_js_sdk".equals(fromType)) {
        	customer.setCreateWay(9);
        } else if("link".equals(fromType)) {
			customer.setCreateWay(8);
		}
	}

	public Map<String, String> getFromInfo(DealUser dealUser, CustomerContext ctx) {
		Map<String, String> fromInfo = new HashMap<String, String>();

		try {
			// dealUser的来源
			String fromInfoStr = dealUser.getFromInfo();
			if (StringUtils.isNotBlank(fromInfoStr)) {
				Map<String, String> info = JsonUtil.jsonToMap(fromInfoStr);

				if (info!=null) {
					fromInfo.putAll(info);
				}
			}

			// 实名化的来源
			fromInfoStr = (String)ctx.get("from_info");
			if (StringUtils.isNotBlank(fromInfoStr)) {
				Map<String, String> info = JsonUtil.jsonToMap(fromInfoStr);

				if (info!=null) {
					fromInfo.putAll(info);
				}
			}
		} catch (Exception e) {
			logger.error("handle in getFromInfo. dealUserId: {}", dealUser.getId());
		}

		return fromInfo;
	}

}
