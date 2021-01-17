package cn.deal.component.kuick.interceptor;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import cn.deal.component.kuick.KuickApiService;

import java.io.IOException;
import java.util.Map;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Component
public class PreRequestInterceptor implements ClientHttpRequestInterceptor {

    private static final Logger log = Logger.getLogger(PreRequestInterceptor.class);

    @Autowired
    private KuickApiService kuickApiService;

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        HttpHeaders headers = request.getHeaders();

        Map map = null;
        try {
            map = kuickApiService.clientToken();
        } catch (Exception e) {
            e.printStackTrace();
        }

        headers.add("Authorization", map.get("token_type") + " " + map.get("access_token"));

        ClientHttpResponse response = execution.execute(request, body);
        
        HttpStatus statusCode = response.getStatusCode();
        
        if (UNAUTHORIZED == statusCode || FORBIDDEN == statusCode) {
            try {
                kuickApiService.clientToken();
                return execution.execute(request, body);
            } catch (Exception e1) {
                log.error("error in retry:", e1);
            }
        }
        
        return response;
    }

}
