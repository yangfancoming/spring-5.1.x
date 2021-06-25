package org.springframework.context.annotation.componentscan.simple;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class SimpleComponent {

	@Bean
	public String exampleBean() {
		return "example";
	}
}
