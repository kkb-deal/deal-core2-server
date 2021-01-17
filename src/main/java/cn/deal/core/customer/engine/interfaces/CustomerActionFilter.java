package cn.deal.core.customer.engine.interfaces;

import cn.deal.core.customer.domain.Customer;

public interface CustomerActionFilter {

	String getName();
	
	int getOrder();
	
	boolean isAsync();
	
	void doBefore(Customer customer, CustomerContext ctx);

	void doAfter(Customer customer, CustomerContext ctx);

	void doFinally(Customer customer, CustomerContext ctx);

}
