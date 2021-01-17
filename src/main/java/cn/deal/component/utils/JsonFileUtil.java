package cn.deal.component.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class JsonFileUtil {

    private static final Logger logger = LoggerFactory.getLogger(JsonFileUtil.class);
    private ResourceLoader resourceLoader = new DefaultResourceLoader();
    
    public String getJsonFromClasspathFile(Resource resource) {
        Assert.notNull(resource, "classpath路径下resource文件不能为空");
        String jsonData = "";
        
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream(), "utf-8"));
            StringBuffer message = new StringBuffer();
            String line = null;
            while ((line = br.readLine()) != null) {
                message.append(line);
            }
            String defaultString = message.toString();
            jsonData = defaultString.replace("\r\n", "").replaceAll(" +", "");
        } catch (IOException e) {
            logger.error("error in read resource:{}", resource);
        }

        return jsonData;
    }

    /**
     * 从类路径加载资源内容
     * 
     * @param classpath
     * @return
     */
	public String getJsonFromClasspath(String classpath) {
		org.springframework.core.io.Resource resource = resourceLoader.getResource(classpath);
        String roleJson = getJsonFromClasspathFile(resource);
        return roleJson;
	}
}
