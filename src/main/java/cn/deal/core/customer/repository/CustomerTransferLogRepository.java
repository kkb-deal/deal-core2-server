package cn.deal.core.customer.repository;

import cn.deal.core.customer.domain.CustomerTransferLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerTransferLogRepository extends JpaRepository<CustomerTransferLog, String> {
}
