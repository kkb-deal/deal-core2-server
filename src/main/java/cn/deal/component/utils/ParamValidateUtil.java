package cn.deal.component.utils;

import cn.deal.component.exception.BusinessException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Map;


public class ParamValidateUtil {

    private static final Logger logger = LoggerFactory.getLogger(ParamValidateUtil.class);

    private static final String COLLECTION_PARAM_ERROR_MSG = "集合为空";
    private static final String STRING_PARAM_ERROR_MSG = "参数为空";

    public static void validateCollection(Collection collection, String msg) {
        if (CollectionUtils.isEmpty(collection)) {
            throw new BusinessException("is_empty", msg + COLLECTION_PARAM_ERROR_MSG);
        }
    }

    public static void validateString(String param) {
        if (StringUtils.isBlank(param)) {
            throw new BusinessException("is_blank", STRING_PARAM_ERROR_MSG);
        }
    }

    public static void validateMap(Map map, String msg) {
        if (CollectionUtils.isEmpty(map)) {
            throw new BusinessException("is_blank", msg);
        }
    }


}
