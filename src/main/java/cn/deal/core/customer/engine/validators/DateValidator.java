package cn.deal.core.customer.engine.validators;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Component;

import java.text.ParseException;


@Component
public class DateValidator extends AbstractValidator {

	private static final String[] PATTERN = { "yyyy-MM-dd HH:mm:ss" };

	@Override
	public boolean isValid(String value) {
		return isDate(value);
	}

	private boolean isDate(String value) {
		try {
			DateUtils.parseDate(value, PATTERN);
			return true;
		} catch (ParseException e) {
			return false;
		}
	}

}
