package cn.deal.core.customer.service.impl;

import cn.deal.component.AsyncTaskComponent;
import cn.deal.component.domain.AsyncTask;
import cn.deal.component.spring.MultipartResource;
import cn.deal.component.utils.JsonUtil;
import cn.deal.core.customer.domain.vo.CustomerImportVo;
import cn.deal.core.customer.service.CustomerExcelImportService;
import cn.deal.core.customer.service.CustomerTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;


@Service
public class CustomerTaskServiceImpl implements CustomerTaskService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private AsyncTaskComponent asyncTaskComponent;

    @Autowired
    private CustomerExcelImportService customerExcelImportService;

    @Override
    public AsyncTask findTask(String taskId) {
        return asyncTaskComponent.findTask(taskId);
    }

    @Override
    public AsyncTask createCustomerImportTask(String appId, Map<String, Object> opts, MultipartFile excelFile) {
        CustomerImportVo customerImportVo = customerExcelImportService.buildCustomerImportData(appId, new MultipartResource(excelFile));
        logger.info("createCustomerImportTask.customerImportVo: {}", customerImportVo);

        AsyncTask task = AsyncTask.builder()
                .type("batch_import_customer_task")
                .params(JsonUtil.toJson(opts))
                .progressText(AsyncTask.Text.HANDLING.getVal())
                .progressVal(0).progressCount(customerImportVo.getContents().size())
                .key("app:" + appId + ":member:" + opts.get("kuickUserId"))
                .build();

        AsyncTask asyncTask = asyncTaskComponent.createAsyncTask(task);
        logger.info("createCustomerImportTask.asyncTask: {}", asyncTask);

        customerExcelImportService.handleImportCustomer(appId, opts, customerImportVo.getTitles(), customerImportVo.getContents(), asyncTask);
        return asyncTask;
    }

}
