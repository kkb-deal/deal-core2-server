package cn.deal.component.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import cn.deal.component.kuick.interceptor.PreRequestInterceptor;

import java.util.Arrays;

/**
 * rest template config
 */
@Configuration
public class RestTemplateConfig {

	@Value("${rest.template.read.timeout}")
	private int readTimeout;
	
	@Value("${rest.template.connect.timeout}")
	private int connectTimeout;
	
	@Bean
	public RestTemplate restTemplate(ClientHttpRequestFactory factory) {
		return new RestTemplate(factory);
	}

	@Bean
	public RestTemplate kuickApiRestTemplate(ClientHttpRequestFactory factory, PreRequestInterceptor interceptor) {
		RestTemplate restTemplate = new RestTemplate(factory);
		restTemplate.setInterceptors(Arrays.asList(interceptor));
		return restTemplate;
	}

	@Bean
	public ClientHttpRequestFactory simpleClientHttpRequestFactory() {
		SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
		factory.setReadTimeout(readTimeout);
		factory.setConnectTimeout(connectTimeout);
		return factory;
	}

}
