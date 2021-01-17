package cn.deal.core.customer.engine.filters;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.deal.component.Phone2LocationComponent;
import cn.deal.component.domain.PhoneLocation;
import cn.deal.core.customer.domain.Customer;
import cn.deal.core.customer.engine.handles.CustomerBuildHandler;
import cn.deal.core.customer.engine.helpers.CustomerFilterManager;
import cn.deal.core.customer.engine.interfaces.CustomerContext;
import cn.deal.core.customer.engine.interfaces.CustomerPropTrigger;
import cn.deal.core.meta.domain.CustomerMetaData;

/**
 * 手机位置过滤器
 */
@Component
public class PhoneLocationFilter extends ActionFilterAdapter implements CustomerPropTrigger {

	@Autowired
	private Phone2LocationComponent phoneLocationComponent;
	
	@Autowired
	private CustomerFilterManager customerFilterManger;
	
	@Autowired
	private CustomerBuildHandler customerBuildHandler;
	
	@PostConstruct
	public void init() {
		customerFilterManger.register(this);
		customerBuildHandler.registerTrigger(this);
	}
	
	/**
	 * 客户手机号发生变更，重置手机号相关位置信息
	 */
	@Override
	public void propChanged(Customer cus, CustomerMetaData meta, String newValue, String oldValue, CustomerContext ctx) {
		String propName = meta.getName();
		
		if ("phone".equals(propName)) {
			cus.setPhoneISP(null);
			cus.setPhoneProvince(null);
			cus.setPhoneCity(null);
		}
	}
	
	/**
	 * 当客户有手机号并且没有手机位置信息时，重新查询位置信息
	 */
	@Override
	public void doBefore(Customer customer, CustomerContext ctx) {
		if (StringUtils.isNotBlank(customer.getPhone()) && StringUtils.isBlank(customer.getPhoneProvince())) {
			PhoneLocation pl = phoneLocationComponent.getPhoneLocationAndISP(customer.getPhone());
			
			if (pl!=null) {
				customer.setPhoneISP(pl.getIsp());
				customer.setPhoneProvince(pl.getProvince());
				customer.setPhoneCity(pl.getCity());
			}
		}
	}

}
