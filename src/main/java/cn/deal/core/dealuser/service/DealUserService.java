package cn.deal.core.dealuser.service;

import cn.deal.component.FileComponent;
import cn.deal.component.RedisService;
import cn.deal.component.domain.DealUserDomainEvent;
import cn.deal.component.domain.FileUploaded;
import cn.deal.component.kuick.domain.ResponseVO;
import cn.deal.component.messaging.producer.DealuserDomainEventProducer;
import cn.deal.component.utils.AESUtils;
import cn.deal.component.utils.AssertUtils;
import cn.deal.component.utils.HttpClientUtils;
import cn.deal.component.utils.OptionUtils;
import cn.deal.core.app.service.DealAppMemberService;
import cn.deal.core.app.service.DealAppService;
import cn.deal.core.customer.dao.CustomerDao;
import cn.deal.core.customer.domain.Customer;
import cn.deal.core.customer.domain.SalesCustomer;
import cn.deal.core.customer.engine.CustomerEngine;
import cn.deal.core.customer.service.CustomerService;
import cn.deal.core.customer.service.MergedCustomerService;
import cn.deal.core.customer.service.SalesCustomerService;
import cn.deal.core.dealuser.dao.DealUserDao;
import cn.deal.core.dealuser.domain.CustomerLinkDealUser;
import cn.deal.core.dealuser.domain.DealUser;
import cn.deal.core.dealuser.repository.DealUserRepository;
import cn.deal.core.dealuser.service.filters.DealUserFromFilter;
import cn.deal.core.meta.domain.CustomerMetaData;
import cn.deal.core.meta.service.CustomerMetaDataService;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Service
@CacheConfig(cacheManager="redis", cacheNames="dealuser")
public class DealUserService {

	private Logger logger = LoggerFactory.getLogger(DealUserService.class);
	private static final String PREFIX_FOR_ENCRYPTED_PHONE = "encrypt-data:";

	@Autowired
	private DealUserRepository dealUserRepository;
	@Autowired
	private DealUserDao dealUserDao;
	@Autowired
	private CustomerDao customerDao;
	@Autowired
	private RedisService redisService;
	@Autowired
	private FileComponent fileComponent;
	@Autowired
	private CustomerService customerService;
	@Autowired
	private DealAppService dealAppService;
	@Autowired
	private CustomerLinkDealUserService customerLinkDealUserService;
	@Autowired
	private DealuserDomainEventProducer dealuserDomainEventProducer;
	@Autowired
	private CustomerEngine customerEngine;
	@Autowired
	private CustomerMetaDataService customerMetaDataService;
	@Autowired
	private SalesCustomerService salesCustomerService;

	@Autowired
	private DealAppMemberService dealAppMemberService;

	@Autowired
	private MergedCustomerService mergedCustomerService;

	@Autowired
	private RedissonClient redissonClient;

	@Value("${default.photopath}")
	private String defaultPhotoPath;
	@Value("${default.salt}")
	private String salt;

	/**
	 * 创建DealUser
	 *
	 * @param appId
	 * @param dealUser
	 *
	 * @return
	 */
	public DealUser createDealUser(String appId, DealUser dealUser) {
		AssertUtils.assertTrue(dealUser.getId()==null, "dealUser's id must be null");

		// 补全信息
		dealUser.setId(UUID.randomUUID().toString());
		dealUser.setAppId(appId);
		dealUser.setPassword(saltMd5(dealUser.getPassword(), salt));
		dealUser.setStatus(0);
		dealUser.setIsNamed(0);
		dealUser.setCreateTime(new Date());

		handleUserPhotoUrl(dealUser);


		// 构造领域事件
		DealUserDomainEvent event = new DealUserDomainEvent();
		event.setEventType(DealUserDomainEvent.CREATE);
		event.setId(UUID.randomUUID().toString());
		event.setBody(dealUser);
		event.setDomainId(dealUser.getId());

		// 保存&发送Kafka
		dealUser = dealUserRepository.saveAndFlush(dealUser);
		dealuserDomainEventProducer.send(event);

		return dealUser;
	}

