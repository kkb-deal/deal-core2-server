package cn.deal.core.customer.engine.interfaces;

import cn.deal.core.customer.domain.Customer;

public interface CustomerEventListener {

	void handle(Customer customer, CustomerContext ctx);

}
