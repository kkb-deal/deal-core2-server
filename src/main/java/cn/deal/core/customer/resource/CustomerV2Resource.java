package cn.deal.core.customer.resource;


import cn.deal.component.exception.BusinessException;
import cn.deal.core.customer.domain.Customer;
import cn.deal.core.customer.service.CustomerService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api("客户管理V2")
@RestController
@RequestMapping("/api")
public class CustomerV2Resource {

    @Autowired
    private CustomerService customerService;

    @GetMapping("v1.0/deal-user/{deal-user-id}/customer")
    public Customer findByDealUserId(@PathVariable("deal-user-id") String dealUserId) {
        return customerService.findByDealUserId(dealUserId);
    }

    @PostMapping("/v1.1/app/{app_id}/customer/app-member-transfers")
    public Boolean transferAscription(@PathVariable("app_id")String appId,
                              @RequestParam("target_kuick_userid")String targetKuickUserId,
                              @RequestParam(value = "source_kuick_userid", required = false)String sourceKuickUserId,
                              @RequestParam("customer_ids")String customerIds,
                              @RequestParam(value = "is_judge_owner", required = false) Integer isJudgeOwner) {
        if (targetKuickUserId.equals(sourceKuickUserId)) {
            throw new BusinessException("param_error", "转出销售和转接销售不能为同一人");
        }
        return customerService.batchTransferAscription(appId, sourceKuickUserId, targetKuickUserId, customerIds, true,
                isJudgeOwner);
    }

}
