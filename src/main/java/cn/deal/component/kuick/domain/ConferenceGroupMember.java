package cn.deal.component.kuick.domain;

import java.util.Date;


public class ConferenceGroupMember {

    private String id;

    private String conferenceGroupId;

    private String conferenceId;

    private Date createdAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getConferenceGroupId() {
        return conferenceGroupId;
    }

    public void setConferenceGroupId(String conferenceGroupId) {
        this.conferenceGroupId = conferenceGroupId;
    }

    public String getConferenceId() {
        return conferenceId;
    }

    public void setConferenceId(String conferenceId) {
        this.conferenceId = conferenceId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "ConferenceGroupMember{" +
                "id='" + id + '\'' +
                ", conferenceGroupId='" + conferenceGroupId + '\'' +
                ", conferenceId='" + conferenceId + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
