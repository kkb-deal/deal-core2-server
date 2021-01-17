package cn.deal.core.meta.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cn.deal.core.meta.domain.PromotionChannel;

public interface PromotionChannelRepository extends JpaRepository<PromotionChannel, String> {

	PromotionChannel getByAppIdAndCode(String appId, String code);

}
