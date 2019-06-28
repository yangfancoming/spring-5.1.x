

package org.springframework.context.annotation.componentscan.level2;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.tests.sample.beans.TestBean;

@Configuration
@ComponentScan("org.springframework.context.annotation.componentscan.level3")
public class Level2Config {
	@Bean
	public TestBean level2Bean() {
		return new TestBean("level2Bean");
	}
}
