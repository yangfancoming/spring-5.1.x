package com.goat.spring.demo.config;


import com.goat.spring.demo.model.SimpleBook;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
     * @Description: 功能描述：如果XML配置不符合你的喜好的话， Spring还支持使用Java来描述配置
     * @author: Goat
     * @Date:   2018/7/24
*/
@Configuration
public class KnightConfig {

	@Bean
	public SimpleBook knight() {
		return new SimpleBook();
	}

}
