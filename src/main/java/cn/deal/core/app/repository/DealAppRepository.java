package cn.deal.core.app.repository;

import cn.deal.core.app.domain.DealApp;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface DealAppRepository extends JpaRepository<DealApp, String> {

    /**
     * 根据创建人Id和项目名称获取项目信息
     * @param creatorId
     * @param name
     * @return
     */
    @Query(nativeQuery=true,value="select da.* from deal_app da where da.creatorId=?1 and da.`name` = ?2 order by da.createTime desc limit 0,1")
    DealApp findByCreatorIdAndAppName(@Param("creatorId")String creatorId , @Param("name")String name);
    
    @Query(nativeQuery=true,value="select da.* from deal_app da where da.`name` = ?1 order by da.createTime desc limit 0,1")
    DealApp findByName(@Param("name")String name);

    @Query(nativeQuery = true, value = "select count(id) from deal_app where id = ?1 and creatorId = ?2")
    int countByIdAndCreatorId(@Param("id") String id, @Param("creatorId") int creatorId);
}
