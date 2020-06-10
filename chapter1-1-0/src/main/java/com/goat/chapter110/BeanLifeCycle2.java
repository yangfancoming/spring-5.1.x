package com.goat.chapter110;

import com.goat.chapter110.bean.*;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;


public class BeanLifeCycle2 {

	@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
	@Bean(initMethod = "init1", destroyMethod = "destroy1")
	public Car car() {
		return new Car();
	}
}
