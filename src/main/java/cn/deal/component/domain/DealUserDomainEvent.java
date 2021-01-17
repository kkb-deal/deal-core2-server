package cn.deal.component.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import cn.deal.core.dealuser.domain.DealUser;

public class DealUserDomainEvent {
    
	public static final String CREATE = "create";
	public static final String UPDATE = "update";
	public static final String DELETE = "delete";
	
	
    private String id;
    
    @JsonProperty("event_type")
    private String eventType;
    
    private String domainId;
    
    private DealUser body;
    
    @JsonProperty("old_body")
    private DealUser oldBody;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getDomainId() {
        return domainId;
    }

    public void setDomainId(String domainId) {
        this.domainId = domainId;
    }

    public DealUser getBody() {
        return body;
    }

    public void setBody(DealUser body) {
        this.body = body;
    }

    public DealUser getOldBody() {
        return oldBody;
    }

    public void setOldBody(DealUser oldBody) {
        this.oldBody = oldBody;
    }

    @Override
    public String toString() {
        return "DealUserDomainEvent [id=" + id + ", eventType=" + eventType + ", domainId=" + domainId + ", body="
                + body + ", oldBody=" + oldBody + "]";
    }
    
}
