package cn.deal.component.interceptor;

import cn.deal.component.utils.IdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.Enumeration;


@Component
public class LoggerInterceptor implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(LoggerInterceptor.class);
    private static final String ATTRIBUTE_FOR_REQUEST_ID = "__INTERCEPTOR_REQ_ID";
    private static final String ATTRIBUTE_FOR_REQUEST_TIME = "__INTERCEPTOR_REQ_TIME";
    private static final String PREFIX_REQUEST_ID = "\n\t+--- Request ID: ";
    private static final String PREFIX_RESPONSE_ID = "\n\t+--- Response ID: ";
    private static final String PREFIX_REQUEST_URL = "\n\t|--- Url: ";
    private static final String PREFIX_REQUEST_HEADER = "\n\t|--- Header: ";
    private static final String PREFIX_REQUEST_BODY = "\n\t|--- Body: ";
    private static final String PREFIX_REQUEST_CLIENT = "\n\t|--- Client: ";
    private static final String PREFIX_RESPONSE_HEADER = "\n\t|--- Header: ";
    private static final String PREFIX_RESPONSE_STATUS = "\n\t|--- Status: ";
    private static final String PREFIX_DURATION = "\n\t|--- Duration: ";
    private static final String DURATION_UNITS = "ms";
    private static final String LAST_LINE = "\n";
    private static final String CHAR_AND = "&";
    private static final String CHAR_EQUAL = "=";
    private static final String CHAR_BLANK_SPACE = " ";
    private static final String CHAR_QUESTION = "?";
    private static final String STRING_BLANK = "";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        StringBuilder content = new StringBuilder();
        String id = IdGenerator.randomUUID();
        request.setAttribute(ATTRIBUTE_FOR_REQUEST_ID, id);
        request.setAttribute(ATTRIBUTE_FOR_REQUEST_TIME, System.currentTimeMillis());

        content.append(PREFIX_REQUEST_ID).append(id);
        content.append(PREFIX_REQUEST_URL);
        String reqQueryString = STRING_BLANK;
        if(request.getQueryString()!=null && !STRING_BLANK.equals(request.getQueryString().trim())){
            reqQueryString = request.getQueryString();
        }
        content.append(request.getMethod()).append(CHAR_BLANK_SPACE).append(request.getRequestURL().append(CHAR_QUESTION).append(reqQueryString));

        content.append(PREFIX_REQUEST_HEADER);
        Enumeration headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = (String) headerNames.nextElement();
            content.append(name).append(CHAR_EQUAL).append(request.getHeader(name)).append(CHAR_AND);
        }

        content.append(PREFIX_REQUEST_BODY);
        Enumeration requestNames = request.getParameterNames();
        while (requestNames.hasMoreElements()) {
            String name = (String) requestNames.nextElement();
            content.append(name).append(CHAR_EQUAL).append(request.getParameter(name)).append(CHAR_AND);
        }
        content.append(PREFIX_REQUEST_CLIENT).append(request.getRemoteAddr());
        content.append(LAST_LINE);
        logger.info(content.toString());

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        StringBuilder content = new StringBuilder();
        String id = (String) request.getAttribute(ATTRIBUTE_FOR_REQUEST_ID);
        long requestTime = (long) request.getAttribute(ATTRIBUTE_FOR_REQUEST_TIME);
        long duration = System.currentTimeMillis() - requestTime;

        content.append(PREFIX_RESPONSE_ID).append(id);
        content.append(PREFIX_RESPONSE_HEADER);
        Collection<String> responseHeaderNames = response.getHeaderNames();
        for (String name : responseHeaderNames) {
            content.append(name).append(CHAR_EQUAL).append(response.getHeader(name)).append(CHAR_AND);
        }

        content.append(PREFIX_RESPONSE_STATUS).append(response.getStatus());
        content.append(PREFIX_DURATION).append(duration).append(DURATION_UNITS);
        content.append(LAST_LINE);
        logger.info(content.toString());
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    }
}
