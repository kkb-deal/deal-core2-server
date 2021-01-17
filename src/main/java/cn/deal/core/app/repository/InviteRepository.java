package cn.deal.core.app.repository;

import cn.deal.core.app.domain.Invite;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InviteRepository extends JpaRepository<Invite, String>{

	Invite findOneByCode(String code);

    Invite findOneByAppIdAndInviterIdAndDepartmentId(String appId, String inviterId, String departmentId);
}