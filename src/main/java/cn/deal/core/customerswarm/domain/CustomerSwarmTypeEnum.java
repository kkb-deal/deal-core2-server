package cn.deal.core.customerswarm.domain;


public enum CustomerSwarmTypeEnum {

    /**
     * 普通分群
     */
    NORMAL(1, "普通分群"),
    /**
     * 基于帅选条件的虚拟分群
     */
    VIRTUAL(2, "基于filterId的虚拟分群");

    private int val;
    private String desc;

    private CustomerSwarmTypeEnum(int val, String desc) {
        this.val = val;
        this.desc = desc;
    }

    public int getVal() {
        return this.val;
    }

    public String getDesc() {
        return this.desc;
    }

}
