package cn.deal.core.dealuser.service.listeners;

import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.deal.component.messaging.producer.CustomerDomainEventProducer;
import cn.deal.core.customer.domain.Customer;
import cn.deal.core.customer.engine.helpers.CustomerEventManager;
import cn.deal.core.customer.engine.interfaces.CustomerContext;
import cn.deal.core.customer.engine.interfaces.CustomerEventListener;
import cn.deal.core.dealuser.domain.DealUser;
import cn.deal.core.dealuser.service.DealUserService;

/**
 * 客户更新和客户合并之后，
 * 将客户的最新信息同步给所有关联的DealUser
 */
@Component
public class SyncCustomerInfoToDealUsersListener implements CustomerEventListener {

	private Logger logger = LoggerFactory.getLogger(SyncCustomerInfoToDealUsersListener.class);
	
	protected static final int MAX_PAGE_SIZE = 100;
	
	@Autowired
	private DealUserService dealUserService;
	
	@Autowired
	private CustomerEventManager customerEventManager;
	
	@PostConstruct
	public void init() {
		customerEventManager.register(this);
	}
	
	@Override
	public void handle(Customer customer, CustomerContext ctx) {
		logger.info("start sync info from customer, customer:{}, ctx:{}", customer, ctx);
		
		int startIndex = 0;
		List<DealUser> users = dealUserService.findDealUserByCustomerIdAndPage(customer.getAppId(), customer.getId(), startIndex, MAX_PAGE_SIZE);
		
		while(users!=null && users.size()>0) {
			logger.info("sync dealUsers:{}", users);
			
			for(DealUser du: users) {
				try {
					syncInfoFromCustomer(du, customer, ctx);
				} catch(Exception e) {
					logger.error("error in sync dealUser:{}, from customer:{}, ctx:{}", du, customer, ctx);
				}
			}
			
			if (users.size()<MAX_PAGE_SIZE) {
				break;
			}
			
			startIndex = startIndex + users.size();
			users = dealUserService.findDealUserByCustomerIdAndPage(customer.getAppId(), customer.getId(), startIndex, MAX_PAGE_SIZE);
		}
		
		logger.info("end sync info from customer");
	}

	/**
	 * 同步客户信息
	 * 
	 * @param du
	 * @param customer
	 * @param ctx
	 */
	protected void syncInfoFromCustomer(DealUser du, Customer customer, CustomerContext ctx) {
		if (du!=null) {
			String oldState = du.toString();
			
			du.setName(customer.getName());
			du.setPhotoURL(customer.getHeadportraitUrl());
			du.setPhone(customer.getPhone());
			du.setPhoneNum(customer.getPhone());
			du.setEmail(customer.getEmail());
			du.setCompany(customer.getCompany());
			String newState = du.toString();
			
			if (!StringUtils.equals(oldState, newState)) {
				dealUserService.updateDealUser(du);
			}
		}
	}

}
