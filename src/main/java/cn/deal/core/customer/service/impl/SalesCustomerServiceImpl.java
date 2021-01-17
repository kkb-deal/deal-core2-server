package cn.deal.core.customer.service.impl;

import cn.deal.component.AvatarGenComponent;
import cn.deal.component.DealBehaviourComponent;
import cn.deal.component.domain.CustomerDomainEvent;
import cn.deal.component.kuick.KuickuserUserService;
import cn.deal.component.kuick.domain.*;
import cn.deal.component.messaging.producer.CustomerDomainEventProducer;
import cn.deal.component.utils.AssertUtils;
import cn.deal.core.app.domain.AppMember;
import cn.deal.core.app.domain.DealApp;
import cn.deal.core.app.service.DealAppMemberService;
import cn.deal.core.app.service.DealAppService;
import cn.deal.core.customer.dao.CustomerDao;
import cn.deal.core.customer.domain.Customer;
import cn.deal.core.customer.domain.CustomerTransferLog;
import cn.deal.core.customer.domain.SalesCustomer;
import cn.deal.core.customer.repository.CustomerTransferLogRepository;
import cn.deal.core.customer.repository.SalesCustomerRepository;
import cn.deal.core.customer.service.SalesCustomerService;
import cn.deal.core.meta.domain.AppSetting;
import cn.deal.core.meta.service.AppSettingService;
import cn.deal.core.permission.service.AppMemberPermissionService;
import com.google.gson.Gson;
import jodd.util.StringUtil;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class SalesCustomerServiceImpl implements SalesCustomerService {

    protected static final int PARALLEL_COUNT = 2;

    /**
     * 锁释放时间
     */
    private static final long LOCK_RELEASE_TIME = 10L;

    private Logger logger = LoggerFactory.getLogger(SalesCustomerServiceImpl.class);

    private static final String ALL_SALES = "all";

    private static final int INCLUDE_SELF = 0;

    @Autowired
    private CustomerDao customerDao;

    @Autowired
    private SalesCustomerRepository salesCustomerRepository;

    @Autowired
    private DealAppMemberService dealAppMemberService;

    @Autowired
    private AppMemberPermissionService appMemberPermissionService;

    @Autowired
    private AppSettingService appSettingService;

    @Autowired
    private KuickuserUserService kuickuserUserService;

    @Autowired
    private DealBehaviourComponent dealBehaviourComponent;

    @Autowired
    private CustomerTransferLogRepository customerTransferLogRepository;

    @Autowired
    private CustomerDomainEventProducer customerDomainEventProducer;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private DealAppService dealAppService;

    @Override
    public SalesCustomer getSalesCustomerByCustomerId(String customerId) {
        return salesCustomerRepository.findByCustomerId(customerId);
    }

    @Override
    public SalesCustomer findSalesCustomerByAppIdAndCustomerId(String appId, String customerId) {
        return salesCustomerRepository.findFirstByAppIdAndCustomerId(appId, customerId);
    }

    /**
     * 解析需要查询的KuickUserIDs
     *
     * @param appId
     * @param kuickUserId
     * @param targetKuickUserIds
     * @return
     */
    protected List<String> parseAllToKuickUserIds(String appId, String kuickUserId, String targetKuickUserIds) {
        String userIdStr = targetKuickUserIds;
        if (ALL_SALES.equals(userIdStr)) {
            userIdStr = kuickUserId;
        }

        List<String> result = new ArrayList<>();

        Integer userId = Integer.parseInt(userIdStr);
        AppMember appMember = dealAppMemberService.getAppMemberByKuickUserId(appId, userId);
        AssertUtils.notNull(appMember, "不是项目成员");

        if (ALL_SALES.equals(targetKuickUserIds)) {
            List<KuickUser> kuickUsers = appMemberPermissionService.getManagedSales(appId, Integer.parseInt(kuickUserId), INCLUDE_SELF, null);

            if (kuickUsers != null && kuickUsers.size() > 0) {
                for (KuickUser user : kuickUsers) {
                    result.add(String.valueOf(user.getId()));
                }
            }
        } else {
            result.add(targetKuickUserIds);
        }

        return result;
    }

    /**
     * 获取客户列表是否按更新时间排序
     *
     * @param appId
     * @return
     */
    protected boolean getCustomerSortByUpdateDateConfig(String appId) {
        AppSetting as = appSettingService.getSetting(appId, "customer_sort_by_updatedat");

        if (as != null) {
            return "1".equals(as.getValue());
        }

        return true;
    }

    /**
     * 客户没有头像设置默认头像
     * 
     * @param appId
     * @param cc
     * @return
     */
    private Customer updateHeadportrait(String appId, Customer cc) {
        if (StringUtil.isBlank(cc.getHeadportraitUrl())) {
        	cc.setHeadportraitUrl(Customer.DEFAULT_HEAD_URL);
        }

        return cc;
    }

    private Customer dealWithSalesCustomer(String appId, String customerGroupId, Customer cc) {
        cc.setWhetherMerge(0);

        String[] kuickUserIds = new String[]{cc.getKuickUserId()};

        if (StringUtils.isNotBlank(cc.getPhone())) {
            List<Customer> ccs = customerDao.findCustomersBySales(appId, kuickUserIds, cc.getPhone(), null, customerGroupId, false, 0, 2);
            if (ccs != null && ccs.size() > 1) {
                cc.setWhetherMerge(1);
                return cc;
            }
        }

        if (StringUtils.isNotBlank(cc.getEmail())) {
            List<Customer> ccs = customerDao.findCustomersBySales(appId, kuickUserIds, null, cc.getEmail(), customerGroupId, false, 0, 2);
            if (ccs != null && ccs.size() > 1) {
                cc.setWhetherMerge(1);
                return cc;
            }
        }

        return cc;
    }

    @SuppressWarnings("unchecked")
	@Override
    public List<Customer> searchSalesCustomers(String appId, String kuickUserId, int hasPhone, String condition, String customerGroupId, String currentUserId) {
        logger.info("searchSalesCustomers: appId={}, kuickUserId={}, currentUserId={}", appId, kuickUserId, currentUserId);
        // 查询所有关联的KuickUserIds
        Mono<List<String>> asyncGetKuickUsers = Mono.fromDirect(sub -> {
            List<String> kuickUserIds = parseAllToKuickUserIds(appId, currentUserId, kuickUserId);
            logger.info("asyncGetKuickUsers: {}", kuickUserIds);

            sub.onNext(kuickUserIds);
            sub.onComplete();
        });

        // 获取项目开关配置
        Mono<Boolean> asyncGetSettings = Mono.fromDirect(new Publisher<Boolean>() {

            @Override
            public void subscribe(Subscriber<? super Boolean> sub) {
                boolean sortByUpdate = getCustomerSortByUpdateDateConfig(appId);
                logger.info("asyncGetSettings: {}", sortByUpdate);

                sub.onNext(sortByUpdate);
                sub.onComplete();
            }

        });


        // 获取满足条件的客户
        List<Customer> result = Mono.zip(asyncGetKuickUsers, asyncGetSettings,  (kuickUserIds, sortByUpdate)->{
			logger.info("Streams.zip result, kuickUserIds:{}, sortByUpdate:{}",
					kuickUserIds, sortByUpdate);
			
			return customerDao.findByAppIdAndKuickUserIds(appId, kuickUserIds, hasPhone, condition, customerGroupId);
		})
		.flatMapMany((cc)->{
			return Flux.fromIterable(cc);
		})
		.subscribeOn(Schedulers.elastic())
		.publishOn(Schedulers.parallel())
        .map((cc) -> updateHeadportrait(appId, cc))
        .collectList().block();
        
        // 如果返回null, 默认[]
        if (result == null) {
            result = ListUtils.EMPTY_LIST;
        }

        logger.info("getSalesCustomers: result:{}", result);

        return result;
    }

    /**
     * 转让客户
     *
     * @param appId  项目id
     * @param kdKuickUserId 当前登录人的用户id
     * @param sourceKuickUserId    原销售id
     * @param targetKuickUserId 目标销售id
     * @param customerIds 客户id
     * @param isAutomatic 是否自动转让
     * @param userAgent 浏览器userAgent
     * @param isJudgeOwner 是否判断转让客户所属人，0：否，1：是
     */
    @Override
    @Transactional(propagation=Propagation.REQUIRED, rollbackFor = Exception.class)
    public ResponseVO customerTransfer(String appId, String kdKuickUserId, String sourceKuickUserId, String targetKuickUserId,
                                       String customerIds, boolean isAutomatic, String userAgent, Integer isJudgeOwner) {
        logger.info("customerTransfer appId = {},  sourceKuickUserId = {}, targetKuickUserId = {}, customerIds = {}",
                appId, sourceKuickUserId, targetKuickUserId, customerIds);
        //增加redis锁来防止销售客户关系重复的问题
        String lockName = "transfer_customer_lock:" + appId + ":" + customerIds;
        RLock lock = redissonClient.getReadWriteLock(lockName).writeLock();
        logger.info("customerTransfer lock key:{}", lockName);
        
        try {
            lock.tryLock(LOCK_RELEASE_TIME, TimeUnit.SECONDS);
            logger.info("客户转让appId:{}, sourceKuickUerId:{}, targetKuickUserID:{}, customerId:{}", appId, sourceKuickUserId, targetKuickUserId, customerIds);

            SalesCustomer salesCustomerRecord = salesCustomerRepository.findFirstByAppIdAndCustomerId(appId, customerIds);
            SalesCustomer result = null;
            
            if(Objects.isNull(salesCustomerRecord)) {
                //如果不存在appid和客户关系，则插入一套新纪录
                logger.info("不存在appId{}和客户{}的关系记录", appId, customerIds);
                SalesCustomer newSaleCustoemr = new SalesCustomer();
                newSaleCustoemr.setId(UUID.randomUUID().toString());
                newSaleCustoemr.setAppId(appId);
                newSaleCustoemr.setKuickUserId(targetKuickUserId);
                newSaleCustoemr.setCustomerId(customerIds);
                newSaleCustoemr.setCreatedAt(new Date());
                newSaleCustoemr.setUpdatedAt(new Date());
                result = salesCustomerRepository.saveAndFlush(newSaleCustoemr);
            } else {
                //如果存在appId和客户的关系，进一步判断
                logger.info("存在appid{}和客户{}的关系记录", appId, customerIds);

                if(isJudgeOwner != null && isJudgeOwner == 1){
                    //判断转让的客户是否属于项目创建人，如果属于则进行转让，如果不属于项目创建人，则不做转让操作
//                    DealApp dealApp = dealAppService.getDealAppInfo(appId);
//                    if(!StringUtils.equals(salesCustomerRecord.getKuickUserId(), dealApp.getCreatorId())){
//                        return new ResponseVO(ResponseVO.Status.OK.getVal(), "客户属于其他销售，不做转让");
//                    }
                    //新逻辑：客户所属人是角色否为非销售，如果是非销售，则线索转让；
                    if(StringUtils.isNotBlank(salesCustomerRecord.getKuickUserId())){
                        AppMember appMember =  dealAppMemberService.getAppMemberByKuickUserId(appId, Integer.parseInt(salesCustomerRecord.getKuickUserId()));
                        logger.info("appMember appMember = {} ", appMember);
                        if(appMember != null && StringUtils.isNotBlank(appMember.getPostRoles()) && appMember.getPostRoles().contains(MemberImportVO.Role.SalesPost.getK())){
                            return new ResponseVO(ResponseVO.Status.OK.getVal(), "客户属于其他销售，不做转让");
                        }
                    }
                }

                if(targetKuickUserId.equals(salesCustomerRecord.getKuickUserId())) {
                    //客户的销售id和被转让的客户销售id一样的话则不更新
                    return new ResponseVO(ResponseVO.Status.OK.getVal(), "客户转移成功");
                } else {
                    salesCustomerRecord.setKuickUserId(targetKuickUserId);
                    salesCustomerRecord.setUpdatedAt(new Date());
                }
                
                result = salesCustomerRepository.saveAndFlush(salesCustomerRecord);
            }
            
            //如果转移成功，新建一条转移记录
            if(Objects.nonNull(result)) {
                logger.info("新建客户转移记录 from:{} to:{} customerId:{}", sourceKuickUserId, targetKuickUserId, customerIds);
                CustomerTransferLog customerTransferLog = new CustomerTransferLog();
                customerTransferLog.setId(UUID.randomUUID().toString());
                customerTransferLog.setAppId(appId);
                KuickUser sourceKuickUser = kuickuserUserService.getUserById(sourceKuickUserId);
                customerTransferLog.setKuickUserId(sourceKuickUserId);
                if(Objects.nonNull(sourceKuickUser)) {
                    customerTransferLog.setKuickUserName(sourceKuickUser.getName());
                }
                customerTransferLog.setCustomerId(customerIds);
                customerTransferLog.setTargetKuickUserId(targetKuickUserId);
                customerTransferLog.setWhen(new Date());
                customerTransferLog.setCreatedAt(new Date());
                customerTransferLogRepository.saveAndFlush(customerTransferLog);
                Customer customer = customerDao.getCustomerById(customerIds);

                sendCustomerDomainEvent(sourceKuickUserId, targetKuickUserId, customerIds, customer);
                createBehaviourLog(appId, kdKuickUserId, sourceKuickUserId, targetKuickUserId, isAutomatic, userAgent, customer);
                
                return new ResponseVO(ResponseVO.Status.OK.getVal(), "客户转移成功");
            } else {
                return new ResponseVO(ResponseVO.Status.ERROR.getVal(), "客户转移失败");
            }
        } catch (Exception e) {
            logger.error("transfer customer appid:" + appId + " customerId:" + customerIds + " try lock error occured.", e);
            return new ResponseVO(ResponseVO.Status.ERROR.getVal(), "客户转移失败");
        } finally {
            //释放锁
            if(Objects.nonNull(lock) && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    /**
     * 发送行为事件
     * 当前登录人将客户转让给目标销售，当前登录人可能是销售，也可能是超级管理员，因此需要获取当前登录人的信息而不是获取kuickUser的信息
     * @param appId
     * @param kdKuickUserId  当前登录人的用户id
     * @param sourceKuickUserId  原销售id
     * @param targetKuickUserId  目标销售id
     * @param isAutomatic  是否自动转让
     * @param userAgent  浏览器信息
     * @param customer   客户信息
     */
    private void createBehaviourLog(String appId, String kdKuickUserId, String sourceKuickUserId, String targetKuickUserId, boolean isAutomatic, String userAgent, Customer customer) {
    	logger.info("create transfer_customer BehaviourLog: appId:{}, kdKuickUserId:{}, sourceKuickUserId:{}, targetKuickUserId:{}, isAutomatic:{}", appId, kdKuickUserId, sourceKuickUserId, targetKuickUserId, isAutomatic);
    	
    	//如果是自动转让则不发送行为事件
        if(!isAutomatic && StringUtils.isNotBlank(kdKuickUserId) && StringUtils.isNotBlank(targetKuickUserId)) {
            //发送行为事件
            try {
                KuickUser currentLoginUser = kuickuserUserService.getUserById(kdKuickUserId);
                KuickUser targetKuickUser = kuickuserUserService.getUserById(targetKuickUserId);
                if(Objects.nonNull(currentLoginUser) && Objects.nonNull(targetKuickUser)){
                    String description = currentLoginUser.getName() + " 将客户 " + customer.getName() + " 转让给了 " + targetKuickUser.getName();
                    logger.info("发送行为事件:{}", description);
                    Map<String, Object> contentMap = new HashMap<>(16);
                    contentMap.put("customer_id", customer.getId());
                    contentMap.put("source_kuick_user_id", sourceKuickUserId);
                    contentMap.put("target_kuick_user_id", targetKuickUserId);
                    dealBehaviourComponent.createBehaviourLog(appId, Integer.valueOf(kdKuickUserId), "transfer_customer", description, currentLoginUser, contentMap, userAgent);
                }
            } catch (Exception e) {
                logger.error("Error in createBehaviourLog", e);
            }
        }
    }

    /**
     * 发送客户领域事件
     * @param sourceKuickUserId
     * @param targetKuickUserId
     * @param customerIds
     * @param customer
     */
    private void sendCustomerDomainEvent(String sourceKuickUserId, String targetKuickUserId, String customerIds, Customer customer) {
        Customer originCustomer = new Gson().fromJson(new Gson().toJson(customer), Customer.class);
        originCustomer.setKuickUserId(sourceKuickUserId);

        customer.setKuickUserId(targetKuickUserId);
        CustomerDomainEvent event = new CustomerDomainEvent(customerIds, CustomerDomainEvent.UPDATE, customer, originCustomer);
        customerDomainEventProducer.send(event);
    }

    /**
     * 批量转让客户
     *
     * @param appId  项目id
     * @param kdKuickUserId 当前登录人的用户id
     * @param datas    原销售id
     * @param targetKuickUserId 目标销售id
     * @param userAgent 浏览器userAgent
     */
    @Override
    @Transactional(propagation=Propagation.REQUIRED, rollbackFor = Exception.class)
    public ResponseVO batchCustomerTransfer(String appId, String kdKuickUserId, String datas, String targetKuickUserId, String userAgent) {
        logger.info("批量转让客户appId:{}, datas:{}, targetKuickUserID:{}", appId, datas, targetKuickUserId);
        JSONArray dataJsonArray = new JSONArray(datas);
        for(int i = 0; i < dataJsonArray.length(); i++) {
            JSONObject item = dataJsonArray.getJSONObject(i);
            String customerId = item.getString("customerId");
            String sourceKuickUserId = item.getString("srouceKuickUserId");
            customerTransfer(appId, kdKuickUserId, sourceKuickUserId, targetKuickUserId,
                    customerId, false, userAgent, 0);
        }
        return new ResponseVO(ResponseVO.Status.OK.getVal(), "success");
    }

    @SuppressWarnings("unchecked")
	@Override
	public List<Customer> getSalesCustomers(final String appId, final String kuickUserId, final String targetKuickUserIds, 
			final String customerGroupId, final String phone, final String email,
			final int startIndex, final int count) {
		final SalesCustomerServiceImpl self = this;
		 
		logger.info("getSalesCustomers: appId:{}, kuickUserId:{}, targetKuickUserIds:{}, customerGroupId:{}, phone:{}, email:{}",
				appId, kuickUserId, targetKuickUserIds, customerGroupId, phone, email);
		
		 
		// 查询所有关联的KuickUserIds
		Mono<List<String>> asyncGetKuickUsers = Mono.fromDirect(new Publisher<List<String>>() {

			@Override
			public void subscribe(Subscriber<? super List<String>> sub) {
				List<String> kuickUserIds = self.parseAllToKuickUserIds(appId, kuickUserId, targetKuickUserIds);
				logger.info("asyncGetKuickUsers: {}", kuickUserIds);
				
				sub.onNext(kuickUserIds);
				sub.onComplete();
			}
			
		});
		
		// 获取项目开关配置
		Mono<Boolean> asyncGetSettings = Mono.fromDirect(new Publisher<Boolean>() {

			@Override
			public void subscribe(Subscriber<? super Boolean> sub) {
				boolean sortByUpdate = self.getCustomerSortByUpdateDateConfig(appId);
				logger.info("asyncGetSettings: {}", sortByUpdate);
				
				sub.onNext(sortByUpdate);
				sub.onComplete();
			}
			
		});
		
		//获取满足条件的客户
		List<Customer> result = Mono.zip(asyncGetKuickUsers, asyncGetSettings, (kuickUserIds, sortByUpdate)->{
			logger.info("Streams.zip result, kuickUserIds:{}, sortByUpdate:{}",
					kuickUserIds, sortByUpdate);
			
			return customerDao.findCustomersBySales(appId, kuickUserIds.toArray(new String[kuickUserIds.size()]), phone, email, customerGroupId, sortByUpdate, startIndex, count);
		}).flatMapMany((cc)->{
			return Flux.fromIterable(cc);
		})
		.subscribeOn(Schedulers.elastic())
		.publishOn(Schedulers.parallel())
		.map((c)->{
			return self.updateHeadportrait(appId, c);
		})
		.map((c)->{
			return self.dealWithSalesCustomer(appId, customerGroupId, c);
		}).collectList().block();
				
		// 如果返回null, 默认[]
		if (result==null) {
			result = ListUtils.EMPTY_LIST;
		}
		
		logger.info("getSalesCustomers: result:{}", result);
		
		return result;
	}

	protected Comparator<Customer> customerActiveComparator() {
		return new Comparator<Customer>() {
			@Override
			public int compare(Customer o1, Customer o2) {
				return o1.getUpdatedAt().compareTo(o2.getUpdatedAt());
			}
			
		};
	}


	@Override
	public SalesCustomer createAppSalesCustomer(String appId, Customer customer, String targetUserId) {
        SalesCustomer sc = new SalesCustomer();
        sc.setId(UUID.randomUUID().toString());
        sc.setAppId(appId);
        sc.setKuickUserId(targetUserId);
        sc.setCustomerId(customer.getId());

        sc.setIsNew(true);
        sc.setNewCount(0);
        sc.setWhetherMerge(false);
        sc.setCreatedAt(new Date());
        sc.setUpdatedAt(new Date());
        return salesCustomerRepository.saveAndFlush(sc);
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public void deleteByCustomer(String customerId) {
		salesCustomerRepository.deleteByCustomerId(customerId);
	}
}
