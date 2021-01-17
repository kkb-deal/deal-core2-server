package cn.deal.component.domain.filter;


public class Condition {

    private String name;
    /**
     * 0：离散变量；1：连续变量
     */
    private int type;
    private String range;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getRange() {
        return range;
    }

    public void setRange(String range) {
        this.range = range;
    }

    @Override
    public String toString() {
        return "Condition{" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", range='" + range + '\'' +
                '}';
    }
}
