package cn.deal.core.category.repository;

import cn.deal.core.category.domain.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {


    Page<Category> findByAppId(String appId, Pageable pageable);
}
