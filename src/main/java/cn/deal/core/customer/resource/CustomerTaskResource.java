package cn.deal.core.customer.resource;

import cn.deal.component.domain.AsyncTask;
import cn.deal.component.utils.MapUtils;
import cn.deal.core.customer.service.CustomerTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;


@RestController
@RequestMapping("api/v1.0")
public class CustomerTaskResource {

    @Autowired
    private CustomerTaskService customerTaskService;

    @PostMapping("app/{app_id}/member/{kuick_user_id}/customer/import-task/{task_id}")
    public AsyncTask createCustomerImportTask(
            @PathVariable("app_id") String appId, @PathVariable(value="kuick_user_id") String kuickUserId, @PathVariable(value="task_id") String taskId) {
        return customerTaskService.findTask(taskId);
    }

    @PostMapping("app/{app_id}/member/{kuick_user_id}/customer/import-tasks")
    public AsyncTask createCustomerImportTask(@PathVariable("app_id") String appId, @PathVariable(value="kuick_user_id") String kuickUserId,
                                              @RequestParam(name = "is_belonged_me", required = false) String isBelongedMe,
                                              @RequestParam(name = "customer_group_id", required = false) String customerGroupId,
                                              @RequestParam(name = "file", required = false) MultipartFile excelFile) {

        Map<String, Object> opts = MapUtils.from(new Object[] {
                "kuickUserId", kuickUserId,
                "isBelongedMe", isBelongedMe,
                "customerGroupId", customerGroupId
        });

        return customerTaskService.createCustomerImportTask(appId, opts, excelFile);
    }

}
