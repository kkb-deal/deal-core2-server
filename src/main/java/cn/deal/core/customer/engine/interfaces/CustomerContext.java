package cn.deal.core.customer.engine.interfaces;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.deal.component.utils.AssertUtils;
import cn.deal.core.customer.domain.Customer;

/**
 * 客户上下文
 */
public class CustomerContext extends HashMap<String, Object> {
	private static final long serialVersionUID = -5947634273021827928L;
	
	public static final String APPID = "appId";
	public static final String KUICKUSERID = "kuick_user_id";
	public static final String OPE = "operation";
	public static final String ORIGIN_CUSTOMER = "originCustomer";
	public static final String TO_MERGE_CUSTOMERS = "toMergeCustomers";
	public static final String MAIN_KUICK_USER_ID = "mainKuickUserId";

	public CustomerContext() {
		super();
	}
	
	public CustomerContext(Map<String, Object> opts) {
		super(opts);
	}
	
	public String getAppId() {
		return (String)this.get(APPID);
	}
	
	public String getKuickUserId() {
		return (String)this.get(KUICKUSERID);
	}
	
	public String getOpe() {
		return (String)this.get(OPE);
	}
	
	public Customer getOriginCustomer() {
		return (Customer)this.get(ORIGIN_CUSTOMER);
	}
	
	@SuppressWarnings("unchecked")
	public List<Customer> getToMergeCustomers() {
		return (List<Customer>)this.get(TO_MERGE_CUSTOMERS);
	}

	public String getMainKuickUserId() {
		return (String)this.get(MAIN_KUICK_USER_ID);
	}

	/**
	 * 从列表构造客户上下文
	 * 
	 * @param list
	 * @return
	 */
	public static CustomerContext fromList(Object[] list) {
		CustomerContext ctx = new CustomerContext();
		AssertUtils.assertTrue(list.length%2==0, "list的元素必须为偶数个");
		
		for(int i=0; i<list.length; i+=2) {
			ctx.put(String.valueOf(list[i]), list[i+1]);
		}
		
		return ctx;
	}

	
	
}
