package cn.deal.core.customer.resource;


import cn.deal.component.kuick.domain.ResponseVO;
import cn.deal.core.customer.service.SalesCustomerService;
import io.swagger.annotations.Api;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 转让客户
 */
@RestController
@Api(value = "转让客户", description = "转让客户",tags={"转让客户"},produces = MediaType.ALL_VALUE)
@RequestMapping("/api/v1.0")
public class CustomerTransferResource {

    @Autowired
    private SalesCustomerService salesCustomerService;

    /**
     * 转让客户
     * @return
     */
    @PostMapping("/app/{app_id}/customer/app-member-transfers")
    public ResponseVO customerTransfer(@PathVariable("app_id") String appId,
                                             @RequestParam("source_kuick_userid") String sourceKuickUserId,
                                             @RequestParam("target_kuick_userid") String targetKuickUserId,
                                             @RequestParam("customer_ids") String customerIds,
                                             @RequestParam(value = "is_judge_owner", required = false) Integer isJudgeOwner,
                                             HttpServletRequest request) {
        if(StringUtils.isBlank(appId) || StringUtils.isBlank(targetKuickUserId) || StringUtils.isBlank(customerIds)) {
            ResponseVO paramIsBlankResponseVo = new ResponseVO(ResponseVO.Status.ERROR.getVal(), "参数为空");
            return paramIsBlankResponseVo;
        }
        String kdKuickUserId = request.getHeader("kd_kuick_user_id");
        ResponseVO responseVO = salesCustomerService.customerTransfer(appId, kdKuickUserId, sourceKuickUserId,
                targetKuickUserId, customerIds, false, request.getHeader("User-Agent"), isJudgeOwner);
        return responseVO;
    }

    /**
     * 批量转让客户
     * @return
     */
    @PostMapping("/app/{app_id}/customer/batch-app-member-transfers")
    public ResponseVO batchCustomerTransfer(@PathVariable("app_id") String appId,
                                            @RequestParam("datas") String datas,
                                            @RequestParam("target_kuick_userid") String targetKuickUserId,
                                            HttpServletRequest request) {
        if(StringUtils.isBlank(appId) || StringUtils.isBlank(targetKuickUserId) || StringUtils.isBlank(datas)) {
            ResponseVO paramIsBlankResponseVo = new ResponseVO(ResponseVO.Status.ERROR.getVal(), "参数为空");
            return paramIsBlankResponseVo;
        }
        String kdKuickUserId = request.getHeader("kd_kuick_user_id");
        ResponseVO responseVO = salesCustomerService.batchCustomerTransfer(appId, kdKuickUserId, datas, targetKuickUserId, request.getHeader("User-Agent"));
        return responseVO;
    }

    /**
     * 链接自动转让客户
     * @return
     */
    @PostMapping("/app/{app_id}/customer/app-member-inner-transfers")
    public ResponseVO customerInnerTransfer(@PathVariable("app_id") String appId,
                                            @RequestParam("source_kuick_userid") String sourceKuickUserId,
                                            @RequestParam("target_kuick_userid") String targetKuickUserId,
                                            @RequestParam("customer_id") String customerIds,

                                       HttpServletRequest request) {
        if(StringUtils.isBlank(appId) || StringUtils.isBlank(targetKuickUserId) || StringUtils.isBlank(customerIds)) {
            ResponseVO paramIsBlankResponseVo = new ResponseVO(ResponseVO.Status.ERROR.getVal(), "参数为空");
            return paramIsBlankResponseVo;
        }
        ResponseVO responseVO = salesCustomerService.customerTransfer(appId, "", sourceKuickUserId,
                targetKuickUserId, customerIds, true, request.getHeader("User-Agent"), 0);
        return responseVO;
    }




}
