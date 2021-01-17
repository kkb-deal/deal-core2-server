package cn.deal.core.customer.engine.validators;

import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class PhoneValidator extends AbstractValidator {

	private static String MOBILE_PATTERN = "^(\\+86)?[1][0-9][0-9]{9}$";

	@Override
	public boolean isValid(String value) {
		if (StringUtils.isBlank(value)) {
			return false;
		}
		
		return Pattern.matches(MOBILE_PATTERN, value);
	}

}
