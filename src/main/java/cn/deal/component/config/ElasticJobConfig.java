package cn.deal.component.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ImportResource(locations = {"classpath:elastic-job.xml"})
public class ElasticJobConfig {

}
