package cn.deal.core.customer.service.impl;

import cn.deal.component.AdminSalesComponent;
import cn.deal.component.CustomerFilterComponent;
import cn.deal.component.domain.CustomerDomainEvent;
import cn.deal.component.domain.filter.Condition;
import cn.deal.component.domain.filter.CustomerFilter;
import cn.deal.component.exception.BusinessException;
import cn.deal.component.kuick.KuickuserUserService;
import cn.deal.component.kuick.domain.KuickUser;
import cn.deal.component.kuick.domain.MemberImportVO;
import cn.deal.component.messaging.producer.CustomerDomainEventProducer;
import cn.deal.component.utils.AssertUtils;
import cn.deal.core.app.domain.AppMember;
import cn.deal.core.app.service.DealAppMemberService;
import cn.deal.core.app.service.DealAppService;
import cn.deal.core.customer.dao.CustomerDao;
import cn.deal.core.customer.dao.MergedCustomerDao;
import cn.deal.core.customer.domain.Customer;
import cn.deal.core.customer.domain.CustomerId;
import cn.deal.core.customer.domain.CustomerTransferLog;
import cn.deal.core.customer.domain.SalesCustomer;
import cn.deal.core.customer.domain.vo.CustomerSearchVO;
import cn.deal.core.customer.repository.CustomerRepository;
import cn.deal.core.customer.repository.CustomerTransferLogRepository;
import cn.deal.core.customer.repository.SalesCustomerRepository;
import cn.deal.core.customer.service.CustomerService;
import cn.deal.core.customerswarm.dao.SwarmMemberDao;
import cn.deal.core.dealuser.domain.CustomerLinkDealUser;
import cn.deal.core.dealuser.domain.CustomerWithDealuserId;
import cn.deal.core.dealuser.repository.CustomerLinkDealUserRepository;
import cn.deal.core.meta.domain.AppSetting;
import cn.deal.core.meta.domain.CustomerMetaData;
import cn.deal.core.meta.service.AppSettingService;
import cn.deal.core.meta.service.CustomerMetaDataService;
import cn.deal.core.permission.service.AppMemberPermissionService;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class CustomerServiceImpl implements CustomerService {

    private Logger logger = LoggerFactory.getLogger(CustomerServiceImpl.class);

	private static final String REQUIRED_SUFFIX = "(必填)";

    @Autowired
    private CustomerFilterComponent customerFilterComponent;
    @Autowired
    private AdminSalesComponent adminSalesComponent;
    @Autowired
    private CustomerDao customerDao;
    @Autowired
    private MergedCustomerDao mergedCustomerDao;
	@Autowired
	private SwarmMemberDao swarmMemberDao;
	@Autowired
	private CustomerRepository customerRepository;
	@Autowired
	private AppSettingService appSettingService;
    @Autowired
    private AppMemberPermissionService appMemberPermissionService;
	@Autowired
	private CustomerDomainEventProducer customerDomainEventProducer;
	@Autowired
	private SalesCustomerRepository salesCustomerRepository;
	@Autowired
	private KuickuserUserService kuickuserUserService;
	@Autowired
	private CustomerTransferLogRepository customerTransferLogRepository;
	@Autowired
	private CustomerMetaDataService customerMetaDataService;

	@Autowired
	private DealAppMemberService dealAppMemberService;

	@Autowired
	private DealAppService dealAppService;

	@Override
	public List<String> getCustomerTitlesByAppId(String appId) throws Exception {
		// 加载客户元数据
		List<CustomerMetaData> metaDatas = customerMetaDataService.getCustomerMetas(appId);
		AssertUtils.notNull(metaDatas, "导出excel模版失败，根据appId获取元数据为空");

		//数据过滤
		return metaDatas.stream()
				.filter(meta -> meta.getVisiable() && !StringUtils.equals(meta.getName(), "groupId"))
				.sorted(Comparator.comparing(CustomerMetaData::getIndex))
				.map(meta -> meta.getRequired() ? meta.getTitle() + REQUIRED_SUFFIX : meta.getTitle())
				.collect(Collectors.toList());
	}

	@Override
    public List<Customer> getScreeningCustomer(String appId, String filterId, Integer startIndex, Integer count) {
        List<Customer> customers = new ArrayList<Customer>();
        try {
            CustomerFilter filter = customerFilterComponent.getCustomerFilterByFilterId(appId, filterId);
            String customerSortByUpdatedat = "1";
            AppSetting setting = appSettingService.getSetting(appId, "customer_sort_by_updatedat");
            if(setting != null){
            	customerSortByUpdatedat = setting.getValue();
            }
            logger.info("customerSortByUpdatedat: {}", customerSortByUpdatedat);
            if(filter != null){
                String creatorId = filter.getCreatorId();
                List<Condition> conditions = filter.getConditions();
                if(conditions != null && !conditions.isEmpty()){
                    this.preHandleConditions(creatorId, appId, conditions);
                    customers = customerDao.findCustomersByConditions(appId, conditions, customerSortByUpdatedat, startIndex, count);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return customers;
    }

    private List<Condition> preHandleConditions(String creatorId, String appId, List<Condition> conditions){
        boolean hasKuickUserId = false;
        for (Condition condition : conditions) {
            String name = condition.getName();
            if (StringUtils.isNotBlank(name)) {
                if (StringUtils.equals("kuickUserId", name)) {
                    hasKuickUserId = true;
                    break;
                }
            }
        }
        logger.info("conditions: " + conditions.toString());
        logger.info("hasKuickUserIdCondition: " + hasKuickUserId);
        if(!hasKuickUserId){
            List<KuickUser> adminSales = adminSalesComponent.getAdminSales(appId, creatorId);
            String kuickUserIds = "";
            if(adminSales != null && !adminSales.isEmpty()){
                for(int i = 0; i < adminSales.size(); i++){
                    KuickUser kuickUser = adminSales.get(i);
                    int kuickUserId = kuickUser.getId();
                    if(i == adminSales.size() - 1){
                        kuickUserIds += kuickUserId;
                    } else {
                        kuickUserIds += kuickUserId + ",";
                    }
                }
                logger.info("kuickUserIds: " + kuickUserIds);
                Condition condition = new Condition();
                condition.setName("kuickUserId");
                condition.setType(1);
                condition.setRange(kuickUserIds);
                conditions.add(condition);
            }
        }
        logger.info("handledConditions: " + conditions.toString());
        return conditions;
    }

	@Override
	public List<Customer> exactMatch(String appId, String phone, String email, int startIndex, int count) {
		return customerDao.findCustomersByExactMatch(appId, phone, email, startIndex, count);
	}


	@Override
	public List<Customer> exactMatchV2(CustomerSearchVO params) {
		List<Customer> customers = customerDao.findCustomersByExactMatchV2(params);
		logger.info("exactMatchV2.customer.size: {}, {}", customers.size(), params.getWithKuickUser() == AppMember.WithKuickuser.YES.getVal());
		if (params.getWithKuickUser() == AppMember.WithKuickuser.YES.getVal()) {
			customers.forEach(customer -> customer.setKuickUser(kuickuserUserService.getUserById(customer.getKuickUserId())));
		}
		return customers;
	}

	@Override
	public List<Customer> getSwarmCustomers(String swarmId, String appId, String groupId, List<String> kuickUserIds,
			int startIndex, int count, String keyword) {
		if (kuickUserIds == null || kuickUserIds.size() == 0) {
			logger.error("获取客户分群需要kuickuserid");
			return new ArrayList<>();
		}
		return customerDao.findCustomersBySwarm(appId, swarmId, groupId, kuickUserIds, keyword, startIndex, count);
	}

    @SuppressWarnings("rawtypes")
	@Override
    public List getCustomerBySwarmIdAndAppId(String appId, String swarmId, int startIndex, int count, Map<String, Integer> attributesMap) {
        return customerDao.findCustomersBySwarmAndAttributes(appId, swarmId, attributesMap, startIndex ,count);
    }

    @Override
    public long getTotalByAppId(String appId) {
        return customerDao.getCustomerCountByAppId(appId, true);
    }

	/**
	*2018年2月5日下午2:45:09
	*panpan
	* @see cn.deal.core.customer.service.CustomerService#getTagCustomers(java.lang.String, java.lang.String, int, java.lang.Integer)
	*  标签客户数
	 */
	@Override
	@Deprecated
	public List<Customer> getTagCustomers(String tags, String appId, int kuickUserId, Integer greateTagCount) {
		List<KuickUser> managedSales=appMemberPermissionService.getManagedSales(appId, kuickUserId);
		List<Customer> tagcustomers;
		List<String> kuickUserIds = new ArrayList<>(),
                tagList;
		
		if(managedSales!=null && managedSales.size()>0){//获取权限销售
			for (KuickUser kuickUser : managedSales) {
				kuickUserIds.add(String.valueOf(kuickUser.getId()));
			}
		}
		
		tagList = Arrays.asList(tags.split(","));
		tagcustomers = customerDao.findCustomersByTag(appId, kuickUserIds, tagList, greateTagCount);// 获取标签所有客户，
		
		return tagcustomers;
	}

	/**
	*2018年2月5日下午2:44:36
	*panpan
	* @see cn.deal.core.customer.service.CustomerService
	* #getTagCustomerCount(java.lang.String, java.lang.String)
	*  标签客户数量
	 */
	@Override
	@Deprecated
	public long getTagCustomerCount(String appId, String tag) {
		return customerDao.getCustomerCountByTag(appId, tag);
	}

	@Override
	public long getTotalByAppId(String appId, List<String> swarmIdsList) {
		if (swarmIdsList == null) {
            return customerDao.getCustomerCountByAppId(appId, false);
        } else {
            return customerDao.getSwarmMemberCountBySwarmAndAppId(appId,swarmIdsList);
        }
	}

	@Override
	public List<CustomerId> getCustomerIdByAppId(String appId, int startIndex, int count) {
		 return customerDao.findCustomerIds(appId, startIndex, count);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List getCustomerByAppId(String appId, int startIndex, int count, Map<String, Integer> attributesMap) {
		 return customerDao.findCustomersByAttributes(appId, startIndex, count ,attributesMap);
	}

    @Override
    public List<CustomerId> getCustomerIdBySwarmIdAndAppId(String appId, String swarmId, int startIndex, int count) {
        return customerDao.findCustomerIdsBySwarm(appId, swarmId, startIndex, count);
    }

	 /**
	  * 获取客户的最新信息，如果客户被合并过，需要返回被合并之后的客户信息
	  */
	@Override
	public Customer getLatestCustomer(String appId, String customerId) {
		//customer是否被合并过
		List<String> customerIds = mergedCustomerDao.getCustomerIdByMergedCustomerId(customerId);
		
		if(customerIds != null && customerIds.size()>0){
			return customerDao.getCustomerById(customerIds.get(0));
		}
		
		//查询customer详情
		return customerDao.getCustomerById(customerId);
	}

	@Override
	public Customer getCustomerById(String customerId) {
		if(StringUtils.isNotBlank(customerId)){
			return customerDao.getCustomerById(customerId);
		}
		
		return null;
	}

	@Override
	public Customer getCustomerById(String appId, String customerId, Integer withKuickuser) {
		Customer customer = getCustomerById(customerId);
		logger.info("查询客户信息： {} -> {}", customerId, customer);
		if (customer == null) {
			return null;
		}
		if (StringUtils.isBlank(appId) || !appId.equals(customer.getAppId())) {
			logger.info("项目id不匹配： {} -> {}", appId, customer.getAppId());
			return null;
		}
		if (withKuickuser == 1) {
			KuickUser kuickUser = kuickuserUserService.getUserById(customer.getKuickUserId());
			if (kuickUser != null) {
				kuickUser.setPassword(null);
				kuickUser.setUnionid(null);
				customer.setKuickUser(kuickUser);
			}
		}
		return customer;
	}

	@Override
	public long getTotalByAppIdWithMerged(String appId, List<String> swarmIdsList) {
		long mergedCountBySwarmIds = swarmMemberDao.getMergedCountBySwarmIds(swarmIdsList);
        return mergedCountBySwarmIds;
	}
	
	@Override
	public List<Customer> getCustomerListByDealuserIds(List<String> dealuserIdList) {
	    List<CustomerWithDealuserId> customerList = customerDao.getCustomerListByDealuserIds(dealuserIdList);
	    boolean exist = false;
	    List<Customer> result = new ArrayList<>();
	    for(String dealUserId : dealuserIdList){
	        for(CustomerWithDealuserId cust : customerList){
	            if(dealUserId.equals(cust.getDealUserId())){
	                exist = true;
	                result.add(cust);
	                break;
	            }
	        }
	        if(!exist){
	            result.add(null);
	        }
	        exist = false;
	    }
	    return result;
	}

    @Override
    public long getRawCustomerCount(String appId) {
        return customerDao.getRawCustomerCount(appId);
    }

    @Override
    public List<Customer> getRawCustomer(String appId, Integer startIndex, Integer count) {
        return customerDao.getRawCustomers(appId, startIndex, count);
    }

	/**
	 * 根据客户ID列表分页查询客户
	 */
	@Override
	public List<Customer> findCustomerByIdsAndPage(String appId, String[] customerIds, int startIndex, int count) {
		return customerDao.findCustomerByIdsAndPage(appId, customerIds, startIndex, count);
	}

	@Override
	public List<Customer> getCustomerByIds(String[] customerIds) {
		return this.customerDao.findCustomerIds(customerIds);
	}

	@Override
	public List<Customer> findCustomerByPropNameAndPage(String appId, CustomerMetaData metaData, String value, int startIndex, int count) {
		return this.customerDao.findCustomerByPropNameAndPage(appId, metaData, value, startIndex, count);
	}
	
	//----------------------------------------------------------------------
	/**
	 * 创建客户
	 */
	@Override
	public Customer createCustomer(Customer customer) {
		AssertUtils.notNull(customer, "customer can not be null");
		AssertUtils.assertTrue(customer.getId()==null, "customer's id must be null");

		// 生成唯一ID
		if (StringUtils.isBlank(customer.getId())) {
			customer.setId(UUID.randomUUID().toString());
		}
		
		// 保存客户基本信息
		logger.debug("createCustomer:{}", customer);
		customerRepository.saveAndFlush(customer);


		// 保存客户扩展信息
		if (customer.getExtensions()!=null) {
			logger.info("创建客户:{}", new Gson().toJson(customer));
			customerDao.updateExtensions(customer.getId(), customer.getExtensions());
		}

		// 发送领域事件
		CustomerDomainEvent event = new CustomerDomainEvent(customer.getId(), CustomerDomainEvent.CREATE, customer);
		customerDomainEventProducer.send(event);

		return customer;
	}

	/**
	 * 根据客户
	 */
	@Override
	public Customer update(Customer customer) {
		AssertUtils.notNull(customer, "customer can not be null");
		AssertUtils.notNull(customer.getId(), "customer's id can not be null");
		
		// 查询修改前信息
		Customer oldBody = customerDao.getCustomerById(customer.getId());
		logger.info("Start update customer, old : {},  new : {}", oldBody, customer);
		// 更新客户信息
		customerRepository.saveAndFlush(customer);

		if (customer.getExtensions()!=null) {
			logger.info("修改客户:{}", new Gson().toJson(customer));
			customerDao.updateExtensions(customer.getId(), customer.getExtensions());
		}
		
		// 发送领域事件
		CustomerDomainEvent event = new CustomerDomainEvent(customer.getId(), CustomerDomainEvent.UPDATE, customer, oldBody);
		customerDomainEventProducer.send(event);
				
		return customer;
	}

	/**
	 * 标记客户合并
	 */
	@Override
	public void markMerged(Customer customer, String mergedCustomerId, List<String> mergedCustomerIds, List<String> dealUserIds) {
		AssertUtils.notNull(customer, "customer can not be null");
		AssertUtils.notNull(customer.getId(), "customer's id can not be null");
		
		// 标记客户被合并
		customer.setKuickUserId(null);
		customer.setMergedCustomerId(mergedCustomerId);
		customer.setMergedCustomerIds(StringUtils.join(mergedCustomerIds, ","));
		customer.setLinkDealUserIds(StringUtils.join(dealUserIds, ","));
		customer.setStatus(Customer.Status.MERGED.getValue());
		
		this.update(customer);
	}

	/**
	 * 删除客户
	 */
	@Override
	public void markDeleteCustomer(Customer customer) {
		AssertUtils.notNull(customer, "customer can not be null");
		AssertUtils.notNull(customer.getId(), "customer's id can not be null");
		
		// 标记客户被合并
		customer.setKuickUserId(null);
		customer.setStatus(Customer.Status.DELETED.getValue());
		
		this.update(customer);
	}

	@Override
	public Customer create(Customer customer) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<CustomerId> getCustomerByTag(String appId, String tag, long startIndex, int count) {
		return customerDao.findByTag(appId, tag, startIndex, count);
	}

    @Override
    public long countCustomerByFilter(String appId, String filterId) {
	    long l = 0L;
        try {
            CustomerFilter filter = customerFilterComponent.getCustomerFilterByFilterId(appId, filterId);
            if(filter != null){
                String creatorId = filter.getCreatorId();
                List<Condition> conditions = filter.getConditions();
                if(conditions != null && !conditions.isEmpty()){
                    this.preHandleConditions(creatorId, appId, conditions);
                    l = customerDao.countByCondition(appId, conditions);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return l;
    }

	@Override
	@Transactional
	public Boolean batchTransferAscription(String appId, String sourceKuickUserId, String targetKuickUserId,
										   String customerIds, boolean isAutomatic, Integer isJudgeOwner) {
	    String[] customerIdArray = customerIds.split(",");
		Arrays.stream(customerIdArray).forEach(customerId -> {
		    String originOwnerId = sourceKuickUserId;
			try {
				SalesCustomer originSalesCustomer = salesCustomerRepository.findFirstByAppIdAndCustomerId(appId, customerId);
				// 判断当前销售是否为目标销售
				if (originSalesCustomer != null && originSalesCustomer.getKuickUserId().equals(targetKuickUserId)) {
					return;
				}
				if (StringUtils.isNotBlank(sourceKuickUserId) && originSalesCustomer != null && !originSalesCustomer.getKuickUserId().equals(sourceKuickUserId)) {
					throw new BusinessException("param_error", "客户不属于当前销售");
				}

				if(isJudgeOwner != null && isJudgeOwner == 1){
					//判断转让的客户是否属于项目创建人，如果属于则进行转让，如果不属于项目创建人，则不做转让操作
//					DealApp dealApp = dealAppService.getDealAppInfo(appId);
//					if(!StringUtils.equals(originSalesCustomer.getKuickUserId(), dealApp.getCreatorId())){
//						logger.info("客户属于其他销售，不做转让");
//						return;
//					}
					if(StringUtils.isNotBlank(originSalesCustomer.getKuickUserId())){
						AppMember appMember =  dealAppMemberService.getAppMemberByKuickUserId(appId, Integer.parseInt(originSalesCustomer.getKuickUserId()));
						logger.info("appMember appMember = {} ", appMember);
						if(appMember != null && StringUtils.isNotBlank(appMember.getPostRoles()) && appMember.getPostRoles().contains(MemberImportVO.Role.SalesPost.getK())){
							logger.info("客户属于其他销售，不做转让");
							return;
						}
					}
				}

				Customer oldBody = customerDao.getCustomerById(customerId);
				if (oldBody == null) {
					throw new BusinessException("param_error", "客户不存在");
				} else if (!appId.equals(oldBody.getAppId())) {
					throw new BusinessException("param_error", "客户不属于该app");
				}
				// 已有销售则更新
				if (originSalesCustomer != null) {
					originOwnerId = originSalesCustomer.getKuickUserId();
					originSalesCustomer.setKuickUserId(targetKuickUserId);
					originSalesCustomer.setUpdatedAt(new Date());
					salesCustomerRepository.saveAndFlush(originSalesCustomer);
				} else {
					// 否则创建
					SalesCustomer sc = new SalesCustomer();
					sc.setId(UUID.randomUUID().toString());
					sc.setAppId(appId);
					sc.setKuickUserId(targetKuickUserId);
					sc.setCustomerId(customerId);
					sc.setIsNew(true);
					sc.setNewCount(0);
					sc.setWhetherMerge(false);
					sc.setCreatedAt(new Date());
					sc.setUpdatedAt(new Date());
					salesCustomerRepository.saveAndFlush(sc);
				}
				if (StringUtils.isNotBlank(originOwnerId)) {
					KuickUser kuickUser = kuickuserUserService.getUserById(originOwnerId);
					if (kuickUser != null) {
						Date now = new Date();
						CustomerTransferLog ctl = new CustomerTransferLog();
						ctl.setId(UUID.randomUUID().toString());
						ctl.setAppId(appId);
						ctl.setKuickUserId(String.valueOf(kuickUser.getId()));
						ctl.setKuickUserName(kuickUser.getName());
						ctl.setTargetKuickUserId(targetKuickUserId);
						ctl.setCustomerId(customerId);
						ctl.setWhen(now);
						ctl.setCreatedAt(now);
						customerTransferLogRepository.saveAndFlush(ctl);
						if (!isAutomatic) {
						    //TODO app member behaviour log
						}
					}
				}
				// 查询修改后信息
				Customer newBody = customerDao.getCustomerById(customerId);
				// 发送领域事件
				CustomerDomainEvent event = new CustomerDomainEvent(newBody.getId(), CustomerDomainEvent.UPDATE, newBody, oldBody);
				customerDomainEventProducer.send(event);
			} catch (Exception e) {
				logger.error("customer={} transfer to deal_kuick_user={} occur exception", customerId, targetKuickUserId, e);
				if (customerIdArray.length == 1) {
					if (e instanceof BusinessException) {
						throw e;
					}
					throw new BusinessException("server_error", "客户转移失败");
				}
			}
		});
		return true;
	}

	@Autowired
	private CustomerLinkDealUserRepository customerLinkDealUserRepository;

	@Override
	public Customer findByDealUserId(String dealUserId) {
		CustomerLinkDealUser link = customerLinkDealUserRepository.findFirstByDealUserId(dealUserId);
		logger.info("findByDealUserId.link: {}", link);

		if (link != null) {
			return customerRepository.findOne(link.getCustomerId());
		} else {
			return null;
		}
	}
}