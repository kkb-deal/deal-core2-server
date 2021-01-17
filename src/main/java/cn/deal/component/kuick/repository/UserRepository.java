package cn.deal.component.kuick.repository;

import cn.deal.component.domain.User;
import cn.deal.component.kuick.domain.KuickUser;
import cn.deal.component.utils.JsonUtil;
import com.google.gson.Gson;
import org.springframework.beans.BeanUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.stream.Collectors;


public interface UserRepository extends JpaRepository<User, Integer> {

    List<User> findAll(Specification<User> specification);

    default List<KuickUser> findByIds(List<Integer> kids, User.IsSimple isSimple) {
        Gson gson = new Gson();
        return findAll(kids).stream().map(user -> {
            KuickUser kuickUser = new KuickUser();
            if (isSimple == User.IsSimple.YES) {
                kuickUser.setId(user.getId());
                kuickUser.setName(user.getName());
                kuickUser.setPhotoURI(user.getPhotoURI());
            } else {
                kuickUser = gson.fromJson(gson.toJson(user), KuickUser.class);
            }
            return kuickUser;
        }).collect(Collectors.toList());
    }

    default List<KuickUser> findAllKuickUser(Specification<User> specification) {
        return findAll(specification).stream().map(user -> {
            KuickUser kuickUser = new KuickUser();
            kuickUser.setId(user.getId());
            kuickUser.setName(user.getName());
            kuickUser.setPhotoURI(user.getPhotoURI());
            return kuickUser;
        }).collect(Collectors.toList());
    }
    @Query(nativeQuery = true, value = "select distinct u.* from user u left join app_member m on u.id = m.kuickUserId " +
            "where (u.phoneNum like %?1% or u.name like %?1% or u.email like %?1% or m.remarkName like %?1%) and u.id in ?3 and  m.appId = ?2 ")
    List<User> findKuickUserByKeword(String keyword, String appId, List<Integer> ids);

    List<User> findAllByIdIn(List<Integer> ids);
}
