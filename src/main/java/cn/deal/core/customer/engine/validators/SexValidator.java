package cn.deal.core.customer.engine.validators;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class SexValidator extends AbstractValidator {

	/**
	 *  "女": 0,"男": 1, "未知": 2
	 */
	private List<String> options = Arrays.asList("0", "1", "2");
	
	@Override
	public boolean isValid(String value) {
		return options.contains(value);
	}

}
