package cn.deal.core.meta.service;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import cn.deal.component.utils.MD5Util;
import cn.deal.core.app.domain.DealApp;
import cn.deal.core.app.service.DealAppService;
import cn.deal.core.meta.domain.AppExtensionPoint;
import cn.deal.core.meta.domain.AppSign;
import cn.deal.core.meta.repository.AppExtensionPointRepository;

@Service
public class AppExtensionPointService {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private AppExtensionPointRepository appExtensionPointRepository;
	
	@Autowired
	private DealAppService dealAppService;
	
	public List<AppExtensionPoint> getExtensionPointsByAppId(String appId, String platform, String module, String type){
		AppExtensionPoint exam = new AppExtensionPoint();
		exam.setAppId(appId);
		if(StringUtils.isNotBlank(platform)){
			exam.setPlatform(platform);
		}
		if(StringUtils.isNotBlank(type)){
			exam.setType(type);
		}
		if (StringUtils.isNotBlank(module)) {
			exam.setModule(module);
		}
		return appExtensionPointRepository.findAll(Example.of(exam));
	}

	public AppSign getSign(String appId, String data) {
		AppSign appSign = new AppSign();
		String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
		DealApp dealAppInfo = dealAppService.getDealAppInfo(appId);
		if(dealAppInfo == null){
			logger.warn("appId 不正确");
			return null;
		}
		String message = appId + ":" + data + ":" + timestamp + ":" + dealAppInfo.getSecret();
		String sign = MD5Util.getMD5(message);
		appSign.setAppId(appId);
		appSign.setData(data);
		appSign.setTimestamp(timestamp);
		appSign.setSign(sign);
		return appSign;
	}
	
}
