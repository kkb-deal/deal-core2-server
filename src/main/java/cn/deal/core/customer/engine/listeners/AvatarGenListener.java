package cn.deal.core.customer.engine.listeners;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import cn.deal.component.AvatarGenComponent;
import cn.deal.component.domain.CustomerDomainEvent;
import cn.deal.component.utils.JsonUtil;
import cn.deal.core.customer.domain.Customer;
import cn.deal.core.customer.engine.helpers.CustomerEventManager;
import cn.deal.core.customer.engine.interfaces.CustomerContext;
import cn.deal.core.customer.engine.interfaces.CustomerEventListener;
import cn.deal.core.customer.service.CustomerService;

/**
 * 如果客户没有头像，基于客户名称生成一个默认头像
 */
@Component
public class AvatarGenListener implements CustomerEventListener {

	protected static final String APP_ID_KEY = "appId";

	protected static final String CUSTOMER_ID_KEY = "customerId";

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private AvatarGenComponent avatarGenComponent;
	
	@Autowired
	private CustomerEventManager customerEventManager;
	
	@Autowired
	private CustomerService customerService;
	
	@Value("${spring.cloud.stream.bindings.avatar_gen_success_input.destination}")
	private String avatarGenSource;
	
	@PostConstruct
	public void init() {
		customerEventManager.register(this);
	}
	
	@Override
	public void handle(Customer customer, CustomerContext ctx) {
		String ope = ctx.getOpe();
		if (!CustomerDomainEvent.CREATE.equals(ope)) {
			return;
		}
		
		// 请求异步生成头像，如果没有头像或者头像为默认头像
		if (StringUtils.isNotBlank(customer.getName()) 
				&& (StringUtils.isBlank(customer.getHeadportraitUrl()) 
						|| StringUtils.equals(customer.getHeadportraitUrl(), Customer.DEFAULT_HEAD_URL))) {
			Map<String, String> state = new LinkedHashMap<String, String>();
			state.put(APP_ID_KEY, ctx.getAppId());
			state.put(CUSTOMER_ID_KEY, customer.getId());
			
			String taskId = avatarGenComponent.asyncGenerater(customer.getName(), JsonUtil.toJson(state), avatarGenSource);
			logger.info("reqeust gen avatar, source:{}, taskId:{}", avatarGenSource, taskId);
		}
	}

	/**
	 * 头像生成成功
	 * 
	 * @param avatarUrl
	 * @param state
	 */
	public void avatarGenSuccess(String taskId, String avatarUrl, String jsonState) {
		logger.info("begin update customer avatar, for taskId: {}, avatarUrl:{}, state:{}", taskId, avatarUrl, jsonState);
		
		Map<String, String> state = JsonUtil.jsonToMap(jsonState);
		String customerId = state.get(CUSTOMER_ID_KEY);
		Customer customer = customerService.getCustomerById(customerId);
		
		if (customer!=null) {
			if (StringUtils.isNotBlank(avatarUrl)) {
				customer.setHeadportraitUrl(avatarUrl);
			}
			
			customerService.update(customer);
			
			logger.info("update customer avatar ok, for taskId: {}", taskId);
		} else {
			logger.warn("not found customer width state: {}, for taskId: {}", jsonState, taskId);
		}
	}

}
