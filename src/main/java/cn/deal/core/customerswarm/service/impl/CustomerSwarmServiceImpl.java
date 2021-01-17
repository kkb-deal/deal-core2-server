package cn.deal.core.customerswarm.service.impl;

import cn.deal.component.DealBehaviourComponent;
import cn.deal.component.DealCustomerSearchComponent;
import cn.deal.component.RedisService;
import cn.deal.component.domain.AddSwarmCustomerParam;
import cn.deal.component.exception.BusinessException;
import cn.deal.component.kuick.KuickuserUserService;
import cn.deal.component.kuick.domain.KuickUser;
import cn.deal.component.messaging.producer.AddSwarmCustomerParamProducer;
import cn.deal.component.utils.JsonUtil;
import cn.deal.core.app.dao.DealAppMemberDao;
import cn.deal.core.app.domain.AppMember;
import cn.deal.core.app.domain.DealApp;
import cn.deal.core.app.service.DealAppService;
import cn.deal.core.customer.domain.Customer;
import cn.deal.core.customer.service.CustomerService;
import cn.deal.core.customerswarm.dao.CustomerSwarmDao;
import cn.deal.core.customerswarm.dao.SwarmMemberDao;
import cn.deal.core.customerswarm.dao.SwarmShareDao;
import cn.deal.core.customerswarm.domain.CustomerSwarm;
import cn.deal.core.customerswarm.domain.CustomerSwarmTypeEnum;
import cn.deal.core.customerswarm.domain.SwarmMember;
import cn.deal.core.customerswarm.domain.SwarmShare;
import cn.deal.core.customerswarm.repository.CustomerSwarmRepository;
import cn.deal.core.customerswarm.repository.SwarmMemberRepository;
import cn.deal.core.customerswarm.service.CustomerSwarmService;
import cn.deal.core.meta.domain.CustomerSwarmMeta;
import cn.deal.core.meta.service.CustomerSwarmMetaService;
import cn.deal.core.permission.service.AppMemberPermissionService;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class CustomerSwarmServiceImpl implements CustomerSwarmService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerSwarmServiceImpl.class);

    @Autowired
    private CustomerSwarmDao customerSwarmDao;
    @Autowired
    private SwarmShareDao swarmShareDao;
    @Autowired
    private DealAppMemberDao dealAppMemberDao;
    @Autowired
    private SwarmMemberDao swarmMemberDao;
    @Autowired
    private KuickuserUserService kuickuserUserService;
    @Autowired
    private AppMemberPermissionService appMemberPermissionService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private CustomerSwarmRepository customerSwarmRepository;
    @Autowired
    private SwarmMemberRepository swarmMemberRepository;
    @Autowired
    private RedisService redisService;
    @Autowired
    private DealAppService dealAppService;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private AddSwarmCustomerParamProducer addSwarmCustomerParamProducer;
    @Autowired
    private DealCustomerSearchComponent dealCustomerSearchComponent;
    @Autowired
    private CustomerSwarmMetaService customerSwarmMetaService;
    @Autowired
    private DealBehaviourComponent dealBehaviourComponent;
    @Value("${redis.swarm.lockleasetime}")
    private Long swarmLockLeaseTime;

    @Override
    public CustomerSwarm create(String appId, String kuickUserId, String name, String photoUrl, String comment, Integer type, String filterId, Map<String, String> data) {
        CustomerSwarm swarm = new CustomerSwarm(appId, kuickUserId, name, photoUrl, comment, type, filterId);
        swarm = this.preHandle(appId, swarm, data);
        swarm = customerSwarmRepository.saveAndFlush(swarm);
        if (swarm != null) {
            KuickUser user = kuickuserUserService.getUserById(kuickUserId);
            swarm.setUserName(user.getName());
            swarm.setShareType(0);
            swarm.setCustomerCount(0);
            logger.info("create swarm: {}", swarm);
            return swarm;
        }
        return null;
    }

    @Override
    public boolean delete(String appId, String swarmId, String kuickUserId) {
        logger.info("delete swarm : appId: {}, swarmId: {}, kuickUserId: {}", appId, swarmId, kuickUserId);
        CustomerSwarm swarm = get(swarmId);
        if (isSwarmCreator(appId, kuickUserId, swarm)) {
            swarm.setStatus(CustomerSwarm.Status.DELETED.getVal());
            this.removeSwarmFromRedis(swarm);
            customerSwarmRepository.saveAndFlush(swarm);
            return true;
        }
        logger.info("不是分群创建人");
        return false;
    }

    @Override
    public CustomerSwarm update(String appId, String swarmId, String kuickUserId, String name, String photoUrl, String comment, String filterId, Map<String, String> data) {
        logger.info("update swarm : appId: {}, swarmId: {}, kuickUserId: {}, name: {}, protoUrl: {}, filterId: {}", appId, swarmId, kuickUserId, name, photoUrl, filterId);
        CustomerSwarm swarm = this.get(swarmId);

        if (swarm.getType() == CustomerSwarm.Type.VIRTUAL.getVal() && StringUtils.isBlank(filterId)) {
            throw new BusinessException("invalid_param_filter_id", "当type为2时，filter_id必填");
        }

        if (isSwarmCreator(appId, kuickUserId, swarm)) {
            if (StringUtils.isNotBlank(name)) {
                //如果更新了分群名称，需要将该名称对应的缓存信息删除
                if (!swarm.getName().equals(name)) {
                    this.removeSwarmFromRedis(swarm);
                }
                swarm.setName(name);
            }
            if (StringUtils.isNotBlank(photoUrl)) {
                swarm.setPhotoUrl(photoUrl);
            }
            if (comment != null) {
                swarm.setComment(comment);
            }
            if (StringUtils.isNotBlank(filterId)) {
                swarm.setFilterId(filterId);
            }

            swarm = this.preHandle(appId, swarm, data);
            customerSwarmRepository.saveAndFlush(swarm);
        } else {
            logger.info("kuickUserId ( {} ) 不是分群创建人", kuickUserId);
        }
        this.handleExtraInfo(swarm);
        return swarm;
    }

    @Override
    public Map<String, Object> getWithExt(String appId, String id) {
        if(StringUtils.isBlank(appId) || StringUtils.isBlank(id)){
            return null;
        }

        CustomerSwarm swarm = this.get(id);
        if(swarm == null || !swarm.getAppId().equals(appId)){
            logger.error("getWithExt. No swarm found by appid: {} and id: {}", appId, id);
            return null;
        }

        List<CustomerSwarmMeta> metas = customerSwarmMetaService.findByAppId(appId);
        return this.postHandle(swarm, metas);
    }

    /**
     * 客户分群支持分页
     */
    @Override
    public List<Map<String, Object>> list(String appId, String kuickUserId, int startIndex, int count, String keyword) {
        logger.info("list customer swarm list: appId: {}, kuickUserId: {}, startIndex:{}, count: {}, keyword:{}", appId, kuickUserId, startIndex, count, keyword);
//        查询别人共享给自己的
        //查询别人共享自己的和自己的客户分群
        List<CustomerSwarm> sharedToMeAndSelf = customerSwarmDao.getSharedAndSelfCustomerSwarmList(appId, kuickUserId, startIndex, count, keyword);

        logger.info("list. shared and self swarms: {}", sharedToMeAndSelf);
        for (CustomerSwarm cs : sharedToMeAndSelf) {
            KuickUser user = kuickuserUserService.getUserById(cs.getKuickUserId());
            if(!StringUtils.equals(kuickUserId, cs.getKuickUserId())) {
                //别人共享给自己的客户分群
                if (user != null) {
                    cs.setUserName(user.getName());
                }
                cs.setShareType(CustomerSwarm.ShareType.TO_ME.getVal());
                this.handleCustomerSwarmCount(cs);
            } else if(StringUtils.equals(kuickUserId, cs.getKuickUserId())) {
                //自己的客户分群
                this.handleExtraInfo(cs);
            }
        }

        List<Map<String, Object>> list = new ArrayList<>();
        List<CustomerSwarmMeta> metas = customerSwarmMetaService.findByAppId(appId);
        for(CustomerSwarm swarm : sharedToMeAndSelf){
            Map map = this.postHandle(swarm, metas);
            list.add(map);
        }

        return list;
    }
    private void handleCustomerSwarmCount(CustomerSwarm cs) {
        int customerCount = 0;
        //从redis中查，如果没有走原来的逻辑
        String redisKey = "swarm:customercount:" + cs.getId();
        String customerCountStr = redisService.get(redisKey);

        if (StringUtils.isNotBlank(customerCountStr)) {
            customerCount = Integer.parseInt(customerCountStr);
        } else {
            customerCount = swarmMemberDao.getCountBySwarmId(cs.getId());
        }

        cs.setCustomerCount(customerCount);
    }

    private void handleExtraInfo(CustomerSwarm cs) {
        List<SwarmShare> swarmShareList = swarmShareDao.fetchBySwarmId(cs.getAppId(), cs.getId());
        if (swarmShareList.size() > 0) {
            cs.setShareCount(swarmShareList.size());
            cs.setShareType(CustomerSwarm.ShareType.TO_OTHERS.getVal());
            cs.setTargetType(swarmShareList.get(0).getTargetType());
        } else {
            cs.setShareType(0);
        }

        KuickUser user = kuickuserUserService.getUserById(cs.getKuickUserId());
        if (user != null) {
            cs.setUserName(user.getName());
        }

        this.handleCustomerSwarmCount(cs);
    }

    @Override
    public Map<String, Object> shareSwarm(String appId, String swarmId, String kuickUserId, int targetType, String targetIds) {
        if (appId == null || swarmId == null || kuickUserId == null || targetIds == null) {
            logger.info("缺少必要参数");
            return Collections.emptyMap();
        }
        CustomerSwarm swarm = get(swarmId);
        if (isSwarmCreator(appId, kuickUserId, swarm)) {
            String[] idArray = targetIds.split(",");
            List<String> idAfterFilter = new ArrayList<>();
            if (SwarmShare.TargetType.APP.getVal() == targetType) {
                //共享给整个项目,只能是当前项目,并且要删除共享给项目成员的记录
                for (String id : idArray) {
                    if (swarm.getAppId().equals(id)) {
                        idAfterFilter.add(id);
                    }
                }
                //删除swarmid对应的所用的共享给项目成员的记录
                swarmShareDao.deleteBySwarmIdAndTargetType(appId, swarmId, SwarmShare.TargetType.APP_MEMBER.getVal());
            } else if (SwarmShare.TargetType.APP_MEMBER.getVal() == targetType) {
                //共享给项目成员，只能是当前的项目成员
                List<AppMember> members = dealAppMemberDao.getDealAppMembers(appId, null, null);
                List<String> memberIds = new ArrayList<>();
                for (AppMember member : members) {
                    memberIds.add(String.valueOf(member.getKuickUserId()));
                }
                for (String id : idArray) {
                    if (memberIds.contains(id) && !id.equals(kuickUserId)) {
                        idAfterFilter.add(id);
                    }
                }
                //删除swarmid对应的所用的共享给整个项目的记录
                swarmShareDao.deleteBySwarmIdAndTargetType(appId, swarmId, SwarmShare.TargetType.APP.getVal());
            }
            swarmShareDao.batchInsert(appId, swarmId, targetType, idAfterFilter);
            List<SwarmShare> swarmShareList = swarmShareDao.fetchBySwarmId(appId, swarmId);
            Map<String, Object> result = swarmShareSourceInfo(kuickUserId, swarmShareList);
            return result;
        } else {
            logger.info("不是分群创建人");
            List<SwarmShare> swarmShareList = swarmShareDao.fetchBySwarmId(appId, swarmId);
            return swarmShareSourceInfo(kuickUserId, swarmShareList);
        }
    }

    private Map<String, Object> swarmShareSourceInfo(String kuickUserId, List<SwarmShare> swarmShareList) {
        KuickUser kuickUser = kuickuserUserService.getUserById(kuickUserId);
        boolean shareApp = false;
        boolean share = false;
        for (SwarmShare swarmShare : swarmShareList) {
            if (swarmShare.getTargetType() == SwarmShare.TargetType.APP.getVal()) {
                if (swarmShare.getAppId().equals(swarmShare.getTargetId())) {
                    shareApp = true;
                    share = true;
                }
            }
            swarmShare.setShareSourceName(kuickUser.getName());
            swarmShare.setShareSourcePhoto(kuickUser.getPhotoURI());
        }
        share = share || swarmShareList.size() > 0;
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("shareMembers", swarmShareList);
        resultMap.put("shareApp", shareApp);
        resultMap.put("share", share);
        return resultMap;
    }

    @Override
    public boolean deleteShareSwarm(String appId, String swarmId, String kuickUserId) {
        CustomerSwarm swarm = get(swarmId);
        if (isSwarmCreator(appId, kuickUserId, swarm)) {
            swarmShareDao.deleteBySwarmId(appId, swarmId);
        }
        return true;
    }

    @Transactional
    @Override
    public Map<String, Object> updateShareSwarm(String appId, String swarmId, String kuickUserId, int targetType, String targetIds) {
        logger.info("update shareSwarm: appId: {}, swarmId: {}, kuickUserId:{}, targetType:{},targetIds:{}", appId, swarmId, kuickUserId, targetType, targetIds);
        if (appId == null || swarmId == null || kuickUserId == null || targetIds == null) {
            logger.info("缺少必要参数");
            return Collections.emptyMap();
        }
        CustomerSwarm swarm = get(swarmId);
        if (isSwarmCreator(appId, kuickUserId, swarm)) {
            swarmShareDao.deleteBySwarmIdAndTargetType(appId, swarmId, targetType);
            return this.shareSwarm(appId, swarmId, kuickUserId, targetType, targetIds);
        } else {
            logger.info("不是分群创建人");
            List<SwarmShare> swarmShareList = swarmShareDao.fetchBySwarmId(appId, swarmId);
            return swarmShareSourceInfo(kuickUserId, swarmShareList);
        }
    }

    @Override
    public Map<String, Object> fetchSwarmSharesBySwarmId(String appId, String kuickUserId, String swarmId) {
        List<SwarmShare> swarmShares = swarmShareDao.fetchBySwarmId(appId, swarmId);
        return swarmShareSourceInfo(kuickUserId, swarmShares);
    }

    @Override
    public boolean addCustomers(String appId, String swarmId, String kuickUserId, String customerIds, int sync, String userAgent) {
        int isSync = 1;
        if (isSync == sync) {
            return this.addCustomers(appId, swarmId, kuickUserId, customerIds, true, userAgent);
        } else {
            return this.addCustomers(appId, swarmId, kuickUserId, customerIds, false, userAgent);
        }
    }

    /**
     * 分群添加客户
     * @param appId  项目id
     * @param swarmId  分群id
     * @param kuickUserId  销售id
     * @param customerIds  客户id
     * @param sync  是否同步
     * @param userAgent  浏览器userAgent
     * @return
     */
    public boolean addCustomers(String appId, String swarmId, String kuickUserId, String customerIds, boolean sync, String userAgent) {
        logger.info("swarm add customers: appId:{}, swarId:{}, kuickUserId:{}, customerIds:{}, sync:{}", appId, swarmId, kuickUserId, customerIds, sync);
        CustomerSwarm swarm = get(swarmId);
        KuickUser operationKuicUser = kuickuserUserService.getUserById(kuickUserId);
        if (StringUtils.isBlank(customerIds)) {
            return true;
        }

        if (isSwarmOwner(appId, kuickUserId, swarm)) {
            String[] custIdArr = customerIds.split(",");
            List<String> custIdList = Arrays.asList(custIdArr);

            // 同步
            if (sync) {
                for (String customerId : custIdList) {
                    String lockName = "swarm_add_customer_lock:" + appId + ":" + swarmId + ":" + customerId;
                    RLock rLock = redissonClient.getReadWriteLock(lockName).writeLock();

                    try {
                        if (rLock.tryLock(1L, swarmLockLeaseTime, TimeUnit.SECONDS)) {
                            int insertCount = swarmMemberDao.singleInsert(appId, swarmId, customerId);
                            if(insertCount > 0) {
                                sendAddCustomerToSwarmBehaviourLog(customerId, operationKuicUser, swarm, appId, kuickUserId, userAgent);
                                incrementRedisCount(swarmId, insertCount);
                            }
                        }
                    } catch (Exception e) {
                       logger.warn("add swarm[{}] customer[{}] try lock occur exception", swarmId, customerId, e);
                    } finally {
                        if (rLock != null && rLock.isHeldByCurrentThread()) {
                            rLock.unlock();
                        }
                    }
                }
            }

            // 异步
            else {
                for (String customerId : custIdList) {
                    sendAddCustomerToSwarmBehaviourLog(customerId, operationKuicUser, swarm, appId, kuickUserId, userAgent);
                    //发送kafka
                    AddSwarmCustomerParam param = new AddSwarmCustomerParam(appId, swarmId, customerId);
                    addSwarmCustomerParamProducer.send(param);
                }
            }
        } else {
            logger.info("kuickUserId ({})不是分群的创建人或者被共享", kuickUserId);
        }

        return true;
    }

    /**
     * 发送添加客户到分群行为事件
     * @param customerId
     * @param operationKuicUser
     * @param swarm
     * @param appId
     * @param kuickUserId
     * @param userAgent
     */
    private void sendAddCustomerToSwarmBehaviourLog(String customerId, KuickUser operationKuicUser, CustomerSwarm swarm, String appId, String kuickUserId, String userAgent) {
        Customer customer = customerService.getCustomerById(customerId);
        String description = operationKuicUser.getName() + " 将客户 "+ customer.getName() + " 添加进分群 " + swarm.getName();
        logger.info("添加客户到分群description:{}", description);
        Map<String, Object> swarmContent = getWithExt(appId, swarm.getId());
        //客户id 关联客户上下文
        swarmContent.put("customer_id", customerId);
        try {
            dealBehaviourComponent.createBehaviourLog(appId, Integer.valueOf(kuickUserId), "add_to_swarm", description, operationKuicUser, swarmContent, userAgent);
        } catch(Exception e) {
            logger.info("添加客户到分群发送行为事件失败:{}", e);
            throw new BusinessException("error", "添加客户到分群失败");
        }
    }


    /**
     * 发送一处客户到分群行为事件
     * @param customerId
     * @param operationKuicUser
     * @param swarm
     * @param appId
     * @param kuickUserId
     * @param userAgent
     */
    private void sendRemoveCustomerFromSwarmBehaviourLog(String customerId, KuickUser operationKuicUser, CustomerSwarm swarm, String appId, String kuickUserId, String userAgent) {
        Customer customer = customerService.getCustomerById(customerId);
        String description = operationKuicUser.getName() + " 将客户 "+ customer.getName() + " 移除出分群 " + swarm.getName();
        logger.info("删除客户从分群description:{}", description);
        Map<String, Object> swarmContent = getWithExt(appId, swarm.getId());
        //客户id 关联客户上下文
        swarmContent.put("customer_id", customerId);
        try {
            dealBehaviourComponent.createBehaviourLog(appId, Integer.valueOf(kuickUserId), "delete_from_swarm", description, operationKuicUser, swarmContent, userAgent);
        } catch(Exception e) {
            logger.info("添加客户到分群发送行为事件失败:{}", e);
            throw new BusinessException("error", "添加客户到分群失败");
        }
    }


    @Override
    public int addSwarmMember(String appId, String swarmId, String customerId) {
        int rows = swarmMemberDao.singleInsert(appId, swarmId, customerId);
        if (rows > 0) {
            this.incrementRedisCount(swarmId, rows);
        }
        return rows;
    }

    @Override
    public void moveCustomersWithSwarmName(String appId, String swarmName, String customerIds, String userAgent) {
        List<SwarmMember> swarmMembers = swarmMemberRepository.findAllByAppIdAndCustomerIdIn(appId, customerIds.split(","));
        List<CustomerSwarm> customerSwarms = getCustomerSwarms(swarmMembers);
        removeCustomerWithSwarms(customerSwarms, customerIds, userAgent);
        addCustomersWithSwarmName(appId, swarmName, customerIds, userAgent);
    }

    private List<CustomerSwarm> getCustomerSwarms(List<SwarmMember> swarmMembers) {
        List<CustomerSwarm> swarms = null;
        if (swarmMembers != null && !swarmMembers.isEmpty()) {
            List<String> swarmIds = Lists.newArrayList();
            for (SwarmMember swarmMember : swarmMembers) {
                swarmIds.add(swarmMember.getSwarmId());
            }

            swarms = customerSwarmRepository.findAllByIdIn(swarmIds);
        }
        return swarms;
    }

    private void removeCustomerWithSwarms(List<CustomerSwarm> swarms, String customerIds, String userAgent) {
        if (swarms != null && !swarms.isEmpty()) {
            swarms.forEach(swarm -> {
                removeCustomersWithSwarmName(swarm.getAppId(), swarm.getName(), customerIds, userAgent);
            });
        }
    }


    @Transactional
    @Override
    public boolean removeCustomers(String appId, String swarmId, String kuickUserId, String customerIds, String userAgent) {
        logger.info("swarm remove customers: appId:{}, swarId:{}, kuickUserId:{}, customerIds:{}",
                appId, swarmId, kuickUserId, customerIds);
        CustomerSwarm swarm = get(swarmId);
        if (isSwarmOwner(appId, kuickUserId, swarm)) {
            String[] custIds = customerIds.split(",");
            KuickUser operationUser = kuickuserUserService.getUserById(kuickUserId);
            Arrays.stream(custIds).forEach(customerId -> {
                int rowUpdate = swarmMemberRepository.deleteByAppIdAndSwarmIdAndCustomerId(appId, swarmId, customerId);
                if(rowUpdate > 0) {
                    sendRemoveCustomerFromSwarmBehaviourLog(customerId, operationUser, swarm, appId, kuickUserId, userAgent);
                    decrementRedisCount(swarmId, rowUpdate);
                }
            });
        } else {
            logger.info("kuickUserId ({})不是分群的创建人或者被共享", kuickUserId);
        }
        return true;
    }

    @Transactional
    @Override
    public boolean swarmMemberTransfer(String appId, String kuickUserId, String fromSwarmId, String toSwarmId, String customerIds) {
        logger.info("swarm transfer customers: appId:{}, kuickUserId:{}, fromSwarId:{}, toSwarId:{}, customerIds:{}",
                appId, kuickUserId, fromSwarmId, toSwarmId, customerIds);
        CustomerSwarm sourceSwarm = get(fromSwarmId);
        CustomerSwarm targetSwarm = get(toSwarmId);
        if (isSwarmOwner(appId, kuickUserId, sourceSwarm) && isSwarmOwner(appId, kuickUserId, targetSwarm)) {
            List<String> custIdList = Arrays.asList(customerIds.split(","));
            //将custIdList中数据移除源分群
            int batchDeleteRows = swarmMemberDao.batchDelete(appId, fromSwarmId, custIdList);
            this.decrementRedisCount(fromSwarmId, batchDeleteRows);
            int batchInsertRows = swarmMemberDao.batchInsert(appId, toSwarmId, custIdList);
            this.incrementRedisCount(toSwarmId, batchInsertRows);
            ;
        } else {
            logger.info("kuickUserId ({})不是分群的创建人或者被共享", kuickUserId);
        }
        return true;
    }

    @Override
    public List<Customer> getSwarmMembers(String appId, String swarmId, String kuickUserId, String customerOwnerId, String groupId, String keyword, int startIndex, int count) {
        logger.info("swarm list customers: appId:{}, kuickUserId:{}, swarId:{}, customerOwnerId:{}, groupId:{}, keyword:{}",
                appId, kuickUserId, swarmId, customerOwnerId, groupId, keyword);
        if (StringUtils.isNotBlank(keyword)) {
            customerOwnerId = "all";
        }
        CustomerSwarm swarm = get(swarmId);
        if (isSwarmOwner(appId, kuickUserId, swarm)) {
            //如果是虚拟分群，需要调用deal-customer-search服务的接口查询
            List<String> kuickUserIds = new ArrayList<>();
            List<KuickUser> managedSales;
            if (StringUtils.isNotBlank(customerOwnerId) && !"all".equals(customerOwnerId)) {
                kuickUserIds.add(customerOwnerId);
            } else {
                managedSales = appMemberPermissionService.getManagedSales(appId, Integer.parseInt(kuickUserId));
                for (KuickUser kuickUser : managedSales) {
                    kuickUserIds.add(String.valueOf(kuickUser.getId()));
                }

            }
            if (swarm.getType() == CustomerSwarmTypeEnum.VIRTUAL.getVal()) {
                if (StringUtils.isNotBlank(swarm.getFilterId())) {
                    String filterId = swarm.getFilterId();
                    dealCustomerSearchComponent.searchCustomer(appId, filterId, startIndex, count);
                } else {
                    logger.info(" 该分群的类型为普通分群，但是没有filterId");
                }
            } else {
                //如果是普通的分群，则直接查询swarm_member表的数据
                return customerService.getSwarmCustomers(swarmId, appId, groupId, kuickUserIds, startIndex, count, keyword);
            }
        } else {
            logger.info("kuickUserId ({})不是分群的创建人或者被共享", kuickUserId);
        }
        return new ArrayList<>();
    }

    @Override
    public List<Customer> getSwarmMemberIds(String appId, String swarmId, String kuickUserId, String customerOwnerId) {
        logger.info("swarm list customers: appId:{}, kuickUserId:{}, swarId:{}, customerOwnerId:{}", appId, kuickUserId, swarmId, customerOwnerId);
        CustomerSwarm swarm = get(swarmId);
        if (isSwarmOwner(appId, kuickUserId, swarm)) {
            //如果是虚拟分群，需要调用deal-customer-search服务的接口查询
            List<String> kuickUserIds = new ArrayList<>();
            List<KuickUser> managedSales;
            if (StringUtils.isNotBlank(customerOwnerId) && !"all".equals(customerOwnerId)) {
                kuickUserIds.add(customerOwnerId);
            } else {
                managedSales = appMemberPermissionService.getManagedSales(appId, Integer.parseInt(kuickUserId));
                for (KuickUser kuickUser : managedSales) {
                    kuickUserIds.add(String.valueOf(kuickUser.getId()));
                }

            }
            if (swarm.getType() == CustomerSwarmTypeEnum.VIRTUAL.getVal()) {
                if (StringUtils.isNotBlank(swarm.getFilterId())) {
                    String filterId = swarm.getFilterId();
                    dealCustomerSearchComponent.searchCustomer(appId, filterId, 0, 1000);
                } else {
                    logger.info(" 该分群的类型为普通分群，但是没有filterId");
                }
            } else {
                logger.info("getSwarmCustomers: appId:{}, kuickUserIds:{}, swarId:{}", appId, kuickUserIds, swarmId);
                //如果是普通的分群，则直接查询swarm_member表的数据
                return customerService.getSwarmCustomers(swarmId, appId, null, kuickUserIds, -1, -1, null);
            }
        } else {
            logger.info("kuickUserId ({})不是分群的创建人或者被共享", kuickUserId);
        }
        return new ArrayList<>();
    }

    private boolean isSwarmCreator(String appId, String kuickUserId, CustomerSwarm swarm) {
        return swarm != null && swarm.getKuickUserId().equals(kuickUserId) && swarm.getAppId().equals(appId);
    }

    private boolean isSwarmOwner(String appId, String kuickUserId, CustomerSwarm swarm) {
        if (swarm == null) {
            return false;
        }
        boolean isSwarmCreator = isSwarmCreator(appId, kuickUserId, swarm);
        if (isSwarmCreator) {
            return true;
        }
        List<SwarmShare> swarmShareList = swarmShareDao.fetchByTargetIdAndSwarmId(appId, kuickUserId, swarm.getId());
        return swarmShareList.size() > 0;
    }

    @Transactional
    @Override
    public void handleMergeCustomer(String appId, String newCustomerId, List<String> oldCustomerIds) {
        List<SwarmMember> members = swarmMemberDao.fetchByCustomerIds(appId, oldCustomerIds);
        swarmMemberDao.batchDelete(members);
        Set<String> swarmIds = new HashSet<>();
        for (SwarmMember swarmMember : members) {
            swarmIds.add(swarmMember.getSwarmId());
            this.decrementRedisCount(swarmMember.getSwarmId(), 1);
        }
        List<String> swarmIdList = new ArrayList<>(swarmIds);
        swarmMemberDao.batchInsert(appId, swarmIdList, newCustomerId);
        for (String swarmId : swarmIdList) {
            this.incrementRedisCount(swarmId, 1);
        }
    }

    @Override
    public void addCustomersWithSwarmNameV2(String appId, String swarmName, String swamOwnerId, String customerIds, String userAgent) {
        CustomerSwarm swarm = getSwarmByAppIdAndName(appId, swarmName);
        DealApp dealAppInfo = dealAppService.getDealAppInfo(appId);
        if (null != dealAppInfo) {
            String kuickUserId = StringUtils.isNotBlank(swamOwnerId) ? swamOwnerId : dealAppInfo.getCreatorId(); //使用项目创建人的ID
            String photoUrl = "https://img-prod.kuick.cn/user/header/guest.png";//使用的是系统默认图片
            //如果swarm不存在，添加swarm
            if (null == swarm) {
                RLock writeLock = null;
                String lockName = "lock:" + appId + ":" + swarmName;
                try {
                    writeLock = redissonClient.getReadWriteLock(lockName).writeLock();
                    writeLock.lock(swarmLockLeaseTime, TimeUnit.SECONDS);
                    logger.info("获取锁成功：{}", lockName);
                    swarm = this.getSwarmByAppIdAndName(appId, swarmName);
                    if (null == swarm) {
                        swarm = this.create(appId, kuickUserId, swarmName, photoUrl, null, 1, null, null);
                        this.putSwarmToRedis(swarm);
                    }
                } finally {
                    //释放锁
                    if (writeLock != null) {
                        writeLock.unlock();
                        logger.info("成功释放锁：{}", lockName);
                    }
                }
            }

            this.addCustomers(appId, swarm.getId(), kuickUserId, customerIds, true, userAgent);
        } else {
            logger.warn("deal app info is not exist");
        }
    }

    /**
     * 往分群添加客户，如果分群不存在，则先创建分群，
     */
    @Override
    public void addCustomersWithSwarmName(String appId, String swarmName, String customerIds, String userAgent) {
        addCustomersWithSwarmNameV2(appId, swarmName, null, customerIds, userAgent);
    }

    /**
     * 删除分群里的客户
     */
    @Override
    public void removeCustomersWithSwarmName(String appId, String swarmName, String customerIds, String userAgent) {
        CustomerSwarm swarm = this.getSwarmByAppIdAndName(appId, swarmName);
        DealApp dealAppInfo = dealAppService.getDealAppInfo(appId);
        if (null != swarm && null != dealAppInfo) {
            String kuickUserId = dealAppInfo.getCreatorId();
            String swarmId = swarm.getId();
            removeCustomers(appId, swarmId, kuickUserId, customerIds, userAgent);
        }
    }

    @Override
    public void swarmCustomerCount() {
        //统计每个分群的count
        int startIndex = 0;
        int count = 10;
        List<String> swarmIds = customerSwarmDao.getIdsWithPage(startIndex, count);
        while (!swarmIds.isEmpty()) {
            Map<String, Integer> countMap = swarmMemberDao.countEverySwarmMember(swarmIds);
            logger.debug("SwarmCustomerCountJob count: {}", countMap);

            for (Map.Entry<String, Integer> entry : countMap.entrySet()) {
                String swarmId = entry.getKey();
                String redisKey = "swarm:customercount:" + swarmId;
                //设置过期时间为两天
                redisService.setex(redisKey, 172800, String.valueOf(entry.getValue()));
            }
            startIndex += count;
            swarmIds = customerSwarmDao.getIdsWithPage(startIndex, count);
        }
    }

    @Override
    public long getMemberCount(String appId, String swarmId) {
        long count;
        String key = "swarm:customercount:" + swarmId;
        String val = redisService.get(key);
        if (StringUtils.isNotBlank(val)) {
            count = Long.parseLong(val);
        } else {
            count = swarmMemberDao.getCountBySwarmId(swarmId);
            redisService.setex(key, 172800, String.valueOf(count));
        }
        return count;
    }

    private CustomerSwarm getSwarmByAppIdAndName(String appId, String swarmName) {
        //从缓存查询
        String redisKey = "swarm:" + appId + ":" + swarmName;
        String swarmStr = redisService.get(redisKey);
        CustomerSwarm swarm = null;

        if (null != swarmStr && !"null".equals(swarmStr)) {
            swarm = JsonUtil.fromJson(swarmStr, CustomerSwarm.class);
        }

        //从数据库查询
        if (null == swarm) {
            CustomerSwarm ex = new CustomerSwarm();
            ex.setAppId(appId);
            ex.setName(swarmName);
            ex.setStatus(CustomerSwarm.Status.UNDELETE.getVal());
            swarm = customerSwarmRepository.findOne(Example.of(ex));
            if (null != swarm) {
                this.putSwarmToRedis(swarm);
            }
        }

        return swarm;
    }

    private void putSwarmToRedis(CustomerSwarm swarm) {
        if (null == swarm) {
            return;
        }

        String redisKey = "swarm:" + swarm.getAppId() + ":" + swarm.getName();
        redisService.setex(redisKey, 86400, JsonUtil.toJson(swarm));
    }

    private void removeSwarmFromRedis(CustomerSwarm swarm) {
        if (null == swarm) {
            return;
        }
        String redisKey = "swarm:" + swarm.getAppId() + ":" + swarm.getName();
        redisService.delete(redisKey);
    }

    private void incrementRedisCount(String swarmId, long increment) {
        String redisKey = "swarm:customercount:" + swarmId;
        if (redisService.exists(redisKey)) {
            redisService.incr(redisKey, increment);
        }
    }

    private void decrementRedisCount(String swarmId, long increment) {
        String redisKey = "swarm:customercount:" + swarmId;
        if (redisService.exists(redisKey)) {
            redisService.decr(redisKey, increment);
        }
    }

    private CustomerSwarm get(String id) {
        return customerSwarmRepository.findOne(id);
    }

    /**
     * 将扩展字段按照分群元数据设置到分群属性中
     * @param appId 项目ID
     * @param swarm 分群
     * @param data 请求中的参数和值
     * @return 设置好扩展字段的分群
     */
    private CustomerSwarm preHandle(String appId, CustomerSwarm swarm, Map<String, String> data) {
        List<CustomerSwarmMeta> metas = customerSwarmMetaService.findByAppId(appId);

        if(metas == null || metas.isEmpty()){
            return swarm;
        }

        if(data == null || data.isEmpty()){
            data = new HashMap<>(1);
        }

        String val, slot;
        for(CustomerSwarmMeta meta : metas){
            val = data.get(meta.getName());
            slot = meta.getSlot();
            swarm.setBySlot(slot, val);
        }
        return swarm;
    }

    /**
     * 将分群扩展字段按照元数据来封装
     * @param swarm 分群
     * @param metas 元数据
     * @return 设置好扩展字段的分群
     */
    private Map<String, Object> postHandle(CustomerSwarm swarm, List<CustomerSwarmMeta> metas) {
        if(swarm == null){
            return null;
        }

        Map map = swarm.toMap();
        if(metas == null || metas.isEmpty()){
            return map;
        }

        String val, slot;
        for(CustomerSwarmMeta meta : metas){
            slot = meta.getSlot();
            val = swarm.getBySlot(slot);
            map.put(meta.getName(), StringUtils.isNotEmpty(val) ? val : "");
        }
        return map;
    }
}