	/**
	 * 更新DealUser
	 *
	 * @param dealUserId
	 * @param dealUser
	 */
	public void updateDealUser(String dealUserId, DealUser dealUser) {
		AssertUtils.assertTrue(dealUser.getId()!=null, "dealUser's id must not be null");

		// 使用DAO从数据库查询老数据
		DealUser oldBody = dealUserDao.getDealUserById(dealUserId);

		// 构造领域事件
		DealUserDomainEvent event = new DealUserDomainEvent();
		event.setEventType(DealUserDomainEvent.UPDATE);
		event.setId(UUID.randomUUID().toString());
		event.setDomainId(dealUser.getId());
		event.setBody(dealUser);
		event.setOldBody(oldBody);

		dealUserRepository.saveAndFlush(dealUser);
		dealuserDomainEventProducer.send(event);
	}

	// ------------------------------------------------------------------------
	/**
	 * 根据客户ID查询DealUser
	 *
	 * @param customerId
	 * @param justIds
	 * @return
	 */
	public List<?> findDealUserByCustomerId(String customerId,Integer justIds){
		if(justIds!=null && justIds==1){
			return customerLinkDealUserService.getDealUserIdsByCustomer(customerId);
		}

		List<String> ids=customerLinkDealUserService.getDealUserIdsByCustomerId(customerId);
		return dealUserRepository.findAll(ids);
	}

	/**
	 * 根据dealUserIDs查询DealUser
	 *
	 * @param dealUserIds
	 * @return
	 */
	public List<DealUser> findDealUserByIds(List<String> dealUserIds){
		return dealUserDao.getDealUsersByIds(dealUserIds);
	}

	/**
	 * 根据dealUserIDs查询DealUser
	 *
	 * @param appId
	 * @param dealUserIds
	 * @return
	 */
	public List<DealUser> findDealUserByIdsWithAppId(String appId, List<String> dealUserIds){
		return dealUserDao.getDealUsersByIdsWithAppId(appId, dealUserIds);
	}

	/**
	 * 根据客户ID分页查询Dealuser
	 *
	 * @param appId
	 * @param customerId
	 * @param startIndex
	 * @param count
	 * @return
	 */
	@Cacheable(key="'get_deal_user_by_customer:' + #p0 + ':' + #p1 + ':' + #p2 + ':' + #p3", cacheNames="query")
	public List<DealUser> findTinyDealUserByCustomerIdAndPage(String appId, String customerId, int startIndex, int count){
		return dealUserDao.findTinyDealUserByCustomerIdAndPage(appId, customerId, startIndex, count);
	}

	/**
	 * 分页查询客户
	 *
	 * @param appId
	 * @param customerId
	 * @param startIndex
	 * @param count
	 * @return
	 */
	public List<DealUser> findDealUserByCustomerIdAndPage(String appId, String customerId, int startIndex, int count){
		return dealUserDao.findDealUserByCustomerIdAndPage(appId, customerId, startIndex, count);
	}

	/**
	 * 注册DealUser
	 *
	 * @param dealUser
	 * @param appKey
	 * @return
	 */
	//@CachePut(key="'dealuser-v2:' + #result.id")
	public DealUser registerDealUser(DealUser dealUser, String appKey) {
		logger.info("register.dealUser: {}, appKey:{}", dealUser, appKey);

		dealUser.setAppId(appKey);

		RLock lock = null;
		try {
			Pair<String, String> field = getNotNullFieldName(dealUser);
			if (field != null) {
				String fieldName = field.getLeft();
				String fieldValue = field.getRight();

				String lockKey = "register:deal-user-key:" + fieldName + ":deal-user-value:" + fieldValue;
				lock = redissonClient.getReadWriteLock(lockKey).writeLock();
				lock.lock(5, TimeUnit.SECONDS);

				String key = "deal_user_with_" + fieldName + ":" + appKey + ":" + fieldValue;
				logger.info("query_dealuser_from_redis:" + key);
				String json = redisService.get(key);
				logger.info("query_dealuser_from_redis_result: {}", json);

				if (StringUtils.isBlank(json)) {
					List<DealUser> list = dealUserDao.findAllByOption(dealUser);
					logger.info("list -> {}", list);

					if (list != null && list.size() > 0) {
						dealUser = list.get(0);
					} else {
						dealUser = createDealUser(appKey, dealUser);
					}
					redisService.setex(key, 20, new Gson().toJson(dealUser));
				} else {
					dealUser = new Gson().fromJson(json, DealUser.class);
				}
			} else {
				dealUser = createDealUser(appKey, dealUser);
			}
		} catch (Exception e) {
			logger.error("error_in_register_deal_user", e);
			throw e;
		} finally {
			if (lock != null) {
				lock.unlock();
			}
		}

		return dealUser;
	}

