package com.goat.chapter201.scope;

import com.goat.chapter201.model.Person;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * Created by Administrator on 2021/6/30.
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2021/6/30---8:43
 */
@Configuration
public class Config {

	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public Person person(){
		Person person = new Person("goat111", 1111);
		return person;
	}
}
