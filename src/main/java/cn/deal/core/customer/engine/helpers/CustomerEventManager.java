package cn.deal.core.customer.engine.helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import cn.deal.core.customer.domain.Customer;
import cn.deal.core.customer.engine.interfaces.CustomerContext;
import cn.deal.core.customer.engine.interfaces.CustomerEventListener;

@Component
public class CustomerEventManager {
	private Logger logger = LoggerFactory.getLogger(CustomerEventManager.class);
	
	private List<CustomerEventListener> listeners = new ArrayList<CustomerEventListener>();
	
	@Autowired
	@Qualifier("asyncExecutor")
	private Executor asyncExecutor;
	
	/**
	 * 注册监听器
	 * 
	 * @param listener
	 */
	public void register(CustomerEventListener listener) {
		this.listeners.add(listener);
	}
	
	/**
	 * 发出事件
	 * 
	 * @param event
	 * @param params
	 */
	public void fireEvent(Customer customer, CustomerContext ctx) {
		String event = ctx.getOpe();
		
		for(CustomerEventListener listener: this.listeners) {
			// 异步执行监听
			asyncExecutor.execute(new Runnable() {
				@Override
				public void run() {
					try {
						listener.handle(customer, ctx);
					} catch(Throwable e) {
						logger.error("error in handle customer event:" + event, e);
					}
				}
			});
		}
	}

}