	@Async
	public void handleUserPhotoUrl(DealUser dealUser) {
		try {
			if (StringUtils.isNotBlank(dealUser.getPhotoURL()) && !StringUtils.equals(dealUser.getPhotoURL(), this.defaultPhotoPath)) {
				String imgName = UUID.randomUUID().toString() + ".jpg";
				String localPath = System.getProperty("java.io.tmpdir") + "/" + imgName;

				String phoneUrl = StringUtils.replace(dealUser.getPhotoURL(), "https://wx.qlogo.cn/g", "http://wx.qlogo.cn");
				HttpClientUtils.getInstance().httpDownloadFile(phoneUrl, localPath, null, null);

				logger.info("handdleUserPhoto.dealUser: {}, localPath: {}", dealUser, localPath);
				FileUploaded uploaded = fileComponent.uploadImagesByDealUser(dealUser.getAppId(), dealUser.getId(), new File(localPath));
				logger.info("uploadFile.result.map: {}", uploaded);
				dealUser.setPhotoURL(uploaded.getUrl());

			} else {
				dealUser.setPhotoURL(this.defaultPhotoPath);
			}

			dealUserRepository.saveAndFlush(dealUser);
		} catch (Exception e) {
			logger.error("error_in_handle_dealuser_photourl", e);
		}
	}

	private Pair<String, String> getNotNullFieldName(DealUser dealUser) {
		Pair<String, String> field = null;

		if (StringUtils.isNotBlank(dealUser.getOpenid())) {
			field = Pair.of("openid", dealUser.getOpenid());
		} else if (StringUtils.isNotBlank(dealUser.getUnionid())) {
			field = Pair.of("unionid", dealUser.getUnionid());
		} else if (StringUtils.isNotBlank(dealUser.getDeviceId())) {
			field = Pair.of("deviceId", dealUser.getDeviceId());
		} else if (StringUtils.isNotBlank(dealUser.getAppUserId())) {
			field = Pair.of("appUserId", dealUser.getAppUserId());
		} else if (StringUtils.isNotBlank(dealUser.getPhoneNum())) {
			field = Pair.of("phoneNum", dealUser.getPhoneNum());
		}

		return field;
	}

	private String saltMd5(String password, String salt) {
		if (StringUtils.isNotBlank(password)){
			return DigestUtils.md5Hex(DigestUtils.md5Hex(password) + salt);
		}

		return "";
	}

	/**
	 * 获取当个DealUser
	 *
	 * @param dealUserId
	 * @return
	 */
	//@Cacheable(key="'dealuser-v2:' + #p0")
	public DealUser getDealUserById(String dealUserId) {
		return dealUserRepository.findOne(dealUserId);
	}

	/**
	 * 根据dealUserID查询DealUser， 带上客户
	 *
	 * @param dealUserId
	 * @return
	 */
	public DealUser getDealUserByIdWithCustomer(String dealUserId) {
		DealUser du = this.getDealUserById(dealUserId);

		if (du!=null && du.isNamed()) {
			List<Customer> cus = customerDao.findCustomerByDealUserId(du.getId());

			if (cus!=null && cus.size()>0) {
				du.setCustomer(cus.get(0));
			} else {
				du.setIsNamed(0); //如果没有找到客户，named返回false
			}
		}

		return du;
	}


