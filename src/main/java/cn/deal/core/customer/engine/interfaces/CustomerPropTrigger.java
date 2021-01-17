package cn.deal.core.customer.engine.interfaces;

import cn.deal.core.meta.domain.CustomerMetaData;
import cn.deal.core.customer.domain.Customer;

/**
 * 客户属性触发器
 */
public interface CustomerPropTrigger {

	void propChanged(Customer cus, CustomerMetaData meta, String newValue, String oldValue, CustomerContext ctx);
}
