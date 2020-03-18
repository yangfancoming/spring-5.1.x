package com.goat.chapter110;

import com.goat.chapter110.bean.*;
import org.springframework.context.annotation.Bean;



public class BeanLifeCycle2 {

	@Bean(initMethod = "init1", destroyMethod = "destroy1")
	public Car car() {
		return new Car();
	}
}