	/**
	 * 查询DealUser
	 *
	 * @param appId
	 * @param id
	 * @param openId
	 * @param unionId
	 * @param deviceId
	 * @param appUserId
	 * @param customerId
	 * @return
	 */
	public DealUser getDealUserByKey(String appId, String id, String openId, String unionId, String deviceId, String appUserId, String customerId) {
		AssertUtils.notAllBlank(Arrays.asList(id, openId, unionId, deviceId, appUserId, customerId), "param is null");

		DealUser option = new DealUser();
		option.setAppId(appId);

		option.setId(id);
		option.setOpenid(openId);
		option.setUnionid(unionId);
		option.setDeviceId(deviceId);
		option.setDeviceId2(deviceId);
		option.setAppUserId(appUserId);

		DealUser dealUser = dealUserDao.findOneByOption(option);
		logger.info("getDealUserByKey.dealUser: {}", dealUser);

		if (dealUser != null) {
			handleSalesCustomer(dealUser);
			logger.info("get.dealUser: {}", dealUser);
		}
		return dealUser;
	}

	/**
	 * 绑定客户
	 *
	 * @param dealUserId
	 * @param customerId
	 * @return
	 */
	//@CacheEvict(key="'dealuser-v2:' + #p0")
	public DealUser bindCustomer(String dealUserId, String customerId) {
		AssertUtils.notNull(dealUserId, "dealUserId不能为空");
		AssertUtils.notNull(customerId, "customerId不能为空");

		Customer customer = customerService.getCustomerById(customerId);
		DealUser dealUser = this.getDealUserById(dealUserId);

		if (customer!=null && dealUser!=null) {
			dealUser.setIsNamed(1);

			dealUser.setName(customer.getName());
			dealUser.setTitle(customer.getTitle());
			dealUser.setPhone(customer.getPhone());
			dealUser.setPhoneNum(customer.getPhone());
			dealUser.setCompany(customer.getCompany());

			if (StringUtils.isNotBlank(customer.getHeadportraitUrl())) {
				dealUser.setPhotoURL(customer.getHeadportraitUrl());
			}

			updateDealUser(dealUser.getId(), dealUser);
			CustomerLinkDealUser link = customerLinkDealUserService.getAndCreateLink(customer, dealUser);

			if (link != null) {
				// 写入customer
				dealUser.setCustomer(customer);
			}
		}

		return dealUser;
	}



