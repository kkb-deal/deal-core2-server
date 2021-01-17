package cn.deal.core.customer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cn.deal.core.customer.domain.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {

}
