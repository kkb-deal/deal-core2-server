package cn.deal.core.tag.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cn.deal.core.tag.domain.DealUserTag;


public interface DealUserTagRepository extends JpaRepository<DealUserTag, String> {
}