	/**
	 * 根据客户基本信息实名化客户
	 *
	 * @param dealUserId
	 * @param name
	 * @param title
	 * @param email
	 * @param phone
	 * @param company
	 * @param exts
	 * @param opts
	 * @return
	 */
	//@CacheEvict(key="'dealuser-v2:' + #p0")
	public DealUser namedDealUser(String dealUserId, String name, String title, String email, String phone,
								  String company, Map<String, String> exts, Map<String, Object> opts) {
		DealUser dealUser = null;
		String lockKey = "named:" + dealUserId;
		RLock lock = null;
		try {
			lock = redissonClient.getReadWriteLock(lockKey).writeLock();
			lock.lock(5, TimeUnit.SECONDS);

			logger.info("namedDealUser start, param is : {}, {}, {}, {}, {}, {}, {}, {}", dealUserId, name, title, email, phone, company, exts, opts);
			dealUser = this.getDealUserByIdWithCustomer(dealUserId);
			AssertUtils.notNull(dealUser, "dealUser不存在");
			if (opts.get("updateCustomer") == null || StringUtils.isBlank(opts.get("updateCustomer").toString())) {
				opts.put("updateCustomer", "1");
			}

			// 组装客户数据
			Map<String, String> data = this.composeData(dealUser.getAppId(), name, title, email, phone, company, exts);
			logger.info("namedDealUser.data: {}", data);

			Customer customer = findExistCustomer(dealUser.getAppId(), phone, email);
			logger.info("namedDealUser findExistCustomer result: {}", customer);

			logger.info("handleAutoMerge.params1: {}, {}", !dealUser.isNamed(), dealUser.getCustomer() != null ? dealUser.getCustomer().getId() : null);

			if (!dealUser.isNamed()) {
				if (customer == null) {
					opts.put(DealUserFromFilter.DEAL_USER_ID_KEY, dealUser.getId());
					customer = customerEngine.handleCreate(dealUser.getAppId(), data, opts);
				} else {
					if (opts.get("updateCustomer").toString().length() == 1 && "123".contains(opts.get("updateCustomer").toString())) {
						customer = customerEngine.handleUpdate(dealUser.getAppId(), customer.getId(), data, opts);
					}
				}

				// 关联客户和DealUser
				dealUser = this.bindCustomer(dealUserId, customer.getId());
			} else {
				if (customer == null) {
					customer = customerService.getCustomerById(dealUser.getCustomer().getId());

					if (opts.get("updateCustomer").toString().length() == 1 && "123".contains(opts.get("updateCustomer").toString())) {
						this.customerEngine.handleUpdate(customer.getAppId(), customer.getId(), data, opts);
					}
				} else {
					String mainCustomerId = dealUser.getCustomer() != null ? dealUser.getCustomer().getId() : null;
					logger.info("handleAutoMerge mainCustomerId: {}", mainCustomerId);

					if (StringUtils.isNotBlank(mainCustomerId)) {
						List<String> customerIds = findExistCustomers(dealUser.getAppId(), phone, email).stream()
								.map(Customer::getId)
								.filter(id -> !id.equals(mainCustomerId)) // 排除dealUser的客户
								.collect(Collectors.toList());

						logger.info("handleAutoMerge customerIds: {}", customerIds);

						if (autoMerge(dealUser, customerIds)) {
							String[] ids = customerIds.toArray(new String[customerIds.size()]);
							String mainKuickUserId = (String)opts.get("kuick_user_id");

							logger.info("handleAutoMerge.start: mainCustomerId: {}, ids:{}", mainCustomerId, ids);
							customer = customerEngine.handleMerge(dealUser.getAppId(), ids, mainCustomerId, mainKuickUserId, data, opts);
							logger.info("handleAutoMerge.result: {}", customer);
						}
					}
				}
			}

			handleAutoTransfer(opts, customer, dealUser);
			handleSalesCustomer(dealUser);

			logger.info("namedDealUser.result: {}", dealUser);
		} finally {
			if (lock != null) {
				lock.unlock();
			}
		}

		return dealUser;
	}

	private boolean autoMerge(DealUser dealUser, List<String> customerIds) {
		Customer customer = dealUser.getCustomer();
		logger.info("autoMerge.customer: {}, {}", customer, customerIds);

		if (customer == null) {
			return false;
		}

		if (customerIds == null || customerIds.isEmpty()) {
			return false;
		}

		if (StringUtils.isBlank(dealUser.getUnionid())) {
			return false;
		}
		
		return true;
	}

	private void handleAutoTransfer(Map<String, Object> opts, Customer customer, DealUser dealUser) {
		try {
			if (OptionUtils.equals(opts, "auto_transfer", "1")
					&& OptionUtils.isNotBlank(opts, "kuick_user_id")) {
				String source = customer.getKuickUserId();
				String target = (String)opts.get("kuick_user_id");

				logger.info("named.autoTransfer.params: {}, {}, {}", source, target, customer);
				Assert.hasText(source, "handleAutoTransfer.source is null");
				Assert.hasText(target, "handleAutoTransfer.target is null");

				if (!StringUtils.equals(source, target)) {
					ResponseVO resp = salesCustomerService.customerTransfer(customer.getAppId(), "", source, target, customer.getId()
							, false, null, 1);
					Assert.notNull(resp, "handleAutoTransfer.resp is null");
					Assert.isTrue(resp.getStatus() == ResponseVO.Status.OK.getVal(), "error in handleAutoTransfer");

					logger.info("named.autoTransfer set kuickUserId: {}", target);

					customer.setKuickUserId(target);
					dealUser.setCustomer(customer);
				}
			}

		} catch (Exception e) {
			logger.info("error in handleAutoTransfer. {}", e.getMessage());
		}
	}

