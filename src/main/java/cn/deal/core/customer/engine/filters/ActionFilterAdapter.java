package cn.deal.core.customer.engine.filters;

import cn.deal.core.customer.domain.Customer;
import cn.deal.core.customer.engine.interfaces.CustomerActionFilter;
import cn.deal.core.customer.engine.interfaces.CustomerContext;

/**
 * 适配器
 */
public class ActionFilterAdapter implements CustomerActionFilter {

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}
	
	/**
	 * 异步
	 */
	@Override
	public boolean isAsync() {
		return true;
	}
	
	@Override
	public int getOrder() {
		return 0;
	}
	
	@Override
	public void doBefore(Customer customer, CustomerContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doAfter(Customer customer, CustomerContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doFinally(Customer customer, CustomerContext ctx) {
		// TODO Auto-generated method stub
		
	}

	

}
