package cn.deal.component.domain;


public class CustomerMetaDataItem {

    /**
     * 字段名
     */
    private String name;

    /**
     * 字段类型
     */
    private String type;

    /**
     *  字段标题
     */
    private String title;

    /**
     * 字段是否唯一，1：唯一，0：不唯一
     */
    private Boolean unique;

    /**
     * 字段是否必填，1：必填，0：非必填
     */
    private Boolean required;

    /**
     * 字段是否可见，1：可见，0：不可见
     */
    private Boolean visiable;

    /**
     * 字段是否为扩展字段，true：扩展字段，false：非扩展字段
     */
    private Boolean isExt;

    /**
     * 字段排序索引
     */
    private Integer index;

    /**
     * 字段默认值
     */
    private String defaultValue;

    /**
     * 字段选项值
     */
    private String optionValues;

    /**
     * 字段是否只读，1：只读，0：非只读
     */
    private Integer readonly;

    /**
     * 字段是否支持筛选，true：支持筛选，false：不支持筛选
     */
    private Boolean supportFilter;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getUnique() {
        return unique;
    }

    public void setUnique(Boolean unique) {
        this.unique = unique;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public Boolean getVisiable() {
        return visiable;
    }

    public void setVisiable(Boolean visiable) {
        this.visiable = visiable;
    }

    public Boolean getExt() {
        return isExt;
    }

    public void setExt(Boolean ext) {
        isExt = ext;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getOptionValues() {
        return optionValues;
    }

    public void setOptionValues(String optionValues) {
        this.optionValues = optionValues;
    }

    public Integer getReadonly() {
        return readonly;
    }

    public void setReadonly(Integer readonly) {
        this.readonly = readonly;
    }

    public Boolean getSupportFilter() {
        return supportFilter;
    }

    public void setSupportFilter(Boolean supportFilter) {
        this.supportFilter = supportFilter;
    }

    @Override
    public String toString() {
        return "CustomerMetaDataItem{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", title='" + title + '\'' +
                ", unique=" + unique +
                ", required=" + required +
                ", visiable=" + visiable +
                ", isExt=" + isExt +
                ", index=" + index +
                ", defaultValue='" + defaultValue + '\'' +
                ", optionValues='" + optionValues + '\'' +
                ", readonly=" + readonly +
                ", supportFilter=" + supportFilter +
                '}';
    }
}
