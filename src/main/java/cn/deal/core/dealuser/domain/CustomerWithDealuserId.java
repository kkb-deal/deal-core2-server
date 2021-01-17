
package cn.deal.core.dealuser.domain;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import cn.deal.core.customer.domain.Customer;


@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomerWithDealuserId extends Customer implements Serializable {

    private static final long serialVersionUID = 2057440926349617239L;
    
    private String dealUserId;

    public String getDealUserId() {
        return dealUserId;
    }

    public void setDealUserId(String dealUserId) {
        this.dealUserId = dealUserId;
    }
}