	private void handleSalesCustomer(DealUser dealUser) {
		if (dealUser.getCustomer() == null || StringUtils.isBlank(dealUser.getCustomer().getKuickUserId())) {
			logger.info("handle.sales.customer: {}", dealUser.getCustomer());

			if (dealUser.getCustomer() == null) {
				Customer customer = customerLinkDealUserService.getCustomerByDealUserId(dealUser.getId());
				dealUser.setCustomer(customer);
			}

			if (dealUser.getCustomer() != null) {
				SalesCustomer salesCustomer = salesCustomerService.findSalesCustomerByAppIdAndCustomerId(dealUser.getAppId(), dealUser.getCustomer().getId());
				if (salesCustomer != null) {
					dealUser.getCustomer().setKuickUserId(salesCustomer.getKuickUserId());
				}
			} else {
				logger.warn("not found customer with dealUser: {}", dealUser);
			}
		}
	}

	private List<Customer> findExistCustomers(String appId, String phone, String email) {
		List<Customer> customers = Lists.newArrayList();

		List<String> metaTypes = findMetaTypes(appId);

		// 根据手机号查询
		if (StringUtils.isNotBlank(phone) && metaTypes.contains("phone")) {
			List<Customer> result = customerService.exactMatch(appId, phone, null, 0, 1);
			if (result != null && result.size() > 0) {
				customers.addAll(result);
			}
		}

		// 根据邮箱查询
		if (StringUtils.isNotBlank(email) && metaTypes.contains("email")) {
			List<Customer> result = customerService.exactMatch(appId, null, email, 0, 1);
			if (result != null && customers.size() > 0) {
				customers.addAll(result);
			}
		}

		return customers;
	}

	private Customer findExistCustomer(String appId, String phone, String email) {
		List<Customer> customers;

		List<String> metaTypes = findMetaTypes(appId);

		// 根据手机号查询
		if (StringUtils.isNotBlank(phone) && metaTypes.contains("phone")) {
			customers = customerService.exactMatch(appId, phone, null, 0, 1);
			if (customers!=null && customers.size()>0) {
				return customers.get(0);
			}
		}

		// 根据邮箱查询
		if (StringUtils.isNotBlank(email) && metaTypes.contains("email")) {
			customers = customerService.exactMatch(appId, null, email, 0, 1);
			if (customers!=null && customers.size()>0) {
				return customers.get(0);
			}
		}

		return null;
	}

	private List<String> findMetaTypes(String appId) {
		List<String> metaTypes;
		try {
			metaTypes = customerMetaDataService.getCustomerMetas(appId).stream()
					.filter(meta ->
							(StringUtils.equals(meta.getType(), "phone") || StringUtils.equals(meta.getType(), "email")) && meta.getUnique())
					.map(CustomerMetaData::getType)
					.collect(Collectors.toList());

		} catch (Exception e) {
			metaTypes = Lists.newArrayList();
		}
		return metaTypes;
	}

	private Map<String, String> composeData(String appid, String name, String title, String email, String phone, String company, Map<String, String> exts) {
		logger.info("composeData.exts: {}", exts);

		phone = this.parsePhone(appid, phone);

		Map<String, String> data = new HashMap<>();
		data.put("name", name);
		data.put("title", title);
		data.put("email", email);
		data.put("phone", phone);
		data.put("company", company);

		// 合并扩展字段
		if (exts!=null && exts.size()>0) {
			data.putAll(exts);
		}
		
		return data;
	}

