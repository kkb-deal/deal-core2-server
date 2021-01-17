package cn.deal.core.customer.engine.interfaces;

public interface CustomerPropValidator {
	boolean isValid(String value, CustomerContext ctx);
}
