package cn.deal.component;

import cn.deal.component.domain.AsyncTask;
import cn.deal.component.utils.JsonUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;


@Component
public class AsyncTaskComponent {

    private static final Logger logger = LoggerFactory.getLogger(AsyncTaskComponent.class);

    @Value("${deal.async.task.baseurl}")
    private String asyncTaskBaseURL;

    @Autowired
    private RestTemplate restTemplate;

    public AsyncTask findTask(String taskId) {
        String url = asyncTaskBaseURL + "async-tasks/{taskId}";
        return restTemplate.getForObject(url, AsyncTask.class, taskId);
    }

    public AsyncTask createAsyncTask(AsyncTask params){
        String url = asyncTaskBaseURL + "async-tasks";
        logger.info("createAsyncTask.url: {}, param: {}", url, params);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        TypeReference<Map<String, String>> type = new TypeReference<Map<String, String>>() {};
        data.setAll(JsonUtil.fromJson(JsonUtil.toJson(params), type));
        logger.info("createAsyncTask.data: {}", data);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(data, headers);
        String result = restTemplate.postForObject(url, entity, String.class);
        logger.info("createAsyncTask.result: {}", result);

        TypeReference<AsyncTask> jType = new TypeReference<AsyncTask>() {};
        return JsonUtil.fromJson(result, jType);
    }

    public void editTaskById(String taskId, Integer status, String progressText, Integer progressVal, String result){
        String url = asyncTaskBaseURL  + "async-tasks/{taskId}?status={status}";
        logger.info("editTaskById.url: {}", url);

        Map<String, Object> uriVariables = new HashMap<>();
        uriVariables.put("taskId", taskId);
        uriVariables.put("status", status);

        if(StringUtils.isNotBlank(progressText)){
            url += "&progressText={progressText}";
            uriVariables.put("progressText", progressText);
        }
        if(progressVal != null){
            url += "&progressVal={progressVal}";
            uriVariables.put("progressVal", progressVal);
        }
        if(StringUtils.isNotBlank(result)){
            url += "&result={result}";
            uriVariables.put("result", result);
        }
        logger.info("editTaskById.params: {}", uriVariables);

        restTemplate.put(url, AsyncTask.class, uriVariables);
    }

}
