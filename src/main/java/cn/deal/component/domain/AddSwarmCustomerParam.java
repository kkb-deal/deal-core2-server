package cn.deal.component.domain;

public class AddSwarmCustomerParam {
    
    private String appId;
    private String swarmId;
    private String customerId;
    
    public AddSwarmCustomerParam() {
        super();
    }
    
    public AddSwarmCustomerParam(String appId, String swarmId, String customerId) {
        super();
        this.appId = appId;
        this.swarmId = swarmId;
        this.customerId = customerId;
    }
    public String getAppId() {
        return appId;
    }
    public void setAppId(String appId) {
        this.appId = appId;
    }
    public String getSwarmId() {
        return swarmId;
    }
    public void setSwarmId(String swarmId) {
        this.swarmId = swarmId;
    }
    public String getCustomerId() {
        return customerId;
    }
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
    
    @Override
    public String toString() {
        return "AddSwarmCustomerParam [appId=" + appId + ", swarmId=" + swarmId + ", customerId=" + customerId + "]";
    }
    
}
