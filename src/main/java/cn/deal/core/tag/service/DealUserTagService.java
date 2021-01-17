package cn.deal.core.tag.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.deal.component.RedisService;
import cn.deal.component.config.TagProperties;
import cn.deal.core.dealuser.service.CustomerLinkDealUserService;
import cn.deal.core.tag.dao.DealUserTagDao;
import cn.deal.core.tag.domain.CustomerTag;
import cn.deal.core.tag.domain.DealUserTag;
import cn.deal.core.tag.repository.DealUserTagRepository;

@Service
public class DealUserTagService {
	private Logger log = LoggerFactory.getLogger(DealUserTagService.class);
	@Autowired
	private DealUserTagRepository dealUserTagRepository;
	
	@Autowired
	private DealUserTagDao dealUserTagDao;
	
	@Autowired
	private RedisService redisUtil;
	
	@Autowired
	private TagProperties tagProperties;
	
	@Autowired
	private  CustomerLinkDealUserService customerLinkDealUserService;
	
	public DealUserTag addDealUserTag(String id,String appId,String  dealUserId, String tag, Date createdAt){
		DealUserTag dealUserTag=new DealUserTag(id, appId, dealUserId, tag, createdAt);
		
		return dealUserTagRepository.save(dealUserTag);
	}

	public List<CustomerTag> getCustomerTags(String appId, String dealUserIds, int timeRangeType) {
		List<CustomerTag> result = new ArrayList<CustomerTag>();
		Set<String> keySet = new HashSet<String>();
		
		SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar cl = Calendar.getInstance();
        cl.setTime(new Date());
        
        String startTime = "";
		String endTime = df.format(new Date());
		if(timeRangeType == 2){
	        cl.add(Calendar.MONTH, -3);  
	        startTime = df.format(cl.getTime());
	    } else if(timeRangeType == 3){
	    	cl.add(Calendar.MONTH, -1);  
	        startTime = df.format(cl.getTime());
	    } else if(timeRangeType == 4){
	    	cl.add(Calendar.DAY_OF_MONTH, -7);  
	        startTime = df.format(cl.getTime());
	    }
		
		String[] colors = tagProperties.getColor().split(",");
		
		String[] dealUserIdArr = dealUserIds.split(",");
		
		for(String dealUserId : dealUserIdArr){
			String redisKey = "deal_user_" + dealUserId + "_" + timeRangeType;
			String value = redisUtil.get(redisKey);
			System.out.println("redis value is "+value);
			if(value == null || "".equals(value)){
				StringBuffer redisValue = new StringBuffer("");
				
				List<String> tags = dealUserTagDao.getTagsByDealUserId(dealUserId, startTime, endTime);
				
				if(tags != null){
					for(String tag : tags){
						redisValue.append(tag).append(",");
					}
					if(redisValue.length()>0){
						value = redisValue.substring(0, redisValue.length()-1);
						redisUtil.setex(redisKey, 5 * 60 , value);
					}
				}
			}
			
			if(value != null && !"".equals(value)){
				String[] values = value.split(",");
				for(String key_value : values){
					String key = key_value.split("_")[0];
					int val = Integer.parseInt(key_value.split("_")[1]);
					if(keySet.contains(key)){
						for(CustomerTag cTag : result){
							if(cTag.getTag().equals(key)){
								cTag.setNum(cTag.getNum()+val);
								break;
							}
						}
					}else{
						CustomerTag cTag = new CustomerTag();
						cTag.setTag(key);
						cTag.setNum(val);
						
						int keyHash = Math.abs(key.hashCode());
						int index = keyHash % colors.length;
						cTag.setColor(colors[index]);
						result.add(cTag);
						
						keySet.add(key);
					}
				}
			}
		}
		
		System.out.println("limitResult length : "+ result.size());
		return result;
	}

	/** 
	* @Title: getDealUserTagsByCustomerId 
	* @Description: TODO(根据客户获取标签) 
	* @param appId
	* @param customerId
	* @return 设定文件 
	* @return List<DealUserTag>    返回类型 
	* @throws 
	*/
	public List<CustomerTag> getDealUserTagsByCustomerId(String appId, String customerId){
		List<String> ids=customerLinkDealUserService.getDealUserIdsByCustomerId(customerId);
		log.info("---getDealUserTagsByCustomerId---ids=--------"+ids);
		String dealUserIds=StringUtils.join(ids,",");
		log.info("---getDealUserTagsByCustomerId---dealUserIds=--------"+dealUserIds);
		return getCustomerTags(appId, dealUserIds, 1);
	}
}
