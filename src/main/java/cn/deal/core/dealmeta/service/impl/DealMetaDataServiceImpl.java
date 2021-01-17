package cn.deal.core.dealmeta.service.impl;

import cn.deal.component.dealmeta.OptionValueService;
import cn.deal.core.dealmeta.domain.DealMetaData;
import cn.deal.core.dealmeta.domain.KeyWord;
import cn.deal.core.dealmeta.domain.OptionValue;
import cn.deal.core.dealmeta.repository.DealMetaDataRepository;
import cn.deal.core.dealmeta.service.DealMetaDataService;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

/**
 * @ClassName DealMetaDataServiceImpl
 */
@Service
public class DealMetaDataServiceImpl implements DealMetaDataService {

    @Autowired
    private DealMetaDataRepository dealMetaDataRepository;

    @Autowired
    private OptionValueService optionValueService;

    @Override
    public List<DealMetaData> getDealMetaDataByAppId(String appId) {
        return dealMetaDataRepository.findAllByAppId(appId);
    }

    @Override
    public List<KeyWord> getOptionValue(String appId, String name, String keyword) {
        DealMetaData data = dealMetaDataRepository.findByAppIdAndName(appId, name);
        if (data == null || StringUtils.isBlank(data.getOptionValues())) {
            return Collections.emptyList();
        }
        OptionValue value = JSON.parseObject(data.getOptionValues(), OptionValue.class);
        List<KeyWord> keywords = value.getData();
        if ("rest".equals(value.getType())) {
            TreeMap<String, String> treeMap = new TreeMap<>();
            treeMap.put("keyword", keyword);
            keywords = optionValueService.getOptionValue(value.getConfig().getUrl(), appId, keyword, treeMap);
        }
        if (keywords == null || keywords.size() == 0) {
            return Collections.emptyList();
        }
        List<KeyWord> results = new ArrayList<>();
        if(StringUtils.isBlank(keyword)){
            return keywords;
        }
        for (KeyWord kw : keywords) {
            if (kw.getLabel().contains(keyword)) {
                results.add(kw);
            }
        }
        return results;
    }
}
