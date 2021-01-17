package cn.deal.core.customer.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown=true)
public class CustomerId implements Serializable {

	private static final long serialVersionUID = 6221078272457918896L;
	
	/**
     * 客户id
     */
    private String id;

    
    public CustomerId() {
		super();
	}
    
    public CustomerId(String id) {
		super();
		this.id = id;
	}

    @Override
    public String toString() {
        return "CustomerId{" +
                "id='" + id + '\'' +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
