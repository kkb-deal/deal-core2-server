package cn.deal.component.dealmeta;

import cn.deal.component.utils.MD5Util;
import cn.deal.core.app.domain.DealApp;
import cn.deal.core.app.service.DealAppService;
import cn.deal.core.dealmeta.domain.KeyWord;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

/**
 * 选项值的服务
 *
 * @ClassName dealmeta
 */
@Service
public class OptionValueService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private DealAppService dealAppService;

    /**
     * @param url     选项值
     * @param appId   项目id
     * @param param 关键字
     * @return
     */
    public List<KeyWord> getOptionValue(String url, String appId, String keyword, TreeMap<String, String> param) {
        DealApp dealApp = dealAppService.getDealAppInfo(appId);
        if (dealApp == null) {
            return Collections.emptyList();
        }
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        String message = parse(param).append(timestamp).append(":").append(dealApp.getSecret()).toString();
        String sign = MD5Util.getMD5(message).toUpperCase();
        KeyWord[] res = restTemplate.getForObject(url + "?keyword=" + keyword + "&app_id=" + appId + "&timestamp=" + timestamp + "&sign=" + sign, KeyWord[].class);
        logger.info("调用" + url + "?keyword=" + keyword + "&app_id=" + appId + "&timestamp=" + timestamp + "&sign=" + sign + " 接口返回：\n {}", Arrays.toString(res));
        if(res == null || res.length == 0){
            return Collections.emptyList();
        }
        return Arrays.asList(res);
    }

    /**
     * 解析参数
     *
     * @param param
     * @return
     */
    private StringBuilder parse(TreeMap<String, String> param) {
        StringBuilder sb = new StringBuilder();
        param.forEach((k, v) -> {
            if (!StringUtils.isBlank(v)) {
                sb.append(v).append(":");
            }
        });
        return sb;
    }
}
