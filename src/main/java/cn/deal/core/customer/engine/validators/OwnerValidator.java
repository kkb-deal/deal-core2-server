package cn.deal.core.customer.engine.validators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.deal.component.kuick.KuickuserUserService;
import cn.deal.component.kuick.domain.KuickUser;

@Component
public class OwnerValidator extends AbstractValidator {

	@Autowired
	private KuickuserUserService kuickuserUserService;

	@Override
	public boolean isValid(String value) {
		KuickUser user = kuickuserUserService.getUserById(value);
		if (user!=null) {
			return true;
		}
		
		return false;
	}

}
