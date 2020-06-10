package com.goat.chapter110;

import com.goat.chapter110.bean.*;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;


@Configuration
public class BeanLifeCycle3 {

	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	@Bean(initMethod = "init1", destroyMethod = "destroy1")
	public Car car() {
		return new Car();
	}
}
