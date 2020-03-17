package com.goat.chapter105.item03;

import com.goat.chapter105.model.Person;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

/**
 * Created by Administrator on 2020/3/17.
 * @ Description: @Conditional 注解作用于类   当类上的注解满足条件时，该类下的所有 @Bean 配置才会生效
 * @ author  山羊来了
 * @ date 2020/3/17---13:48
 */
@Configuration
@Conditional(ConditionWindows.class)
//@Conditional(ConditionLinux.class)
public class ConditionalClassConfig {

	@Bean
	public Person zoo(){
		return new Person("zoo",62);
	}

	@Bean
	public Person foo(){
		return new Person("foo", 48);
	}

}
