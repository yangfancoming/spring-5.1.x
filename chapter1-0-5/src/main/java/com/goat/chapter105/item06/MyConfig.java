package com.goat.chapter105.item06;

import com.goat.chapter105.common.ValueTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 *  使用@PropertySource读取外部配置文件,使用${}取出配置文件的值
 *   然后 bean的属性可以使用 @Value("${ValueTest.nickName}") 给属性赋值
 */
@PropertySource(value={"classpath:/value.properties"}) //  对应传统xml方式的 <context:property-placeholder location="classpath:person.properties"/>
@Configuration
public class MyConfig {

	@Bean
	public ValueTest valueTest(){
		return new ValueTest();
	}
}
