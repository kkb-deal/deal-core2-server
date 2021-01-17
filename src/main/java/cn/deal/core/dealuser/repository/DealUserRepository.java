package cn.deal.core.dealuser.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import cn.deal.core.dealuser.domain.DealUser;

public interface DealUserRepository extends JpaRepository<DealUser, String> {


	
}
