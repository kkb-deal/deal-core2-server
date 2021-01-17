package cn.deal.core.customer.engine.listeners;

import cn.deal.component.AccessTokenComponent;
import cn.deal.component.DealBehaviourComponent;
import cn.deal.component.domain.CustomerDomainEvent;
import cn.deal.component.utils.JsonUtil;
import cn.deal.core.customer.domain.Customer;
import cn.deal.core.customer.engine.helpers.CustomerEventManager;
import cn.deal.core.customer.engine.interfaces.CustomerContext;
import cn.deal.core.customer.engine.interfaces.CustomerEventListener;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @description: 生成客户创建行为
 */
@Component
public class CustomerBehaviourLogListener implements CustomerEventListener {

    @Autowired
    private CustomerEventManager customerEventManager;

    @Autowired
    private AccessTokenComponent accessTokenComponent;

    @Autowired
    private DealBehaviourComponent dealBehaviourComponent;

    @PostConstruct
    public void init() {
        customerEventManager.register(this);
    }

    @Override
    public void handle(Customer customer, CustomerContext ctx) {
        String ope = ctx.getOpe();
        if (!CustomerDomainEvent.CREATE.equals(ope)) {
            return;
        }

        String kuickUserId = customer.getKuickUserId();
        if(StringUtils.isNotBlank(kuickUserId)){
            String accessToken = accessTokenComponent.getAccessTokenByKuickUserId(kuickUserId);
            String action = CustomerDomainEvent.CREATE;
            String content = JsonUtil.toJson(customer);
            //添加客户创建行为
            dealBehaviourComponent.createCustomerBehaviourLog(customer.getAppId(), kuickUserId, customer.getId(),
                    customer.getName(), action, null, content, accessToken);
        }
    }
}
