package cn.deal.component.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
@Component
public class TagProperties {
    @Value("${tag.color}")
    private String color;

	public String getColor() {
		return color;
	}

}
