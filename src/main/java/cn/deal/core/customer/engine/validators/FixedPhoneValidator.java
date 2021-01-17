package cn.deal.core.customer.engine.validators;

import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class FixedPhoneValidator extends AbstractValidator {

	//区号+座机号码+分机号码：regexp="^(0[0-9]{2,3}/-)?([2-9][0-9]{6,7})+(/-[0-9]{1,4})?$"
	private static String PHONE_PATTERN = "^(0[0-9]{2,3}\\-)?([2-9][0-9]{6,8})+(\\-[0-9]{1,4})?$";

	@Override
	public boolean isValid(String value) {
		if (StringUtils.isBlank(value)) {
			return false;
		}
		
		return Pattern.matches(PHONE_PATTERN, value);
	}
}
