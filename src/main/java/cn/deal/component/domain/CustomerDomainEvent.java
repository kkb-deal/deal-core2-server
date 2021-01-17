package cn.deal.component.domain;

import cn.deal.component.utils.IdGenerator;
import cn.deal.core.customer.domain.Customer;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;


@Data
@ToString
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomerDomainEvent {

	public static final String
            CREATE = "create",
            UPDATE = "update",
            MERGE = "merge",
            DELETE = "delete";
	
	private String id;
    @JsonProperty("event_type")
    private String eventType;
    @JsonProperty("domainId")
    private String domainId;
    @JsonProperty("body")
    private Customer body;
    @JsonProperty("old_body")
    private Customer oldBody;
    @JsonProperty("old_bodys")
    private List<Customer> oldBodys;

    public CustomerDomainEvent(String domainId, String type, Customer body) {
        this.id = IdGenerator.randomUUID();
        this.domainId = domainId;
        this.eventType = type;
        this.body = body;
    }

    public CustomerDomainEvent(String domainId, String type, Customer body, Customer oldBody) {
        this(domainId, type, body);
        this.oldBody = oldBody;
    }

    public CustomerDomainEvent(String domainId, String type, Customer body, Customer oldBody, List<Customer> oldBodys) {
        this(domainId, type, body, oldBody);
        this.oldBodys = oldBodys;
    }
}
