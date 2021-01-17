package cn.deal.core.customer.service;

import cn.deal.component.kuick.domain.ResponseVO;
import cn.deal.core.customer.domain.Customer;
import cn.deal.core.customer.domain.SalesCustomer;

import java.util.List;

public interface SalesCustomerService {

	/**
	 * 获取客户所属销售
	 * 
	 * @param customerId
	 * @return
	 */
    SalesCustomer getSalesCustomerByCustomerId(String customerId);

    /**
     * 获取销售的客户列表
     * 
     * @param appId
     * @param kuickUserId
     * @param customerGroupId
     * @param phone
     * @param startIndex
     * @param count
     * @return
     */
	List<Customer> getSalesCustomers(String appId, String kuickUserId, String targetKuickUserIds, String customerGroupId, String phone, final String email,
			int startIndex, int count);

 
	/**
	 * 分配客户给销售
	 * 
	 * @param appId
	 * @param customer
	 * @param targetUserId
	 */
	SalesCustomer createAppSalesCustomer(String appId, Customer customer, String targetUserId);

	/**
	 * 删除客户关联的销售
	 * 
	 * @param customerId
	 */
	void deleteByCustomer(String customerId);

	SalesCustomer findSalesCustomerByAppIdAndCustomerId(String appId, String customerId);

	List<Customer> searchSalesCustomers(String appId, String kuickUserId, int hasPhone, String condition, String customerGroupId, String currentUserId);

	/**
	 * 客户转让
	 * @param appId  项目id
	 * @param kdKuickUserId  当前登录人的用户id
	 * @param sourceKuickUserId  原销售id
	 * @param targetKuickUserId  目标销售id
	 * @param customerIds  客户id
	 * @param iaAutomatic 是否自动转让
	 * @param userAgentStr 浏览器userAgent
	 * @param isJudgeOwner 是否判断转让客户所属人，0：否，1：是
	 */
    ResponseVO customerTransfer(String appId, String kdKuickUserId, String sourceKuickUserId, String targetKuickUserId,
								String customerIds, boolean iaAutomatic, String userAgentStr, Integer isJudgeOwner);

	/**
	 * 批量客户转让
	 * @param appId  项目id
	 * @param kdKuickUserId 当前登录人的用户id
	 * @param datas  {customerId:xxx,srouceKuickUserId:xxx}
	 * @param targetKuickUserId  目标销售id
	 * @param userAgentStr 浏览器userAgent
	 */
	ResponseVO batchCustomerTransfer(String appId, String kdKuickUserId,  String datas, String targetKuickUserId, String userAgentStr);
}
