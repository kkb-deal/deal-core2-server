package cn.deal.core.dealmeta.domain;

import java.util.List;

/**
 * 选项值
 *
 * @ClassName OptionValue
 */
public class OptionValue {

    /**
     * 类型：local/rest
     */
    private String type;

    /**
     * 选项
     */
    private List<KeyWord> data;

    /**
     * 配置
     */
    private Config config;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<KeyWord> getData() {
        return data;
    }

    public void setData(List<KeyWord> data) {
        this.data = data;
    }

    public Config getConfig() {
        return config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }
}
