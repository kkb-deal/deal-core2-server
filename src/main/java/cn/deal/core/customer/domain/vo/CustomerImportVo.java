package cn.deal.core.customer.domain.vo;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.util.Arrays;
import java.util.Map;


@Data
@Builder
public class CustomerImportVo {

    @Tolerate
    public CustomerImportVo() {

    }

    private String appId;
    private String[] titles;
    private Map<Integer, Map<Integer, Object>> contents;

    @Override
    public String toString() {
        return "CustomerImportVo{" +
                "appId='" + appId + '\'' +
                ", titles=" + Arrays.toString(titles) +
                ", contents=" + contents +
                '}';
    }

}
