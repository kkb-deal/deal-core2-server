package cn.deal.core.app.repository;

import cn.deal.core.app.domain.DealAppSecret;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface DealAppSecretRepository extends JpaRepository<DealAppSecret, String> {

}
