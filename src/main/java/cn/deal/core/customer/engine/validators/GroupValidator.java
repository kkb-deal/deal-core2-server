package cn.deal.core.customer.engine.validators;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.deal.core.customer.domain.CustomerGroups;
import cn.deal.core.customer.engine.interfaces.CustomerContext;
import cn.deal.core.customer.engine.interfaces.CustomerPropValidator;
import cn.deal.core.customer.service.CustomerGroupService;

@Component
public class GroupValidator implements CustomerPropValidator {

	@Autowired
	private CustomerGroupService customerGroupService;
	
	@Override
	public boolean isValid(String value, CustomerContext ctx) {
		String appId = ctx.getAppId();
		
		List<CustomerGroups> cgs = customerGroupService.getCustomerGroups(appId);
		
		if (cgs!=null && cgs.size()>0) {
			for(CustomerGroups cg: cgs) {
				if (cg.getId().equals(value)) {
					return true;
				}
			}
		}
		
		return false;
	}

}
