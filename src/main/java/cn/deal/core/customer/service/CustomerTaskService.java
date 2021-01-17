package cn.deal.core.customer.service;

import cn.deal.component.domain.AsyncTask;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;


public interface CustomerTaskService {

    AsyncTask findTask(String taskId);

    /**
     *
     * 创建客户导入异步任务
     *
     * @param appId
     * @param opts
     * @param excelFile
     * @return
     */
    AsyncTask createCustomerImportTask(String appId, Map<String, Object> opts, MultipartFile excelFile);

}
