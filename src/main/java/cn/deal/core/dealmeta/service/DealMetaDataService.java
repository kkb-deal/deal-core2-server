package cn.deal.core.dealmeta.service;

import cn.deal.core.dealmeta.domain.DealMetaData;
import cn.deal.core.dealmeta.domain.KeyWord;

import java.util.List;

/**
 * @ClassName Deal
 */
public interface DealMetaDataService {

    /**
     * 获取成交记录元数据
     * @param appId 项目id
     * @return
     */
    List<DealMetaData> getDealMetaDataByAppId(String appId);

    /**
     * 获取成交记录元数据选项值
     * @param appId 项目id
     * @param name 元数据名称
     * @param keyword 关键字
     * @return
     */
    List<KeyWord> getOptionValue(String appId, String name, String keyword);
}
