package cn.deal.component.config;


import com.google.common.base.Predicate;
import org.springframework.boot.actuate.endpoint.mvc.*;
import org.springframework.boot.autoconfigure.web.BasicErrorController;
//import org.springframework.cloud.context.environment.EnvironmentManagerMvcEndpoint;
//import org.springframework.cloud.context.restart.RestartMvcEndpoint;
//import org.springframework.cloud.endpoint.GenericPostableMvcEndpoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.RequestHandler;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
	@Bean
	public Docket api(){
		Predicate<RequestHandler> predicate = new Predicate<RequestHandler>() {
			@Override
			public boolean apply(RequestHandler input) {
				Class<?> declaringClass = input.getHandlerMethod().getMethod().getDeclaringClass();
				List<Class> filter = new ArrayList<Class>();
				filter.add(BasicErrorController.class);
				filter.add(HealthMvcEndpoint.class);
				filter.add(HeapdumpMvcEndpoint.class);
				filter.add(EndpointMvcAdapter.class);
//				filter.add(EnvironmentManagerMvcEndpoint.class);
//				filter.add(GenericPostableMvcEndpoint.class);
				filter.add(HalJsonMvcEndpoint.class);
				filter.add(MetricsMvcEndpoint.class);
//				filter.add(RestartMvcEndpoint.class);
				filter.add(EnvironmentMvcEndpoint.class);
				//过滤掉错误返回页面
				if (filter.contains(declaringClass)) {
					return false;
				}
				return true;
			}
		};
		
		
		return new Docket(DocumentationType.SWAGGER_2)
				.apiInfo(apiInfo())
				.directModelSubstitute(LocalDate.class, Date.class)
				.directModelSubstitute(LocalDateTime.class, Date.class)
				//选择哪些路径和api会生成document
				.select()
				//对所有api进行监控
				.apis(RequestHandlerSelectors.any())
				.apis(predicate)
				//对所有路径进行监控
				.paths(PathSelectors.any())
				.build();
	}

	private ApiInfo apiInfo(){
		return new ApiInfoBuilder()
				.title("deal-core2-server API文档")
				.build();
	}
}