	private String parsePhone(String appid, String source){
		if(StringUtils.isBlank(source) || !source.startsWith(PREFIX_FOR_ENCRYPTED_PHONE)){
			logger.info("parsePhone. source is blank or not start with '{}', source: {}", PREFIX_FOR_ENCRYPTED_PHONE, source);
			return source;
		}

		source = source.replace(PREFIX_FOR_ENCRYPTED_PHONE, "");
		String[] array = source.split(",");

//        格式: encrypt-data:AES,${iv},${手机号加密字符串}
		if(array.length != 3){
			logger.warn("parsePhone. failed. wrong pattern: {}", source);
			return source;
		}

		String aesSecretKey = dealAppService.getSecret(appid);
		if(StringUtils.isBlank(aesSecretKey)){
			logger.warn("parsePhone. appSecret is null. appid: {}", appid);
			return source;
		} else if(aesSecretKey.length() != 36){
			logger.warn("parsePhone. wrong pattern of secret: {}, appid: {}", aesSecretKey, appid);
			return source;
		} else {
			aesSecretKey = aesSecretKey.substring(0, aesSecretKey.length()-4);
		}

		String encrypted = array[2],
				iv = array[1],
				phone = null;
		try {
			phone = AESUtils.decodeStr(encrypted, aesSecretKey, iv);
		} catch (Exception e) {
			logger.error("parsePhone. error. source: {}, msg: {}, stacktrace: {}", encrypted, e.getMessage(), e.getStackTrace());
		}
		logger.info("parsePhone. source: {}, result: {}", source, phone);
		return phone;
	}

	/**
	 * 使用一个DealUser实名化另一个
	 *
	 * @param dealUser
	 * @return
	 */
	protected List<DealUser> namedOtherDealUsers(DealUser dealUser, List<DealUser> otherDealUsers, Map<String, Object> opts) {
		AssertUtils.notNull(dealUser, "dealUser不存在");
		AssertUtils.assertTrue(dealUser.isNamed(), "dealUser没有实名化");

		// 获取已经实名化的客户
		Customer customer = dealUser.getCustomer();
		List<String> toMergeIds = new ArrayList<>();

		// 关联客户
		for(int i=0; i<otherDealUsers.size(); i++) {
			DealUser other = otherDealUsers.get(i);

			if (other!=null) {
				if (!other.isNamed()) {
					// 关联客户和DealUser
					this.bindCustomer(other.getId(), customer.getId());
				} else {
					// 合并客户
					if (canMerge(customer, other.getCustomer())) {
						toMergeIds.add(other.getCustomer().getId());
					}
				}
			}
		}

		// 合并客户
		if (toMergeIds!=null && toMergeIds.size()>0) {
			Map<String, String> data = new HashMap<String, String>();

			this.customerEngine.handleMerge(dealUser.getAppId(), toMergeIds.toArray(new String[toMergeIds.size()]),
					customer.getId(), null, data, opts);
		}

		// 返回合并后的DealUser
		List<DealUser> rets = new ArrayList<DealUser>();

		for(int i=0; i<otherDealUsers.size(); i++) {
			DealUser other = this.getDealUserByIdWithCustomer(otherDealUsers.get(0).getId());

			if (other!=null) {
				rets.add(other);
			}
		}

		return rets;
	}


	/**
	 * 是否可以自动合并
	 *
	 * @param c1
	 * @param c2
	 * @return
	 */
	private boolean canMerge(Customer c1, Customer c2) {
		if (c1 == null || c2 == null) {
			return false;
		}
		// 如果两个客户不相同，并且手机号和邮箱匹配则合并
		if (!StringUtils.equals(c1.getId(), c2.getId())
				&& (
				StringUtils.equals(c1.getPhone(), c2.getPhone())
						|| StringUtils.equals(c1.getEmail(), c2.getEmail()
				))) {
			return true;
		}

		return false;
	}

