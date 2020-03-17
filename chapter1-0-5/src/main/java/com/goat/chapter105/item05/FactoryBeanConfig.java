package com.goat.chapter105.item05;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by Administrator on 2020/3/17.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2020/3/17---17:24
 */
@Configuration
public class FactoryBeanConfig {

	@Bean
	public ColorFactoryBean test(){
		return new ColorFactoryBean();
	}
}
