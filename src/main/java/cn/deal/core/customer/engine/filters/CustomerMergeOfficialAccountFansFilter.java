package cn.deal.core.customer.engine.filters;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.deal.component.domain.CustomerDomainEvent;
import cn.deal.component.utils.AssertUtils;
import cn.deal.core.customer.domain.Customer;
import cn.deal.core.customer.engine.helpers.CustomerFilterManager;
import cn.deal.core.customer.engine.interfaces.CustomerContext;

/**
 * 合并后，是否为公众号粉丝标志运算
 */
@Component
public class CustomerMergeOfficialAccountFansFilter extends ActionFilterAdapter {
	
	@Autowired
	private CustomerFilterManager customerFilterManger;
	
	@PostConstruct
	public void init() {
		customerFilterManger.register(this);
	}

	@Override
	public void doBefore(Customer customer, CustomerContext ctx) {
		AssertUtils.notNull(customer, "customer can not be null");
		AssertUtils.notNull(ctx, "customer context can not be null");
		
		String ope = ctx.getOpe();
		Customer originCustomer = ctx.getOriginCustomer();
		List<Customer> toMergeCustomers = ctx.getToMergeCustomers();
		
		if (!CustomerDomainEvent.MERGE.equals(ope)) {
			return;
		}
		
		if (toMergeCustomers==null || toMergeCustomers.size()==0) {
			return;
		}
		
		Integer isFans = originCustomer.getIsOfficialAccountFans();
		
		for(int i=0; i<toMergeCustomers.size(); i++) {
			Customer cus = toMergeCustomers.get(i);
			if (cus.getIsOfficialAccountFans()!=null && cus.getIsOfficialAccountFans().intValue()==1) {
				isFans = 1;
			}
		}
		
		customer.setIsOfficialAccountFans(isFans);
	}
}
