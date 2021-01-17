package cn.deal.core.meta.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.deal.core.meta.domain.PromotionChannel;
import cn.deal.core.meta.repository.PromotionChannelRepository;

@Service
public class PromotionChannelService {

	@Autowired
	private PromotionChannelRepository promotionChannelRepository;
	
	public PromotionChannel getChannelByCode(String appId, String code) {
		return promotionChannelRepository.getByAppIdAndCode(appId, code);
	}

}
