package cn.deal.core.dealmeta.domain;

/**
 * 关键字
 *
 * @ClassName KeyWord
 */
public class KeyWord {

    /**
     * 标签
     */
    private String label;

    /**
     * 价值
     */
    private String value;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "KeyWord{" +
                "label='" + label + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
