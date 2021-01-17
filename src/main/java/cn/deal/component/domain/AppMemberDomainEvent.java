package cn.deal.component.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import cn.deal.core.app.resource.vo.AppMemberVO;


@JsonIgnoreProperties(ignoreUnknown=true)
public class AppMemberDomainEvent {

    private String id;
    
    @JsonProperty("event_type")
    private String eventType;
    
    private String domainId;
    
    private AppMemberVO body;
    
    @JsonProperty("old_body")
    private AppMemberVO oldBody;

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

    public AppMemberVO getBody() {
        return body;
    }

    public void setBody(AppMemberVO body) {
        this.body = body;
    }

    public AppMemberVO getOldBody() {
        return oldBody;
    }

    public void setOldBody(AppMemberVO oldBody) {
        this.oldBody = oldBody;
    }

    @Override
    public String toString() {
        return "AppMemberDomainEvent [id=" + id + ", eventType=" + eventType + ", domainId=" + domainId + ", body="
                + body + ", oldBody=" + oldBody + "]";
    }
    
}
