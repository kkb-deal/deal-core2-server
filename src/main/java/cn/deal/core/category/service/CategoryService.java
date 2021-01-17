package cn.deal.core.category.service;

import cn.deal.core.category.domain.Category;
import cn.deal.core.category.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@CacheConfig(cacheNames = "category", cacheManager = "redis")
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    /**
     * 获取系统类别分页
     * @param appId
     * @param startIndex
     * @param count
     * @return
     */
    @Cacheable(key = "'get_category_by_app_id:' + #p0 + ':' + #p1 + ':' + #p2")
    public List<Category> getCategorys(String appId, Integer startIndex, Integer count) {
        Pageable pageable = new PageRequest(startIndex, count);
        if(count > 100) {
            pageable = new PageRequest(startIndex, 100);
        }
        Page<Category> categoryPage = categoryRepository.findByAppId(appId, pageable);
        return categoryPage.getContent();
    }
}
