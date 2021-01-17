package cn.deal.core.meta.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cn.deal.core.meta.domain.AppCustomerRecordSetting;

@Repository
public interface AppCustomerRecordSettingRepository extends JpaRepository<AppCustomerRecordSetting,String>{

}
