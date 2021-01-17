package cn.deal.core.customer.engine.validators;

import cn.deal.core.app.service.DealAppMemberService;
import cn.deal.core.customer.engine.interfaces.CustomerContext;
import cn.deal.core.customer.engine.interfaces.CustomerPropValidator;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class AppMemberValidator implements CustomerPropValidator {

	@Autowired
	private DealAppMemberService dealAppMemberService;

	@Override
	public boolean isValid(String value, CustomerContext ctx) {
		if (NumberUtils.isNumber(value)) {
			return dealAppMemberService.isAppMember(ctx.getAppId(), NumberUtils.toInt(value));
		} else {
			return false;
		}
	}

}
