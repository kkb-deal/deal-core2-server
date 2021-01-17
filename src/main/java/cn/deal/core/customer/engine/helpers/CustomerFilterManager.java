package cn.deal.core.customer.engine.helpers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.deal.component.utils.ParalleUtils;
import cn.deal.core.customer.domain.Customer;
import cn.deal.core.customer.engine.interfaces.CustomerActionFilter;
import cn.deal.core.customer.engine.interfaces.CustomerContext;

@Component
public class CustomerFilterManager {

	private List<CustomerActionFilter> filters = new ArrayList<CustomerActionFilter>();
	private List<CustomerActionFilter> syncFilters = new ArrayList<CustomerActionFilter>();
	private List<CustomerActionFilter> asyncFilters = new ArrayList<CustomerActionFilter>();
 
	public static class OrderComparator implements Comparator<CustomerActionFilter> {

		@Override
		public int compare(CustomerActionFilter o1, CustomerActionFilter o2) {
			return o2.getOrder() - o1.getOrder();
		}
		
	}
	
	@Autowired
	private CustomerEventManager customerEventManager;
	
	public void register(CustomerActionFilter filter) {
		this.filters.add(filter);
		this.filters.sort(new OrderComparator());
		
		this.syncFilters = this.getSyncFilters();
		this.asyncFilters = this.getAsyncFilters();
	}
	
	
	protected List<CustomerActionFilter> getSyncFilters() {
		List<CustomerActionFilter> syncFilters = this.filters.stream()
				.filter(it -> !it.isAsync())
				.collect(Collectors.toList());
		return syncFilters;
	}
	
	protected List<CustomerActionFilter> getAsyncFilters() {
		List<CustomerActionFilter> asyncFilters = this.filters.stream()
			.filter(it -> it.isAsync())
			.collect(Collectors.toList());
		return asyncFilters;
	}
	
	public void doBefore(Customer customer, CustomerContext ctx) {
		// 同步执行
		ParalleUtils.forEach(this.syncFilters, filter->{
			filter.doBefore(customer, ctx);
		});
		
		// 异步执行
		ParalleUtils.asyncForEach(this.asyncFilters, filter->{
			filter.doBefore(customer, ctx);
		});
	}

	public void doAfter(Customer customer, CustomerContext ctx) {
		// 同步执行
		ParalleUtils.forEach(this.syncFilters, filter->{
			filter.doAfter(customer, ctx);
		});
		
		// 异步执行
		ParalleUtils.asyncForEach(this.asyncFilters, filter->{
			filter.doAfter(customer, ctx);
		});
		
		customerEventManager.fireEvent(customer, ctx);
	}

	public void doFinally(Customer customer, CustomerContext ctx) {
		// 同步执行
		ParalleUtils.forEach(this.syncFilters, filter->{
			filter.doFinally(customer, ctx);
		});
		
		// 异步执行
		ParalleUtils.asyncForEach(this.asyncFilters, filter->{
			filter.doFinally(customer, ctx);
		});
	}

	public List<CustomerActionFilter> getFilters() {
		return filters;
	}
}
