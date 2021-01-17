package cn.deal.core.customer.engine.validators;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component("ageState")
public class AgeStateValidator extends AbstractValidator {

	/**
	 *      "未知": -1,
            "25~35": 0,
            "35~45": 1,
            "45~55": 2,
            "55~65": 3,
            "60以上": 4
	 */
	private static final List<String> AGE_STATE = Arrays.asList("-1", "0", "1", "2", "3", "4", "5", "6", "7");

	@Override
	public boolean isValid(String value) {
		if (StringUtils.isBlank(value)) {
			return false;
		}
		
		return AGE_STATE.contains(value);
	}

}
