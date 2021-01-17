package cn.deal.core.app.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "deal_app")
public class DealAppSecret {

    @Id
    private String id;
    @Column
    private String secret;

    @Override
    public String toString() {
        return "DealAppSecret{" +
                "id='" + id + '\'' +
                ", secret='" + secret + '\'' +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }
}