	/**
	 * 根据其他已经实名的Dealuser实名化DealUser
	 *
	 * @param dealUserId
	 * @param dealUserIds
	 * @param name
	 * @param title
	 * @param email
	 * @param phone
	 * @param company
	 * @param exts
	 * @param opts
	 * @return
	 */
	public List<DealUser> namedDealUserWithDealUserIds(String dealUserId, String[] dealUserIds, String name, String title,
													   String email, String phone, String company, Map<String, String> exts, Map<String, Object> opts) {
		AssertUtils.notNull(dealUserId, "dealUserId can not be null");
		AssertUtils.notNull(dealUserIds, "dealUserIds can not be null");
		AssertUtils.assertTrue(dealUserIds.length > 0, "dealUserIds length must greater than 0");

		List<DealUser> result = Lists.newArrayList();
		DealUser dealUser = this.namedDealUser(dealUserId, name, title, email, phone, company, exts, opts);
		if (dealUser != null) {
			result.add(dealUser);

			List<DealUser> others = Lists.newArrayList();
			for (String otherDealUserId : dealUserIds) {
				DealUser other = this.getDealUserByIdWithCustomer(otherDealUserId);

				if (other != null) {
					others.add(other);
				}
			}

			// 实名化其他DealUsers
			others = namedOtherDealUsers(dealUser, others, opts);
			if (others != null) {
				result.addAll(others);
			}
		}

		return result;
	}

	/**
	 * 根据appUserId实名化DealUser
	 *
	 * @param dealUserId
	 * @param appUserId
	 * @param name
	 * @param title
	 * @param email
	 * @param phone
	 * @param company
	 * @param exts
	 * @param opts
	 * @return
	 */
	public List<DealUser> namedDealUserWithAppUserId(String dealUserId, String appUserId, String name, String title,
													 String email, String phone, String company, Map<String, String> exts, Map<String, Object> opts) {
		AssertUtils.notNull(dealUserId, "dealUserId can not be null");
		AssertUtils.notNull(appUserId, "appUserId can not be null");

		List<DealUser> result = new ArrayList<>();

		DealUser dealUser = this.namedDealUser(dealUserId, name, title, email, phone, company, exts, opts);
		if (dealUser!=null) {
			result.add(dealUser);

			DealUser other = this.getDealUserByKey(dealUser.getAppId(), null, null, null, null, appUserId, null);
			if (other!=null) {
				List<DealUser> others = namedOtherDealUsers(dealUser, Collections.singletonList(other), opts);

				if (others!=null) {
					result.addAll(others);
				}
			}
		}

		return result;
	}

	/**
	 * 更新DealUser的信息
	 *
	 * @param du
	 */
	//@CacheEvict(key="'dealuser-v2:' + #p0.id")
	public void updateDealUser(DealUser du) {
		AssertUtils.notNull(du, "dealUser is null");
		AssertUtils.notNull(du.getId(), "dealUser id is null");

		dealUserRepository.saveAndFlush(du);
	}

	public List<DealUser> batch(String appId, DealUser dealUser) {
		List<DealUser> dealUsers = Lists.newArrayList();

		while (true) {
			Pair<String, String> pair = getNotNullFieldName(dealUser);
			if (pair == null) {
				break;
			}

			dealUsers.add(handleRegister(appId, dealUser, pair));
		}

		return dealUsers;
	}

	private DealUser handleRegister(String appId, DealUser dealUser, Pair<String, String> pair) {
		DealUser obj = buildDealUser(dealUser, pair);
		logger.info("handleRegister.obj: {}", obj);
		return registerDealUser(obj, appId);
	}

	private DealUser buildDealUser(DealUser dealUser, Pair<String, String> pair) {
		Field field = ReflectionUtils.findField(DealUser.class, pair.getLeft());
		field.setAccessible(true);
		Object value = ReflectionUtils.getField(field, dealUser);

		DealUser result = new DealUser();
		BeanUtils.copyProperties(dealUser, result, "appUserId", "unionid", "openid", "deviceId", "phoneNum");

		ReflectionUtils.setField(field, dealUser, null);
		ReflectionUtils.setField(field, result, value);
		return result;
	}

}
