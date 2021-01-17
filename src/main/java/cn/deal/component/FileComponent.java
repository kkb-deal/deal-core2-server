package cn.deal.component;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import cn.deal.component.domain.FileUploaded;

@Component
public class FileComponent {

    private Logger logger = LoggerFactory.getLogger(FileComponent.class);

    @Autowired
    private RestTemplate restTemplate;

    @Value("${deal.file.baseurl}")
    private String dealFileBaseurl;

    public FileUploaded uploadImagesByDealUser(String appId, String dealUserId, File file) throws Exception {
        String url = dealFileBaseurl + "app/" + appId + "/deal-user/" + dealUserId + "/images";
        if (file == null || !file.exists()) {
            logger.error("uploadFile -> file is null or not exists");
            throw new FileNotFoundException();
        }

        FileSystemResource fileSystemResource = new FileSystemResource(file);
        MediaType type = MediaType.parseMediaType("multipart/form-data");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(type);
        String cd = "filename=\"" + file.getName() + "\"";
        headers.add("Content-Disposition", cd);
        MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
        form.add("file", fileSystemResource);
        HttpEntity<MultiValueMap<String, Object>> files = new HttpEntity<>(form, headers);
        ResponseEntity<FileUploaded> responseEntity = restTemplate.postForEntity(url, files, FileUploaded.class);

        logger.info("uploadFile.result: {}", responseEntity.getBody());
        try {
            if (file != null && file.exists()) {
                FileUtils.forceDelete(file);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
        return responseEntity.getBody();
    }

    /**
     * 上传音频文件
     * 
     * @param appId
     * @param file
     * @return
     * @throws Exception 
     */
	public FileUploaded uploadFileByKuickUser(String appId, String kuickUserId, File file) throws Exception {
		
		String url = dealFileBaseurl + "app/" + appId + "/files";
        if (file == null || !file.exists()) {
            logger.error("uploadFile -> file is null or not exists");
            throw new FileNotFoundException();
        }

        FileSystemResource fileSystemResource = new FileSystemResource(file);
        MediaType type = MediaType.parseMediaType("multipart/form-data");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(type);
        String cd = "filename=\"" + file.getName() + "\"";
        headers.add("Content-Disposition", cd);
        MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
        form.add("file", fileSystemResource);
        form.add("kuick_user_id", kuickUserId);
        HttpEntity<MultiValueMap<String, Object>> files = new HttpEntity<>(form, headers);
        ResponseEntity<FileUploaded> responseEntity = restTemplate.postForEntity(url, files, FileUploaded.class);

        logger.info("uploadFile.result: {}", responseEntity.getBody());
        try {
            if (file != null && file.exists()) {
                FileUtils.forceDelete(file);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
        
        return responseEntity.getBody();
	}

}
