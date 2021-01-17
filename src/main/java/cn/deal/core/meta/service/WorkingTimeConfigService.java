package cn.deal.core.meta.service;

import cn.deal.core.meta.domain.WorkingTimeConfig;
import cn.deal.core.meta.repository.WorkingTimeConfigRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class WorkingTimeConfigService {

    @Autowired
    private WorkingTimeConfigRepository workingTimeConfigRepository;

    /**
     * 获取时间配置
     * @param appId
     * @return
     */
    public List<WorkingTimeConfig> getWorkingTimeConfigByAppId(String appId) {
        return workingTimeConfigRepository.getWorkingTimeConfigByAppId(appId);
    }
}
