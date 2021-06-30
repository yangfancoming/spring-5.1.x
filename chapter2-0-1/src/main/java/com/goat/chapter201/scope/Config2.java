package com.goat.chapter201.scope;

import com.goat.chapter201.model.Person;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;

/**
 * Created by Administrator on 2021/6/30.
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2021/6/30---8:43
 */
@Configuration
public class Config2 {

	@Bean
	@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
	public Person person(){
		Person person = new Person("goat222", 2222);
		return person;
	}
}
