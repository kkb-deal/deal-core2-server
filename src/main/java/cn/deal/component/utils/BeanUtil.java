package cn.deal.component.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;


public class BeanUtil {

    private static final Logger logger = LoggerFactory.getLogger(BeanUtil.class);

    public static void fillBase(Object target, Object source) {
        Field[] fields = target.getClass().getDeclaredFields();
        for (Field field : fields) handleField(target, source, field);
    }

    private static void handleField(Object target, Object source, Field field) {
        try {
            field.setAccessible(true);
            Object targetValue = ReflectionUtils.getField(field, target);
            Object sourceValue = ReflectionUtils.getField(field, source);

            String targetStringValue = targetValue != null ? StringUtils.strip(targetValue.toString()) : null;
            String sourceStringValue = sourceValue != null ? StringUtils.strip(sourceValue.toString()) : null;

            handleBaseVal(target, field, sourceValue, targetStringValue, sourceStringValue);

        } catch (IllegalStateException e) {
            logger.info("fillBase.error.field: {}", e.getMessage());
        }
    }

    private static void handleBaseVal(Object target, Field field, Object sourceValue, String targetStringValue, String sourceStringValue) {
        if (StringUtils.isBlank(sourceStringValue)) {
            return;
        }

        if (StringUtils.isBlank(targetStringValue) && StringUtils.isNotBlank(sourceStringValue)) {
            ReflectionUtils.setField(field, target, sourceValue);
        }
    }


}
