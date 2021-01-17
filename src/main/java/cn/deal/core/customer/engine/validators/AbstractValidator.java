package cn.deal.core.customer.engine.validators;

import cn.deal.core.customer.engine.interfaces.CustomerContext;
import cn.deal.core.customer.engine.interfaces.CustomerPropValidator;

/**
 * 抽象验证器
 */
public abstract class AbstractValidator implements CustomerPropValidator {

	abstract boolean isValid(String value);

	@Override
	public boolean isValid(String value, CustomerContext ctx) {
		return this.isValid(value);
	}

}
